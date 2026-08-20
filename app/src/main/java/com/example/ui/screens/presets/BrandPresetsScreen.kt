package com.example.ui.screens.presets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrandPreset
import com.example.data.model.BrandPresetLibrary
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCardContainer
import com.example.ui.theme.*

@Composable
fun BrandPresetsScreen(
    currentModel: String,
    onSelectPreset: (BrandPreset) -> Unit,
    onBack: () -> Unit
) {
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("brand_presets_back_btn")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Text(
                text = "PHONE BRAND PRESETS",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Select any device brand profile to auto-tune sensitivity, touch polling curves, and safe DPI formulas for that specific phone model.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(BrandPresetLibrary.presets) { preset ->
                val isCurrent = currentModel.contains(preset.brand, ignoreCase = true)
                PresetCard(
                    preset = preset,
                    isCurrent = isCurrent,
                    onSelect = {
                        onSelectPreset(preset)
                    }
                )
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: BrandPreset,
    isCurrent: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrent) CyberCardLight else CyberCard)
            .border(1.dp, if (isCurrent) CyberCyan else CyberBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = preset.name,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) CyberCyan else TextPrimary,
                fontSize = 15.sp
            )
            if (isCurrent) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCyan.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "ACTIVE",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${preset.model} • ${preset.refreshRateHz}Hz • ${preset.ramGb}GB RAM",
            color = CyberYellow,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = preset.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "⚡ UI Layer: ${preset.skinName}",
            color = CyberNeonGreen,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}
