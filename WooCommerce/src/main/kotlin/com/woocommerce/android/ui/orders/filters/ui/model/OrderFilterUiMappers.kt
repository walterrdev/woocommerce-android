package com.woocommerce.android.ui.orders.filters.ui.model

import com.woocommerce.android.R
import com.woocommerce.android.ui.orders.filters.ui.model.OrderFilterDateRangeUiModel.LAST_2_DAYS
import com.woocommerce.android.ui.orders.filters.ui.model.OrderFilterDateRangeUiModel.THIS_MONTH
import com.woocommerce.android.ui.orders.filters.ui.model.OrderFilterDateRangeUiModel.THIS_WEEK
import com.woocommerce.android.ui.orders.filters.ui.model.OrderFilterDateRangeUiModel.TODAY
import com.woocommerce.android.util.DateUtils
import com.woocommerce.android.viewmodel.ResourceProvider
import org.wordpress.android.fluxc.model.WCOrderStatusModel

fun WCOrderStatusModel.toFilterListOptionUiModel(resourceProvider: ResourceProvider) =
    OrderListFilterOptionUiModel(
        key = statusKey,
        displayName = getDisplayNameForOrderStatus(resourceProvider),
    )

private fun WCOrderStatusModel.getDisplayNameForOrderStatus(resourceProvider: ResourceProvider) =
    if (statusCount > 0) {
        resourceProvider.getString(R.string.orderfilters_order_status_with_count_filter_option, label, statusCount)
    } else {
        label
    }

fun OrderFilterDateRangeUiModel.toFilterListOptionUiModel(resourceProvider: ResourceProvider) =
    OrderListFilterOptionUiModel(
        key = filterKey,
        displayName = resourceProvider.getString(stringResource),
    )

fun OrderFilterDateRangeUiModel.toAfterIso8061DateString(dateUtils: DateUtils): String? {
    val afterDate = when (this) {
        TODAY -> dateUtils.getDateForTodayAtTheStartOfTheDay()
        LAST_2_DAYS -> dateUtils.getCurrentDateTimeMinusDays(2)
        THIS_WEEK -> dateUtils.getDateForFirstDayOfCurrentWeek()
        THIS_MONTH -> dateUtils.getDateForFirstDayOfCurrentMonth()
    }
    return dateUtils.toIso8601Format(afterDate)
}
