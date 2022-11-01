package com.woocommerce.android.di

import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.mystore.MyStoreViewModel
import com.woocommerce.android.ui.orders.list.OrderListViewModel
import com.woocommerce.android.ui.payments.SelectPaymentMethodViewModel
import com.woocommerce.android.ui.payments.cardreader.hub.CardReaderHubViewModel
import com.woocommerce.android.util.UtmProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WooCommerceComUTMProviderModule {
    @Provides
    @Singleton
    @Named("order-list")
    fun provideOrderListUpsellCardReaderUtm(
        selectedSite: SelectedSite
    ) = UtmProvider(
        campaign = OrderListViewModel.UTM_CAMPAIGN,
        source = OrderListViewModel.UTM_SOURCE,
        content = OrderListViewModel.UTM_CONTENT,
        siteId = selectedSite.getIfExists()?.siteId
    )

    @Provides
    @Singleton
    @Named("select-payment")
    fun provideSelectPaymentMethodUpsellCardReaderUtm(
        selectedSite: SelectedSite
    ) = UtmProvider(
        campaign = SelectPaymentMethodViewModel.UTM_CAMPAIGN,
        source = SelectPaymentMethodViewModel.UTM_SOURCE,
        content = SelectPaymentMethodViewModel.UTM_CONTENT,
        siteId = selectedSite.getIfExists()?.siteId
    )

    @Provides
    @Singleton
    @Named("payment-menu")
    fun providePaymentMenuUtm(
        selectedSite: SelectedSite
    ) = UtmProvider(
        campaign = CardReaderHubViewModel.UTM_CAMPAIGN,
        source = CardReaderHubViewModel.UTM_SOURCE,
        content = null,
        siteId = selectedSite.getIfExists()?.siteId
    )

    @Provides
    @Singleton
    @Named("my-store")
    fun provideMyStoreUtm(
        selectedSite: SelectedSite
    ) = UtmProvider(
        campaign = MyStoreViewModel.UTM_CAMPAIGN,
        source = MyStoreViewModel.UTM_SOURCE,
        content = MyStoreViewModel.UTM_CONTENT,
        siteId = selectedSite.getIfExists()?.siteId
    )
}
