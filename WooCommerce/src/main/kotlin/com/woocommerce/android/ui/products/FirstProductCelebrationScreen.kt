package com.woocommerce.android.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.component.WCColoredButton
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground

@Composable
fun FirstProductCelebrationScreen(viewModel: FirstProductCelebrationViewModel) {
    FirstProductCelebrationScreen(
        onShareClick = viewModel::onShareButtonClicked
    )
}

@Composable
fun FirstProductCelebrationScreen(
    onShareClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.major_100))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(top = dimensionResource(id = R.dimen.major_350))
                .verticalScroll(rememberScrollState())
        ) {
            WCColoredButton(onClick = onShareClick, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.share))
            }
        }
    }
}

@Composable
@Preview
private fun FirstProductCelebrationScreenPreview() {
    WooThemeWithBackground {
        FirstProductCelebrationScreen()
    }
}
