package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.screens.analyzing.AnalyzingScreen
import com.example.ui.screens.drills.DrillsScreen
import com.example.ui.screens.guide.GuideScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.optimizer.OptimizerScreen
import com.example.ui.screens.presets.BrandPresetsScreen
import com.example.ui.screens.profiles.ProfilesScreen
import com.example.ui.screens.result.ResultScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.visualizer.DragVisualizerScreen
import com.example.ui.screens.wizard.WizardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SensitivityViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {

    private val viewModel: SensitivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by remember(viewModel) {
                viewModel.uiState.map { it.isDarkMode }.distinctUntilChanged()
            }.collectAsState(initial = true)

            MyApplicationTheme(darkTheme = isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                ) {
                    SensitivityLabApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SensitivityLabApp(
    viewModel: SensitivityViewModel
) {
    val currentScreen by remember(viewModel) {
        viewModel.uiState.map { it.currentScreen }.distinctUntilChanged()
    }.collectAsState(initial = ScreenDestination.Splash)

    val animationsEnabled by remember(viewModel) {
        viewModel.uiState.map { it.animationsEnabled }.distinctUntilChanged()
    }.collectAsState(initial = true)

    // Android back handler logic
    BackHandler(enabled = currentScreen != ScreenDestination.Home && currentScreen != ScreenDestination.Splash) {
        when (currentScreen) {
            is ScreenDestination.Wizard -> {
                val currentStep = (currentScreen as ScreenDestination.Wizard).step
                if (currentStep > 1) {
                    viewModel.navigateTo(ScreenDestination.Wizard(currentStep - 1))
                } else {
                    viewModel.navigateTo(ScreenDestination.Home)
                }
            }
            is ScreenDestination.Analyzing -> {
                viewModel.navigateTo(ScreenDestination.Home)
            }
            is ScreenDestination.Result,
            is ScreenDestination.Drills,
            is ScreenDestination.Visualizer,
            is ScreenDestination.Optimizer,
            is ScreenDestination.BrandPresets,
            is ScreenDestination.Profiles,
            is ScreenDestination.CompareProfiles,
            is ScreenDestination.Settings,
            is ScreenDestination.Guide -> {
                viewModel.navigateTo(ScreenDestination.Home)
            }
            else -> {}
        }
    }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = if (animationsEnabled) 75 else 0),
        label = "screen_crossfade"
    ) { screen ->
        when (screen) {
            is ScreenDestination.Splash -> {
                SplashScreen(
                    onContinue = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Home -> {
                val savedProfilesCount by remember(viewModel) {
                    viewModel.uiState.map { it.savedProfiles.size }.distinctUntilChanged()
                }.collectAsState(initial = 0)
                val deviceForm by remember(viewModel) {
                    viewModel.uiState.map { it.deviceForm }.distinctUntilChanged()
                }.collectAsState(initial = com.example.data.model.DeviceSpec())

                HomeScreen(
                    detectedDevice = deviceForm,
                    onRescanDevice = {
                        viewModel.scanRealDevice()
                    },
                    onStartAnalysis = {
                        viewModel.navigateTo(ScreenDestination.Wizard(1))
                    },
                    onOpenDrills = {
                        viewModel.navigateTo(ScreenDestination.Drills)
                    },
                    onOpenVisualizer = {
                        viewModel.navigateTo(ScreenDestination.Visualizer)
                    },
                    onOpenOptimizer = {
                        viewModel.navigateTo(ScreenDestination.Optimizer)
                    },
                    onOpenBrandPresets = {
                        viewModel.navigateTo(ScreenDestination.BrandPresets)
                    },
                    onOpenProfiles = {
                        viewModel.navigateTo(ScreenDestination.Profiles)
                    },
                    onOpenGuide = {
                        viewModel.navigateTo(ScreenDestination.Guide)
                    },
                    onOpenSettings = {
                        viewModel.navigateTo(ScreenDestination.Settings)
                    },
                    savedProfilesCount = savedProfilesCount
                )
            }

            is ScreenDestination.Wizard -> {
                val uiState by viewModel.uiState.collectAsState()

                WizardScreen(
                    currentStep = screen.step,
                    onStepChange = { nextStep ->
                        viewModel.navigateTo(ScreenDestination.Wizard(nextStep))
                    },
                    deviceForm = uiState.deviceForm,
                    onDeviceUpdate = viewModel::updateDeviceForm,
                    controlForm = uiState.controlForm,
                    onControlUpdate = viewModel::updateControlForm,
                    gameChoice = uiState.gameChoice,
                    onGameChoice = viewModel::updateGameChoice,
                    graphicsForm = uiState.graphicsForm,
                    onGraphicsUpdate = viewModel::updateGraphicsForm,
                    playStyleForm = uiState.playStyleForm,
                    onPlayStyleUpdate = viewModel::updatePlayStyleForm,
                    weaponChoice = uiState.weaponChoice,
                    onWeaponChoice = viewModel::updateWeaponChoice,
                    currentSensForm = uiState.currentSensForm,
                    onCurrentSensUpdate = viewModel::updateCurrentSensForm,
                    onCompleteWizard = {
                        viewModel.runAnalysisAndGenerate()
                    },
                    onCancel = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Analyzing -> {
                val progressIndex by remember(viewModel) {
                    viewModel.uiState.map { it.analysisProgressIndex }.distinctUntilChanged()
                }.collectAsState(initial = 0)

                AnalyzingScreen(
                    progressIndex = progressIndex
                )
            }

            is ScreenDestination.Result -> {
                val activeResult by remember(viewModel) {
                    viewModel.uiState.map { it.activeResult }.distinctUntilChanged()
                }.collectAsState(initial = null)

                activeResult?.let { result ->
                    ResultScreen(
                        result = result,
                        onSaveProfile = { title ->
                            viewModel.saveCurrentResultAsProfile(title)
                        },
                        onOpenDrills = {
                            viewModel.navigateTo(ScreenDestination.Drills)
                        },
                        onOpenVisualizer = {
                            viewModel.navigateTo(ScreenDestination.Visualizer)
                        },
                        onOpenOptimizer = {
                            viewModel.navigateTo(ScreenDestination.Optimizer)
                        },
                        onBackHome = {
                            viewModel.navigateTo(ScreenDestination.Home)
                        }
                    )
                }
            }

            is ScreenDestination.Visualizer -> {
                DragVisualizerScreen(
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Optimizer -> {
                val deviceForm by remember(viewModel) {
                    viewModel.uiState.map { it.deviceForm }.distinctUntilChanged()
                }.collectAsState(initial = com.example.data.model.DeviceSpec())

                OptimizerScreen(
                    device = deviceForm,
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.BrandPresets -> {
                val deviceForm by remember(viewModel) {
                    viewModel.uiState.map { it.deviceForm }.distinctUntilChanged()
                }.collectAsState(initial = com.example.data.model.DeviceSpec())

                BrandPresetsScreen(
                    currentModel = deviceForm.model,
                    onSelectPreset = { preset ->
                        viewModel.applyBrandPreset(preset)
                        viewModel.navigateTo(ScreenDestination.Home)
                    },
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Drills -> {
                DrillsScreen(
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Profiles, is ScreenDestination.CompareProfiles -> {
                val profiles by remember(viewModel) {
                    viewModel.uiState.map { it.savedProfiles }.distinctUntilChanged()
                }.collectAsState(initial = emptyList())

                ProfilesScreen(
                    profiles = profiles,
                    onSelectProfile = { profile ->
                        viewModel.loadProfileAsActive(profile)
                    },
                    onDeleteProfile = { profile ->
                        viewModel.deleteProfile(profile)
                    },
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Guide -> {
                GuideScreen(
                    onBack = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }

            is ScreenDestination.Settings -> {
                val isDark by remember(viewModel) {
                    viewModel.uiState.map { it.isDarkMode }.distinctUntilChanged()
                }.collectAsState(initial = true)
                val anims by remember(viewModel) {
                    viewModel.uiState.map { it.animationsEnabled }.distinctUntilChanged()
                }.collectAsState(initial = true)
                val haptics by remember(viewModel) {
                    viewModel.uiState.map { it.hapticsEnabled }.distinctUntilChanged()
                }.collectAsState(initial = true)
                val sounds by remember(viewModel) {
                    viewModel.uiState.map { it.soundsEnabled }.distinctUntilChanged()
                }.collectAsState(initial = true)

                SettingsScreen(
                    isDarkMode = isDark,
                    onToggleDarkMode = viewModel::toggleDarkMode,
                    animationsEnabled = anims,
                    onToggleAnimations = viewModel::toggleAnimations,
                    hapticsEnabled = haptics,
                    onToggleHaptics = viewModel::toggleHaptics,
                    soundsEnabled = sounds,
                    onToggleSounds = viewModel::toggleSounds,
                    onClearAllProfiles = viewModel::clearAllProfiles,
                    onBackToHome = {
                        viewModel.navigateTo(ScreenDestination.Home)
                    }
                )
            }
        }
    }
}
