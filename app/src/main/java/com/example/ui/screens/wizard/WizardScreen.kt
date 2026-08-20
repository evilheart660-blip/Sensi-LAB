package com.example.ui.screens.wizard

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun WizardScreen(
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    deviceForm: DeviceSpec,
    onDeviceUpdate: (DeviceSpec) -> Unit,
    controlForm: ControlSetup,
    onControlUpdate: (ControlSetup) -> Unit,
    gameChoice: GameTarget,
    onGameChoice: (GameTarget) -> Unit,
    graphicsForm: GraphicsSetting,
    onGraphicsUpdate: (GraphicsSetting) -> Unit,
    playStyleForm: PlayStyle,
    onPlayStyleUpdate: (PlayStyle) -> Unit,
    weaponChoice: WeaponCategory,
    onWeaponChoice: (WeaponCategory) -> Unit,
    currentSensForm: CurrentSensInputs,
    onCurrentSensUpdate: (CurrentSensInputs) -> Unit,
    onCompleteWizard: () -> Unit,
    onCancel: () -> Unit
) {
    val totalSteps = 4
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CALIBRATION • STEP $currentStep OF $totalSteps",
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            TextButton(
                onClick = onCancel,
                modifier = Modifier.testTag("wizard_cancel_btn")
            ) {
                Text("Exit", color = TextSecondary)
            }
        }

        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = CyberCyan,
            trackColor = CyberCardLight
        )

        when (currentStep) {
            1 -> {
                // Step 1: Game & Combat Style & Weapon Focus
                Text("Game, Role & Weapon Focus", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                
                CyberCardContainer {
                    Text("Target Mobile Game", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    GameTarget.values().forEach { game ->
                        SelectableRow(
                            label = game.displayName,
                            selected = game == gameChoice,
                            onClick = { onGameChoice(game) }
                        )
                    }
                }

                CyberCardContainer {
                    Text("Primary Weapon Category", style = MaterialTheme.typography.titleMedium, color = CyberPink)
                    Spacer(modifier = Modifier.height(8.dp))
                    WeaponCategory.values().forEach { weapon ->
                        SelectableRow(
                            label = weapon.title,
                            description = "${weapon.popularGuns} • ${weapon.recommendedDrag}",
                            selected = weapon == weaponChoice,
                            onClick = { onWeaponChoice(weapon) }
                        )
                    }
                }

                CyberCardContainer {
                    Text("Combat Playstyle", style = MaterialTheme.typography.titleMedium, color = CyberYellow)
                    Spacer(modifier = Modifier.height(8.dp))
                    PlayStyle.values().forEach { style ->
                        SelectableRow(
                            label = style.label,
                            description = style.description,
                            selected = style == playStyleForm,
                            onClick = { onPlayStyleUpdate(style) }
                        )
                    }
                }
            }

            2 -> {
                // Step 2: Real Device Hardware Telemetry
                Text("Mobile Device & Hardware", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)

                // Live Scanned Device Header
                CyberCardContainer(borderColor = CyberNeonGreen) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = CyberNeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTO-ANALYZED HARDWARE",
                                color = CyberNeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${deviceForm.manufacturer} ${deviceForm.model}",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "${deviceForm.androidVersion} • ${deviceForm.processorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Resolution: ${deviceForm.screenResolution} • Base Smallest Width: ${deviceForm.smallestScreenWidthDp} dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberYellow
                    )
                }

                CyberCardContainer {
                    Text("Display Refresh Rate", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(60, 90, 120, 144, 165).forEach { hz ->
                        SelectableRow(
                            label = "$hz Hz Display",
                            description = if (hz >= 120) "Ultra-fast touch polling" else "Standard polling",
                            selected = deviceForm.refreshRateHz == hz,
                            onClick = { onDeviceUpdate(deviceForm.copy(refreshRateHz = hz)) }
                        )
                    }
                }

                CyberCardContainer {
                    Text("System RAM Capacity", style = MaterialTheme.typography.titleMedium, color = CyberPink)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(3, 4, 6, 8, 12, 16).forEach { ram ->
                        SelectableRow(
                            label = "$ram GB RAM",
                            selected = deviceForm.ramGb == ram,
                            onClick = { onDeviceUpdate(deviceForm.copy(ramGb = ram)) }
                        )
                    }
                }
            }

            3 -> {
                // Step 3: Controls & HUD Layout
                Text("Controls & HUD Layout", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)

                CyberCardContainer {
                    Text("Finger Grip & Layout", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    ControlSetup.values().forEach { control ->
                        SelectableRow(
                            label = control.label,
                            selected = control == controlForm,
                            onClick = { onControlUpdate(control) }
                        )
                    }
                }

                CyberCardContainer {
                    Text("In-Game Graphics Profile", style = MaterialTheme.typography.titleMedium, color = CyberYellow)
                    Spacer(modifier = Modifier.height(8.dp))
                    GraphicsSetting.values().forEach { gfx ->
                        SelectableRow(
                            label = gfx.label,
                            selected = gfx == graphicsForm,
                            onClick = { onGraphicsUpdate(gfx) }
                        )
                    }
                }
            }

            4 -> {
                // Step 4: Verification & Run
                Text("Ready to Calibrate", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                CyberCardContainer(borderColor = CyberCyan) {
                    Text("Full Physics Profile", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Device: ${deviceForm.manufacturer} ${deviceForm.model}", color = TextPrimary)
                    Text("• Display & RAM: ${deviceForm.refreshRateHz}Hz / ${deviceForm.ramGb}GB RAM", color = TextPrimary)
                    Text("• Base System DPI: ${deviceForm.smallestScreenWidthDp} dp", color = TextPrimary)
                    Text("• Target Game: ${gameChoice.displayName}", color = TextPrimary)
                    Text("• Primary Weapon: ${weaponChoice.title}", color = TextPrimary)
                    Text("• Combat Style: ${playStyleForm.label}", color = TextPrimary)
                    Text("• Finger Setup: ${controlForm.label}", color = TextPrimary)
                    Text("• Graphics: ${graphicsForm.label}", color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { onStepChange(currentStep - 1) },
                    modifier = Modifier.weight(1f).height(50.dp).testTag("wizard_prev_btn")
                ) {
                    Text("BACK", color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            }

            val nextText = if (currentStep == totalSteps) "CALCULATE REAL SENSI" else "NEXT"
            CyberButton(
                text = nextText,
                onClick = {
                    if (currentStep == totalSteps) {
                        onCompleteWizard()
                    } else {
                        onStepChange(currentStep + 1)
                    }
                },
                modifier = Modifier.weight(1f),
                testTag = "wizard_next_btn"
            )
        }
    }
}

@Composable
fun SelectableRow(
    label: String,
    description: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) CyberCyan.copy(alpha = 0.15f) else CyberDark)
            .border(1.dp, if (selected) CyberCyan else CyberBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = if (selected) CyberCyan else TextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
            if (description != null) {
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
