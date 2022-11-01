package com.woocommerce.android.iap.internal.core

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails

val ProductDetails.priceOfTheFirstPurchasedOfferInMicros
    get() = subscriptionOfferDetails?.get(0)!!.pricingPhases.pricingPhaseList[0]!!.priceAmountMicros

val ProductDetails.currencyOfTheFirstPurchasedOffer
    get() = subscriptionOfferDetails?.get(0)!!.pricingPhases.pricingPhaseList[0]!!.priceCurrencyCode

val ProductDetails.firstOfferToken
    get() = subscriptionOfferDetails!!.first().offerToken

val BillingResult.isSuccess
    get() = this.responseCode == BillingResponseCode.OK
