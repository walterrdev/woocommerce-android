package com.woocommerce.android.ui.payments.changeduecalculator

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.model.OrderNote
import com.woocommerce.android.ui.orders.details.OrderDetailRepository
import com.woocommerce.android.ui.products.ParameterRepository
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ScopedViewModel
import com.woocommerce.android.viewmodel.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ChangeDueCalculatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderDetailRepository: OrderDetailRepository
) : ScopedViewModel(savedStateHandle) {
    @Inject
    lateinit var parameterRepository: ParameterRepository
    val navArgs: ChangeDueCalculatorFragmentArgs by savedStateHandle.navArgs()
    private val orderId: Long = navArgs.orderId

    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val amountDue: BigDecimal,
            val change: BigDecimal,
            val amountReceived: BigDecimal
        ) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        launch {
            val order = orderDetailRepository.getOrderById(orderId)!!
            _uiState.value = UiState.Success(
                amountDue = order.total,
                change = BigDecimal.ZERO,
                amountReceived = BigDecimal.ZERO
            )
        }
    }

    fun onBackPressed() {
        triggerEvent(MultiLiveEvent.Event.Exit)
    }

    fun updateAmountReceived(amount: BigDecimal) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val newChange = amount - currentState.amountDue
            _uiState.value = currentState.copy(amountReceived = amount, change = newChange)
        }
    }

    fun getCurrencySymbol(): String {
        val siteParameters = parameterRepository.getParameters()
        return siteParameters.currencySymbol.orEmpty()
    }

    suspend fun addOrderNote(noteString: String) {
        val draftNote = OrderNote(note = noteString, isCustomerNote = false)

        orderDetailRepository.addOrderNote(orderId, draftNote)
            .fold(
                onSuccess = {
                    AnalyticsTracker.track(AnalyticsEvent.ORDER_NOTE_ADD_SUCCESS)
                },
                onFailure = {
                    AnalyticsTracker.track(
                        AnalyticsEvent.ORDER_NOTE_ADD_FAILED,
                    )
                }
            )
    }
}
