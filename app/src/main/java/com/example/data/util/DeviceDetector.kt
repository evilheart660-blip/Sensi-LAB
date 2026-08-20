package com.example.data.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.data.model.DeviceSpec
import com.example.data.model.ProcessorTier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object DeviceDetector {

    fun scanDevice(context: Context): DeviceSpec {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

        // 1. RAM Detection
        var ramGb = 6
        var totalRamBytes = 6L * 1024 * 1024 * 1024
        if (am != null) {
            val memInfo = ActivityManager.MemoryInfo()
            am.getMemoryInfo(memInfo)
            totalRamBytes = memInfo.totalMem
            ramGb = max(2, (memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)).roundToInt())
        }

        // 2. Display Metrics & Resolution
        val displayMetrics = context.resources.displayMetrics
        val widthPx = displayMetrics.widthPixels
        val heightPx = displayMetrics.heightPixels
        val densityDpi = displayMetrics.densityDpi
        val density = displayMetrics.density

        val minDimensionPx = min(widthPx, heightPx)
        val smallestScreenWidthDp = (minDimensionPx / density).roundToInt()

        // Calculate physical screen diagonal in inches
        val xdpi = if (displayMetrics.xdpi > 50f) displayMetrics.xdpi else densityDpi.toFloat()
        val ydpi = if (displayMetrics.ydpi > 50f) displayMetrics.ydpi else densityDpi.toFloat()
        val widthInches = widthPx / xdpi
        val heightInches = heightPx / ydpi
        val diagonalInches = (sqrt(widthInches.pow(2) + heightInches.pow(2)) * 10f).roundToInt() / 10f
        val safeDiagonal = if (diagonalInches in 4.0f..12.0f) diagonalInches else 6.67f

        // 3. Display Refresh Rate Detection
        var refreshRateHz = 60
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                if (display != null) {
                    val currentMode = display.mode
                    val supportedModes = display.supportedModes
                    val maxSupported = supportedModes.maxOfOrNull { it.refreshRate } ?: currentMode.refreshRate
                    refreshRateHz = maxSupported.roundToInt()
                }
            } else {
                val defaultDisplay = wm?.defaultDisplay
                val rate = defaultDisplay?.refreshRate ?: 60f
                refreshRateHz = rate.roundToInt()
            }
        } catch (_: Exception) {
            refreshRateHz = 60
        }

        // Normalize refresh rate to standard gaming bands (60, 90, 120, 144, 165)
        refreshRateHz = when {
            refreshRateHz >= 155 -> 165
            refreshRateHz >= 135 -> 144
            refreshRateHz >= 110 -> 120
            refreshRateHz >= 80 -> 90
            else -> 60
        }

        // 4. Touch Sampling Rate Estimation based on display & hardware tier
        val touchSamplingHz = when {
            refreshRateHz >= 144 -> 360
            refreshRateHz >= 120 -> 240
            refreshRateHz >= 90 -> 180
            else -> 120
        }

        // 5. Processor & Hardware Analysis
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val brand = Build.BRAND.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val hardware = Build.HARDWARE.lowercase()
        val board = Build.BOARD.lowercase()
        val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        // Classify processor tier
        val processorTier = when {
            ramGb >= 12 && (refreshRateHz >= 120) -> ProcessorTier.ELITE_GAMING
            ramGb >= 8 && (refreshRateHz >= 90) -> ProcessorTier.FLAGSHIP
            ramGb >= 6 -> ProcessorTier.MID_RANGE
            else -> ProcessorTier.ENTRY_LEVEL
        }

        val processorName = when {
            hardware.contains("qcom") || hardware.contains("qualcomm") -> "Snapdragon ($board)"
            hardware.contains("mt") || hardware.contains("mediatek") -> "MediaTek Dimensity/Helio"
            hardware.contains("exynos") || hardware.contains("samsung") -> "Samsung Exynos"
            hardware.contains("tensor") || hardware.contains("google") -> "Google Tensor"
            hardware.contains("unisoc") || hardware.contains("sprd") -> "Unisoc / Spreadtrum"
            else -> "${Build.HARDWARE.uppercase()} SoC"
        }

        return DeviceSpec(
            brand = brand,
            model = model,
            manufacturer = manufacturer,
            androidVersion = androidVersion,
            sdkInt = Build.VERSION.SDK_INT,
            ramGb = ramGb,
            totalRamBytes = totalRamBytes,
            refreshRateHz = refreshRateHz,
            touchSamplingHz = touchSamplingHz,
            processorTier = processorTier,
            processorName = processorName,
            screenWidthPx = widthPx,
            screenHeightPx = heightPx,
            screenResolution = "$widthPx x $heightPx",
            densityDpi = densityDpi,
            smallestScreenWidthDp = smallestScreenWidthDp,
            screenDiagonalInches = safeDiagonal,
            isRealDeviceScanned = true
        )
    }
}
