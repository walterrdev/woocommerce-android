package com.woocommerce.android.cardreader.config

import com.woocommerce.android.cardreader.connection.ReaderType
import com.woocommerce.android.cardreader.payments.CardPaymentStatus.PaymentMethodType
import kotlinx.parcelize.Parcelize

@Parcelize
object CardReaderConfigForUSA : CardReaderConfigForSupportedCountry(
    currency = "USD",
    countryCode = "US",
    supportedReaders = listOf(ReaderType.ExternalReader.Chipper2X, ReaderType.ExternalReader.StripeM2),
    paymentMethodTypes = listOf(PaymentMethodType.CARD_PRESENT),
    supportedExtensions = listOf(
        SupportedExtension(
            type = SupportedExtensionType.STRIPE,
            supportedSince = "6.2.0"
        ),
        SupportedExtension(
            type = SupportedExtensionType.WC_PAY,
            supportedSince = "3.2.1"
        ),
    ),
)
