package com.woocommerce.android.ui.woopos.common.composeui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.woocommerce.android.ui.woopos.common.composeui.WooPosPreview

@Composable
fun WooPosCircularLoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "RotationTransition"
    )
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
        ),
        label = "RotationAnimation"
    )

    Canvas(modifier = modifier) {
        val radius = size.width / 2

        drawCircle(
            color = Color(0xFFD1C4E9),
            radius = radius,
        )

        rotate(animatedRotation) {
            drawArc(
                color = Color(0xFF7E57C2),
                startAngle = 0f,
                sweepAngle = 110f,
                useCenter = true,
                style = Fill,
            )
        }

        drawCircle(
            color = Color.White,
            radius = radius * 0.4f,
        )
    }
}

@Composable
@WooPosPreview
fun PreviewCircularLoadingIndicatorBig() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        WooPosCircularLoadingIndicator(
            modifier = Modifier.size(156.dp)
        )
    }
}

@Composable
@WooPosPreview
fun PreviewCircularLoadingIndicatorSmall() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        WooPosCircularLoadingIndicator(
            modifier = Modifier.size(64.dp)
        )
    }
}
