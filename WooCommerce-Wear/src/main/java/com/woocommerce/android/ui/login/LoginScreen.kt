package com.woocommerce.android.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.woocommerce.android.R
import com.woocommerce.android.compose.component.LoadingScreen
import com.woocommerce.android.compose.theme.WooTheme

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val viewState by viewModel.viewState.observeAsState()
    LoginScreen(
        isLoading = viewState?.isLoading ?: false,
        onTryAgainClicked = viewModel::reloadData
    )
}

@Composable
fun LoginScreen(
    isLoading: Boolean,
    onTryAgainClicked: () -> Unit
) {
    WooTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                TimeText()
                LoadingScreen()
            } else {
                LoginInstructionsScreen(onTryAgainClicked)
            }
        }
    }
}

@Composable
private fun LoginInstructionsScreen(
    onTryAgainClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(
                vertical = dimensionResource(id = R.dimen.activity_vertical_margin),
                horizontal = dimensionResource(id = R.dimen.activity_horizontal_margin)
            )
    ){
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.login_screen_error_caption),
                textAlign = TextAlign.Center,
                modifier = modifier.weight(2f)
            )
            Icon(painter = painterResource(
                id = R.drawable.ic_lightning),
                contentDescription = null,
                tint = Color.Yellow,
            )
            Button(
                onClick = onTryAgainClicked,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.DarkGray
                ),
                modifier = modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.login_screen_action_button))
            }
        }
    }
}

@Preview(name = "Error Round", device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(name = "Error Square", device = WearDevices.SQUARE, showSystemUi = true)
@Composable
fun PreviewError() {
    LoginScreen(
        isLoading = false,
        onTryAgainClicked = {}
    )
}

@Preview(name = "Loading Round", device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(name = "Loading Square", device = WearDevices.SQUARE, showSystemUi = true)
@Composable
fun PreviewLoading() {
    LoginScreen(
        isLoading = true,
        onTryAgainClicked = {}
    )
}
