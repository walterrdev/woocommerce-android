package com.woocommerce.android.model

import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.parcelize.Parcelize
import org.wordpress.android.fluxc.model.WCMetaData
import org.wordpress.android.fluxc.model.WCProductModel.SubscriptionMetadataKeys
import java.math.BigDecimal

@Parcelize
data class SubscriptionDetails(
    val price: BigDecimal?,
    val period: SubscriptionPeriod,
    val periodInterval: Int,
    val length: Int?,
    val signUpFee: BigDecimal?,
    val trialPeriod: SubscriptionPeriod?,
    val trialLength: Int?,
    val oneTimeShipping: Boolean,
    val paymentsSyncDate: Int?
) : Parcelable 

fun SubscriptionDetails.toMetadataJson(): JsonArray {
    val subscriptionValues = mapOf(
        SubscriptionMetadataKeys.SUBSCRIPTION_PRICE to price,
        SubscriptionMetadataKeys.SUBSCRIPTION_PERIOD to period.value,
        SubscriptionMetadataKeys.SUBSCRIPTION_PERIOD_INTERVAL to periodInterval,
        SubscriptionMetadataKeys.SUBSCRIPTION_LENGTH to length,
        SubscriptionMetadataKeys.SUBSCRIPTION_SIGN_UP_FEE to signUpFee?.toString().orEmpty(),
        SubscriptionMetadataKeys.SUBSCRIPTION_TRIAL_PERIOD to trialPeriod?.value,
        SubscriptionMetadataKeys.SUBSCRIPTION_TRIAL_LENGTH to (trialLength ?: 0),
        SubscriptionMetadataKeys.SUBSCRIPTION_ONE_TIME_SHIPPING to oneTimeShipping
    )
    val jsonArray = JsonArray()
    subscriptionValues.forEach { (key, value) ->
        jsonArray.add(
            JsonObject().also { json ->
                json.addProperty(WCMetaData.KEY, key)
                json.addProperty(WCMetaData.VALUE, value.toString())
            }
        )
    }
    return jsonArray
}
