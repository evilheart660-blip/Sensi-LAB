package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    animationsEnabled: Boolean,
    onToggleAnimations: () -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    soundsEnabled: Boolean,
    onToggleSounds: () -> Unit,
    onClearAllProfiles: () -> Unit,
    onBackToHome: () -> Unit
) {
    val scrollState = rememberScrollState()

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
            IconButton(onClick = onBackToHome, modifier = Modifier.testTag("settings_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "LAB PREFERENCES",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        CyberCardContainer {
            Text("Interface & Animation", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Cyber Theme", color = TextPrimary)
                Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode() })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Smooth Screen Transitions", color = TextPrimary)
                Switch(checked = animationsEnabled, onCheckedChange = { onToggleAnimations() })
            }
        }

        CyberCardContainer {
            Text("Feedback", style = MaterialTheme.typography.titleMedium, color = CyberPink)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Haptic Feedback on Hits", color = TextPrimary)
                Switch(checked = hapticsEnabled, onCheckedChange = { onToggleHaptics() })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sound Effects", color = TextPrimary)
                Switch(checked = soundsEnabled, onCheckedChange = { onToggleSounds() })
            }
        }

        CyberCardContainer {
            Text("Data Management", style = MaterialTheme.typography.titleMedium, color = CyberYellow)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onClearAllProfiles,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPink)
            ) {
                Text("Clear All Saved Profiles", fontWeight = FontWeight.Bold)
            }
        }
    }
}
