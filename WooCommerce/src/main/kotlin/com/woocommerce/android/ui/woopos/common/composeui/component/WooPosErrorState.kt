package com.woocommerce.android.ui.woopos.common.composeui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.woocommerce.android.R
import com.woocommerce.android.ui.woopos.common.composeui.WooPosPreview

@Composable
fun WooPosErrorState(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    message: String,
    reason: String,
    primaryButton: Button? = null,
    secondaryButton: Button? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colors.onSurface)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.body2
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = reason,
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        primaryButton?.let {
            WooPosButton(
                text = it.text,
                onClick = it.click,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        secondaryButton?.let {
            WooPosButton(
                text = it.text,
                onClick = it.click,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

data class Button(
    val text: String,
    val click: () -> Unit
)

@WooPosPreview
@Composable
fun WooPosErrorStatePreview() {
    WooPosErrorState(
        icon = Icons.Default.Error,
        message = stringResource(R.string.woopos_totals_main_error_label),
        reason = "Reason",
        primaryButton = Button(
            text = stringResource(R.string.retry),
            click = { /* Handle click */ }
        ),
        secondaryButton = Button(
            text = stringResource(R.string.cancel),
            click = { /* Handle click */ }
        )
    )
}