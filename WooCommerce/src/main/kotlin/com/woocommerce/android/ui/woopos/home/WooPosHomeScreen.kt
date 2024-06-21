package com.woocommerce.android.ui.woopos.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.woocommerce.android.ui.woopos.common.composeui.WooPosPreview
import com.woocommerce.android.ui.woopos.home.cart.WooPosCartScreen
import com.woocommerce.android.ui.woopos.home.products.WooPosProductsScreen
import com.woocommerce.android.ui.woopos.home.totals.WooPosTotalsScreen
import com.woocommerce.android.ui.woopos.root.navigation.WooPosNavigationEvent

@Composable
fun WooPosHomeScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    onNavigationEvent: (WooPosNavigationEvent) -> Unit
) {
    val homeViewModel: WooPosHomeViewModel = hiltViewModel(viewModelStoreOwner)

    WooPosHomeScreen(
        homeViewModel.state.collectAsState().value,
        onNavigationEvent,
        homeViewModel::onUIEvent,
        viewModelStoreOwner
    )
    LaunchedEffect(homeViewModel.events) {
        homeViewModel.events.collect {
            when (it) {
                is WooPosHomeEvent.OrderSuccessfullyPaid ->
                    onNavigationEvent(WooPosNavigationEvent.NavigateToPaymentSuccess(it.orderId))
            }
        }
    }
}

@Composable
private fun WooPosHomeScreen(
    state: WooPosHomeState,
    onNavigationEvent: (WooPosNavigationEvent) -> Unit,
    onHomeUIEvent: (WooPosHomeUIEvent) -> Boolean,
    viewModelStoreOwner: ViewModelStoreOwner,
) {
    BackHandler {
        val result = onHomeUIEvent(WooPosHomeUIEvent.SystemBackClicked)
        if (!result) {
            onNavigationEvent(WooPosNavigationEvent.BackFromHomeClicked)
        }
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val cartWidth = (screenWidthDp / 3)
    val totalsProductsWidth = (screenWidthDp / 3 * 2)
    val halfScreenWidthPx = with(LocalDensity.current) { totalsProductsWidth.roundToPx() }

    val scrollState = rememberScrollState()

    LaunchedEffect(state) {
        val animationSpec = spring<Float>(
            dampingRatio = 0.8f,
            stiffness = 200f
        )
        when (state) {
            is WooPosHomeState.Cart -> {
                scrollState.animateScrollTo(
                    0,
                    animationSpec = animationSpec
                )
            }

            WooPosHomeState.Checkout -> scrollState.animateScrollTo(
                halfScreenWidthPx,
                animationSpec = animationSpec
            )
        }
    }

    when (state) {
        is WooPosHomeState.Cart -> WooPosHomeScreen(
            scrollState,
            totalsProductsWidth,
            cartWidth,
            viewModelStoreOwner
        )

        WooPosHomeState.Checkout -> WooPosHomeScreen(
            scrollState,
            totalsProductsWidth,
            cartWidth,
            viewModelStoreOwner
        )
    }
}

@Composable
private fun WooPosHomeScreen(
    scrollState: ScrollState,
    totalsProductsWidth: Dp,
    cartWidth: Dp,
    viewModelStoreOwner: ViewModelStoreOwner,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState, enabled = false)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.width(totalsProductsWidth)) {
            WooPosProductsScreen(viewModelStoreOwner)
        }
        Box(modifier = Modifier.width(cartWidth)) {
            WooPosCartScreen(viewModelStoreOwner)
        }
        Box(modifier = Modifier.width(totalsProductsWidth)) {
            WooPosTotalsScreen(viewModelStoreOwner)
        }
    }
}

@Composable
@WooPosPreview
fun WooPosHomeCartScreenPreview() {
    WooPosHomeScreen(
        state = WooPosHomeState.Cart,
        onHomeUIEvent = { true },
        onNavigationEvent = {},
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    )
}

@Composable
@WooPosPreview
fun WooPosHomeCartWithExitPOSConfirmationScreenPreview() {
    WooPosHomeScreen(
        state = WooPosHomeState.Cart,
        onHomeUIEvent = { true },
        onNavigationEvent = {},
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    )
}

@Composable
@WooPosPreview
fun WooPosHomeCheckoutScreenPreview() {
    WooPosHomeScreen(
        state = WooPosHomeState.Checkout,
        onHomeUIEvent = { true },
        onNavigationEvent = {},
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    )
}
