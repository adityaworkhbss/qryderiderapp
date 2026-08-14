package com.qryde.qryderiderapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Stand-in for a real map SDK - no Maps integration exists yet, this just
 * evokes the route-on-a-map look from the mockups (a polyline with start/end
 * markers) so the booking flow's background isn't a blank box.
 */
@Composable
fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color(0xFFEFEDE4))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val points = listOf(
                Offset(w * 0.58f, h * 0.10f),
                Offset(w * 0.30f, h * 0.32f),
                Offset(w * 0.68f, h * 0.48f),
                Offset(w * 0.34f, h * 0.68f),
                Offset(w * 0.56f, h * 0.88f)
            )
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path,
                color = Color(0xFF2563EB),
                style = Stroke(width = 7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawCircle(color = Color.Black, radius = 8f, center = points.first())
            drawCircle(color = Color(0xFFE53935), radius = 8f, center = points.last())
        }
    }
}
