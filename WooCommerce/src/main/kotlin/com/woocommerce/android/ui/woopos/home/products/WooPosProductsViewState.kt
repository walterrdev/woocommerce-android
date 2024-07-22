package com.woocommerce.android.ui.woopos.home.products

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

sealed class WooPosProductsViewState(
    open val reloadingProducts: Boolean,
) {
    data class Content(
        val products: List<WooPosProductsListItem>,
        val loadingMore: Boolean,
        val bannerState: BannerState? = null,
        override val reloadingProducts: Boolean = false,
    ) : WooPosProductsViewState(reloadingProducts) {
        data class BannerState(
            val isSimpleProductsOnlyBannerShown: Boolean,
            @StringRes val title: Int,
            @StringRes val message: Int,
            val onBannerClosed: () -> Unit,
            val onLearnMoreClicked: () -> Unit,
        )
    }

    data class Loading(override val reloadingProducts: Boolean = false) :
        WooPosProductsViewState(reloadingProducts)

    data class Error(override val reloadingProducts: Boolean = false) :
        WooPosProductsViewState(reloadingProducts)

    data class Empty(override val reloadingProducts: Boolean = false) :
        WooPosProductsViewState(reloadingProducts)
}

@Parcelize
data class WooPosProductsListItem(
    val id: Long,
    val name: String,
    val price: String,
    val imageUrl: String?,
) : Parcelable
