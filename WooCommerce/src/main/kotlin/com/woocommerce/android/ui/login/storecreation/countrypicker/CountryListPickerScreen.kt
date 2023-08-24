package com.woocommerce.android.ui.login.storecreation.countrypicker

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.component.Toolbar
import com.woocommerce.android.ui.compose.component.WCColoredButton
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground

@Composable
fun CountryListPickerScreen(viewModel: CountryListPickerViewModel) {
    viewModel.countryListPickerState.observeAsState().value?.let { viewState ->
        Scaffold(topBar = {
            Toolbar(
                title = { Text(stringResource(id = R.string.store_creation_country_list_picker_toolbar_title)) },
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigationButtonClick = viewModel::onArrowBackPressed,
            )
        }) { padding ->
            CountryListPickerForm(
                countries = viewState.countries,
                onCountrySelected = viewModel::onCountrySelected,
                onContinueClicked = viewModel::onContinueClicked,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(padding)
            )
        }
    }
}

@Composable
fun CountryListPickerForm(
    countries: List<StoreCreationCountry>,
    onCountrySelected: (StoreCreationCountry) -> Unit,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            CountryListPickerHeader(countries.first { it.isSelected })
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(id = R.dimen.major_100))
        ) {
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                item {
                    CountryListPickerHeader(countries.first { it.isSelected })
                }
            }

            itemsIndexed(countries) { _, country ->
                CountryItem(
                    country = country,
                    onCountrySelected = onCountrySelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.major_100))
                )
            }
        }

        Divider(
            color = colorResource(id = R.color.divider_color),
            thickness = dimensionResource(id = R.dimen.minor_10)
        )
        WCColoredButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.major_100)),
            onClick = onContinueClicked,
        ) {
            Text(text = stringResource(id = R.string.continue_button))
        }
    }
}

@Composable
private fun CountryListPickerHeader(selectedCountry: StoreCreationCountry) {
    Column(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.major_100))
    ) {
        Text(
            text = stringResource(id = R.string.store_creation_country_picker_current_location),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.minor_100))
        )

        CurrentCountryItem(
            country = selectedCountry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.major_200))
        )

        Text(
            text = stringResource(id = R.string.store_creation_country_picker_countries_header),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.minor_100))
        )
    }
}

@Composable
private fun CountryItem(
    country: StoreCreationCountry,
    onCountrySelected: (StoreCreationCountry) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = dimensionResource(id = if (country.isSelected) R.dimen.minor_25 else R.dimen.minor_10),
                color = colorResource(
                    if (country.isSelected) R.color.color_primary else R.color.divider_color
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.minor_100))
            )
            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.minor_100)))
            .background(
                color = colorResource(
                    id = if (country.isSelected)
                        if (isSystemInDarkTheme()) R.color.color_surface else R.color.woo_purple_10
                    else R.color.color_surface
                )
            )
            .clickable { onCountrySelected(country) }
    ) {
        Row(
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.major_100),
                top = dimensionResource(id = R.dimen.major_75),
                bottom = dimensionResource(id = R.dimen.major_75),
                end = dimensionResource(id = R.dimen.major_100),
            )
        ) {
            Text(
                text = country.emojiFlag,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.major_100))
            )
            Text(
                text = country.name,
                color = colorResource(
                    id = if (isSystemInDarkTheme() && country.isSelected) R.color.color_primary
                    else R.color.color_on_surface
                )
            )
        }
    }
}

@Composable
private fun CurrentCountryItem(
    country: StoreCreationCountry,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = dimensionResource(id = if (country.isSelected) R.dimen.minor_25 else R.dimen.minor_10),
                color = colorResource(
                    if (country.isSelected) R.color.color_primary else R.color.divider_color
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.minor_100))
            )
            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.minor_100)))
            .background(
                color = colorResource(
                    id = if (country.isSelected)
                        if (isSystemInDarkTheme()) R.color.color_surface else R.color.woo_purple_10
                    else R.color.color_surface
                )
            )
    ) {
        Row(
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.major_100),
                top = dimensionResource(id = R.dimen.major_75),
                bottom = dimensionResource(id = R.dimen.major_75),
                end = dimensionResource(id = R.dimen.major_100),
            )
        ) {
            Text(
                text = country.emojiFlag,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.major_100))
            )
            Text(
                text = country.name,
                color = colorResource(
                    id = if (isSystemInDarkTheme() && country.isSelected) R.color.color_primary
                    else R.color.color_on_surface
                )
            )
        }
    }
}

@ExperimentalFoundationApi
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "small screen", device = Devices.PIXEL)
@Preview(name = "mid screen", device = Devices.PIXEL_4)
@Preview(name = "large screen", device = Devices.NEXUS_10)
@Composable
fun CountryListPickerPreview() {
    WooThemeWithBackground {
        CountryListPickerForm(
            countries = listOf(
                StoreCreationCountry(
                    name = "Canada",
                    code = "CA",
                    emojiFlag = "\uD83C\uDDE8\uD83C\uDDE6",
                    isSelected = false
                ),
                StoreCreationCountry(
                    name = "Spain",
                    code = "ES",
                    emojiFlag = "\uD83C\uDDEA\uD83C\uDDF8",
                    isSelected = true
                ),
                StoreCreationCountry(
                    name = "United States",
                    code = "US",
                    emojiFlag = "\uD83C\uDDFA\uD83C\uDDF8",
                    isSelected = false
                ),
                StoreCreationCountry(
                    name = "Italy",
                    code = "IT",
                    emojiFlag = "\uD83C\uDDEE\uD83C\uDDF9",
                    isSelected = false
                )
            ),
            onCountrySelected = {},
            onContinueClicked = {},
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
        )
    }
}