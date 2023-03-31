package com.woocommerce.android.ui.payments.banner

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.woocommerce.android.R
import com.woocommerce.android.model.UiString
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground
import com.woocommerce.android.util.UiHelpers

@Composable
fun Banner(bannerState: BannerState) {
    if (bannerState is BannerState.DisplayBannerState) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.major_100),
                        top = dimensionResource(id = R.dimen.minor_100)
                    ),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = bannerState.onDismissClicked
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(
                            id = R.string.card_reader_upsell_card_reader_banner_dismiss
                        ),
                        tint = colorResource(id = R.color.color_on_surface)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.major_100),
                        top = dimensionResource(id = R.dimen.minor_100)
                    ),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SecondaryIcon(bannerState)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = UiHelpers.getTextOfUiString(LocalContext.current, bannerState.title),
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            bottom = dimensionResource(id = R.dimen.minor_100)
                        )
                    )
                    Text(
                        text = UiHelpers.getTextOfUiString(LocalContext.current, bannerState.description),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(
                            bottom = dimensionResource(id = R.dimen.minor_100)
                        )
                    )
                    TextButton(
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.minor_100),
                                bottom = dimensionResource(id = R.dimen.minor_100),
                            ),
                        contentPadding = PaddingValues(start = dimensionResource(id = R.dimen.minor_00)),
                        onClick = bannerState.onPrimaryActionClicked
                    ) {
                        Text(
                            text = UiHelpers.getTextOfUiString(LocalContext.current, bannerState.primaryActionLabel),
                            color = colorResource(id = R.color.color_secondary),
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Column {
                    PrimaryIcon(bannerState)
                }
            }
        }
    }
}

@Composable
private fun PrimaryIcon(bannerState: BannerState.DisplayBannerState) {
    when (val icon = bannerState.primaryIcon) {
        is BannerState.LocalOrRemoteIcon.Local -> {
            Image(
                painter = painterResource(id = icon.drawableId),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier.width(154.dp)
            )
        }
        is BannerState.LocalOrRemoteIcon.Remote -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(icon.url)
                    .build(),
                contentDescription = null,
                modifier = Modifier.width(154.dp)
            )
        }
    }
}

@Composable
private fun SecondaryIcon(bannerState: BannerState.DisplayBannerState) {
    when (val icon = bannerState.secondaryIcon) {
        is BannerState.LabelOrRemoteIcon.Label -> {
            val bcgColor = colorResource(id = R.color.woo_purple_10)
            Text(
                text = UiHelpers.getTextOfUiString(LocalContext.current, icon.label),
                color = colorResource(id = R.color.woo_purple_60),
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = bcgColor,
                            cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        is BannerState.LabelOrRemoteIcon.Remote -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(icon.url)
                    .build(),
                contentDescription = null,
                modifier = Modifier.height(26.dp)
            )
        }
    }
}

@Preview(name = "Light mode")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PaymentScreenBannerPreview() {
    WooThemeWithBackground {
        Banner(
            BannerState.DisplayBannerState(
                onPrimaryActionClicked = {},
                onDismissClicked = {},
                title = UiString.UiStringRes(R.string.card_reader_upsell_card_reader_banner_title),
                description = UiString.UiStringRes(R.string.card_reader_upsell_card_reader_banner_description),
                primaryActionLabel = UiString.UiStringRes(R.string.card_reader_upsell_card_reader_banner_cta),
                primaryIcon = BannerState.LocalOrRemoteIcon.Local(
                    R.drawable.ic_banner_upsell_card_reader_illustration
                ),
                secondaryIcon = BannerState.LabelOrRemoteIcon.Label(
                    UiString.UiStringRes(
                        R.string.card_reader_upsell_card_reader_banner_new
                    )
                ),
            )
        )
    }
}
