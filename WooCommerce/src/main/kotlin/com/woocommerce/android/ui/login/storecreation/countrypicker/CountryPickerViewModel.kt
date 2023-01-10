package com.woocommerce.android.ui.login.storecreation.countrypicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.woocommerce.android.support.help.HelpOrigin
import com.woocommerce.android.ui.login.storecreation.NewStore
import com.woocommerce.android.viewmodel.MultiLiveEvent
import com.woocommerce.android.viewmodel.ScopedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CountryPickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newStore: NewStore,
) : ScopedViewModel(savedStateHandle) {
    private val availableCountries = MutableStateFlow(emptyList<StoreCreationCountry>())
    val countryPickerContent = availableCountries.map { countries ->
        CountryPickerContent(
            storeName = newStore.data.name ?: "",
            countries = countries
        )
    }.asLiveData()

    fun onArrowBackPressed() {
        triggerEvent(MultiLiveEvent.Event.Exit)
    }

    fun onHelpPressed() {
        triggerEvent(MultiLiveEvent.Event.NavigateToHelpScreen(HelpOrigin.STORE_CREATION))
    }

    fun onContinueClicked() {
        triggerEvent(NavigateToDomainPickerStep)
    }

    fun onCountrySelected(country: StoreCreationCountry){
        newStore.update(country = country.countryName)
    }

    object NavigateToDomainPickerStep : MultiLiveEvent.Event()

    data class CountryPickerContent(
        val storeName: String,
        val countries: List<StoreCreationCountry>
    )

    data class StoreCreationCountry(
        val countryName: String,
        val isSelected: Boolean = false
    )
}
