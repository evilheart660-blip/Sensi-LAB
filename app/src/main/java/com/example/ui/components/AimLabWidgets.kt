package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun AimPracticeArea(
    modifier: Modifier = Modifier,
    sensitivityMultiplier: Float = 1.0f
) {
    var hitCount by remember { mutableIntStateOf(0) }
    var missCount by remember { mutableIntStateOf(0) }
    var crosshairPos by remember { mutableStateOf(Offset(220f, 220f)) }
    var targetPos by remember { mutableStateOf(Offset(320f, 260f)) }
    val targetRadius = 26f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CyberDark)
            .pointerInput(sensitivityMultiplier) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val nextX = (crosshairPos.x + dragAmount.x * sensitivityMultiplier)
                        .coerceIn(30f, size.width.toFloat() - 30f)
                    val nextY = (crosshairPos.y + dragAmount.y * sensitivityMultiplier)
                        .coerceIn(30f, size.height.toFloat() - 30f)
                    crosshairPos = Offset(nextX, nextY)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val dist = (tapOffset - targetPos).getDistance()
                    if (dist <= targetRadius + 24f) {
                        hitCount++
                        val safeW = (size.width - 100f).coerceAtLeast(100f)
                        val safeH = (size.height - 100f).coerceAtLeast(100f)
                        targetPos = Offset(
                            Random.nextFloat() * safeW + 50f,
                            Random.nextFloat() * safeH + 50f
                        )
                    } else {
                        missCount++
                    }
                }
            }
            .testTag("aim_practice_area")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val gridStep = 40.dp.toPx()

            // Smooth clean background radar grid
            var curX = gridStep
            while (curX < canvasW) {
                drawLine(
                    color = CyberBorder.copy(alpha = 0.4f),
                    start = Offset(curX, 0f),
                    end = Offset(curX, canvasH),
                    strokeWidth = 1f
                )
                curX += gridStep
            }

            var curY = gridStep
            while (curY < canvasH) {
                drawLine(
                    color = CyberBorder.copy(alpha = 0.4f),
                    start = Offset(0f, curY),
                    end = Offset(canvasW, curY),
                    strokeWidth = 1f
                )
                curY += gridStep
            }

            // Target Outer Ring & Core
            drawCircle(
                color = CyberPink.copy(alpha = 0.3f),
                radius = targetRadius + 10f,
                center = targetPos
            )
            drawCircle(
                color = CyberPink,
                radius = targetRadius,
                center = targetPos
            )
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = targetPos
            )
            drawCircle(
                color = CyberBlack,
                radius = 3f,
                center = targetPos
            )

            // Dynamic Crosshair
            drawCircle(
                color = CyberCyan,
                radius = 15f,
                center = crosshairPos,
                style = Stroke(width = 2.5f)
            )
            // Precise crosshair reticles
            drawLine(
                color = CyberCyan,
                start = Offset(crosshairPos.x - 22f, crosshairPos.y),
                end = Offset(crosshairPos.x - 5f, crosshairPos.y),
                strokeWidth = 2.5f
            )
            drawLine(
                color = CyberCyan,
                start = Offset(crosshairPos.x + 5f, crosshairPos.y),
                end = Offset(crosshairPos.x + 22f, crosshairPos.y),
                strokeWidth = 2.5f
            )
            drawLine(
                color = CyberCyan,
                start = Offset(crosshairPos.x, crosshairPos.y - 22f),
                end = Offset(crosshairPos.x, crosshairPos.y - 5f),
                strokeWidth = 2.5f
            )
            drawLine(
                color = CyberCyan,
                start = Offset(crosshairPos.x, crosshairPos.y + 5f),
                end = Offset(crosshairPos.x, crosshairPos.y + 22f),
                strokeWidth = 2.5f
            )
        }

        // Live score HUD overlay
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberBlack.copy(alpha = 0.85f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "HITS: $hitCount",
                color = CyberNeonGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "MISS: $missCount",
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "ACC: ${if (hitCount + missCount > 0) (hitCount * 100 / (hitCount + missCount)) else 100}%",
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
