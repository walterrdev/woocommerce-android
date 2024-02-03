package com.woocommerce.android.ui.blaze

import android.os.Parcelable
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.products.ProductDetailRepository
import com.woocommerce.android.util.TimezoneProvider
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize
import org.wordpress.android.fluxc.persistence.blaze.BlazeCampaignsDao.BlazeAdSuggestionEntity
import org.wordpress.android.fluxc.store.blaze.BlazeCampaignsStore
import java.util.Date
import javax.inject.Inject

class BlazeRepository @Inject constructor(
    private val selectedSite: SelectedSite,
    private val blazeCampaignsStore: BlazeCampaignsStore,
    private val productDetailRepository: ProductDetailRepository,
    private val timezoneProvider: TimezoneProvider,
) {
    companion object {
        const val BLAZE_DEFAULT_CURRENCY_CODE = "USD" // For now only USD are supported
        const val DEFAULT_CAMPAIGN_DURATION = 7 // Days
        const val DEFAULT_CAMPAIGN_TOTAL_BUDGET = 35F // USD
        const val CAMPAIGN_MINIMUM_DAILY_SPEND_LIMIT = 5F // USD
        const val CAMPAIGN_MAXIMUM_DAILY_SPEND_LIMIT = 50F // USD
        const val CAMPAIGN_MAX_DURATION = 28 // Days
        const val ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24
    }

    fun observeLanguages() = blazeCampaignsStore.observeBlazeTargetingLanguages()
        .map { it.map { language -> Language(language.id, language.name) } }

    suspend fun fetchLanguages() = blazeCampaignsStore.fetchBlazeTargetingLanguages()

    fun observeDevices() = blazeCampaignsStore.observeBlazeTargetingDevices()
        .map { it.map { device -> Device(device.id, device.name) } }

    suspend fun fetchDevices() = blazeCampaignsStore.fetchBlazeTargetingDevices()

    fun observeInterests() = blazeCampaignsStore.observeBlazeTargetingTopics()
        .map { it.map { interest -> Interest(interest.id, interest.description) } }

    suspend fun fetchInterests() = blazeCampaignsStore.fetchBlazeTargetingTopics()

    suspend fun getMostRecentCampaign() = blazeCampaignsStore.getMostRecentBlazeCampaign(selectedSite.get())

    suspend fun getAdSuggestions(productId: Long): List<AiSuggestionForAd>? {
        fun List<BlazeAdSuggestionEntity>.mapToUiModel(): List<AiSuggestionForAd> {
            return map { AiSuggestionForAd(it.tagLine, it.description) }
        }

        val suggestions = blazeCampaignsStore.getBlazeAdSuggestions(selectedSite.get(), productId)
        return if (suggestions.isNotEmpty()) {
            suggestions.mapToUiModel()
        } else {
            blazeCampaignsStore.fetchBlazeAdSuggestions(selectedSite.get(), productId).model?.mapToUiModel()
        }
    }

    fun getCampaignPreviewDetails(productId: Long): CampaignPreview {
        val product = productDetailRepository.getProduct(productId)
        return CampaignPreview(
            productId = productId,
            userTimeZone = timezoneProvider.deviceTimezone.displayName,
            targetUrl = product?.permalink ?: "",
            urlParams = null,
            campaignImageUrl = product?.firstImageUrl
        )
    }

    @Parcelize
    data class CampaignPreview(
        val productId: Long,
        val userTimeZone: String,
        val targetUrl: String,
        val urlParams: Pair<String, String>?,
        val campaignImageUrl: String?,
    ) : Parcelable

    @Parcelize
    data class AiSuggestionForAd(
        val tagLine: String,
        val description: String,
    ) : Parcelable

    @Parcelize
    data class Budget(
        val totalBudget: Float,
        val spentBudget: Float,
        val currencyCode: String,
        val durationInDays: Int,
        val startDate: Date,
    ) : Parcelable

    @Parcelize
    data class Location(
        val id: String,
        val name: String,
    ) : Parcelable

    @Parcelize
    data class Language(
        val code: String,
        val name: String,
    ) : Parcelable

    @Parcelize
    data class Device(
        val id: String,
        val name: String,
    ) : Parcelable

    @Parcelize
    data class Interest(
        val id: String,
        val description: String,
    ) : Parcelable
}
