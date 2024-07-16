package com.woocommerce.android.ui.dashboard.google

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.woocommerce.android.AppUrls
import com.woocommerce.android.R
import com.woocommerce.android.extensions.adminUrlOrDefault
import com.woocommerce.android.model.DashboardWidget
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.dashboard.DashboardViewModel
import com.woocommerce.android.ui.dashboard.DashboardViewModel.DashboardWidgetAction
import com.woocommerce.android.ui.dashboard.DashboardViewModel.DashboardWidgetMenu
import com.woocommerce.android.ui.dashboard.DashboardViewModel.RefreshEvent
import com.woocommerce.android.ui.dashboard.defaultHideMenuEntry
import com.woocommerce.android.ui.google.CanUseAutoLoginWebview
import com.woocommerce.android.ui.google.HasGoogleAdsCampaigns
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import org.wordpress.android.fluxc.store.WCGoogleStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltViewModel(assistedFactory = DashboardGoogleAdsViewModel.Factory::class)
@Suppress("MagicNumber")
class DashboardGoogleAdsViewModel @AssistedInject constructor(
    savedStateHandle: SavedStateHandle,
    private val selectedSite: SelectedSite,
    @Assisted private val parentViewModel: DashboardViewModel,
    private val hasGoogleAdsCampaigns: HasGoogleAdsCampaigns,
    private val canUseAutoLoginWebview: CanUseAutoLoginWebview,
    private val googleAdsStore: WCGoogleStore,
) : ScopedViewModel(savedStateHandle) {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val distantPastDate: String = dateFormatter.format(LocalDateTime.of(1970, 1, 1, 0, 0))
    private val currentDate: String = dateFormatter.format(LocalDateTime.now())

    private val _refreshTrigger = MutableSharedFlow<RefreshEvent>(extraBufferCapacity = 1)
    private val refreshTrigger = merge(_refreshTrigger, (parentViewModel.refreshTrigger))
        .onStart { emit(RefreshEvent()) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState = refreshTrigger
        .transformLatest {
            emit(DashboardGoogleAdsState.Loading)

            hasGoogleAdsCampaigns().fold(
                onSuccess = { hasCampaigns ->
                    emit(
                        if (hasCampaigns) {
                            googleAdsStore.fetchImpressionsAndClicks(
                                site = selectedSite.get(),
                                startDate = distantPastDate,
                                endDate = currentDate
                            ).let { result ->

                                when {
                                    result.isError -> DashboardGoogleAdsState.Error(widgetMenu)
                                    else -> {
                                        val impressions = result.model?.first?.toInt()?.toString() ?: "0"
                                        val clicks = result.model?.second?.toInt()?.toString() ?: "0"

                                        val campaignButton = DashboardWidgetAction(
                                            titleResource =
                                            R.string.dashboard_google_ads_card_view_all_campaigns_button,
                                            action = { launchCampaignDetails() }
                                        )
                                        DashboardGoogleAdsState.HasCampaigns(
                                            impressions = impressions,
                                            clicks = clicks,
                                            onCreateCampaignClicked = { launchCampaignCreation() },
                                            onPerformanceAreaClicked = { launchCampaignDetails() },
                                            showAllCampaignsButton = campaignButton,
                                            menu = widgetMenu
                                        )
                                    }
                                }
                            }
                        } else {
                            DashboardGoogleAdsState.NoCampaigns(
                                onCreateCampaignClicked = { launchCampaignCreation() },
                                menu = widgetMenu
                            )
                        }
                    )
                },
                onFailure = {
                    emit(DashboardGoogleAdsState.Error(widgetMenu))
                }
            )
        }
        .asLiveData()

    private val widgetMenu = DashboardWidgetMenu(
        items = listOf(
            DashboardWidget.Type.GOOGLE_ADS.defaultHideMenuEntry {
                parentViewModel.onHideWidgetClicked(DashboardWidget.Type.GOOGLE_ADS)
            }
        )
    )

    private fun launchCampaignCreation() {
        val creationUrl = selectedSite.get().adminUrlOrDefault + AppUrls.GOOGLE_ADMIN_CAMPAIGN_CREATION_SUFFIX
        triggerEvent(ViewGoogleForWooEvent(creationUrl, canUseAutoLoginWebview()))
    }

    private fun launchCampaignDetails() {
        val adminUrl = selectedSite.get().adminUrlOrDefault + AppUrls.GOOGLE_ADMIN_DASHBOARD
        triggerEvent(ViewGoogleForWooEvent(adminUrl, canUseAutoLoginWebview()))
    }

    fun onRefresh() {
        _refreshTrigger.tryEmit(RefreshEvent(isForced = true))
    }

    sealed class DashboardGoogleAdsState(
        open val menu: DashboardWidgetMenu,
        val mainButton: DashboardWidgetAction? = null
    ) {
        data object Loading : DashboardGoogleAdsState(DashboardWidgetMenu(emptyList()))
        data class Error(
            override val menu: DashboardWidgetMenu
        ) : DashboardGoogleAdsState(menu)

        data class NoCampaigns(
            val onCreateCampaignClicked: () -> Unit,
            override val menu: DashboardWidgetMenu
        ) : DashboardGoogleAdsState(menu)

        data class HasCampaigns(
            val impressions: String,
            val clicks: String,
            val onCreateCampaignClicked: () -> Unit,
            val onPerformanceAreaClicked: () -> Unit,
            val showAllCampaignsButton: DashboardWidgetAction,
            override val menu: DashboardWidgetMenu
        ) : DashboardGoogleAdsState(menu, showAllCampaignsButton)
    }

    data class ViewGoogleForWooEvent(val url: String, val canAutoLogin: Boolean) : MultiLiveEvent.Event()

    @AssistedFactory
    interface Factory {
        fun create(parentViewModel: DashboardViewModel?): DashboardGoogleAdsViewModel
    }
}
