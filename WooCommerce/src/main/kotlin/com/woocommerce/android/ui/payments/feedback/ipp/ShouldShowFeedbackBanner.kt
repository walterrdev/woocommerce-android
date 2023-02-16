package com.woocommerce.android.ui.payments.feedback.ipp

import com.woocommerce.android.AppPrefsWrapper
import com.woocommerce.android.cardreader.config.CardReaderConfigFactory
import com.woocommerce.android.cardreader.config.CardReaderConfigForSupportedCountry
import com.woocommerce.android.ui.payments.cardreader.CashOnDeliverySettingsRepository
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.WooCommerceStore
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ShouldShowFeedbackBanner @Inject constructor(
    private val prefs: AppPrefsWrapper,
    private val cashOnDeliverySettings: CashOnDeliverySettingsRepository,
    private val wooCommerceStore: WooCommerceStore,
    private val siteModel: SiteModel,
    private val cardReaderConfig: CardReaderConfigFactory,
) {
    suspend operator fun invoke(): Boolean {
        return isStoreInSupportedCountry() &&
            isCashOnDeliveryEnabled() &&
            !isSurveyCompletedOrDismissedForever() &&
            wasDismissedLongAgoEnoughToShowAgain()
    }

    private fun isStoreInSupportedCountry(): Boolean {
        val config = cardReaderConfig.getCardReaderConfigFor(wooCommerceStore.getSiteSettings(siteModel)?.countryCode)
        return config is CardReaderConfigForSupportedCountry
    }

    private suspend fun isCashOnDeliveryEnabled(): Boolean =
        cashOnDeliverySettings.isCashOnDeliveryEnabled()

    private fun isSurveyCompletedOrDismissedForever(): Boolean =
        prefs.isIPPFeedbackSurveyCompleted() || prefs.isIPPFeedbackBannerDismissedForever()

    private fun wasDismissedLongAgoEnoughToShowAgain(): Boolean {
        val lastDismissed = prefs.getIPPFeedbackBannerLastDismissed()
        val now = Calendar.getInstance().time.time
        val differenceDays = TimeUnit.DAYS.convert(now - lastDismissed, TimeUnit.MILLISECONDS)
        return differenceDays >= REMIND_LATER_INTERVAL_DAYS
    }

    private companion object {
        private const val REMIND_LATER_INTERVAL_DAYS = 7
    }
}