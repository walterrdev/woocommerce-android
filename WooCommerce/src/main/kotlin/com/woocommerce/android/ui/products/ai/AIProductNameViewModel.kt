package com.woocommerce.android.ui.products.ai

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.woocommerce.android.ai.AIRepository
import com.woocommerce.android.ai.AIRepository.Companion.PRODUCT_NAME_FEATURE
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.analytics.AnalyticsTrackerWrapper
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event.ExitWithResult
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIProductNameViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val analyticsTracker: AnalyticsTrackerWrapper,
    savedStateHandle: SavedStateHandle
) : ScopedViewModel(savedStateHandle) {

    private val _viewState = MutableStateFlow(
        ViewState()
    )

    val viewState = _viewState.asLiveData()

    private var identifiedLanguageISOCode: String? = null

    private suspend fun identifyLanguage(): Result<String> {
        return aiRepository.identifyISOLanguageCode(
            text = _viewState.value.keywords,
            feature = PRODUCT_NAME_FEATURE
        ).fold(
            onSuccess = { languageISOCode ->
                handleIdentificationSuccess(languageISOCode)
                Result.success(languageISOCode)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    private fun handleIdentificationSuccess(languageISOCode: String) {
        identifiedLanguageISOCode = languageISOCode
    }

    private suspend fun generateProductName(languageISOCode: String) {
        val result = aiRepository.generateProductName(
            keywords = _viewState.value.keywords,
            languageISOCode = languageISOCode
        )
        result.fold(
            onSuccess = ::handleCompletionsSuccess,
            onFailure = ::handleCompletionsFailure
        )
    }

    private fun handleCompletionsSuccess(completions: String) {
        _viewState.update {
            _viewState.value.copy(
                generatedProductName = completions,
                generationState = ViewState.GenerationState.Generated()
            )
        }
    }

    private fun handleCompletionsFailure(throwable: Throwable) {
        when (throwable) {
            is AIRepository.JetpackAICompletionsException -> {
                WooLog.e(WooLog.T.AI, "Fetching Jetpack AI completions failed: ${throwable.errorMessage}")
                _viewState.update {
                    _viewState.value.copy(
                        generationState = ViewState.GenerationState.Generated(hasError = true)
                    )
                }
            }
            else -> {
                WooLog.e(WooLog.T.AI, "Unknown error occurred: ${throwable.message}")
            }
        }
    }

    fun onGenerateButtonClicked(isRegenerate: Boolean = false) {
        analyticsTracker.track(
            AnalyticsEvent.PRODUCT_NAME_AI_GENERATE_BUTTON_TAPPED,
            mapOf(AnalyticsTracker.KEY_IS_RETRY to isRegenerate)
        )

        _viewState.update {
            _viewState.value.copy(
                generationState = ViewState.GenerationState.Generating
            )
        }
        launch {
            val languageISOCode = identifiedLanguageISOCode ?: identifyLanguage().getOrNull()
            languageISOCode?.let { generateProductName(languageISOCode) } ?: run {
                _viewState.update {
                    _viewState.value.copy(
                        generationState = ViewState.GenerationState.Generated(hasError = true)
                    )
                }
            }
        }
    }

    fun onRegenerateButtonClicked() = onGenerateButtonClicked(isRegenerate = true)

    fun onProductKeywordsChanged(keywords: String) {
        _viewState.update {
            _viewState.value.copy(
                keywords = keywords,
            )
        }
    }

    fun onCopyButtonClicked() {
        analyticsTracker.track(AnalyticsEvent.PRODUCT_NAME_AI_COPY_BUTTON_TAPPED)
        triggerEvent(CopyProductNameToClipboard(_viewState.value.generatedProductName))
    }

    fun onApplyButtonClicked() {
        triggerEvent(ExitWithResult(_viewState.value.generatedProductName))
    }

    data class ViewState(
        val keywords: String = "",
        val generatedProductName: String = "",
        val generationState: GenerationState = GenerationState.Start
    ) {
        sealed class GenerationState {
            object Start : GenerationState()
            object Generating : GenerationState()
            data class Generated(val hasError: Boolean = false) : GenerationState()
        }
    }

    data class CopyProductNameToClipboard(val productName: String) : Event()
}
