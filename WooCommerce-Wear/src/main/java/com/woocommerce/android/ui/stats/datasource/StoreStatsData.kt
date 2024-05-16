package com.woocommerce.android.ui.stats.datasource

import com.woocommerce.commons.extensions.convertedFrom

data class StoreStatsData(
    private val revenueData: RevenueData?,
    private val visitorData: Int?
) {
    val revenue get() = revenueData?.totalRevenue.orEmpty()
    val ordersCount get() = revenueData?.orderCount ?: 0
    val visitorsCount get() = visitorData ?: 0
    val conversionRate: String
        get() {
            val ordersCount = revenueData?.orderCount ?: 0
            val visitorsCount = visitorData ?: 0
            return ordersCount convertedFrom visitorsCount
        }

    val isFinished
        get() = revenueData != null &&
            visitorData != null

    data class RevenueData(
        val totalRevenue: String,
        val orderCount: Int
    )
}
