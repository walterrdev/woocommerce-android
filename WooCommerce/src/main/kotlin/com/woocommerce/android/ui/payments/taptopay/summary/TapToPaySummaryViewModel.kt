package com.woocommerce.android.ui.payments.taptopay.summary

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.analytics.AnalyticsTrackerWrapper
import com.woocommerce.android.extensions.exhaustive
import com.woocommerce.android.model.Order
import com.woocommerce.android.ui.orders.creation.OrderCreateEditRepository
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event.ShowSnackbar
import com.woocommerce.android.viewmodel.ScopedViewModel
import com.woocommerce.android.viewmodel.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TapToPaySummaryViewModel @Inject constructor(
    private val orderCreateEditRepository: OrderCreateEditRepository,
    private val analyticsTrackerWrapper: AnalyticsTrackerWrapper,
    savedStateHandle: SavedStateHandle,
) : ScopedViewModel(savedStateHandle) {
    private val navArgs: TapToPaySummaryFragmentArgs by savedState.navArgs()

    private val _viewState = MutableLiveData(UiState())
    val viewState: LiveData<UiState> = _viewState

    init {
        analyticsTrackerWrapper.track(AnalyticsEvent.TAP_TO_PAY_SUMMARY_SHOWN)

        when (val flow = navArgs.testTapToPayFlow) {
            TapToPaySummaryFragment.TestTapToPayFlow.Initial -> {
                // no-op
            }
            is TapToPaySummaryFragment.TestTapToPayFlow.OrderRefunded -> {
                showSuccessfulRefundNotification(flow.orderId)
            }
        }.exhaustive
    }

    fun onTryPaymentClicked() {
        analyticsTrackerWrapper.track(AnalyticsEvent.TAP_TO_PAY_SUMMARY_TRY_PAYMENT_TAPPED)
        launch {
            _viewState.value = UiState(isProgressVisible = true)
            val result = orderCreateEditRepository.createSimplePaymentOrder(TEST_ORDER_AMOUNT)
            result.fold(
                onSuccess = {
                    triggerEvent(StartTryPaymentFlow(it))
                },
                onFailure = {
                    analyticsTrackerWrapper.track(
                        AnalyticsEvent.PAYMENTS_FLOW_FAILED,
                        mapOf(
                            AnalyticsTracker.KEY_SOURCE to AnalyticsTracker.VALUE_TAP_TO_PAY_SOURCE_TRY_PAYMENT_PROMPT,
                            AnalyticsTracker.KEY_FLOW to AnalyticsTracker.VALUE_TTP_TRY_PAYMENT_FLOW,
                        )
                    )
                    triggerEvent(ShowSnackbar(R.string.card_reader_tap_to_pay_explanation_test_payment_error))
                }
            )
            _viewState.value = UiState(isProgressVisible = false)
        }
    }

    fun onBackClicked() {
        triggerEvent(Event.Exit)
    }

    private fun showSuccessfulRefundNotification(orderId: Long) {
        triggerEvent(
            ShowSuccessfulRefundNotification(
                message = R.string.card_reader_tap_to_pay_successful_refund_message,
                actionLabel = R.string.card_reader_tap_to_pay_successful_refund_action_label,
                action = {
                    triggerEvent(NavigateToOrderDetails(orderId))
                }
            )
        )
    }

    data class UiState(
        val isProgressVisible: Boolean = false
    )

    data class StartTryPaymentFlow(val order: Order) : Event()

    data class NavigateToOrderDetails(val orderId: Long) : Event()

    data class ShowSuccessfulRefundNotification(
        @StringRes val message: Int,
        @StringRes val actionLabel: Int,
        val action: () -> Unit
    ): Event()

    companion object {
        private val TEST_ORDER_AMOUNT = BigDecimal.valueOf(0.5)
    }
}
