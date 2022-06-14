package com.woocommerce.android.ui.orders.creation.customerlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.animations.SkeletonView
import org.wordpress.android.fluxc.model.customer.WCCustomerModel

@Composable
fun CustomerListScreen(
    viewModel: CustomerListViewModel
) {
    val state = viewModel.customerList.observeAsState(emptyList())
    CustomerList(state, viewModel::onCustomerClick)
}

@Composable
private fun CustomerList(
    state: State<List<WCCustomerModel>>,
    onCustomerClick: ((WCCustomerModel) -> Unit?)? = null
) {
    val listState = rememberLazyListState()
    val customers = state.value
    LazyColumn(
        state = listState,
        modifier = Modifier.background(color = MaterialTheme.colors.surface)
    ) {
        itemsIndexed(
            items = customers,
            key = { _, customer -> customer.id }
        ) { _, customer ->
            CustomerListItem(customer, onCustomerClick)
            Divider(
                modifier = Modifier.offset(x = dimensionResource(id = R.dimen.major_100)),
                color = colorResource(id = R.color.divider_color),
                thickness = dimensionResource(id = R.dimen.minor_10)
            )
        }
        /*if (loadingState == LoadingState.Appending) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.minor_100))
                )
            }
        }*/
    }
}

@Composable
private fun CustomerListItem(
    customer: WCCustomerModel,
    onCustomerClick: ((WCCustomerModel) -> Unit?)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.minor_50)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                role = Role.Button,
                onClick = {
                    onCustomerClick?.let {
                        it(customer)
                    }
                }
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.major_100),
                vertical = dimensionResource(id = R.dimen.minor_100)
            ),
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(customer.avatarUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_photos_grey_c_24dp),
                error = painterResource(R.drawable.ic_photos_grey_c_24dp),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.major_300))
                    .clip(RoundedCornerShape(3.dp))
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.major_100))
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "${customer.firstName} ${customer.lastName}",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun EmptyCustomerList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.major_200)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.order_creation_customer_search_empty),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.major_150),
                end = dimensionResource(id = R.dimen.major_150)
            )
        )
        Spacer(Modifier.size(dimensionResource(id = R.dimen.major_325)))
        Image(
            painter = painterResource(id = R.drawable.img_empty_search),
            contentDescription = null,
        )
    }
}

@Composable
private fun CustomerListSkeleton() {
    val numberOfSkeletonRows = 10
    LazyColumn(Modifier.background(color = MaterialTheme.colors.surface)) {
        repeat(numberOfSkeletonRows) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.minor_50)),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.major_100),
                        vertical = dimensionResource(id = R.dimen.minor_100)
                    )
                ) {
                    SkeletonView(
                        dimensionResource(id = R.dimen.skeleton_text_medium_width),
                        dimensionResource(id = R.dimen.major_125)
                    )
                    SkeletonView(
                        dimensionResource(id = R.dimen.skeleton_text_large_width),
                        dimensionResource(id = R.dimen.major_100)
                    )
                    SkeletonView(
                        dimensionResource(id = R.dimen.skeleton_text_small_width),
                        dimensionResource(id = R.dimen.major_125)
                    )
                }
                Divider(
                    modifier = Modifier
                        .offset(x = dimensionResource(id = R.dimen.major_100)),
                    color = colorResource(id = R.color.divider_color),
                    thickness = dimensionResource(id = R.dimen.minor_10)
                )
            }
        }
    }
}

/*@Preview
@Composable
private fun CustomerListPreview() {
    val customers = listOf(
        WCCustomerModel(
            id = 1,
            firstName = "George",
            lastName = "Carlin"
        )
    )

    CustomerList(customers)
}*/

@Preview
@Composable
private fun EmptyCustomerListPreview() {
    EmptyCustomerList()
}

@Preview
@Composable
private fun CustomerListSkeletonPreview() {
    CustomerListSkeleton()
}
