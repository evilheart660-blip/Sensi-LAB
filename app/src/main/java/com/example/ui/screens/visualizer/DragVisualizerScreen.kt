package com.example.ui.screens.visualizer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DragTechnique
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun DragVisualizerScreen(
    onBack: () -> Unit
) {
    var selectedTechnique by remember { mutableStateOf(DragTechnique.J_CURVE_DRAG) }
    val scrollState = rememberScrollState()

    // Infinite animation for finger drag path
    val infiniteTransition = rememberInfiniteTransition(label = "drag_anim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drag_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("visualizer_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "DRAG ANGLE VISUALIZER",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Technique Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DragTechnique.values().forEach { tech ->
                val isSelected = tech == selectedTechnique
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberCard)
                        .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedTechnique = tech }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (tech) {
                            DragTechnique.STRAIGHT_DRAG -> "Straight"
                            DragTechnique.J_CURVE_DRAG -> "J-Curve"
                            DragTechnique.V_SHAPE_DRAG -> "V-Shape"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) CyberCyan else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Animated Interactive Finger Drag Canvas
        CyberCardContainer(borderColor = CyberCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MOTION PATH ANIMATION",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${selectedTechnique.angleDegrees}° Attack Vector",
                    color = CyberYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDark),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid
                    val step = 40.dp.toPx()
                    var x = step
                    while (x < w) {
                        drawLine(CyberBorder.copy(alpha = 0.3f), Offset(x, 0f), Offset(x, h), 1f)
                        x += step
                    }
                    var y = step
                    while (y < h) {
                        drawLine(CyberBorder.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 1f)
                        y += step
                    }

                    // Headshot Target Zone (Top center-right)
                    val targetCenter = Offset(w * 0.55f, h * 0.22f)
                    drawCircle(CyberPink.copy(alpha = 0.25f), radius = 32f, center = targetCenter)
                    drawCircle(CyberPink, radius = 22f, center = targetCenter)
                    drawCircle(Color.White, radius = 8f, center = targetCenter)

                    // Fire Button Starting Anchor (Bottom right area)
                    val startBtnCenter = Offset(w * 0.50f, h * 0.78f)
                    drawCircle(CyberBorder, radius = 38f, center = startBtnCenter)
                    drawCircle(CyberCyan.copy(alpha = 0.3f), radius = 34f, center = startBtnCenter)
                    drawCircle(CyberCyan, radius = 10f, center = startBtnCenter)

                    // Compute dynamic curve path based on technique
                    val path = Path()
                    var currentPoint = startBtnCenter

                    when (selectedTechnique) {
                        DragTechnique.STRAIGHT_DRAG -> {
                            path.moveTo(startBtnCenter.x, startBtnCenter.y)
                            path.lineTo(startBtnCenter.x, targetCenter.y)
                            currentPoint = Offset(
                                startBtnCenter.x,
                                startBtnCenter.y + (targetCenter.y - startBtnCenter.y) * animProgress
                            )
                        }
                        DragTechnique.J_CURVE_DRAG -> {
                            // J hook: dip down-left then sweep up-right
                            val dipPoint = Offset(startBtnCenter.x - 40f, startBtnCenter.y + 20f)
                            path.moveTo(startBtnCenter.x, startBtnCenter.y)
                            path.quadraticTo(dipPoint.x, dipPoint.y, targetCenter.x, targetCenter.y)

                            val t = animProgress
                            val p0 = startBtnCenter
                            val p1 = dipPoint
                            val p2 = targetCenter
                            val curX = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
                            val curY = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
                            currentPoint = Offset(curX, curY)
                        }
                        DragTechnique.V_SHAPE_DRAG -> {
                            val dipPoint = Offset(startBtnCenter.x - 30f, startBtnCenter.y + 35f)
                            path.moveTo(startBtnCenter.x, startBtnCenter.y)
                            path.lineTo(dipPoint.x, dipPoint.y)
                            path.lineTo(targetCenter.x, targetCenter.y)

                            if (animProgress < 0.35f) {
                                val subT = animProgress / 0.35f
                                currentPoint = Offset(
                                    startBtnCenter.x + (dipPoint.x - startBtnCenter.x) * subT,
                                    startBtnCenter.y + (dipPoint.y - startBtnCenter.y) * subT
                                )
                            } else {
                                val subT = (animProgress - 0.35f) / 0.65f
                                currentPoint = Offset(
                                    dipPoint.x + (targetCenter.x - dipPoint.x) * subT,
                                    dipPoint.y + (targetCenter.y - dipPoint.y) * subT
                                )
                            }
                        }
                    }

                    // Draw motion guide path
                    drawPath(
                        path = path,
                        color = CyberCyan.copy(alpha = 0.5f),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // Animated Finger Touch Indicator
                    drawCircle(
                        color = CyberYellow.copy(alpha = 0.35f),
                        radius = 24f,
                        center = currentPoint
                    )
                    drawCircle(
                        color = CyberYellow,
                        radius = 12f,
                        center = currentPoint
                    )
                }

                Text(
                    text = "TARGET HEAD",
                    color = CyberPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                )

                Text(
                    text = "FIRE BUTTON ANCHOR",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
                )
            }
        }

        // Technique Execution Details
        CyberCardContainer {
            Text(
                text = selectedTechnique.title,
                style = MaterialTheme.typography.titleMedium,
                color = CyberYellow
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🎯 Best For: ${selectedTechnique.bestFor}",
                color = CyberNeonGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = selectedTechnique.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "STEP-BY-STEP EXECUTION:",
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            selectedTechnique.executionSteps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}. ",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = step,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
