package com.woocommerce.android.ui.login.storecreation.countrypicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.woocommerce.android.R

@Composable
fun CountryPickerSelectorBottomSheet(viewModel: CountryListPickerViewModel) {
    viewModel.countryListPickerState.observeAsState().value?.let { viewState ->
        LazyColumn {
            itemsIndexed(viewState.countries) { _, country ->
                CountryItem(
                    country = country,
                    onCountrySelected = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.major_100))
                )
            }
        }
    }
}

@Composable
private fun CountryItem(
    country: CountryListPickerViewModel.StoreCreationCountry,
    onCountrySelected: (CountryListPickerViewModel.StoreCreationCountry) -> Unit,
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

@Preview
@Composable
fun CountryItemPreview() {
    CountryItem(
        country = CountryListPickerViewModel.StoreCreationCountry(
            name = "United States",
            code = "US",
            emojiFlag = "🇺🇸",
            isSelected = true
        ),
        onCountrySelected = { }
    )
}
