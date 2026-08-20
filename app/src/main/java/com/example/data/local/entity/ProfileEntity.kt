package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensitivity_profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val gameName: String,
    val generalSens: Int,
    val redDotSens: Int,
    val scope2xSens: Int,
    val scope4xSens: Int,
    val sniperSens: Int,
    val freeLookSens: Int,
    val recommendedDpi: Int,
    val fireButtonSize: Int,
    val playStyle: String,
    val headshotRating: Int,
    val timestamp: Long = System.currentTimeMillis()
)
