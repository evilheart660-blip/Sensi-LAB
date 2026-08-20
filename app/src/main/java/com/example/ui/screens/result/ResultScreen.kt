package com.example.ui.screens.result

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.CalculatedSensResult
import com.example.data.model.WeaponCategory
import com.example.ui.components.AimPracticeArea
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCardContainer
import com.example.ui.components.MetricGauge
import com.example.ui.theme.*

@Composable
fun ResultScreen(
    result: CalculatedSensResult,
    onSaveProfile: (String) -> Unit,
    onOpenDrills: () -> Unit,
    onOpenVisualizer: () -> Unit,
    onOpenOptimizer: () -> Unit,
    onBackHome: () -> Unit
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var showShareBadgeDialog by remember { mutableStateOf(false) }
    var profileTitleInput by remember { mutableStateOf("${result.game.displayName} Sensi") }
    var copiedToClipboard by remember { mutableStateOf(false) }
    var showDpiGuide by remember { mutableStateOf(false) }

    // Weapon category filter
    var selectedWeaponTab by remember { mutableStateOf(result.weaponCategory) }
    val currentWeaponDetail = result.allWeaponDetails.firstOrNull { it.category == selectedWeaponTab }
        ?: result.selectedWeaponDetail

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save to Sensi Vault", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = profileTitleInput,
                    onValueChange = { profileTitleInput = it },
                    label = { Text("Profile Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProfile(profileTitleInput)
                        showSaveDialog = false
                    },
                    modifier = Modifier.testTag("confirm_save_profile_btn")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = CyberDark
        )
    }

    if (showShareBadgeDialog) {
        AlertDialog(
            onDismissRequest = { showShareBadgeDialog = false },
            title = { Text("Hacker Sensi Card Badge", color = CyberCyan) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberBlack)
                        .border(1.5.dp, CyberPink, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("⚡ SENSI PRO PASS", color = CyberYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${result.headshotRating}% HEADSHOT", color = CyberNeonGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                    Text(result.game.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("📱 ${result.deviceSummary}", color = TextSecondary, fontSize = 11.sp)
                    HorizontalDivider(color = CyberBorder)
                    Text("• General Camera: ${currentWeaponDetail.generalSens}", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Red Dot / ADS: ${currentWeaponDetail.redDotSens}", color = CyberPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Smallest Width: ${result.recommendedDpi} DPI", color = CyberYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Fire Button: ${currentWeaponDetail.fireButtonSize}% (${currentWeaponDetail.oneTapDragSpeed})", color = CyberNeonGreen, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareText = """
                            🔥 HACKER SENSITIVITY PROFILE 🔥
                            🎮 Game: ${result.game.displayName}
                            📱 Device: ${result.deviceSummary}
                            🎯 Rating: ${result.headshotRating}% Headshot Lock
                            
                            📊 SENSITIVITY NUMBERS:
                            • General: ${currentWeaponDetail.generalSens}
                            • Red Dot: ${currentWeaponDetail.redDotSens}
                            • 2X Scope: ${result.scope2x}
                            • 4X Scope: ${result.scope4x}
                            • Sniper Scope: ${result.sniper}
                            • Free Look: ${result.freeLook}
                            
                            ⚙️ HARDWARE TWEAKS:
                            • Smallest Width: ${result.recommendedDpi} DPI
                            • Fire Button: ${currentWeaponDetail.fireButtonSize}%
                            • Pointer Speed: Step ${result.pointerSpeedStep}/11
                            • Drag Technique: ${currentWeaponDetail.dragTechnique.title}
                        """.trimIndent()
                        clipboardManager.setText(AnnotatedString(shareText))
                        showShareBadgeDialog = false
                    }
                ) {
                    Text("Copy Share Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareBadgeDialog = false }) {
                    Text("Close")
                }
            },
            containerColor = CyberDark
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackHome,
                modifier = Modifier.testTag("result_back_home_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "CALIBRATION OUTPUT",
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontSize = 14.sp
            )
            Row {
                IconButton(
                    onClick = { showShareBadgeDialog = true },
                    modifier = Modifier.testTag("share_badge_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberYellow)
                }
                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.testTag("save_profile_btn")
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = "Save", tint = CyberNeonGreen)
                }
            }
        }

        // Hardware Summary Banner
        CyberCardContainer(borderColor = CyberNeonGreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HARDWARE-CALIBRATED SENSI",
                        color = CyberNeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.deviceSummary,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "${result.headshotRating}%",
                    color = CyberNeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Drag Curve: ${result.dragAccelerationCurve}",
                style = MaterialTheme.typography.bodySmall,
                color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "💡 ${result.brandOptimizationTip}",
                style = MaterialTheme.typography.bodySmall,
                color = CyberYellow
            )
        }

        // Weapon Class Filter Tabs
        Text(
            text = "GUN CATEGORY FILTER",
            style = MaterialTheme.typography.labelLarge,
            color = CyberCyan,
            letterSpacing = 1.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WeaponCategory.values().forEach { cat ->
                val isSelected = cat == selectedWeaponTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberPink.copy(alpha = 0.25f) else CyberCard)
                        .border(1.dp, if (isSelected) CyberPink else CyberBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedWeaponTab = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (cat) {
                            WeaponCategory.SHOTGUN_ONETAP -> "Shotgun"
                            WeaponCategory.SMG_SPRAY -> "SMG"
                            WeaponCategory.AR_DMR_MARKSMAN -> "AR/DMR"
                            WeaponCategory.SNIPER_RIFLE -> "Sniper"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) CyberPink else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Weapon Specific Tuning Banner
        CyberCardContainer(borderColor = CyberPink.copy(alpha = 0.6f)) {
            Text(
                text = currentWeaponDetail.category.title,
                style = MaterialTheme.typography.titleMedium,
                color = CyberPink
            )
            Text(
                text = "Popular Guns: ${currentWeaponDetail.category.popularGuns}",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🎯 Recommended Drag: ${currentWeaponDetail.category.recommendedDrag}",
                color = CyberYellow,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = "⚡ Drag Speed: ${currentWeaponDetail.oneTapDragSpeed}",
                color = CyberNeonGreen,
                fontSize = 12.sp
            )
        }

        // In-Game Sensitivity Gauges
        CyberCardContainer {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IN-GAME VALUES (${if (result.game.is200Scale) "200-SCALE" else "STANDARD"})",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )

                TextButton(
                    onClick = {
                        val textToCopy = """
                            🎮 ${result.game.displayName} Sensitivity (${currentWeaponDetail.category.title})
                            📱 Device: ${result.deviceSummary}
                            • General: ${currentWeaponDetail.generalSens}
                            • Red Dot: ${currentWeaponDetail.redDotSens}
                            • 2X Scope: ${result.scope2x}
                            • 4X Scope: ${result.scope4x}
                            • Sniper Scope: ${result.sniper}
                            • Free Look: ${result.freeLook}
                            • Gyroscope: ${result.gyroGeneral}
                            📐 Smallest Width (DPI): ${result.recommendedDpi}
                            🎯 Fire Button: ${currentWeaponDetail.fireButtonSize}% (Pos: ${result.fireButtonPosX}% X, ${result.fireButtonPosY}% Y)
                            ⚡ Pointer Speed: Step ${result.pointerSpeedStep}/11
                        """.trimIndent()
                        clipboardManager.setText(AnnotatedString(textToCopy))
                        copiedToClipboard = true
                    },
                    modifier = Modifier.testTag("copy_sens_btn")
                ) {
                    Icon(
                        imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = if (copiedToClipboard) CyberNeonGreen else CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copiedToClipboard) "Copied!" else "Copy All",
                        color = if (copiedToClipboard) CyberNeonGreen else CyberCyan,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            MetricGauge(title = "General Camera", value = currentWeaponDetail.generalSens, max = result.game.scaleMax, accentColor = CyberCyan)
            MetricGauge(title = "Red Dot / Holographic", value = currentWeaponDetail.redDotSens, max = result.game.scaleMax, accentColor = CyberPink)
            MetricGauge(title = "2X Scope ADS", value = result.scope2x, max = result.game.scaleMax, accentColor = CyberYellow)
            MetricGauge(title = "4X Scope ADS", value = result.scope4x, max = result.game.scaleMax, accentColor = CyberNeonGreen)
            MetricGauge(title = "Sniper Rifle Scope", value = result.sniper, max = result.game.scaleMax, accentColor = CyberPurple)
            MetricGauge(title = "Free Look 360", value = result.freeLook, max = result.game.scaleMax, accentColor = TextSecondary)
            if (result.gyroGeneral > 0) {
                MetricGauge(title = "Gyroscope General", value = result.gyroGeneral, max = 300, accentColor = CyberCyan)
            }
        }

        // Real Calculated DPI Card
        CyberCardContainer(borderColor = CyberYellow.copy(alpha = 0.6f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL SMALLEST WIDTH (DPI)",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberYellow,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "+${result.dpiIncreasePercent}% Boost",
                    color = CyberNeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HardwareTweakBox(
                    label = "Base System DPI",
                    value = "${result.baseDpi} dp",
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                HardwareTweakBox(
                    label = "Recommended DPI",
                    value = "${result.recommendedDpi} DPI",
                    color = CyberYellow,
                    modifier = Modifier.weight(1f)
                )
                HardwareTweakBox(
                    label = "Safe Max Limit",
                    value = "${result.maxSafeDpi} DPI",
                    color = CyberPink,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { showDpiGuide = !showDpiGuide },
                modifier = Modifier.fillMaxWidth().testTag("toggle_dpi_guide_btn")
            ) {
                Icon(
                    imageVector = if (showDpiGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyberYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showDpiGuide) "Hide DPI Setup Guide" else "How to Apply DPI in Developer Options",
                    color = CyberYellow,
                    fontSize = 13.sp
                )
            }

            AnimatedVisibility(visible = showDpiGuide) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberDark)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("1. Open Phone Settings > About Phone", color = TextPrimary, fontSize = 13.sp)
                    Text("2. Tap 'Build Number' 7 times until Developer Options are unlocked", color = TextPrimary, fontSize = 13.sp)
                    Text("3. Go to System > Developer Options > 'Smallest Width' (or Minimum Width)", color = TextPrimary, fontSize = 13.sp)
                    Text("4. Note your default (${result.baseDpi}) and change it to ${result.recommendedDpi}", color = CyberYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("⚠️ Warning: Never exceed ${result.maxSafeDpi} DPI to protect system launcher layout.", color = CyberPink, fontSize = 12.sp)
                }
            }
        }

        // Fire Button & System Settings
        CyberCardContainer {
            Text(
                text = "HUD & TOUCH REACTION TWEAKS",
                style = MaterialTheme.typography.labelLarge,
                color = CyberPink,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HardwareTweakBox(
                    label = "Fire Button Size",
                    value = "${currentWeaponDetail.fireButtonSize}%",
                    color = CyberPink,
                    modifier = Modifier.weight(1f)
                )
                HardwareTweakBox(
                    label = "Pointer Speed",
                    value = "Step ${result.pointerSpeedStep}/11",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                HardwareTweakBox(
                    label = "Touch Delay",
                    value = result.touchDelaySetting,
                    color = CyberNeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• ${result.fireButtonPositionTip}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Quick Action Hub Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onOpenVisualizer,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Drag Guide", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onOpenOptimizer,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = CyberYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lag Fix Hub", color = CyberYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Test Live Drag Aim
        Text(
            text = "LIVE AIM TESTBENCH",
            style = MaterialTheme.typography.labelLarge,
            color = CyberYellow,
            letterSpacing = 1.sp
        )
        AimPracticeArea(
            sensitivityMultiplier = (currentWeaponDetail.generalSens.toFloat() / 100f).coerceIn(0.8f, 2.0f)
        )

        CyberButton(
            text = "PRACTICE IN DRILLS ARENA",
            onClick = onOpenDrills,
            leadingIcon = Icons.Default.FitnessCenter,
            testTag = "result_open_drills_btn"
        )
    }
}

@Composable
fun HardwareTweakBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberDark)
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
