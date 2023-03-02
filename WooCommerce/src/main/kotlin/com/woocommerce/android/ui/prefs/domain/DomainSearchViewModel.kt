package com.woocommerce.android.ui.prefs.domain

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.support.help.HelpOrigin
import com.woocommerce.android.ui.common.domain.DomainSuggestionsRepository
import com.woocommerce.android.ui.common.domain.DomainSuggestionsViewModel
import com.woocommerce.android.util.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DomainSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    domainSuggestionsRepository: DomainSuggestionsRepository,
    currencyFormatter: CurrencyFormatter,
) : DomainSuggestionsViewModel(savedStateHandle, domainSuggestionsRepository, currencyFormatter) {
    override val helpOrigin = HelpOrigin.DOMAIN_CHANGE
}