package com.example.data.engine

import com.example.data.model.ControlSetup
import com.example.data.model.DeviceSpec
import com.example.data.model.DragTechnique
import com.example.data.model.FormInputs
import com.example.data.model.GameTarget
import com.example.data.model.GraphicsSetting
import com.example.data.model.PlayStyle
import com.example.data.model.ProcessorTier
import com.example.data.model.WeaponCategory
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class WeaponSensDetail(
    val category: WeaponCategory,
    val generalSens: Int,
    val redDotSens: Int,
    val fireButtonSize: Int,
    val dragTechnique: DragTechnique,
    val oneTapDragSpeed: String,
    val weaponNotes: String
)

data class CalculatedSensResult(
    val game: GameTarget,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int,
    val gyroGeneral: Int,
    val gyroRedDot: Int,
    val recommendedDpi: Int,
    val baseDpi: Int,
    val maxSafeDpi: Int,
    val dpiIncreasePercent: Int,
    val pointerSpeedStep: Int, // 1 to 11
    val touchDelaySetting: String,
    val fireButtonSizePercent: Int,
    val fireButtonPosX: Int,
    val fireButtonPosY: Int,
    val fireButtonPositionTip: String,
    val headshotRating: Int, // 0 to 100
    val dragAccelerationCurve: String,
    val deviceSummary: String,
    val weaponCategory: WeaponCategory,
    val selectedWeaponDetail: WeaponSensDetail,
    val allWeaponDetails: List<WeaponSensDetail>,
    val recommendedDragTechnique: DragTechnique,
    val brandOptimizationTip: String,
    val analysisNotes: List<String>
)

object SensitivityEngine {

    fun calculate(inputs: FormInputs): CalculatedSensResult {
        val device = inputs.deviceSpec
        val game = inputs.gameTarget
        val style = inputs.playStyle
        val control = inputs.controlSetup
        val graphics = inputs.graphicsSetting
        val selectedWeapon = inputs.weaponCategory

        // 1. Hardware-Specific Scaling Factors
        // Refresh Rate factor: High refresh displays require slightly lower raw sensitivity because touch frames arrive faster
        val refreshMultiplier = when {
            device.refreshRateHz >= 165 -> 0.92f
            device.refreshRateHz >= 144 -> 0.95f
            device.refreshRateHz >= 120 -> 0.98f
            device.refreshRateHz >= 90 -> 1.03f
            else -> 1.12f // 60Hz displays need extra drag sensitivity to compensate for input frame latency
        }

        // Touch Sampling Rate factor:
        val touchMultiplier = when {
            device.touchSamplingHz >= 360 -> 0.94f
            device.touchSamplingHz >= 240 -> 1.00f
            device.touchSamplingHz >= 180 -> 1.05f
            else -> 1.10f
        }

        // RAM & Processor Frame Stability factor:
        val ramMultiplier = when {
            device.ramGb <= 3 -> 1.14f
            device.ramGb <= 4 -> 1.08f
            device.ramGb <= 6 -> 1.03f
            else -> 1.00f
        }

        // Screen Density & Travel Distance factor:
        val screenDiagonal = if (device.screenDiagonalInches > 0) device.screenDiagonalInches else 6.67f
        val screenTravelFactor = (6.7f / screenDiagonal).coerceIn(0.90f, 1.15f)

        // Brand UI Layer Calibration:
        val (brandMultiplier, brandSkinTip) = when {
            device.manufacturer.contains("Xiaomi", ignoreCase = true) ||
            device.brand.contains("Poco", ignoreCase = true) ||
            device.brand.contains("Redmi", ignoreCase = true) -> Pair(
                0.98f,
                "MIUI / HyperOS: Enable Game Turbo > Touch Response: Max & Aim Sensitivity: High."
            )
            device.manufacturer.contains("Samsung", ignoreCase = true) -> Pair(
                1.05f,
                "Samsung One UI: Enable 'Touch Sensitivity' in Display Settings & Game Booster 120Hz mode."
            )
            device.manufacturer.contains("OnePlus", ignoreCase = true) ||
            device.brand.contains("Realme", ignoreCase = true) -> Pair(
                1.00f,
                "OxygenOS / RealmeUI: Set Touch Optimization to Pro Gamer & HyperBoost Ultra Touch."
            )
            device.manufacturer.contains("Vivo", ignoreCase = true) ||
            device.brand.contains("iQOO", ignoreCase = true) -> Pair(
                0.97f,
                "FuntouchOS / Monster Mode: Turn on 4D Game Vibration & 1200Hz Instant Touch Response."
            )
            device.manufacturer.contains("Infinix", ignoreCase = true) ||
            device.manufacturer.contains("Tecno", ignoreCase = true) -> Pair(
                1.08f,
                "XOS / HiOS: Enable Dar-Link Game Booster and set Pointer Speed to 9/11."
            )
            else -> Pair(
                1.00f,
                "Android System: Set Developer Options Animation Scales to 0.5x for zero touch input delay."
            )
        }

        // Playstyle Multiplier
        val styleGeneralBias = when (style) {
            PlayStyle.RUSHER -> 1.18f
            PlayStyle.SNIPER -> 0.88f
            PlayStyle.BALANCED -> 1.04f
        }

        // Control / Grip Multiplier
        val controlBias = when (control) {
            ControlSetup.TWO_FINGER -> 1.12f // 2-finger thumb players have limited travel range
            ControlSetup.THREE_FINGER -> 1.05f
            ControlSetup.FOUR_FINGER -> 1.00f
            ControlSetup.GYRO_ALWAYS -> 0.92f
        }

        val totalHardwareMultiplier = refreshMultiplier * touchMultiplier * ramMultiplier * 
            device.processorTier.multiplier * graphics.fpsImpact * screenTravelFactor * brandMultiplier

        val is200 = game.is200Scale

        // 2. Compute Base In-Game Sensitivities
        val baseGeneral = if (is200) 140f else 100f
        val calculatedGeneral = (baseGeneral * styleGeneralBias * controlBias * totalHardwareMultiplier).coerceIn(
            if (is200) 85f else 40f,
            if (is200) 200f else 200f
        ).roundToInt()

        // Red Dot sensitivity (drag headshot recoil compensation)
        val redDotBias = if (style == PlayStyle.RUSHER) 0.94f else 0.88f
        val calculatedRedDot = (calculatedGeneral * redDotBias).coerceIn(if (is200) 75f else 30f, if (is200) 200f else 190f).roundToInt()

        // 2X Scope (medium distance drag)
        val scope2xBias = if (style == PlayStyle.SNIPER) 0.82f else 0.88f
        val calculated2x = (calculatedRedDot * scope2xBias).coerceIn(if (is200) 65f else 25f, if (is200) 200f else 180f).roundToInt()

        // 4X Scope (long distance tracking)
        val scope4xBias = if (style == PlayStyle.SNIPER) 0.76f else 0.82f
        val calculated4x = (calculated2x * scope4xBias).coerceIn(if (is200) 55f else 20f, if (is200) 195f else 160f).roundToInt()

        // Sniper Rifle Scope (quick-scope micro alignment)
        val sniperBias = when (style) {
            PlayStyle.SNIPER -> 0.68f
            PlayStyle.RUSHER -> 0.54f
            PlayStyle.BALANCED -> 0.60f
        }
        val calculatedSniper = (calculatedGeneral * sniperBias).coerceIn(if (is200) 42f else 15f, if (is200) 180f else 140f).roundToInt()

        // Free Look (360 view)
        val calculatedFreeLook = (calculatedGeneral * 0.85f).coerceIn(if (is200) 65f else 30f, if (is200) 200f else 180f).roundToInt()

        // Gyroscope Values
        val calculatedGyroGeneral = (calculatedGeneral * 1.5f).coerceIn(120f, 300f).roundToInt()
        val calculatedGyroRedDot = (calculatedRedDot * 1.45f).coerceIn(110f, 300f).roundToInt()

        // 3. True Adaptive Smallest Width (DPI) Calculation
        val baseDpi = if (device.smallestScreenWidthDp in 300..500) device.smallestScreenWidthDp else 392
        val optimalDpiRatio = when {
            device.ramGb <= 4 && device.refreshRateHz <= 60 -> 1.22f // +22% for budget
            device.ramGb <= 6 -> 1.28f // +28% for mid
            device.refreshRateHz >= 120 -> 1.35f // +35% for high-refresh gaming
            else -> 1.25f
        }
        val calculatedDpi = (baseDpi * optimalDpiRatio).roundToInt().coerceIn(baseDpi + 40, 620)
        val maxSafeDpi = min(baseDpi + 220, 640)
        val dpiIncrease = ((calculatedDpi - baseDpi).toFloat() / baseDpi.toFloat() * 100f).roundToInt()

        // 4. Fire Button Sizing & Placement
        val baseFireButton = when (control) {
            ControlSetup.TWO_FINGER -> 46
            ControlSetup.THREE_FINGER -> 42
            ControlSetup.FOUR_FINGER -> 38
            ControlSetup.GYRO_ALWAYS -> 36
        }
        val fireButtonSize = if (screenDiagonal < 6.4f) (baseFireButton - 3) else baseFireButton

        val fireButtonPosX = 84 // % from left
        val fireButtonPosY = 86 // % from top

        val pointerSpeed = when (style) {
            PlayStyle.RUSHER -> if (device.refreshRateHz >= 120) 8 else 9
            PlayStyle.SNIPER -> 5
            PlayStyle.BALANCED -> 7
        }

        // 5. Generate Multi-Weapon Details
        val allWeaponDetails = WeaponCategory.values().map { weapon ->
            val wGeneral = (calculatedGeneral * weapon.dragMultiplier).coerceIn(if (is200) 80f else 35f, 200f).roundToInt()
            val wRedDot = (calculatedRedDot * weapon.dragMultiplier).coerceIn(if (is200) 70f else 30f, 200f).roundToInt()
            val wButtonSize = (weapon.suggestedFireButtonSize + (if (control == ControlSetup.TWO_FINGER) 2 else 0)).coerceIn(36, 58)
            val wTechnique = when (weapon) {
                WeaponCategory.SHOTGUN_ONETAP -> DragTechnique.J_CURVE_DRAG
                WeaponCategory.SMG_SPRAY -> DragTechnique.STRAIGHT_DRAG
                WeaponCategory.AR_DMR_MARKSMAN -> DragTechnique.STRAIGHT_DRAG
                WeaponCategory.SNIPER_RIFLE -> DragTechnique.V_SHAPE_DRAG
            }
            val wSpeed = when (weapon) {
                WeaponCategory.SHOTGUN_ONETAP -> "Fast Snap (0.15s flick duration)"
                WeaponCategory.SMG_SPRAY -> "Gradual Momentum (0.35s continuous pull)"
                WeaponCategory.AR_DMR_MARKSMAN -> "Sharp Micro-Tap (0.10s flick)"
                WeaponCategory.SNIPER_RIFLE -> "Micro Linear Adjustment"
            }
            val wNotes = "Optimized for ${weapon.popularGuns} on ${device.model}."

            WeaponSensDetail(
                category = weapon,
                generalSens = wGeneral,
                redDotSens = wRedDot,
                fireButtonSize = wButtonSize,
                dragTechnique = wTechnique,
                oneTapDragSpeed = wSpeed,
                weaponNotes = wNotes
            )
        }

        val selectedWeaponDetail = allWeaponDetails.firstOrNull { it.category == selectedWeapon }
            ?: allWeaponDetails.first()

        val recommendedTechnique = selectedWeaponDetail.dragTechnique

        // 6. Headshot Accuracy Rating & Notes
        val headshotRating = when {
            device.refreshRateHz >= 120 && device.ramGb >= 8 && style == PlayStyle.RUSHER -> 98
            device.refreshRateHz >= 90 && device.ramGb >= 6 -> 94
            device.ramGb >= 4 -> 88
            else -> 84
        }

        val dragCurve = when (style) {
            PlayStyle.RUSHER -> "Fast 'J-Curve' Drag (Chest to Forehead Flick)"
            PlayStyle.SNIPER -> "Micro Linear Lock (Centered Crosshair ADS)"
            PlayStyle.BALANCED -> "Smooth Vertical Drag with Thumb Momentum"
        }

        val deviceSummary = "${device.manufacturer} ${device.model} • ${device.refreshRateHz}Hz • ${device.ramGb}GB RAM • ${device.screenResolution}"

        val notes = mutableListOf<String>()
        if (device.isRealDeviceScanned) {
            notes.add("Real hardware analyzed: ${device.brand} ${device.model} (${device.androidVersion}).")
        }
        if (device.refreshRateHz >= 120) {
            notes.add("High-Refresh ${device.refreshRateHz}Hz panel detected: Tuned for zero-overshoot drag flicks.")
        } else {
            notes.add("Standard 60Hz display: Applied +12% drag flick compensation for touch latency.")
        }
        notes.add("Calculated safe Smallest Width (DPI): $calculatedDpi DPI (Default was $baseDpi DPI, +$dpiIncrease%).")
        notes.add("Fire Button size set to $fireButtonSize% for optimal vertical thumb travel space.")
        notes.add(brandSkinTip)

        return CalculatedSensResult(
            game = game,
            general = calculatedGeneral,
            redDot = calculatedRedDot,
            scope2x = calculated2x,
            scope4x = calculated4x,
            sniper = calculatedSniper,
            freeLook = calculatedFreeLook,
            gyroGeneral = calculatedGyroGeneral,
            gyroRedDot = calculatedGyroRedDot,
            recommendedDpi = calculatedDpi,
            baseDpi = baseDpi,
            maxSafeDpi = maxSafeDpi,
            dpiIncreasePercent = dpiIncrease,
            pointerSpeedStep = pointerSpeed,
            touchDelaySetting = "Short (0.5s)",
            fireButtonSizePercent = fireButtonSize,
            fireButtonPosX = fireButtonPosX,
            fireButtonPosY = fireButtonPosY,
            fireButtonPositionTip = "Place Fire Button at $fireButtonPosX% X, $fireButtonPosY% Y (lower right) to maximize upward swipe room.",
            headshotRating = headshotRating,
            dragAccelerationCurve = dragCurve,
            deviceSummary = deviceSummary,
            weaponCategory = selectedWeapon,
            selectedWeaponDetail = selectedWeaponDetail,
            allWeaponDetails = allWeaponDetails,
            recommendedDragTechnique = recommendedTechnique,
            brandOptimizationTip = brandSkinTip,
            analysisNotes = notes
        )
    }
}
