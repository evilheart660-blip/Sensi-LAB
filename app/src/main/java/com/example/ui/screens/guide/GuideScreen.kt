package com.example.ui.screens.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun GuideScreen(
    onBack: () -> Unit
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("guide_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "PRO SENSITIVITY MANUAL",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        CyberCardContainer {
            Text("1. The 200-Scale General Sensitivity", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "In 200-scale games (like Free Fire / FF MAX), general sensitivity between 120-165 delivers the best balance between 360° camera rotation and controllable upward drag without flying above enemy heads.",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        CyberCardContainer {
            Text("2. Fire Button Sizing & Placement", color = CyberPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "A fire button size between 40%-48% allows maximum swipe travel space. Place it slightly lower on your right side so your thumb has a longer vertical trajectory to accelerate the drag headshot.",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        CyberCardContainer {
            Text("3. Smallest Width (DPI) Guide", color = CyberNeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Increasing Developer Options 'Smallest Width' from default (usually 392-411) to 480-560 shrinks system UI scale and increases Android touch pixel sensitivity. Avoid values above 650 to prevent system UI crashes.",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
