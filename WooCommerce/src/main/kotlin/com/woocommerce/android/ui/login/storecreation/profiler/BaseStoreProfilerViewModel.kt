package com.woocommerce.android.ui.login.storecreation.profiler

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.woocommerce.android.ui.login.storecreation.NewStore
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ScopedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

abstract class BaseStoreProfilerViewModel(
    savedStateHandle: SavedStateHandle,
    private val newStore: NewStore,
) : ScopedViewModel(savedStateHandle) {
    protected abstract val profilerStep: ProfilerOptionType

    protected val profilerOptions = MutableStateFlow(emptyList<StoreProfilerOptionUi>())
    val storeProfilerContent: LiveData<ViewState> = profilerOptions
        .map { options ->
            if (options.isEmpty()) {
                LoadingState
            } else {
                StoreProfilerContent(
                    storeName = newStore.data.name ?: "",
                    title = getProfilerStepTitle(),
                    description = getProfilerStepDescription(),
                    options = options
                )
            }
        }.asLiveData()

    protected abstract fun getProfilerStepDescription(): String

    protected abstract fun getProfilerStepTitle(): String

    abstract fun onContinueClicked()

    fun onSkipPressed() {
        triggerEvent(NavigateToDomainPickerStep)
    }

    fun onArrowBackPressed() {
        triggerEvent(MultiLiveEvent.Event.Exit)
    }

    fun onOptionSelected(option: StoreProfilerOptionUi) {
        profilerOptions.update { currentOptions ->
            currentOptions.map {
                if (option.name == it.name) it.copy(isSelected = true)
                else it.copy(isSelected = false)
            }
        }
    }

    sealed class ViewState : Parcelable

    @Parcelize
    object LoadingState : ViewState()

    @Parcelize
    data class StoreProfilerContent(
        val storeName: String,
        val title: String,
        val description: String,
        val options: List<StoreProfilerOptionUi> = emptyList()
    ) : ViewState(), Parcelable

    @Parcelize
    data class StoreProfilerOptionUi(
        val type: ProfilerOptionType,
        val name: String,
        val isSelected: Boolean,
    ) : Parcelable

    object NavigateToDomainPickerStep : MultiLiveEvent.Event()
    object NavigateToCommerceJourneyStep : MultiLiveEvent.Event()
    object NavigateToEcommercePlatformsStep : MultiLiveEvent.Event()

    enum class ProfilerOptionType {
        SITE_INDUSTRY,
        COMMERCE_JOURNEY,
        ECOMMERCE_PLATFORM
    }
}
