package com.woocommerce.android.ui.themes

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.woocommerce.android.AppPrefsWrapper
import com.woocommerce.android.R
import com.woocommerce.android.model.Theme
import com.woocommerce.android.ui.common.wpcomwebview.WPComWebViewAuthenticator
import com.woocommerce.android.ui.login.storecreation.NewStore
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ResourceProvider
import com.woocommerce.android.viewmodel.ScopedViewModel
import com.woocommerce.android.viewmodel.getNullableStateFlow
import com.woocommerce.android.viewmodel.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.parcelize.Parcelize
import org.wordpress.android.fluxc.network.UserAgent
import org.wordpress.android.fluxc.store.ThemeCoroutineStore
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThemePreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val wpComWebViewAuthenticator: WPComWebViewAuthenticator,
    val userAgent: UserAgent,
    private val themeCoroutineStore: ThemeCoroutineStore,
    private val resourceProvider: ResourceProvider,
    private val themeRepository: ThemeRepository,
    private val appPrefsWrapper: AppPrefsWrapper,
    private val newStore: NewStore
) : ScopedViewModel(savedStateHandle) {
    private val navArgs: ThemePreviewFragmentArgs by savedStateHandle.navArgs()

    private val theme = flow {
        val theme = themeRepository.getTheme(navArgs.themeId) ?: run {
            WooLog.w(WooLog.T.THEMES, "Theme not found!!")
            triggerEvent(MultiLiveEvent.Event.Exit)
            return@flow
        }
        emit(theme)
    }
    private val themePages = theme.flatMapLatest { it.prepareThemeDemoPages() }
    private val _selectedPage = savedStateHandle.getNullableStateFlow(
        viewModelScope,
        null,
        ThemeDemoPage::class.java,
        "selectedPage"
    )

    val viewState = combine(theme, _selectedPage, themePages) { theme, selectedPage, demoPages ->
        ViewState(
            themeName = theme.name,
            isFromStoreCreation = true, // TODO Pass this from the previous screen
            themePages = demoPages.mapIndexed { index, page ->
                page.copy(isLoaded = if (selectedPage == null) index == 0 else page.uri == selectedPage.uri)
            }
        )
    }.asLiveData()

    fun onPageSelected(demoPage: ThemeDemoPage) {
        _selectedPage.value = demoPage
    }

    fun onBackNavigationClicked() {
        triggerEvent(MultiLiveEvent.Event.Exit)
    }

    fun onActivateThemeClicked() {
        if (viewState.value?.isFromStoreCreation == true) {
            appPrefsWrapper.saveThemeIdForStoreCreation(newStore.data.siteId!!, navArgs.themeId)
            triggerEvent(ContinueStoreCreationWithTheme)
        } else {
            TODO()
        }
    }

    private suspend fun Theme.prepareThemeDemoPages(): Flow<List<ThemeDemoPage>> = flow {
        val homePage = ThemeDemoPage(
            uri = demoUrl,
            title = resourceProvider.getString(R.string.theme_preview_bottom_sheet_home_section),
            isLoaded = true
        )
        emit(listOf(homePage))
        emit(
            buildList {
                add(homePage)
                addAll(
                    themeCoroutineStore.fetchDemoThemePages(demoUrl).map {
                        ThemeDemoPage(
                            uri = it.link,
                            title = it.title,
                            isLoaded = false
                        )
                    }
                )
            }
        )
    }

    data class ViewState(
        val themeName: String,
        val isFromStoreCreation: Boolean,
        val themePages: List<ThemeDemoPage>
    ) {
        val demoUri: String
            get() = themePages.first { it.isLoaded }.uri
    }

    @Parcelize
    data class ThemeDemoPage(
        val uri: String,
        val title: String,
        val isLoaded: Boolean
    ) : Parcelable

    object ContinueStoreCreationWithTheme : MultiLiveEvent.Event()
}
