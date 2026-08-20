package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceSpec
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    detectedDevice: DeviceSpec,
    onRescanDevice: () -> Unit,
    onStartAnalysis: () -> Unit,
    onOpenDrills: () -> Unit,
    onOpenVisualizer: () -> Unit,
    onOpenOptimizer: () -> Unit,
    onOpenBrandPresets: () -> Unit,
    onOpenProfiles: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    savedProfilesCount: Int
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HACKER LVL",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CyberCyan,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Universal Device Sensitivity & DPI Lab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = CyberCyan
                )
            }
        }

        // Live Real Device Telemetry Card
        CyberCardContainer(
            borderColor = CyberNeonGreen.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberNeonGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DETECTED USER HARDWARE",
                        color = CyberNeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Row {
                    TextButton(
                        onClick = onOpenBrandPresets,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Presets", color = CyberYellow, fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = onRescanDevice,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("rescan_device_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Re-Scan", color = CyberCyan, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${detectedDevice.manufacturer} ${detectedDevice.model}",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${detectedDevice.androidVersion} • ${detectedDevice.processorName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeviceStatPill(
                    label = "Display",
                    value = "${detectedDevice.refreshRateHz} Hz",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                DeviceStatPill(
                    label = "RAM",
                    value = "${detectedDevice.ramGb} GB",
                    color = CyberPink,
                    modifier = Modifier.weight(1f)
                )
                DeviceStatPill(
                    label = "Base DPI",
                    value = "${detectedDevice.smallestScreenWidthDp} dp",
                    color = CyberYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Hero CTA Card
        CyberCardContainer(
            borderColor = CyberCyan.copy(alpha = 0.5f)
        ) {
            Text(
                text = "REAL SENSITIVITY ENGINE",
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generate Tailored Sensi & DPI",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calibrates one-tap drag flick acceleration and safe smallest width based on your actual phone's touch sampling, display latency & grip.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CyberButton(
                text = "CALIBRATE SENSITIVITY",
                onClick = onStartAnalysis,
                leadingIcon = Icons.Default.FlashOn,
                testTag = "start_wizard_button"
            )
        }

        // Quick Access Grid
        Text(
            text = "LAB TOOLS & SUITE",
            style = MaterialTheme.typography.labelLarge,
            color = CyberPink,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HomeGridCard(
                title = "Drag Visualizer",
                subtitle = "J-Curve & V-Shape Paths",
                icon = Icons.Default.TouchApp,
                accentColor = CyberPink,
                modifier = Modifier.weight(1f),
                onClick = onOpenVisualizer,
                testTag = "visualizer_card"
            )

            HomeGridCard(
                title = "FPS & Lag Fix",
                subtitle = "MSAA & RAM Booster",
                icon = Icons.Default.Speed,
                accentColor = CyberYellow,
                modifier = Modifier.weight(1f),
                onClick = onOpenOptimizer,
                testTag = "optimizer_card"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HomeGridCard(
                title = "Aim Drills",
                subtitle = "Crosshair Practice",
                icon = Icons.Default.TrackChanges,
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f),
                onClick = onOpenDrills,
                testTag = "drills_card"
            )

            HomeGridCard(
                title = "Vault Profiles",
                subtitle = "$savedProfilesCount Saved",
                icon = Icons.Default.FolderSpecial,
                accentColor = CyberNeonGreen,
                modifier = Modifier.weight(1f),
                onClick = onOpenProfiles,
                testTag = "profiles_card"
            )
        }

        HomeGridCard(
            title = "Pro Sensitivity Manual",
            subtitle = "Learn DPI, Fire button size, & drag angles",
            icon = Icons.Default.MenuBook,
            accentColor = CyberYellow,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenGuide,
            testTag = "guide_card"
        )
    }
}

@Composable
fun DeviceStatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp)
        Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun HomeGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
