package com.example.data.model

data class DeviceSpec(
    val brand: String = "Generic Android",
    val model: String = "Smartphone",
    val manufacturer: String = "Android",
    val androidVersion: String = "Android 14",
    val sdkInt: Int = 34,
    val ramGb: Int = 8,
    val totalRamBytes: Long = 8589934592L,
    val refreshRateHz: Int = 120,
    val touchSamplingHz: Int = 240,
    val processorTier: ProcessorTier = ProcessorTier.MID_RANGE,
    val processorName: String = "Octa-Core Processor",
    val screenWidthPx: Int = 1080,
    val screenHeightPx: Int = 2400,
    val screenResolution: String = "1080 x 2400",
    val densityDpi: Int = 411,
    val smallestScreenWidthDp: Int = 392,
    val screenDiagonalInches: Float = 6.67f,
    val isRealDeviceScanned: Boolean = false,
    val customBrandSkin: String = "Stock Android"
)

enum class ProcessorTier(val label: String, val multiplier: Float) {
    ENTRY_LEVEL("Budget (Helio G / Snapdragon 6xx / Unisoc)", 0.90f),
    MID_RANGE("Mid-Range (Snapdragon 7xx / Dimensity 7000-8000)", 1.00f),
    FLAGSHIP("Flagship (Snapdragon 8 Gen 1-3 / Dimensity 9000+)", 1.08f),
    ELITE_GAMING("Elite Flagship (Snapdragon 8 Gen 3/4 / ROG / iQOO)", 1.14f)
}

enum class GameTarget(
    val displayName: String,
    val scaleMax: Int = 200,
    val is200Scale: Boolean = true,
    val defaultGeneralSens: Int = 135
) {
    FREE_FIRE_MAX("Free Fire / FF MAX", 200, true, 140),
    BGMI_PUBG("BGMI / PUBG Mobile", 300, false, 120),
    COD_MOBILE("Call of Duty: Mobile", 300, false, 110),
    STANDOFF_2("Standoff 2", 100, false, 45)
}

enum class PlayStyle(val label: String, val description: String) {
    RUSHER("Aggressive Rusher (One-Tap / Fast Drag)", "Maximum camera responsiveness, fast drag headshots, sharp 2x/Red Dot tracking."),
    SNIPER("Precision Sniper (Scope Lock / Micro Flick)", "Smooth scoped control, zero-jitter micro-adjustments at long range."),
    BALANCED("Balanced All-Rounder (Close & Mid Range)", "Optimal 360° rotation with controllable drag curve.")
}

enum class ControlSetup(val label: String, val fingerCount: Int) {
    TWO_FINGER("2-Finger Thumb Player", 2),
    THREE_FINGER("3-Finger Claw Player", 3),
    FOUR_FINGER("4-Finger Claw Player", 4),
    GYRO_ALWAYS("Full Gyroscope Player", 4)
}

enum class GraphicsSetting(val label: String, val fpsImpact: Float) {
    SMOOTH_HIGH_FPS("Smooth + Ultra / High FPS (Recommended)", 1.06f),
    STANDARD_HIGH_FPS("Standard Graphics + High FPS", 1.00f),
    ULTRA_GRAPHICS("Ultra / MAX Graphics (Frame Drop Risk)", 0.92f)
}

enum class WeaponCategory(
    val title: String,
    val popularGuns: String,
    val dragMultiplier: Float,
    val suggestedFireButtonSize: Int,
    val recommendedDrag: String
) {
    SHOTGUN_ONETAP(
        title = "Shotguns & Deagle (One-Tap)",
        popularGuns = "M1887, M1014, MAG-7, Desert Eagle",
        dragMultiplier = 1.14f,
        suggestedFireButtonSize = 42,
        recommendedDrag = "Fast 'J-Curve' Drag (Swipe down then flick up sharply)"
    ),
    SMG_SPRAY(
        title = "SMG Headshot Recoil (Mid/Close)",
        popularGuns = "MP40, UMP, Thompson, MP5, Bizon",
        dragMultiplier = 1.05f,
        suggestedFireButtonSize = 45,
        recommendedDrag = "Steady Vertical Drag (Gradual upward pull at chest height)"
    ),
    AR_DMR_MARKSMAN(
        title = "AR & Marksman Rifles",
        popularGuns = "Woodpecker, SVD, AC80, AK, SCAR, M4A1",
        dragMultiplier = 0.96f,
        suggestedFireButtonSize = 48,
        recommendedDrag = "Micro Straight Drag (Precise quick-tap flick to forehead)"
    ),
    SNIPER_RIFLE(
        title = "Sniper Scope & Quick Switch",
        popularGuns = "AWM, M82B, Kar98k, Barrett",
        dragMultiplier = 0.88f,
        suggestedFireButtonSize = 52,
        recommendedDrag = "Centered Crosshair Lock (Scope + Shoot + Switch instantly)"
    )
}

enum class DragTechnique(
    val title: String,
    val bestFor: String,
    val description: String,
    val executionSteps: List<String>,
    val angleDegrees: Int
) {
    STRAIGHT_DRAG(
        title = "Straight Vertical Drag",
        bestFor = "Mid & Long Range Encounters",
        description = "Pull the fire button in a direct straight line upward toward the top of your screen.",
        executionSteps = listOf(
            "Align crosshair on the enemy's upper chest.",
            "Drag straight up with constant medium velocity.",
            "Release touch as soon as the red headshot marker pops."
        ),
        angleDegrees = 90
    ),
    J_CURVE_DRAG(
        title = "J-Curve Rotation Drag",
        bestFor = "Close-Combat One-Tap & Running Enemies",
        description = "Swipe slightly down/outward then sweep up violently into a 'J' hook curve.",
        executionSteps = listOf(
            "Keep crosshair slightly white (unlocked) beside enemy.",
            "Dip fire button down-left, then flick up-right toward their head.",
            "Ideal for M1887 and Desert Eagle close one-taps."
        ),
        angleDegrees = 65
    ),
    V_SHAPE_DRAG(
        title = "V-Shape Jump Shot Drag",
        bestFor = "Jump-Shooting & Corner Peeking",
        description = "Dip thumb quickly into a V-dip before accelerating straight to the head.",
        executionSteps = listOf(
            "Jump while moving sideways.",
            "At the peak of your jump, flick down slightly then sharply up.",
            "Locks onto head hitbox before landing."
        ),
        angleDegrees = 115
    )
}

data class BrandPreset(
    val name: String,
    val brand: String,
    val model: String,
    val ramGb: Int,
    val refreshRateHz: Int,
    val baseDpi: Int,
    val tier: ProcessorTier,
    val skinName: String,
    val description: String
)

object BrandPresetLibrary {
    val presets = listOf(
        BrandPreset(
            name = "iQOO & ROG Phone",
            brand = "iQOO",
            model = "12 Pro / ROG 8 (Snapdragon 8 Gen 3)",
            ramGb = 16,
            refreshRateHz = 144,
            baseDpi = 411,
            tier = ProcessorTier.ELITE_GAMING,
            skinName = "FuntouchOS Ultra Gaming",
            description = "300Hz+ touch sampling, zero touch lag, low friction curve."
        ),
        BrandPreset(
            name = "Poco / Redmi / Xiaomi",
            brand = "Poco",
            model = "F5 / X6 Pro / Redmi Note 13",
            ramGb = 8,
            refreshRateHz = 120,
            baseDpi = 392,
            tier = ProcessorTier.FLAGSHIP,
            skinName = "Xiaomi HyperOS",
            description = "Game Turbo touch boost enabled, sharp vertical drag."
        ),
        BrandPreset(
            name = "OnePlus & Realme",
            brand = "OnePlus",
            model = "12R / Realme GT 6",
            ramGb = 12,
            refreshRateHz = 120,
            baseDpi = 411,
            tier = ProcessorTier.FLAGSHIP,
            skinName = "OxygenOS / RealmeUI",
            description = "High-precision touch algorithm with smooth momentum."
        ),
        BrandPreset(
            name = "Samsung Galaxy (OneUI)",
            brand = "Samsung",
            model = "Galaxy S23/S24 & A54/A55",
            ramGb = 8,
            refreshRateHz = 120,
            baseDpi = 384,
            tier = ProcessorTier.FLAGSHIP,
            skinName = "Samsung One UI",
            description = "Linear touch response curve, requires +6% drag compensation."
        ),
        BrandPreset(
            name = "Vivo / Oppo",
            brand = "Vivo",
            model = "V30 / T3 / Reno 11",
            ramGb = 8,
            refreshRateHz = 120,
            baseDpi = 392,
            tier = ProcessorTier.MID_RANGE,
            skinName = "FuntouchOS / ColorOS",
            description = "Fast multi-finger tracking with balanced acceleration."
        ),
        BrandPreset(
            name = "Infinix & Tecno (Budget)",
            brand = "Infinix",
            model = "GT 20 Pro / Hot 40 / Spark 20",
            ramGb = 6,
            refreshRateHz = 90,
            baseDpi = 360,
            tier = ProcessorTier.ENTRY_LEVEL,
            skinName = "XOS Gaming",
            description = "Optimized for Helio/Dimensity chips with +15% DPI boost."
        )
    )
}

data class CurrentSensInputs(
    val general: Int = 100,
    val redDot: Int = 90,
    val scope2x: Int = 85,
    val scope4x: Int = 75,
    val sniper: Int = 60,
    val freeLook: Int = 80
)

data class FormInputs(
    val deviceSpec: DeviceSpec = DeviceSpec(),
    val gameTarget: GameTarget = GameTarget.FREE_FIRE_MAX,
    val playStyle: PlayStyle = PlayStyle.RUSHER,
    val controlSetup: ControlSetup = ControlSetup.TWO_FINGER,
    val graphicsSetting: GraphicsSetting = GraphicsSetting.SMOOTH_HIGH_FPS,
    val weaponCategory: WeaponCategory = WeaponCategory.SHOTGUN_ONETAP,
    val currentSensInputs: CurrentSensInputs = CurrentSensInputs()
)
