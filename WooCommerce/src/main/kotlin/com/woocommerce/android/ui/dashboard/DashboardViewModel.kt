package com.woocommerce.android.ui.dashboard

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.woocommerce.android.AppPrefsWrapper
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.analytics.AnalyticsTrackerWrapper
import com.woocommerce.android.extensions.isEligibleForAI
import com.woocommerce.android.extensions.isSitePublic
import com.woocommerce.android.network.ConnectionChangeReceiver
import com.woocommerce.android.network.ConnectionChangeReceiver.ConnectionChangeEvent
import com.woocommerce.android.tools.NetworkStatus
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.analytics.hub.sync.AnalyticsUpdateDataStore
import com.woocommerce.android.ui.analytics.ranges.StatsTimeRangeSelection
import com.woocommerce.android.ui.analytics.ranges.StatsTimeRangeSelection.SelectionType
import com.woocommerce.android.ui.dashboard.DashboardViewModel.DashboardEvent.OpenEditWidgets
import com.woocommerce.android.ui.dashboard.domain.GetTopPerformers
import com.woocommerce.android.ui.dashboard.domain.GetTopPerformers.TopPerformerProduct
import com.woocommerce.android.ui.dashboard.domain.ObserveLastUpdate
import com.woocommerce.android.ui.dashboard.stats.GetSelectedDateRange
import com.woocommerce.android.ui.prefs.privacy.banner.domain.ShouldShowPrivacyBanner
import com.woocommerce.android.util.CurrencyFormatter
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ResourceProvider
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.apache.commons.text.StringEscapeUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.wordpress.android.fluxc.store.WooCommerceStore
import org.wordpress.android.util.FormatUtils
import org.wordpress.android.util.PhotonUtils
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val networkStatus: NetworkStatus,
    private val resourceProvider: ResourceProvider,
    private val wooCommerceStore: WooCommerceStore,
    private val getTopPerformers: GetTopPerformers,
    private val currencyFormatter: CurrencyFormatter,
    private val selectedSite: SelectedSite,
    private val appPrefsWrapper: AppPrefsWrapper,
    private val usageTracksEventEmitter: DashboardStatsUsageTracksEventEmitter,
    private val analyticsTrackerWrapper: AnalyticsTrackerWrapper,
    dashboardTransactionLauncher: DashboardTransactionLauncher,
    private val observeLastUpdate: ObserveLastUpdate,
    getSelectedDateRange: GetSelectedDateRange,
    shouldShowPrivacyBanner: ShouldShowPrivacyBanner
) : ScopedViewModel(savedState) {
    companion object {
        val SUPPORTED_RANGES_ON_MY_STORE_TAB = listOf(
            SelectionType.TODAY,
            SelectionType.WEEK_TO_DATE,
            SelectionType.MONTH_TO_DATE,
            SelectionType.YEAR_TO_DATE,
            SelectionType.CUSTOM
        )
    }

    val performanceObserver: LifecycleObserver = dashboardTransactionLauncher

    private var _topPerformersState = MutableLiveData<TopPerformersState>()
    val topPerformersState: LiveData<TopPerformersState> = _topPerformersState

    private var _hasOrders = MutableLiveData<OrderState>()
    val hasOrders: LiveData<OrderState> = _hasOrders

    private var _lastUpdateTopPerformers = MutableLiveData<Long?>()
    val lastUpdateTopPerformers: LiveData<Long?> = _lastUpdateTopPerformers

    private var _appbarState = MutableLiveData<AppbarState>()
    val appbarState: LiveData<AppbarState> = _appbarState

    private val refreshTrigger = MutableSharedFlow<RefreshState>(extraBufferCapacity = 1)

    private val _selectedDateRange = getSelectedDateRange()
    val selectedDateRange: LiveData<StatsTimeRangeSelection> = _selectedDateRange.asLiveData()

    val storeName = selectedSite.observe().map { site ->
        if (!site?.displayName.isNullOrBlank()) {
            site?.displayName
        } else {
            site?.name
        } ?: resourceProvider.getString(R.string.store_name_default)
    }.asLiveData()

    init {
        ConnectionChangeReceiver.getEventBus().register(this)

        _topPerformersState.value = TopPerformersState(isLoading = true)

        viewModelScope.launch {
            combine(
                _selectedDateRange,
                refreshTrigger.onStart { emit(RefreshState()) }
            ) { selectedRange, refreshEvent ->
                Pair(selectedRange, refreshEvent.shouldRefresh)
            }.collectLatest { (selectedRange, isForceRefresh) ->
                loadTopPerformersStats(selectedRange, isForceRefresh)
            }
        }

        observeTopPerformerUpdates()

        launch {
            shouldShowPrivacyBanner().let {
                if (it) {
                    triggerEvent(DashboardEvent.ShowPrivacyBanner)
                }
            }
        }

        if (selectedSite.getOrNull()?.isEligibleForAI == true &&
            !appPrefsWrapper.wasAIProductDescriptionPromoDialogShown
        ) {
            triggerEvent(DashboardEvent.ShowAIProductDescriptionDialog)
            appPrefsWrapper.wasAIProductDescriptionPromoDialogShown = true
        }

        updateShareStoreButtonVisibility()
    }

    private fun updateShareStoreButtonVisibility() {
        _appbarState.value = AppbarState(showShareStoreButton = selectedSite.get().isSitePublic)
    }

    override fun onCleared() {
        ConnectionChangeReceiver.getEventBus().unregister(this)
        super.onCleared()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ConnectionChangeEvent) {
        if (event.isConnected) {
            refreshTrigger.tryEmit(RefreshState())
        }
    }

    fun onPullToRefresh() {
        usageTracksEventEmitter.interacted()
        analyticsTrackerWrapper.track(AnalyticsEvent.DASHBOARD_PULLED_TO_REFRESH)
        refreshTrigger.tryEmit(RefreshState(isForced = true))
    }

    fun onShareStoreClicked() {
        AnalyticsTracker.track(AnalyticsEvent.DASHBOARD_SHARE_YOUR_STORE_BUTTON_TAPPED)
        triggerEvent(
            DashboardEvent.ShareStore(storeUrl = selectedSite.get().url)
        )
    }

    fun onEditWidgetsClicked() {
        // TODO ADD TRACKING HERE
        triggerEvent(OpenEditWidgets)
    }

    private suspend fun loadTopPerformersStats(selectedRange: StatsTimeRangeSelection, forceRefresh: Boolean) =
        coroutineScope {
            if (!networkStatus.isConnected()) return@coroutineScope

            _topPerformersState.value = _topPerformersState.value?.copy(isLoading = true, isError = false)
            val result = getTopPerformers.fetchTopPerformers(selectedRange, forceRefresh)
            result.fold(
                onFailure = { _topPerformersState.value = _topPerformersState.value?.copy(isError = true) },
                onSuccess = {
                    analyticsTrackerWrapper.track(
                        AnalyticsEvent.DASHBOARD_TOP_PERFORMERS_LOADED,
                        mapOf(AnalyticsTracker.KEY_RANGE to selectedRange.selectionType.identifier)
                    )
                }
            )
            _topPerformersState.value = _topPerformersState.value?.copy(isLoading = false)

            launch {
                observeLastUpdate(
                    selectedRange,
                    AnalyticsUpdateDataStore.AnalyticData.TOP_PERFORMERS
                ).collect { lastUpdateMillis -> _lastUpdateTopPerformers.value = lastUpdateMillis }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTopPerformerUpdates() {
        viewModelScope.launch {
            _selectedDateRange
                .flatMapLatest { dateRange ->
                    getTopPerformers.observeTopPerformers(dateRange)
                }
                .collectLatest {
                    _topPerformersState.value = _topPerformersState.value?.copy(
                        topPerformers = it.toTopPerformersUiList(),
                    )
                }
        }
    }

    private fun onTopPerformerSelected(productId: Long) {
        triggerEvent(DashboardEvent.OpenTopPerformer(productId))
        analyticsTrackerWrapper.track(AnalyticsEvent.TOP_EARNER_PRODUCT_TAPPED)
        usageTracksEventEmitter.interacted()
    }

    private fun List<TopPerformerProduct>.toTopPerformersUiList() = map { it.toTopPerformersUiModel() }

    private fun TopPerformerProduct.toTopPerformersUiModel() =
        TopPerformerProductUiModel(
            productId = productId,
            name = StringEscapeUtils.unescapeHtml4(name),
            timesOrdered = FormatUtils.formatDecimal(quantity),
            netSales = resourceProvider.getString(
                R.string.dashboard_top_performers_net_sales,
                getTotalSpendFormatted(total.toBigDecimal(), currency)
            ),
            imageUrl = imageUrl?.toImageUrl(),
            onClick = ::onTopPerformerSelected
        )

    private fun getTotalSpendFormatted(totalSpend: BigDecimal, currency: String) =
        currencyFormatter.formatCurrency(
            totalSpend,
            wooCommerceStore.getSiteSettings(selectedSite.get())?.currencyCode ?: currency
        )

    private fun String.toImageUrl() =
        PhotonUtils.getPhotonImageUrl(
            this,
            resourceProvider.getDimensionPixelSize(R.dimen.image_minor_100),
            0
        )

    data class TopPerformersState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val topPerformers: List<TopPerformerProductUiModel> = emptyList(),
    )

    sealed class OrderState {
        data object Empty : OrderState()
        data object AtLeastOne : OrderState()
    }

    data class AppbarState(
        val showShareStoreButton: Boolean = false,
    )

    sealed class DashboardEvent : MultiLiveEvent.Event() {
        data class OpenTopPerformer(
            val productId: Long
        ) : DashboardEvent()

        data object ShowPrivacyBanner : DashboardEvent()

        data object ShowAIProductDescriptionDialog : DashboardEvent()

        data class ShareStore(val storeUrl: String) : DashboardEvent()

        data object OpenEditWidgets : DashboardEvent()
    }

    data class RefreshState(private val isForced: Boolean = false) {
        /**
         * [shouldRefresh] will be true only the first time the refresh event is consulted and when
         * isForced is initialized on true. Once the event is handled the property will change its value to false
         */
        var shouldRefresh: Boolean = isForced
            private set
            get(): Boolean {
                val result = field
                if (field) {
                    field = false
                }
                return result
            }
    }
}