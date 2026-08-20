package com.example.ui.screens.analyzing

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AnalyzingScreen(
    progressIndex: Int
) {
    val steps = listOf(
        "Scanning Touch Sampling & Display Latency...",
        "Calibrating Drag Multipliers & Acceleration Curve...",
        "Optimizing 200-Scale Red Dot & Scopes...",
        "Generating Custom DPI & Fire Button Sizing...",
        "Finalizing Competitive Profile..."
    )

    val currentText = steps.getOrElse(progressIndex) { "Optimizing Sensitivity..." }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = CyberCyan,
                trackColor = CyberBorder,
                strokeWidth = 4.dp,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CALIBRATING HACKER ENGINE",
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentText,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
