package com.woocommerce.android.ui.orders.connectivitytool

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.woocommerce.android.R

typealias OnReadMoreClicked = (url: String) -> Unit

sealed class ConnectivityCheckCardData(
    @StringRes val title: Int,
    @StringRes val suggestion: Int,
    @DrawableRes val icon: Int,
    val connectivityCheckStatus: ConnectivityCheckStatus,
    val readMoreAction: OnReadMoreClicked? = null
) {
    class InternetConnectivityCheckData(
        checkStatus: ConnectivityCheckStatus
    ) : ConnectivityCheckCardData(
        title = R.string.orderlist_connectivity_tool_internet_check_title,
        suggestion = R.string.orderlist_connectivity_tool_internet_check_suggestion,
        icon = R.drawable.ic_wifi,
        connectivityCheckStatus = checkStatus
    )

    class WordPressConnectivityCheckData(
        checkStatus: ConnectivityCheckStatus
    ) : ConnectivityCheckCardData(
        title = R.string.orderlist_connectivity_tool_wordpress_check_title,
        suggestion = R.string.orderlist_connectivity_tool_wordpress_check_suggestion,
        icon = R.drawable.ic_storage,
        connectivityCheckStatus = checkStatus
    )

    class StoreConnectivityCheckData(
        checkStatus: ConnectivityCheckStatus,
        readMoreAction: OnReadMoreClicked
    ) : ConnectivityCheckCardData(
        title = R.string.orderlist_connectivity_tool_store_check_title,
        suggestion = R.string.orderlist_connectivity_tool_store_check_suggestion,
        icon = R.drawable.ic_more_menu_store,
        connectivityCheckStatus = checkStatus,
        readMoreAction = readMoreAction
    )

    class StoreOrdersConnectivityCheckData(
        checkStatus: ConnectivityCheckStatus,
        readMoreAction: OnReadMoreClicked
    ) : ConnectivityCheckCardData(
        title = R.string.orderlist_connectivity_tool_store_orders_check_title,
        suggestion = R.string.orderlist_connectivity_tool_store_orders_check_suggestion,
        icon = R.drawable.ic_clipboard,
        connectivityCheckStatus = checkStatus,
        readMoreAction = readMoreAction
    )
}

enum class ConnectivityCheckStatus {
    NotStarted,
    InProgress,
    Success,
    Failure;

    fun isFinished() = this == Success || this == Failure
}
