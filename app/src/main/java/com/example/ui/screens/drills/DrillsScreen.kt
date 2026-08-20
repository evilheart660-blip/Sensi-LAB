package com.example.ui.screens.drills

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AimDrill
import com.example.data.model.AimDrillRepository
import com.example.ui.components.AimPracticeArea
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun DrillsScreen(
    onBack: () -> Unit
) {
    var selectedDrill by remember { mutableStateOf<AimDrill?>(AimDrillRepository.sampleDrills.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("drills_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "AIM DRILLS ARENA",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Active Interactive Target Canvas
        AimPracticeArea(
            sensitivityMultiplier = 1.15f
        )

        Text(
            text = "CHOOSE DRILL ROUTINE",
            style = MaterialTheme.typography.labelLarge,
            color = CyberCyan,
            letterSpacing = 1.sp
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(AimDrillRepository.sampleDrills) { drill ->
                val isSelected = drill.id == selectedDrill?.id
                DrillCard(
                    drill = drill,
                    isSelected = isSelected,
                    onClick = { selectedDrill = drill }
                )
            }
        }
    }
}

@Composable
fun DrillCard(
    drill: AimDrill,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CyberCardLight else CyberCard)
            .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = drill.title,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) CyberCyan else TextPrimary,
                fontSize = 15.sp
            )
            Text(
                text = drill.difficulty,
                color = CyberPink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = drill.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 ${drill.tip}",
            style = MaterialTheme.typography.bodySmall,
            color = CyberNeonGreen
        )
    }
}
