package com.woocommerce.android.ui.login.storecreation.domainpicker

import com.woocommerce.android.WooException
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.util.dispatchAndAwait
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.wordpress.android.fluxc.Dispatcher
import org.wordpress.android.fluxc.generated.SiteActionBuilder
import org.wordpress.android.fluxc.model.products.Product
import org.wordpress.android.fluxc.network.BaseRequest.GenericErrorType
import org.wordpress.android.fluxc.network.rest.wpapi.WPAPIResponse
import org.wordpress.android.fluxc.network.rest.wpcom.wc.WooError
import org.wordpress.android.fluxc.network.rest.wpcom.wc.WooErrorType
import org.wordpress.android.fluxc.store.ProductsStore
import org.wordpress.android.fluxc.store.SiteStore
import org.wordpress.android.util.AppLog
import org.wordpress.android.util.AppLog.T
import javax.inject.Inject

class DomainSuggestionsRepository @Inject constructor(
    private val dispatcher: Dispatcher,
    private val productStore: ProductsStore,
    private val siteStore: SiteStore
) {
    companion object {
        private const val SUGGESTIONS_REQUEST_COUNT = 20
        private const val DOMAINS_PRODUCT_TYPE = "domains"
    }

    val domainSuggestions: MutableStateFlow<List<DomainSuggestion>> = MutableStateFlow(emptyList())
    val products: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())

    suspend fun fetchDomainSuggestions(domainQuery: String, freeOnly: Boolean): Result<Unit> {
        val domainSuggestionsPayload = SiteStore.SuggestDomainsPayload(
            domainQuery,
            onlyWordpressCom = freeOnly,
            includeWordpressCom = false,
            includeDotBlogSubdomain = false,
            SUGGESTIONS_REQUEST_COUNT,
            includeVendorDot = true
        )
        dispatcher.dispatchAndAwait<SiteStore.SuggestDomainsPayload, SiteStore.OnSuggestedDomains>(
            SiteActionBuilder.newSuggestDomainsAction(domainSuggestionsPayload)
        )
            .let { domainSuggestionsEvent ->
                return if (domainSuggestionsEvent.isError) {
                    WooLog.w(
                        WooLog.T.LOGIN,
                        "Error fetching domain suggestions: ${domainSuggestionsEvent.error.message}"
                    )
                    Result.failure(
                        WooException(
                            WooError(
                                WooErrorType.API_ERROR,
                                GenericErrorType.UNKNOWN,
                                domainSuggestionsEvent.error.message
                            )
                        )
                    )
                } else {
                    WooLog.w(WooLog.T.LOGIN, "Domain suggestions loaded successfully")
                    domainSuggestions.update {
                        domainSuggestionsEvent.suggestions
                            .filter { it.is_free == freeOnly }
                            .map { suggestion ->
                                if (suggestion.is_free) {
                                    DomainSuggestion.Free(
                                        suggestion.domain_name,
                                        suggestion.relevance
                                    )
                                } else {
                                    DomainSuggestion.Paid(
                                        suggestion.domain_name,
                                        suggestion.relevance,
                                        suggestion.cost,
                                        suggestion.product_id,
                                        suggestion.supports_privacy
                                    )
                                }
                            }
                    }
                    Result.success(Unit)
                }
            }
    }

    suspend fun fetchProducts(): Result<Unit> {
        val result = productStore.fetchProducts(DOMAINS_PRODUCT_TYPE)
        return when {
            result.isError -> {
                AppLog.e(T.DOMAIN_REGISTRATION, "An error occurred while fetching WP.com products")
                Result.failure(Exception(result.error.message))
            }
            else -> {
                AppLog.d(T.DOMAIN_REGISTRATION, result.products.toString())
                products.update { result.products ?: emptyList() }
                Result.success(Unit)
            }
        }
    }

    suspend fun fetchDomainPrice(domainName: String): Result<String> {
        return when (val result = siteStore.fetchDomainPrice(domainName)) {
            is WPAPIResponse.Error -> {
                AppLog.e(T.DOMAIN_REGISTRATION, "An error occurred while fetching WP.com products")
                Result.failure(Exception(result.error.message))
            }
            is WPAPIResponse.Success -> {
                AppLog.d(T.DOMAIN_REGISTRATION, result.data.toString())
                if (result.data?.raw_price == null) {
                    return Result.failure(Exception("Domain price is null"))
                } else {
                    Result.success(result.data!!.cost!!)
                }
            }
        }
    }

    sealed interface DomainSuggestion {
        val name: String
        val relevance: Float

        data class Free(
            override val name: String,
            override val relevance: Float
        ) : DomainSuggestion
        data class Paid(
            override val name: String,
            override val relevance: Float,
            val cost: String?,
            val productId: Int,
            val supportsPrivacy: Boolean
        ) : DomainSuggestion
    }
}
