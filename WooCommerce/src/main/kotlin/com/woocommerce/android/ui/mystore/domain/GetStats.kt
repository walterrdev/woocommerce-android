package com.woocommerce.android.ui.mystore.domain

import com.woocommerce.android.AppPrefsWrapper
import com.woocommerce.android.extensions.formatToYYYYmmDDhhmmss
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.tools.SiteConnectionType
import com.woocommerce.android.ui.analytics.hub.sync.AnalyticsUpdateDataStore
import com.woocommerce.android.ui.analytics.ranges.StatsTimeRangeSelection
import com.woocommerce.android.ui.mystore.data.StatsRepository
import com.woocommerce.android.ui.mystore.data.StatsRepository.StatsException
import com.woocommerce.android.ui.mystore.data.asRevenueRangeId
import com.woocommerce.android.util.CoroutineDispatchers
import com.woocommerce.android.util.DateUtils
import com.woocommerce.android.util.locale.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import org.wordpress.android.fluxc.model.WCRevenueStatsModel
import org.wordpress.android.fluxc.store.WCStatsStore.OrderStatsErrorType
import org.wordpress.android.fluxc.store.WCStatsStore.StatsGranularity
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetStats @Inject constructor(
    private val selectedSite: SelectedSite,
    private val localeProvider: LocaleProvider,
    private val statsRepository: StatsRepository,
    private val appPrefsWrapper: AppPrefsWrapper,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val analyticsUpdateDataStore: AnalyticsUpdateDataStore,
    private val dateUtils: DateUtils
) {
    suspend operator fun invoke(refresh: Boolean, granularity: StatsGranularity): Flow<LoadStatsResult> {
        val selectionRange = granularity.asRangeSelection(
            dateUtils = dateUtils,
            locale = localeProvider.provideLocale()
        )
        val shouldRefreshRevenue =
            shouldUpdateStats(selectionRange, refresh, AnalyticsUpdateDataStore.AnalyticData.REVENUE)
        val shouldRefreshVisitors =
            shouldUpdateStats(selectionRange, refresh, AnalyticsUpdateDataStore.AnalyticData.VISITORS)
        return merge(
            hasOrders(),
            revenueStats(selectionRange, granularity, shouldRefreshRevenue),
            visitorStats(selectionRange, granularity, shouldRefreshRevenue)
        ).onEach { result ->
            if (result is LoadStatsResult.RevenueStatsSuccess && shouldRefreshRevenue) {
                analyticsUpdateDataStore.storeLastAnalyticsUpdate(
                    rangeSelection = selectionRange,
                    analyticData = AnalyticsUpdateDataStore.AnalyticData.REVENUE
                )
            }
            if (result is LoadStatsResult.VisitorsStatsSuccess && shouldRefreshVisitors) {
                analyticsUpdateDataStore.storeLastAnalyticsUpdate(
                    rangeSelection = selectionRange,
                    analyticData = AnalyticsUpdateDataStore.AnalyticData.VISITORS
                )
            }
        }.flowOn(coroutineDispatchers.computation)
    }

    private suspend fun hasOrders(): Flow<LoadStatsResult.HasOrders> =
        statsRepository.checkIfStoreHasNoOrders()
            .transform {
                if (it.getOrNull() == true) {
                    emit(LoadStatsResult.HasOrders(false))
                } else {
                    emit(LoadStatsResult.HasOrders(true))
                }
            }

    private suspend fun revenueStats(
        rangeSelection: StatsTimeRangeSelection,
        granularity: StatsGranularity,
        forceRefresh: Boolean
    ): Flow<LoadStatsResult> {
        // This is a temporary fix until we update the ViewModel to use the new date range selection
        // and offer the correct granularities
        fun StatsGranularity.fixRevenueGranularity() = when (this) {
            StatsGranularity.YEARS -> StatsGranularity.MONTHS
            StatsGranularity.MONTHS, StatsGranularity.WEEKS -> StatsGranularity.DAYS
            StatsGranularity.DAYS -> StatsGranularity.HOURS
            else -> this
        }

        val revenueRangeId = rangeSelection.selectionType.identifier.asRevenueRangeId(
            startDate = rangeSelection.currentRange.start,
            endDate = rangeSelection.currentRange.end
        )
        if (forceRefresh.not()) {
            statsRepository.getRevenueStatsById(revenueRangeId)
                .takeIf { it.isSuccess && it.getOrNull() != null }
                ?.let { return flowOf(LoadStatsResult.RevenueStatsSuccess(it.getOrNull())) }
        }

        val revenueStatsResult = statsRepository.fetchRevenueStats(
            range = rangeSelection.currentRange,
            granularity = granularity.fixRevenueGranularity(),
            forced = forceRefresh,
            revenueRangeId = revenueRangeId
        ).let { result ->
            result.fold(
                onSuccess = { stats ->
                    appPrefsWrapper.setV4StatsSupported(true)
                    LoadStatsResult.RevenueStatsSuccess(stats)
                },
                onFailure = {
                    if (isPluginNotActiveError(it)) {
                        appPrefsWrapper.setV4StatsSupported(false)
                        LoadStatsResult.PluginNotActive
                    } else {
                        LoadStatsResult.RevenueStatsError
                    }
                }
            )
        }
        return flowOf(revenueStatsResult)
    }

    private suspend fun visitorStats(
        rangeSelection: StatsTimeRangeSelection,
        granularity: StatsGranularity,
        forceRefresh: Boolean
    ): Flow<LoadStatsResult> {
        // Visitor stats are only available for Jetpack connected sites
        return when (selectedSite.connectionType) {
            SiteConnectionType.Jetpack -> {
                // This is a temporary fix until we update the ViewModel to use the new date range selection
                // and offer the correct granularities
                fun StatsGranularity.fixVisitorsGranularity() = when (this) {
                    StatsGranularity.YEARS -> StatsGranularity.MONTHS
                    StatsGranularity.MONTHS, StatsGranularity.WEEKS -> StatsGranularity.DAYS
                    else -> this
                }

                val result = statsRepository.fetchVisitorStats(
                    range = rangeSelection.currentRange,
                    granularity = granularity.fixVisitorsGranularity(),
                    forced = forceRefresh
                )
                    .let { result ->
                        result.fold(
                            onSuccess = { stats -> LoadStatsResult.VisitorsStatsSuccess(stats) },
                            onFailure = { LoadStatsResult.VisitorsStatsError }
                        )
                    }
                flowOf(result)
            }

            else -> selectedSite.connectionType?.let {
                flowOf(LoadStatsResult.VisitorStatUnavailable(it))
            } ?: emptyFlow()
        }
    }

    private fun isPluginNotActiveError(error: Throwable): Boolean =
        (error as? StatsException)?.error?.type == OrderStatsErrorType.PLUGIN_NOT_ACTIVE

    private val StatsGranularity.statsDateRange
        get() = asRangeSelection(
            dateUtils = dateUtils,
            locale = localeProvider.provideLocale()
        ).let {
            Pair(
                it.currentRange.start.formatToYYYYmmDDhhmmss(),
                it.currentRange.end.formatToYYYYmmDDhhmmss()
            )
        }

    private suspend fun shouldUpdateStats(
        selectionRange: StatsTimeRangeSelection,
        refresh: Boolean,
        analyticData: AnalyticsUpdateDataStore.AnalyticData
    ): Boolean {
        if (refresh) return true
        return analyticsUpdateDataStore
            .shouldUpdateAnalytics(
                rangeSelection = selectionRange,
                analyticData = analyticData
            )
            .firstOrNull() ?: true
    }

    sealed class LoadStatsResult {
        data class RevenueStatsSuccess(
            val stats: WCRevenueStatsModel?
        ) : LoadStatsResult()

        data class VisitorsStatsSuccess(
            val stats: Map<String, Int>
        ) : LoadStatsResult()

        data class HasOrders(
            val hasOrder: Boolean
        ) : LoadStatsResult()

        object RevenueStatsError : LoadStatsResult()
        object VisitorsStatsError : LoadStatsResult()
        object PluginNotActive : LoadStatsResult()
        data class VisitorStatUnavailable(
            val connectionType: SiteConnectionType
        ) : LoadStatsResult()
    }
}

fun StatsGranularity.asRangeSelection(dateUtils: DateUtils, locale: Locale? = null) =
    StatsTimeRangeSelection.SelectionType.from(this)
        .generateSelectionData(
            calendar = Calendar.getInstance(),
            locale = locale ?: Locale.getDefault(),
            referenceStartDate = dateUtils.getCurrentDateInSiteTimeZone() ?: Date(),
            referenceEndDate = dateUtils.getCurrentDateInSiteTimeZone() ?: Date()
        )
