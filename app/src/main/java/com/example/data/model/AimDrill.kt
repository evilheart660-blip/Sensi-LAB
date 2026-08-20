package com.example.data.model

data class AimDrill(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val targetDurationSec: Int,
    val tip: String
)

object AimDrillRepository {
    val sampleDrills = listOf(
        AimDrill(
            id = "one_tap_flick",
            title = "One-Tap Drag Headshot Drill",
            description = "Flick your crosshair from chest level upward in a fast 'J' or 'Straight' swipe motion.",
            difficulty = "Medium",
            targetDurationSec = 60,
            tip = "Release your drag immediately after the shot fires to prevent recoil over-aim."
        ),
        AimDrill(
            id = "scope_tracking_2x",
            title = "2X Scope Micro-Tracking",
            description = "Track moving targets horizontally at medium range while holding 2X scope ADS.",
            difficulty = "Easy",
            targetDurationSec = 90,
            tip = "Keep steady thumb pressure; do not swipe in abrupt bursts."
        ),
        AimDrill(
            id = "rapid_target_switch",
            title = "360° Reflex Target Switching",
            description = "Quickly alternate between 3 surrounding targets across your screen.",
            difficulty = "Hard",
            targetDurationSec = 120,
            tip = "Rely on camera sensitivity to center targets before pressing scope or fire."
        ),
        AimDrill(
            id = "sniper_quick_scope",
            title = "Quick-Scope Lock & Release",
            description = "Open sniper scope, micro-adjust to head within 200ms, and release fire.",
            difficulty = "Expert",
            targetDurationSec = 60,
            tip = "Align the white center dot on target before tapping ADS."
        )
    )
}
