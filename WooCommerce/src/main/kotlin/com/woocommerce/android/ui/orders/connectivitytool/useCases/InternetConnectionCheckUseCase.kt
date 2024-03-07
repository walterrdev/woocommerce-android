package com.woocommerce.android.ui.orders.connectivitytool.useCases

import com.woocommerce.android.tools.NetworkStatus
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.Failure
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.InProgress
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InternetConnectionCheckUseCase @Inject constructor(
    private val networkStatus: NetworkStatus
) {
    operator fun invoke(): Flow<ConnectivityCheckStatus> = flow {
        emit(InProgress)
        if (networkStatus.isConnected()) emit(Success)
        else emit(Failure)
    }
}
