package com.example.ui.screens.optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceSpec
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

data class OptimizationTip(
    val title: String,
    val impact: String,
    val category: String,
    val icon: ImageVector,
    val steps: List<String>,
    val badgeColor: Color
)

@Composable
fun OptimizerScreen(
    device: DeviceSpec,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    val tips = listOf(
        OptimizationTip(
            title = "Zero Touch Latency (Window Scale 0.5x)",
            impact = "-18ms Touch Delay",
            category = "SYSTEM UI",
            icon = Icons.Default.Speed,
            steps = listOf(
                "Open Settings > Developer Options.",
                "Scroll down to Drawing Section.",
                "Change 'Window animation scale' to 0.5x or Off.",
                "Change 'Transition animation scale' to 0.5x or Off.",
                "Change 'Animator duration scale' to 0.5x."
            ),
            badgeColor = CyberCyan
        ),
        OptimizationTip(
            title = "Force 4x MSAA & GPU Rendering",
            impact = "+Smooth Frame Pacing",
            category = "GRAPHICS ENGINE",
            icon = Icons.Default.Memory,
            steps = listOf(
                "In Developer Options, find 'Hardware Accelerated Rendering'.",
                "Enable 'Force 4x MSAA' for OpenGL ES 2.0 apps.",
                "Enable 'Disable HW overlays' to force GPU for screen compositing."
            ),
            badgeColor = CyberPink
        ),
        OptimizationTip(
            title = "Background Process RAM Limiter",
            impact = "+1.2GB Free Game RAM",
            category = "MEMORY STABILITY",
            icon = Icons.Default.Storage,
            steps = listOf(
                "Go to Settings > Developer Options > Apps section.",
                "Tap 'Background process limit'.",
                "Select 'At most 2 processes' or 'At most 3 processes'.",
                "Prevents background social apps from causing FPS drops during battle."
            ),
            badgeColor = CyberYellow
        ),
        OptimizationTip(
            title = "Touch Sensitivity & Mistouch Prevention",
            impact = "+15% Faster Flick Response",
            category = "DISPLAY HARDWARE",
            icon = Icons.Default.TouchApp,
            steps = listOf(
                "Open Phone Settings > Display.",
                "Turn ON 'Touch sensitivity' (increases touch response with screen protector).",
                "Turn ON 'Accidental touch protection' to eliminate edge mistouches during claw grip."
            ),
            badgeColor = CyberNeonGreen
        ),
        OptimizationTip(
            title = "${device.manufacturer} Brand Game Booster Tuning",
            impact = "Maximum GPU Clock Locking",
            category = "OEM OPTIMIZATION",
            icon = Icons.Default.ElectricBolt,
            steps = listOf(
                "Launch your device's Game Space / Game Turbo / Game Booster.",
                "Set Performance Mode to 'Ultimate' / 'Monster' / 'Pro Gamer'.",
                "Adjust Touch Response to 100% and Sensitivity to continuous track."
            ),
            badgeColor = CyberPurple
        )
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("optimizer_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "FPS & LAG OPTIMIZER HUB",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Hardware Status Card
        CyberCardContainer(borderColor = CyberNeonGreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DETECTED TARGET HARDWARE",
                        color = CyberNeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${device.manufacturer} ${device.model}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${device.refreshRateHz}Hz Display • ${device.ramGb}GB RAM • ${device.processorTier.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Text(
            text = "ESSENTIAL COMPETITIVE TWEAKS",
            style = MaterialTheme.typography.labelLarge,
            color = CyberCyan,
            letterSpacing = 1.sp
        )

        tips.forEach { tip ->
            OptimizationCard(tip = tip)
        }
    }
}

@Composable
fun OptimizationCard(tip: OptimizationTip) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCard)
            .border(1.dp, if (expanded) tip.badgeColor else CyberBorder, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tip.badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tip.icon,
                        contentDescription = null,
                        tint = tip.badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = tip.title,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${tip.category} • ${tip.impact}",
                        color = tip.badgeColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CyberBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "HOW TO CONFIGURE:",
                color = CyberYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            tip.steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}. ",
                        color = tip.badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = step,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
