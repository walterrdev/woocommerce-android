package com.woocommerce.android.ui.woopos.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.woocommerce.android.ui.woopos.common.composeui.WooPosPreview
import com.woocommerce.android.ui.woopos.common.composeui.WooPosTheme
import com.woocommerce.android.ui.woopos.root.navigation.WooPosRootHost

@Composable
fun WooPosRootScreen() {
    val viewModel: WooPosRootViewModel = hiltViewModel()
    WooPosRootScreen(viewModel.bottomToolbarState.collectAsState(), viewModel::onUiEvent)
}

@Composable
private fun WooPosRootScreen(state: State<BottomToolbarState>, onUIEvent: (WooPosRootUIEvent) -> Unit) {
    WooPosTheme {
        Column(modifier = Modifier.background(MaterialTheme.colors.background)) {
            WooPosRootHost(modifier = Modifier.weight(1f))
            WooPosBottomToolbar(state.value, onUIEvent)
        }
    }
}

@WooPosPreview
@Composable
fun PreviewWooPosRootScreen() {
    val state = mutableStateOf(BottomToolbarState(BottomToolbarState.CardReaderStatus("Connected")))
    WooPosTheme {
        WooPosRootScreen(state) {}
    }
}
