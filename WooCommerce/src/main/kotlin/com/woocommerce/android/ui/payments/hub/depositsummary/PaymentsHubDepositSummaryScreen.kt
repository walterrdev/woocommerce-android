@file:OptIn(ExperimentalFoundationApi::class)

package com.woocommerce.android.ui.payments.hub.depositsummary

import android.content.res.Configuration
import android.icu.text.MessageFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground
import com.woocommerce.android.util.StringUtils
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PaymentsHubDepositSummaryView(
    viewModel: PaymentsHubDepositSummaryViewModel = viewModel()
) {
    viewModel.viewState.observeAsState().let {
        WooThemeWithBackground {
            when (val value = it.value) {
                is PaymentsHubDepositSummaryState.Success -> PaymentsHubDepositSummaryView(value.overview)
                null,
                PaymentsHubDepositSummaryState.Loading,
                is PaymentsHubDepositSummaryState.Error -> {
                    // show nothing
                }
            }
        }
    }
}

@Composable
fun PaymentsHubDepositSummaryView(
    overview: PaymentsHubDepositSummaryState.Overview,
    isPreview: Boolean = LocalInspectionMode.current,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val pageCount = overview.infoPerCurrency.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.color_surface))
    ) {
        val pagerState = rememberPagerState()
        val currencies = overview.infoPerCurrency.keys.toList()
        val selectedCurrencyInfo = overview.infoPerCurrency[currencies[pagerState.currentPage]] ?: return@Column

        AnimatedVisibility(
            visible = (isExpanded || isPreview) && pageCount > 1,
            modifier = Modifier.fillMaxWidth(),
        ) {
            CurrenciesTabs(
                currencies = currencies.map { it.uppercase() }.toList(),
                pagerState = pagerState
            )
        }

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.color_surface))
            ) {
                FundsOverview(selectedCurrencyInfo, isExpanded) { isExpanded = !isExpanded }

                AnimatedVisibility(
                    visible = isExpanded || isPreview,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DepositsInfo(selectedCurrencyInfo)
                }
            }
        }
    }
}

@Composable
private fun FundsOverview(
    currencyInfo: PaymentsHubDepositSummaryState.Info,
    isExpanded: Boolean,
    onExpandCollapseClick: () -> Unit,
) {
    val chevronRotation by animateFloatAsState(
        if (isExpanded) 180f else 0f, label = "chevronRotation"
    )
    val topRowIS = remember { MutableInteractionSource() }
    val topRowCoroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = topRowIS,
                indication = null
            ) {
                val press = PressInteraction.Press(Offset.Zero)
                topRowCoroutineScope.launch {
                    topRowIS.emit(press)
                    topRowIS.emit(PressInteraction.Release(press))
                }
                onExpandCollapseClick()
            }
            .padding(
                start = dimensionResource(id = R.dimen.major_100),
                end = dimensionResource(id = R.dimen.major_100),
                top = dimensionResource(id = R.dimen.major_150),
                bottom = dimensionResource(id = R.dimen.major_100)
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.body2,
                text = stringResource(id = R.string.card_reader_hub_deposit_summary_available_funds),
                color = colorResource(id = R.color.color_on_surface)
            )
            Text(
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                text = currencyInfo.availableFunds,
                color = colorResource(id = R.color.color_on_surface)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.body2,
                text = stringResource(id = R.string.card_reader_hub_deposit_summary_pending_funds),
                color = colorResource(id = R.color.color_on_surface)
            )
            Text(
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                text = currencyInfo.pendingFunds,
                color = colorResource(id = R.color.color_on_surface)
            )
            Text(
                style = MaterialTheme.typography.caption,
                text = StringUtils.getQuantityString(
                    context = LocalContext.current,
                    quantity = currencyInfo.pendingBalanceDepositsCount,
                    default = R.string.card_reader_hub_deposit_summary_pending_deposits_plural,
                    one = R.string.card_reader_hub_deposit_summary_pending_deposits_one,
                ),
                color = colorResource(id = R.color.color_surface_variant)
            )
        }

        Column(
            modifier = Modifier.weight(.3f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = { onExpandCollapseClick() },
                interactionSource = topRowIS,
            ) {
                Icon(
                    modifier = Modifier.rotate(chevronRotation),
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription =
                    stringResource(R.string.card_reader_hub_deposit_summary_collapse_expand_content_description),
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
    }
    val dividerPaddingAnimation by animateDpAsState(
        targetValue = if (isExpanded) {
            dimensionResource(id = R.dimen.major_100)
        } else {
            dimensionResource(id = R.dimen.minor_00)
        },
        label = "dividerPaddingAnimation"
    )

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dividerPaddingAnimation)
    )
}

@Composable
private fun DepositsInfo(
    currencyInfo: PaymentsHubDepositSummaryState.Info,
) {
    Column {
        Column(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.major_100),
                    end = dimensionResource(id = R.dimen.major_100),
                    top = 10.dp,
                    bottom = dimensionResource(id = R.dimen.major_150)
                )
        ) {
            currencyInfo.fundsAvailableInDays?.let { fundsAvailableInDays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_calendar_gray_16), contentDescription = null)
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.minor_100)))
                    Text(
                        style = MaterialTheme.typography.caption,
                        text = StringUtils.getQuantityString(
                            context = LocalContext.current,
                            quantity = fundsAvailableInDays,
                            default = R.string.card_reader_hub_deposit_summary_funds_available_after_plural,
                            one = R.string.card_reader_hub_deposit_summary_funds_available_after_one,
                        ),
                        color = colorResource(id = R.color.color_surface_variant),
                    )
                }
            }

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.major_150)))

            Text(
                style = MaterialTheme.typography.body2,
                text = stringResource(id = R.string.card_reader_hub_deposit_funds_deposits_title).uppercase(),
                color = colorResource(id = R.color.color_surface_variant),
            )

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.minor_100)))

            currencyInfo.nextDeposit?.let {
                Deposit(
                    depositType = R.string.card_reader_hub_deposit_summary_next,
                    deposit = it,
                    textColor = R.color.color_on_surface
                )
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.major_100)))
            }

            currencyInfo.lastDeposit?.let {
                Deposit(
                    depositType = R.string.card_reader_hub_deposit_summary_last,
                    deposit = it,
                    textColor = R.color.color_surface_variant
                )
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.major_100)))
            }

            Divider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.minor_100)))

            currencyInfo.fundsDepositInterval?.let { interval ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_acropolis_gray_15), contentDescription = null)
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.minor_100)))
                    Text(
                        style = MaterialTheme.typography.caption,
                        text = interval.buildText(),
                        color = colorResource(id = R.color.color_surface_variant),
                    )
                }
            }

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.major_100)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(
                        id = R.drawable.ic_info_outline_20dp
                    ),
                    contentDescription = null,
                    tint = colorResource(id = R.color.color_primary)
                )
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.minor_100)))
                Text(
                    style = MaterialTheme.typography.caption,
                    text = stringResource(id = R.string.card_reader_hub_deposit_summary_learn_more),
                    color = colorResource(id = R.color.color_primary),
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Deposit(
    depositType: Int,
    deposit: PaymentsHubDepositSummaryState.Deposit,
    textColor: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.weight(.5f),
            style = MaterialTheme.typography.body1,
            text = stringResource(id = depositType),
            color = colorResource(id = textColor),
        )

        Text(
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.body1,
            text = deposit.date,
            color = colorResource(id = textColor),
        )

        Box(modifier = Modifier.weight(1f)) {
            when (deposit.status) {
                PaymentsHubDepositSummaryState.Deposit.Status.ESTIMATED ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_estimated,
                        backgroundColor = R.color.woo_gray_40,
                        textColor = R.color.woo_gray_80
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.PENDING ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_pending,
                        backgroundColor = R.color.woo_gray_40,
                        textColor = R.color.woo_gray_80
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.IN_TRANSIT ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_in_transit,
                        backgroundColor = R.color.woo_gray_80,
                        textColor = R.color.woo_gray_5
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.PAID ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_paid,
                        backgroundColor = R.color.woo_celadon_5,
                        textColor = R.color.woo_green_50
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.CANCELED ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_canceled,
                        backgroundColor = R.color.woo_gray_40,
                        textColor = R.color.woo_gray_80
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.FAILED ->
                    DepositStatus(
                        text = R.string.card_reader_hub_deposit_summary_status_failed,
                        backgroundColor = R.color.woo_gray_40,
                        textColor = R.color.woo_gray_80
                    )

                PaymentsHubDepositSummaryState.Deposit.Status.UNKNOWN -> DepositStatus(
                    text = R.string.card_reader_hub_deposit_summary_status_unknown,
                    backgroundColor = R.color.woo_gray_40,
                    textColor = R.color.woo_gray_80
                )
            }
        }

        Box(
            modifier = Modifier.weight(.8f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                style = MaterialTheme.typography.body1,
                text = deposit.amount,
                color = colorResource(id = textColor),
                fontWeight = FontWeight(600),
            )
        }
    }
}

@Composable
private fun CurrenciesTabs(
    currencies: List<String>,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = colorResource(id = R.color.color_surface),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .padding(horizontal = 16.dp),
                height = 4.dp,
                color = colorResource(id = R.color.color_primary)
            )
        }
    ) {
        currencies.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    val isSelected = pagerState.currentPage == index
                    Text(
                        style = MaterialTheme.typography.body1,
                        text = title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            colorResource(id = R.color.color_primary)
                        } else {
                            colorResource(id = R.color.color_on_surface_disabled)
                        }
                    )
                },
            )
        }
    }
}

@Composable
private fun DepositStatus(
    text: Int,
    backgroundColor: Int,
    textColor: Int
) {
    Box(
        modifier = Modifier
            .background(
                color = colorResource(backgroundColor),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.minor_100),
                vertical = dimensionResource(id = R.dimen.minor_50)
            )
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.caption,
            color = colorResource(id = textColor),
        )
    }
}

@Composable
private fun PaymentsHubDepositSummaryState.Info.Interval.buildText() =
    when (this) {
        PaymentsHubDepositSummaryState.Info.Interval.Daily -> stringResource(
            id = R.string.card_reader_hub_deposit_summary_available_deposit_time_daily
        )

        is PaymentsHubDepositSummaryState.Info.Interval.Weekly -> {
            val dayOfWeek = DayOfWeek.valueOf(weekDay.uppercase(Locale.getDefault()))
            stringResource(
                id = R.string.card_reader_hub_deposit_summary_available_deposit_time_weekly,
                dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            )
        }

        is PaymentsHubDepositSummaryState.Info.Interval.Monthly -> {
            val formatter = MessageFormat("{0,ordinal}", Locale.getDefault())
            stringResource(
                id = R.string.card_reader_hub_deposit_summary_available_deposit_time_monthly,
                formatter.format(arrayOf(day))
            )
        }
    }

@Preview(name = "Light mode")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Ru locale", locale = "ru_RU")
@Composable
fun PaymentsHubDepositSummaryViewPreview() {
    WooThemeWithBackground {
        PaymentsHubDepositSummaryView(
            PaymentsHubDepositSummaryState.Overview(
                defaultCurrency = "USD",
                infoPerCurrency = mapOf(
                    "USD" to PaymentsHubDepositSummaryState.Info(
                        availableFunds = "100$",
                        pendingFunds = "200$",
                        pendingBalanceDepositsCount = 1,
                        fundsAvailableInDays = 5,
                        fundsDepositInterval = PaymentsHubDepositSummaryState.Info.Interval.Monthly(20),
                        nextDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.ESTIMATED,
                            date = "13 Oct 2023"
                        ),
                        lastDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.FAILED,
                            date = "13 Oct 2023"
                        )
                    ),
                    "EUR" to PaymentsHubDepositSummaryState.Info(
                        availableFunds = "100$",
                        pendingFunds = "200$",
                        pendingBalanceDepositsCount = 1,
                        fundsAvailableInDays = 2,
                        fundsDepositInterval = PaymentsHubDepositSummaryState.Info.Interval.Weekly("Friday"),
                        nextDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.ESTIMATED,
                            date = "13 Oct 2023"
                        ),
                        lastDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.PAID,
                            date = "13 Oct 2023"
                        )
                    ),
                    "RUB" to PaymentsHubDepositSummaryState.Info(
                        availableFunds = "100$",
                        pendingFunds = "200$",
                        pendingBalanceDepositsCount = 1,
                        fundsAvailableInDays = 4,
                        fundsDepositInterval = PaymentsHubDepositSummaryState.Info.Interval.Monthly(3),
                        nextDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.ESTIMATED,
                            date = "13 Oct 2023"
                        ),
                        lastDeposit = PaymentsHubDepositSummaryState.Deposit(
                            amount = "100$",
                            status = PaymentsHubDepositSummaryState.Deposit.Status.PAID,
                            date = "13 Oct 2023"
                        )
                    )
                )
            )
        )
    }
}
