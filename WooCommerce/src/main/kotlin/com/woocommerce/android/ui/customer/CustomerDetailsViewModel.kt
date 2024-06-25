package com.woocommerce.android.ui.customer

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.woocommerce.android.model.CustomerWithAnalytics
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ScopedViewModel
import com.woocommerce.android.viewmodel.getStateFlow
import com.woocommerce.android.viewmodel.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val getCustomerWithStats: GetCustomerWithStats,
) : ScopedViewModel(savedState) {

    private val navArgs: CustomerDetailsFragmentArgs by savedState.navArgs()

    private val _viewState = savedState.getStateFlow(
        scope = viewModelScope,
        initialValue = CustomerViewState(navArgs.customer, true)
    )
    val viewState: LiveData<CustomerViewState> = _viewState.asLiveData()

    init {
        launch {
            val customer = navArgs.customer
            getCustomerWithStats(customer.remoteCustomerId).fold(
                onSuccess = { refreshedCustomer ->
                    _viewState.value = CustomerViewState(
                        customerWithAnalytics = refreshedCustomer,
                        isLoadingAnalytics = false
                    )
                },
                onFailure = {
                    _viewState.value = _viewState.value.copy(isLoadingAnalytics = false)
                }
            )
        }
    }

    fun onNavigateBack() {
        triggerEvent(MultiLiveEvent.Event.Exit)
    }
}

@Parcelize
data class CustomerViewState(
    val customerWithAnalytics: CustomerWithAnalytics,
    val isLoadingAnalytics: Boolean
) : Parcelable
