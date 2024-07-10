package com.woocommerce.android.ui.moremenu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.woocommerce.android.AppUrls
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsEvent.BLAZE_CAMPAIGN_LIST_ENTRY_POINT_SELECTED
import com.woocommerce.android.analytics.AnalyticsEvent.BLAZE_ENTRY_POINT_DISPLAYED
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.KEY_OPTION
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_ADMIN_MENU
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_COUPONS
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_CUSTOMERS
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_INBOX
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_PAYMENTS
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_PAYMENTS_BADGE_VISIBLE
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_REVIEWS
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_UPGRADES
import com.woocommerce.android.analytics.AnalyticsTracker.Companion.VALUE_MORE_MENU_VIEW_STORE
import com.woocommerce.android.extensions.adminUrlOrDefault
import com.woocommerce.android.notifications.UnseenReviewsCountHandler
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.tools.SiteConnectionType
import com.woocommerce.android.tools.connectionType
import com.woocommerce.android.ui.blaze.BlazeUrlsHelper.BlazeFlowSource
import com.woocommerce.android.ui.blaze.IsBlazeEnabled
import com.woocommerce.android.ui.google.HasGoogleAdsCampaigns
import com.woocommerce.android.ui.google.IsGoogleForWooEnabled
import com.woocommerce.android.ui.moremenu.MoreMenuViewModel.WooPOSCheckStatus.Disabled
import com.woocommerce.android.ui.moremenu.MoreMenuViewModel.WooPOSCheckStatus.Enabled
import com.woocommerce.android.ui.moremenu.MoreMenuViewModel.WooPOSCheckStatus.Loading
import com.woocommerce.android.ui.moremenu.domain.MoreMenuRepository
import com.woocommerce.android.ui.payments.taptopay.TapToPayAvailabilityStatus
import com.woocommerce.android.ui.payments.taptopay.isAvailable
import com.woocommerce.android.ui.plans.domain.SitePlan
import com.woocommerce.android.ui.plans.repository.SitePlanRepository
import com.woocommerce.android.ui.woopos.IsWooPosEnabled
import com.woocommerce.android.ui.woopos.IsWooPosFFEnabled
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ResourceProvider
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.AccountStore
import org.wordpress.android.fluxc.store.blaze.BlazeCampaignsStore
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MoreMenuViewModel @Inject constructor(
    savedState: SavedStateHandle,
    accountStore: AccountStore,
    unseenReviewsCountHandler: UnseenReviewsCountHandler,
    private val selectedSite: SelectedSite,
    private val moreMenuRepository: MoreMenuRepository,
    private val planRepository: SitePlanRepository,
    private val resourceProvider: ResourceProvider,
    private val blazeCampaignsStore: BlazeCampaignsStore,
    private val moreMenuNewFeatureHandler: MoreMenuNewFeatureHandler,
    private val tapToPayAvailabilityStatus: TapToPayAvailabilityStatus,
    private val isBlazeEnabled: IsBlazeEnabled,
    private val isGoogleForWooEnabled: IsGoogleForWooEnabled,
    private val hasGoogleAdsCampaigns: HasGoogleAdsCampaigns,
    private val isWooPosEnabled: IsWooPosEnabled,
    private val isWooPosFFEnabled: IsWooPosFFEnabled,
) : ScopedViewModel(savedState) {
    private var hasCreatedGoogleAdsCampaign = false

    val moreMenuViewState =
        combine(
            unseenReviewsCountHandler.observeUnseenCount(),
            selectedSite.observe().filterNotNull(),
            moreMenuNewFeatureHandler.moreMenuPaymentsFeatureWasClicked,
            loadSitePlanName(),
            checkWooPosAvailability()
        ) { count, selectedSite, paymentsFeatureWasClicked, sitePlanName, wooPosAvailabilityStatus ->
            MoreMenuViewState(
                menuSections = listOf(
                    generatePOSSection(wooPosAvailabilityStatus),
                    generateSettingsMenuButtons(),
                    generateGeneralSection(
                        unseenReviewsCount = count,
                        paymentsFeatureWasClicked = paymentsFeatureWasClicked,
                    )
                ).map { section ->
                    section.copy(
                        items = section.items.filter { it.isVisible }
                    )
                }.filter { it.isVisible && it.items.isNotEmpty() },
                siteName = selectedSite.getSelectedSiteName(),
                siteUrl = selectedSite.getSelectedSiteAbsoluteUrl(),
                sitePlan = sitePlanName,
                userAvatarUrl = accountStore.account.avatarUrl,
                isStoreSwitcherEnabled = selectedSite.connectionType != SiteConnectionType.ApplicationPasswords,
            )
        }.asLiveData()

    fun onViewResumed() {
        moreMenuNewFeatureHandler.markNewFeatureAsSeen()
        launch { trackBlazeDisplayed() }
    }

    private fun generatePOSSection(wooPosAvailabilityStatus: WooPOSCheckStatus) =
        when (wooPosAvailabilityStatus) {
            Loading -> MoreMenuItemSection(
                title = null,
                items = listOf<MoreMenuItem>(MoreMenuItem.Loading(isVisible = true))
            )

            Enabled, Disabled -> {
                MoreMenuItemSection(
                    title = null,
                    items = listOf<MoreMenuItem>(
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_woo_pos,
                            description = R.string.more_menu_button_woo_pos_description,
                            icon = R.drawable.ic_more_menu_pos,
                            extraIcon = R.drawable.ic_more_menu_pos_extra,
                            isVisible = wooPosAvailabilityStatus == Enabled,
                            onClick = {
                                triggerEvent(MoreMenuEvent.NavigateToWooPosEvent)
                            }
                        )
                    )
                )
            }
        }

    @Suppress("LongMethod")
    private suspend fun generateGeneralSection(
        unseenReviewsCount: Int,
        paymentsFeatureWasClicked: Boolean,
    ) = MoreMenuItemSection(
        title = R.string.more_menu_general_section_title,
        items = listOf(
            MoreMenuItem.Button(
                title = R.string.more_menu_button_payments,
                description = R.string.more_menu_button_payments_description,
                icon = R.drawable.ic_more_menu_payments,
                badgeState = buildPaymentsBadgeState(paymentsFeatureWasClicked),
                onClick = ::onPaymentsButtonClick,
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_google,
                description = R.string.more_menu_button_google_description,
                icon = R.drawable.google_logo,
                onClick = ::onPromoteProductsWithGoogle,
                isVisible = isGoogleForWooEnabled()
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_blaze,
                description = R.string.more_menu_button_blaze_description,
                icon = R.drawable.ic_blaze,
                onClick = ::onPromoteProductsWithBlaze,
                isVisible = isBlazeEnabled()
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_wс_admin,
                description = R.string.more_menu_button_wc_admin_description,
                icon = R.drawable.ic_more_menu_wp_admin,
                extraIcon = R.drawable.ic_external,
                onClick = ::onViewAdminButtonClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_store,
                description = R.string.more_menu_button_store_description,
                icon = R.drawable.ic_more_menu_store,
                extraIcon = R.drawable.ic_external,
                onClick = ::onViewStoreButtonClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_coupons,
                description = R.string.more_menu_button_coupons_description,
                icon = R.drawable.ic_more_menu_coupons,
                onClick = ::onCouponsButtonClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_reviews,
                description = R.string.more_menu_button_reviews_description,
                icon = R.drawable.ic_more_menu_reviews,
                badgeState = buildUnseenReviewsBadgeState(unseenReviewsCount),
                onClick = ::onReviewsButtonClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_customers,
                description = R.string.more_menu_button_customers_description,
                icon = R.drawable.icon_multiple_users,
                onClick = ::onCustomersButtonClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_inbox,
                description = R.string.more_menu_button_inbox_description,
                icon = R.drawable.ic_more_menu_inbox,
                isVisible = moreMenuRepository.isInboxEnabled(),
                onClick = ::onInboxButtonClick,
            )
        )
    )

    private fun generateSettingsMenuButtons() = MoreMenuItemSection(
        title = R.string.more_menu_settings_section_title,
        items = listOf(
            MoreMenuItem.Button(
                title = R.string.more_menu_button_settings,
                description = R.string.more_menu_button_settings_description,
                icon = R.drawable.ic_more_screen_settings,
                onClick = ::onSettingsClick
            ),
            MoreMenuItem.Button(
                title = R.string.more_menu_button_subscriptions,
                description = R.string.more_menu_button_subscriptions_description,
                icon = R.drawable.ic_more_menu_upgrades,
                isVisible = moreMenuRepository.isUpgradesEnabled(),
                onClick = ::onUpgradesButtonClick
            )
        )
    )

    private suspend fun trackBlazeDisplayed() {
        if (isBlazeEnabled()) {
            AnalyticsTracker.track(
                stat = BLAZE_ENTRY_POINT_DISPLAYED,
                properties = mapOf(AnalyticsTracker.KEY_BLAZE_SOURCE to BlazeFlowSource.MORE_MENU_ITEM.trackingName)
            )
        }
    }

    private fun buildPaymentsBadgeState(paymentsFeatureWasClicked: Boolean) =
        if (!paymentsFeatureWasClicked && tapToPayAvailabilityStatus().isAvailable) {
            BadgeState(
                badgeSize = R.dimen.major_110,
                backgroundColor = R.color.color_secondary,
                textColor = R.color.color_on_surface,
                textState = TextState("", R.dimen.text_minor_80),
                animateAppearance = true,
            )
        } else {
            null
        }

    private fun buildUnseenReviewsBadgeState(unseenReviewsCount: Int) =
        if (unseenReviewsCount > 0) {
            BadgeState(
                badgeSize = R.dimen.major_150,
                backgroundColor = R.color.color_primary,
                textColor = R.color.color_on_primary,
                textState = TextState(unseenReviewsCount.toString(), R.dimen.text_minor_80),
            )
        } else {
            null
        }

    private fun SiteModel.getSelectedSiteName(): String =
        if (!displayName.isNullOrBlank()) {
            displayName
        } else {
            name
        }

    private fun SiteModel.getSelectedSiteAbsoluteUrl(): String = runCatching { URL(url).host }.getOrDefault("")

    fun onSwitchStoreClick() {
        AnalyticsTracker.track(
            AnalyticsEvent.HUB_MENU_SWITCH_STORE_TAPPED
        )
        triggerEvent(MoreMenuEvent.StartSitePickerEvent)
    }

    private fun onSettingsClick() {
        AnalyticsTracker.track(
            AnalyticsEvent.HUB_MENU_SETTINGS_TAPPED
        )
        triggerEvent(MoreMenuEvent.NavigateToSettingsEvent)
    }

    private fun onPaymentsButtonClick() {
        trackMoreMenuOptionSelected(
            VALUE_MORE_MENU_PAYMENTS,
            mapOf(VALUE_MORE_MENU_PAYMENTS_BADGE_VISIBLE to isPaymentBadgeVisible().toString())
        )
        moreMenuNewFeatureHandler.markPaymentsIconAsClicked()
        triggerEvent(MoreMenuEvent.ViewPayments)
    }

    private fun onPromoteProductsWithGoogle() {
        WooLog.d(WooLog.T.GOOGLE_ADS, "onPromoteProductsWithGoogle")

        launch {
            val urlToOpen = when {
                hasCreatedGoogleAdsCampaign || hasGoogleAdsCampaigns() -> {
                    selectedSite.get().adminUrlOrDefault + AppUrls.GOOGLE_ADMIN_DASHBOARD
                }

                else -> {
                    selectedSite.get().adminUrlOrDefault + AppUrls.GOOGLE_ADMIN_CAMPAIGN_CREATION_SUFFIX
                }
            }

            triggerEvent(MoreMenuEvent.ViewGoogleEvent(urlToOpen))
            // This is just temporary to test this function,
            // in practice we want to set this to true if a campaign is successfully created in webview.
            if (!hasCreatedGoogleAdsCampaign) {
                hasCreatedGoogleAdsCampaign = true
            }
        }
    }

    private fun onPromoteProductsWithBlaze() {
        launch {
            val hasCampaigns = blazeCampaignsStore.getBlazeCampaigns(selectedSite.get()).isNotEmpty()
            if (hasCampaigns) {
                AnalyticsTracker.track(
                    stat = BLAZE_CAMPAIGN_LIST_ENTRY_POINT_SELECTED,
                    properties = mapOf(AnalyticsTracker.KEY_BLAZE_SOURCE to BlazeFlowSource.MORE_MENU_ITEM.trackingName)
                )
                triggerEvent(MoreMenuEvent.OpenBlazeCampaignListEvent)
            } else {
                triggerEvent(
                    MoreMenuEvent.OpenBlazeCampaignCreationEvent(source = BlazeFlowSource.MORE_MENU_ITEM)
                )
            }
        }
    }

    private fun onViewAdminButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_ADMIN_MENU)
        triggerEvent(MoreMenuEvent.ViewAdminEvent(selectedSite.get().adminUrlOrDefault))
    }

    private fun onViewStoreButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_VIEW_STORE)
        triggerEvent(MoreMenuEvent.ViewStoreEvent(selectedSite.get().url))
    }

    private fun onCouponsButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_COUPONS)
        triggerEvent(MoreMenuEvent.ViewCouponsEvent)
    }

    private fun onCustomersButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_CUSTOMERS)
        triggerEvent(MoreMenuEvent.ViewCustomersEvent)
    }

    private fun onReviewsButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_REVIEWS)
        triggerEvent(MoreMenuEvent.ViewReviewsEvent)
    }

    private fun onInboxButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_INBOX)
        triggerEvent(MoreMenuEvent.ViewInboxEvent)
    }

    private fun onUpgradesButtonClick() {
        trackMoreMenuOptionSelected(VALUE_MORE_MENU_UPGRADES)
        triggerEvent(MoreMenuEvent.NavigateToSubscriptionsEvent)
    }

    private fun trackMoreMenuOptionSelected(
        selectedOption: String,
        extraOptions: Map<String, String> = emptyMap()
    ) {
        AnalyticsTracker.track(
            AnalyticsEvent.HUB_MENU_OPTION_TAPPED,
            mapOf(KEY_OPTION to selectedOption) + extraOptions
        )
    }

    private fun isPaymentBadgeVisible() = moreMenuViewState.value
        ?.menuSections
        ?.filterIsInstance<MoreMenuItem.Button>()
        ?.find { it.title == R.string.more_menu_button_payments }
        ?.badgeState != null

    private fun loadSitePlanName(): Flow<String> = selectedSite.observe()
        .filterNotNull()
        .map { site ->
            planRepository.fetchCurrentPlanDetails(site)
                ?.formattedPlanName.orEmpty()
        }
        .onStart { emit("") }

    private fun checkWooPosAvailability(): Flow<WooPOSCheckStatus> =
        flow {
            if (!isWooPosFFEnabled()) {
                emit(Disabled)
                return@flow
            }

            emit(Loading)
            emit(if (isWooPosEnabled()) Enabled else Disabled)
        }

    private val SitePlan.formattedPlanName
        get() = generateFormattedPlanName(resourceProvider)

    private enum class WooPOSCheckStatus {
        Loading, Enabled, Disabled
    }

    data class MoreMenuViewState(
        val menuSections: List<MoreMenuItemSection>,
        val siteName: String = "",
        val siteUrl: String = "",
        val sitePlan: String = "",
        val userAvatarUrl: String = "",
        val isStoreSwitcherEnabled: Boolean = false
    )

    sealed class MoreMenuEvent : MultiLiveEvent.Event() {
        data object NavigateToSettingsEvent : MoreMenuEvent()
        data object NavigateToSubscriptionsEvent : MoreMenuEvent()
        data object StartSitePickerEvent : MoreMenuEvent()
        data object ViewPayments : MoreMenuEvent()
        data object OpenBlazeCampaignListEvent : MoreMenuEvent()
        data class OpenBlazeCampaignCreationEvent(val source: BlazeFlowSource) : MoreMenuEvent()
        data class ViewAdminEvent(val url: String) : MoreMenuEvent()
        data class ViewGoogleEvent(val url: String) : MoreMenuEvent()
        data class ViewStoreEvent(val url: String) : MoreMenuEvent()
        data object ViewReviewsEvent : MoreMenuEvent()
        data object ViewInboxEvent : MoreMenuEvent()
        data object ViewCouponsEvent : MoreMenuEvent()

        data object ViewCustomersEvent : MoreMenuEvent()
        data object NavigateToWooPosEvent : MoreMenuEvent()
    }
}
