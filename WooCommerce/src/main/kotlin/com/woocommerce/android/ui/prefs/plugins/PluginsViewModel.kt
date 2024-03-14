package com.woocommerce.android.ui.prefs.plugins

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.woocommerce.android.R
import com.woocommerce.android.extensions.isNotNullOrEmpty
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.common.PluginRepository
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Error
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loaded
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loaded.Plugin
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loaded.Plugin.PluginStatus.INACTIVE
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loaded.Plugin.PluginStatus.UPDATE_AVAILABLE
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loaded.Plugin.PluginStatus.UP_TO_DATE
import com.woocommerce.android.ui.prefs.plugins.PluginsViewModel.ViewState.Loading
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.util.WooLog.T
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event.Exit
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.wordpress.android.fluxc.model.plugin.ImmutablePluginModel
import org.wordpress.android.util.helpers.Version
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PluginsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pluginRepository: PluginRepository,
    private val site: SelectedSite
) : ScopedViewModel(savedStateHandle) {
    private val _viewState = MutableSharedFlow<ViewState>(1)
    val viewState = _viewState.asLiveData()

    init {
        loadPlugins()
    }

    private fun loadPlugins() {
        viewModelScope.launch {
            _viewState.emit(Loading)
            pluginRepository.fetchInstalledPlugins(site.get()).fold(
                onSuccess = { plugins ->
                    _viewState.emit(
                        Loaded(
                            plugins = plugins
                                .filter { it.installedVersion.isNotNullOrEmpty() && it.displayName.isNotNullOrEmpty() }
                                .map { Plugin(it.displayName!!, it.authorName, it.installedVersion!!, it.getState()) }
                        )
                    )
                },
                onFailure = { _viewState.emit(Error) }
            )
        }
    }

    private fun ImmutablePluginModel.getState(): Plugin.PluginStatus {
        return when {
            !isActive -> INACTIVE
            isUpdateAvailable() -> UPDATE_AVAILABLE
            else -> UP_TO_DATE
        }
    }

    private fun ImmutablePluginModel.isUpdateAvailable(): Boolean {
        return when {
            installedVersion.isNullOrEmpty() || wpOrgPluginVersion.isNullOrEmpty() -> false
            else -> {
                try {
                    val currentVersion = Version(installedVersion)
                    val availableVersion = Version(wpOrgPluginVersion)
                    currentVersion < availableVersion
                } catch (_: IllegalArgumentException) {
                    val errorStr = String.format(
                        "An IllegalArgumentException occurred while trying to compare site plugin version: %s" +
                            " with wporg plugin version: %s",
                        installedVersion,
                        wpOrgPluginVersion
                    )
                    WooLog.w(T.PLUGINS, errorStr)
                    !installedVersion.equals(wpOrgPluginVersion, ignoreCase = true)
                }
            }
        }
    }

    fun onRetryClicked() {
        loadPlugins()
    }

    fun onBackPressed() {
        triggerEvent(Exit)
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data object Error : ViewState
        data class Loaded(
            val plugins: List<Plugin> = emptyList()
        ) : ViewState {
            data class Plugin(
                val name: String,
                val authorName: String?,
                val version: String,
                val status: PluginStatus
            ) {
                enum class PluginStatus(@StringRes val title: Int, @ColorRes val color: Int) {
                    UP_TO_DATE(R.string.plugin_state_up_to_date, R.color.color_info),
                    UPDATE_AVAILABLE(R.string.plugin_state_update_available, R.color.color_alert),
                    INACTIVE(R.string.plugin_state_inactive, R.color.color_on_surface_disabled)
                }
            }
        }
    }
}
