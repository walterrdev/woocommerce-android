package com.woocommerce.android.ui.mystore.domain

import com.woocommerce.android.AppPrefsWrapper
import com.woocommerce.android.extensions.formatToYYYYmmDDhhmmss
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.tools.SiteConnectionType
import com.woocommerce.android.ui.analytics.hub.sync.AnalyticsUpdateDataStore
import com.woocommerce.android.ui.analytics.ranges.StatsTimeRangeSelection
import com.woocommerce.android.ui.mystore.data.StatsRepository
import com.woocommerce.android.ui.mystore.data.StatsRepository.StatsException
import com.woocommerce.android.util.CoroutineDispatchers
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
import java.util.Locale
import javax.inject.Inject

class GetStats @Inject constructor(
    private val selectedSite: SelectedSite,
    private val localeProvider: LocaleProvider,
    private val statsRepository: StatsRepository,
    private val appPrefsWrapper: AppPrefsWrapper,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val analyticsUpdateDataStore: AnalyticsUpdateDataStore
) {
    suspend operator fun invoke(refresh: Boolean, granularity: StatsGranularity): Flow<LoadStatsResult> {
        val selectionType = StatsTimeRangeSelection.SelectionType.from(granularity)
        val isForcedRefresh = shouldUpdateStats(selectionType, refresh)
        return merge(
            hasOrders(),
            revenueStats(isForcedRefresh, granularity),
            visitorStats(isForcedRefresh, granularity)
        ).onEach { result ->
            if (result is LoadStatsResult.RevenueStatsSuccess && isForcedRefresh) {
                analyticsUpdateDataStore.storeLastAnalyticsUpdate(selectionRange)
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

    private suspend fun revenueStats(forceRefresh: Boolean, granularity: StatsGranularity): Flow<LoadStatsResult> {
        val (startDate, endDate) = granularity.statsDateRange
        return statsRepository.fetchRevenueStats(
            granularity,
            forceRefresh,
            startDate,
            endDate
        ).transform { result ->
            result.fold(
                onSuccess = { stats ->
                    appPrefsWrapper.setV4StatsSupported(true)
                    emit(LoadStatsResult.RevenueStatsSuccess(stats))
                },
                onFailure = {
                    if (isPluginNotActiveError(it)) {
                        appPrefsWrapper.setV4StatsSupported(false)
                        emit(LoadStatsResult.PluginNotActive)
                    } else {
                        emit(LoadStatsResult.RevenueStatsError)
                    }
                }
            )
        }
    }

    private suspend fun visitorStats(forceRefresh: Boolean, granularity: StatsGranularity): Flow<LoadStatsResult> {
        val (startDate, endDate) = granularity.statsDateRange
        // Visitor stats are only available for Jetpack connected sites
        return when (selectedSite.connectionType) {
            SiteConnectionType.Jetpack -> {
                statsRepository.fetchVisitorStats(granularity, forceRefresh, startDate, endDate)
                    .transform { result ->
                        result.fold(
                            onSuccess = { stats -> emit(LoadStatsResult.VisitorsStatsSuccess(stats)) },
                            onFailure = { emit(LoadStatsResult.VisitorsStatsError) }
                        )
                    }
            }

            else -> selectedSite.connectionType?.let {
                flowOf(LoadStatsResult.VisitorStatUnavailable(it))
            } ?: emptyFlow()
        }
    }

    private fun isPluginNotActiveError(error: Throwable): Boolean =
        (error as? StatsException)?.error?.type == OrderStatsErrorType.PLUGIN_NOT_ACTIVE

    private val StatsGranularity.statsDateRange
        get() = StatsTimeRangeSelection.SelectionType.from(this)
            .generateSelectionData(
                calendar = Calendar.getInstance(),
                locale = localeProvider.provideLocale() ?: Locale.getDefault()
            ).let {
                Pair(
                    it.currentRange.start.formatToYYYYmmDDhhmmss(),
                    it.currentRange.end.formatToYYYYmmDDhhmmss()
                )
            }

    private suspend fun shouldUpdateStats(
        selectionType: StatsTimeRangeSelection.SelectionType,
        refresh: Boolean
    ): Boolean {
        if (refresh) return true
        return analyticsUpdateDataStore
            .shouldUpdateAnalytics(selectionType)
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
