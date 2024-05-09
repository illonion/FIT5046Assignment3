package com.example.todolist.Analytics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

// Composable function for drawing a pie chart
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    input: List<PieChartInput>,
    centerText: String = "Your Progress",
    centerTextSize: Float = 60f,
    centerLabelColor: Color,
    centerTransparentColor: Color
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2f

        var currentAngle = -90f
        val totalValue = input.sumOf { it.value }

        // Draw a pie chart with segments proportional to the values
        input.forEach { data ->
            val sliceAngle = (data.value.toFloat() / totalValue.toFloat()) * 360f
            drawArc(
                color = data.color,
                startAngle = currentAngle,
                sweepAngle = sliceAngle,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )
            currentAngle += sliceAngle
        }

        // Draw center circle with a label
        drawCircle(
            color = centerLabelColor,
            radius = radius * 0.6f,
            center = Offset(centerX, centerY)
        )

        // Define inner circle properties
        val innerRadius = radius * 0.4f
        val transparentWidth = radius * 0.1f

        // Draw inner circle with shadow effect
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                centerX,
                centerY,
                innerRadius,
                Paint().apply {
                    color = Color.White.copy(alpha = 0.6f).toArgb() // Set color with transparency
                    setShadowLayer(10f, 0f, 0f, Color.Gray.toArgb()) // Apply shadow layer effect
                }
            )
        }

        // Draw transparent overlay circle
        drawCircle(
            color = centerTransparentColor,
            radius = innerRadius + transparentWidth / 2f, // Calculate radius for overlay circle
            center = Offset(centerX, centerY)
        )

        // Draw bold text at the center
        val textPaint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
            color = Color.Black.toArgb()
            textSize = centerTextSize
            isFakeBoldText = true // Make the text bold
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffsetY = centerY + (textHeight / 2)

        drawContext.canvas.nativeCanvas.drawText(
            centerText,
            centerX,
            textOffsetY,
            textPaint
        )
    }
}

// Data class representing a slice in the pie chart
data class PieChartInput(
    val color: Color,
    val value: Double,
    val description: String
)