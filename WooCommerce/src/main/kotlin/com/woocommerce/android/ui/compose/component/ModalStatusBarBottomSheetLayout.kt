package com.woocommerce.android.ui.compose.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat

/*
 * This is a custom implementation of the ModalBottomSheetLayout that fixes the scrim color of the status bar
 * and the show animation.
 *
 * Source: https://stackoverflow.com/a/76998328
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalStatusBarBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState =
        rememberModalBottomSheetState(Hidden),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    content: @Composable () -> Unit
): Unit = ModalBottomSheetLayout(
    sheetContent = {
        Box(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            sheetContent.invoke(this@ModalBottomSheetLayout)
        }
    },
    modifier = modifier,
    sheetState = sheetState,
    sheetShape = sheetShape,
    sheetElevation = sheetElevation,
    sheetBackgroundColor = sheetBackgroundColor,
    sheetContentColor = sheetContentColor,
    scrimColor = scrimColor
) {
    fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("Permissions should be called in the context of an Activity")
    }

    val context = LocalContext.current
    var statusBarColor by remember { mutableStateOf(Color.Transparent) }
    val backgroundColor = remember {
        val typedValue = TypedValue()
        if (context.findActivity().theme
                .resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        ) {
            Color(typedValue.data)
        } else {
            sheetBackgroundColor
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(statusBarColor)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            content()
        }
    }

    DisposableEffect(Unit) {
        val window = context.findActivity().window
        val originalStatusBarColor = window.statusBarColor
        statusBarColor = Color(originalStatusBarColor)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        onDispose {
            window.statusBarColor = originalStatusBarColor
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }
}
