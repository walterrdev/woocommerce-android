package com.woocommerce.android.ui.moremenu

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.woocommerce.android.R
import com.woocommerce.android.ui.compose.animations.SkeletonView
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground
import com.woocommerce.android.ui.moremenu.MoreMenuViewModel.MoreMenuViewState

@Composable
fun MoreMenuScreen(viewModel: MoreMenuViewModel) {
    viewModel.moreMenuViewState.observeAsState().value?.let { moreMenuState ->
        MoreMenuScreen(
            moreMenuState,
            viewModel::onSwitchStoreClick
        )
    }
}

@Composable
fun MoreMenuScreen(
    state: MoreMenuViewState,
    onSwitchStore: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.major_100)))

        MoreMenuHeader(onSwitchStore, state)

        Spacer(modifier = Modifier.height(8.dp))

        state.menuSections.forEach { section -> MoreMenuSection(section) }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.major_100)))
    }
}

@Composable
private fun MoreMenuHeader(
    onSwitchStore: () -> Unit,
    state: MoreMenuViewState
) {
    HeaderButton(
        onClick = onSwitchStore,
        enabled = state.isStoreSwitcherEnabled
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderContent(
                userAvatarUrl = state.userAvatarUrl,
                siteName = state.siteName,
                planName = state.sitePlan,
                siteUrl = state.siteUrl,
                modifier = Modifier.weight(1f, false)
            )

            if (state.isStoreSwitcherEnabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                    contentDescription = null,
                    tint = colorResource(id = R.color.color_on_surface),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(dimensionResource(id = R.dimen.major_150))
                )
            }
        }
    }
}

/**
 * Since the header is also a button that opens the store switch, it can be disabled
 * through the [MoreMenuViewState.isStoreSwitcherEnabled] flag.
 *
 * But since it's also a header with store information,
 * we want to just hide the drop down arrow to reflect the disabled appearance.
 *
 * This custom color scheme ensures that.
 */
@Composable
private fun HeaderButton(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    val headerBackgroundColors = colorResource(
        id = R.color.more_menu_button_background
    ).let { backgroundColor ->
        ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = backgroundColor,
            disabledContentColor = contentColorFor(backgroundColor)
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.major_75)),
        colors = headerBackgroundColors,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.major_75)),
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.major_100)),
        content = content
    )
}

@Composable
private fun HeaderContent(
    modifier: Modifier,
    userAvatarUrl: String,
    siteName: String,
    planName: String,
    siteUrl: String
) {
    Row(modifier) {
        HeaderAvatar(
            modifier = Modifier.align(Alignment.CenterVertically),
            avatarUrl = userAvatarUrl
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.major_100)))
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.minor_50))
            ) {
                Text(
                    text = siteName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, false)
                )
                if (planName.isNotEmpty()) {
                    StorePlanBadge(planName)
                }
            }
            Text(
                text = siteUrl,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.minor_50))
            )
        }
    }
}

@Composable
private fun StorePlanBadge(planName: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(colorResource(id = R.color.free_trial_component_background))
    ) {
        Text(
            text = planName.uppercase(),
            color = colorResource(id = R.color.free_trial_component_text),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                vertical = dimensionResource(id = R.dimen.minor_25),
                horizontal = dimensionResource(id = R.dimen.minor_100)
            )
        )
    }
}

@Composable
private fun HeaderAvatar(
    modifier: Modifier,
    avatarUrl: String
) {
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    if (avatarUrl.isNotEmpty()) {
        Glide.with(LocalContext.current)
            .asBitmap()
            .load(avatarUrl)
            .into(
                object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        bitmapState.value = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Nothing to do here.
                    }
                }
            )
    }

    val circledModifier = modifier
        .size(dimensionResource(id = R.dimen.major_250))
        .clip(CircleShape)
        .background(color = colorResource(id = R.color.more_menu_button_icon_background))

    bitmapState.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = R.string.more_menu_avatar),
            modifier = circledModifier
        )
    } ?: Image(
        painter = painterResource(id = R.drawable.img_gravatar_placeholder),
        contentDescription = stringResource(id = R.string.more_menu_avatar),
        modifier = circledModifier
    )
}

@Composable
private fun MoreMenuSection(section: MoreMenuItemSection) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        section.title?.let { title ->
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.subtitle1,
                color = colorResource(id = R.color.color_surface_variant),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Column {
            section.items.forEach { item ->
                when (item) {
                    is MoreMenuItem.Button -> MoreMenuButton(item)
                    is MoreMenuItem.Loading -> MoreMenuLoading()
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MoreMenuLoading() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.more_menu_button_background),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.major_75)),
            )
            .padding(all = 12.dp)
    ) {
        SkeletonView(
            modifier = Modifier
                .clip(CircleShape),
            height = 40.dp,
            width = 40.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            SkeletonView(
                modifier = Modifier
                    .height(16.dp)
                    .width(120.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonView(
                modifier = Modifier
                    .height(14.dp)
                    .width(200.dp)
            )
        }
    }
}

@Composable
private fun MoreMenuButton(button: MoreMenuItem.Button) {
    Button(
        onClick = button.onClick,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.major_75)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.more_menu_button_background),
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.major_75)),
    ) {
        Box(Modifier.fillMaxSize()) {
            MoreMenuBadge(badgeState = button.badgeState)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.major_250))
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.more_menu_button_icon_background))
                ) {
                    Image(
                        painter = painterResource(id = button.icon),
                        contentDescription = stringResource(id = button.title),
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.major_125))
                            .align(Alignment.Center)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.major_100))
                ) {
                    Text(
                        text = stringResource(id = button.title),
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = stringResource(id = button.description),
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_surface_variant),
                    )
                }
            }

            button.extraIcon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = colorResource(id = R.color.color_icon),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MoreMenuBadge(badgeState: BadgeState?) {
    if (badgeState != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            @Suppress("RememberReturnType")
            val visible = remember {
                MutableTransitionState(badgeState.animateAppearance.not()).apply { targetState = true }
            }
            AnimatedVisibility(
                visibleState = visible,
                enter = createBadgeEnterAnimation()
            ) {
                val backgroundColor = colorResource(id = badgeState.backgroundColor)
                Text(
                    text = badgeState.textState.text,
                    fontSize = dimensionResource(id = badgeState.textState.fontSize).value.sp,
                    color = colorResource(id = badgeState.textColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(dimensionResource(id = badgeState.badgeSize))
                        .drawBehind { drawCircle(color = backgroundColor) }
                        .wrapContentHeight()
                )
            }
        }
    }
}

private fun createBadgeEnterAnimation(): EnterTransition {
    val animationSpec = TweenSpec<Float>(durationMillis = 400, delay = 200)
    return scaleIn(animationSpec = animationSpec) + fadeIn(animationSpec = animationSpec)
}

@ExperimentalFoundationApi
@Preview(name = "dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "small screen", device = Devices.PIXEL)
@Preview(name = "mid screen", device = Devices.PIXEL_4)
@Preview(name = "large screen", device = Devices.NEXUS_10)
@Composable
private fun MoreMenuPreview() {
    WooThemeWithBackground {
        val state = MoreMenuViewState(
            menuSections = listOf(
                MoreMenuItemSection(
                    title = null,
                    items = listOf(
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_woo_pos,
                            description = R.string.more_menu_button_woo_pos_description,
                            icon = R.drawable.ic_more_menu_pos,
                            extraIcon = R.drawable.ic_more_menu_pos_extra,
                        ),
                    ),
                ),
                MoreMenuItemSection(
                    title = R.string.more_menu_settings_section_title,
                    items = listOf(
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_settings,
                            description = R.string.more_menu_button_settings_description,
                            icon = R.drawable.ic_more_screen_settings,
                        ),
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_subscriptions,
                            description = R.string.more_menu_button_subscriptions_description,
                            icon = R.drawable.ic_more_menu_upgrades,
                        ),
                    ),
                ),

                MoreMenuItemSection(
                    title = R.string.more_menu_general_section_title,
                    items = listOf(
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_payments,
                            description = R.string.more_menu_button_payments_description,
                            icon = R.drawable.ic_more_menu_payments,
                            badgeState = BadgeState(
                                badgeSize = R.dimen.major_110,
                                backgroundColor = R.color.color_secondary,
                                textColor = R.color.color_on_primary,
                                textState = TextState("", R.dimen.text_minor_80),
                                animateAppearance = true
                            )
                        ),
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_wс_admin,
                            description = R.string.more_menu_button_wc_admin_description,
                            icon = R.drawable.ic_more_menu_wp_admin,
                            extraIcon = R.drawable.ic_external
                        ),
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_store,
                            description = R.string.more_menu_button_store_description,
                            icon = R.drawable.ic_more_menu_store
                        ),
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_reviews,
                            description = R.string.more_menu_button_reviews_description,
                            icon = R.drawable.ic_more_menu_reviews,
                            badgeState = BadgeState(
                                badgeSize = R.dimen.major_150,
                                backgroundColor = R.color.color_primary,
                                textColor = R.color.color_on_primary,
                                textState = TextState("3", R.dimen.text_minor_80),
                                animateAppearance = false
                            )
                        ),
                        MoreMenuItem.Button(
                            title = R.string.more_menu_button_coupons,
                            description = R.string.more_menu_button_coupons_description,
                            icon = R.drawable.ic_more_menu_coupons,
                        ),
                        MoreMenuItem.Loading(isVisible = true),
                    ),
                )
            ),
            siteName = "Example Site",
            siteUrl = "woocommerce.com",
            sitePlan = "free trial",
            userAvatarUrl = "", // To force displaying placeholder image
            isStoreSwitcherEnabled = true
        )
        MoreMenuScreen(state, {})
    }
}
