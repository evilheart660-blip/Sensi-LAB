package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.CalculatedSensResult
import com.example.data.engine.SensitivityEngine
import com.example.data.engine.WeaponSensDetail
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ProfileEntity
import com.example.data.model.BrandPreset
import com.example.data.model.ControlSetup
import com.example.data.model.CurrentSensInputs
import com.example.data.model.DeviceSpec
import com.example.data.model.DragTechnique
import com.example.data.model.FormInputs
import com.example.data.model.GameTarget
import com.example.data.model.GraphicsSetting
import com.example.data.model.PlayStyle
import com.example.data.model.WeaponCategory
import com.example.data.repository.ProfileRepository
import com.example.data.util.DeviceDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Splash : ScreenDestination()
    object Home : ScreenDestination()
    data class Wizard(val step: Int) : ScreenDestination()
    object Analyzing : ScreenDestination()
    object Result : ScreenDestination()
    object Drills : ScreenDestination()
    object Profiles : ScreenDestination()
    object CompareProfiles : ScreenDestination()
    object Optimizer : ScreenDestination()
    object Visualizer : ScreenDestination()
    object BrandPresets : ScreenDestination()
    object Settings : ScreenDestination()
    object Guide : ScreenDestination()
}

data class SensitivityUiState(
    val currentScreen: ScreenDestination = ScreenDestination.Splash,
    val isDarkMode: Boolean = true,
    val animationsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val soundsEnabled: Boolean = true,
    // Wizard form fields
    val deviceForm: DeviceSpec = DeviceSpec(),
    val gameChoice: GameTarget = GameTarget.FREE_FIRE_MAX,
    val controlForm: ControlSetup = ControlSetup.TWO_FINGER,
    val graphicsForm: GraphicsSetting = GraphicsSetting.SMOOTH_HIGH_FPS,
    val playStyleForm: PlayStyle = PlayStyle.RUSHER,
    val weaponChoice: WeaponCategory = WeaponCategory.SHOTGUN_ONETAP,
    val currentSensForm: CurrentSensInputs = CurrentSensInputs(),
    // Analysis state
    val analysisProgressIndex: Int = 0,
    val activeResult: CalculatedSensResult? = null,
    // Room Profiles
    val savedProfiles: List<ProfileEntity> = emptyList(),
    val compareProfile1: ProfileEntity? = null,
    val compareProfile2: ProfileEntity? = null
)

class SensitivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProfileRepository
    private val _uiState = MutableStateFlow(SensitivityUiState())
    val uiState: StateFlow<SensitivityUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProfileRepository(db.profileDao())

        // Initial real device hardware auto-detection
        val realDevice = DeviceDetector.scanDevice(application)
        _uiState.update { it.copy(deviceForm = realDevice) }

        viewModelScope.launch {
            repository.allProfiles.collect { profiles ->
                _uiState.update { it.copy(savedProfiles = profiles) }
            }
        }

        // Fast splash transition
        viewModelScope.launch {
            delay(400)
            if (_uiState.value.currentScreen is ScreenDestination.Splash) {
                _uiState.update { it.copy(currentScreen = ScreenDestination.Home) }
            }
        }
    }

    fun scanRealDevice() {
        val realDevice = DeviceDetector.scanDevice(getApplication())
        _uiState.update { it.copy(deviceForm = realDevice) }
    }

    fun applyBrandPreset(preset: BrandPreset) {
        val customDevice = _uiState.value.deviceForm.copy(
            brand = preset.brand,
            model = preset.model,
            manufacturer = preset.brand,
            ramGb = preset.ramGb,
            refreshRateHz = preset.refreshRateHz,
            smallestScreenWidthDp = preset.baseDpi,
            processorTier = preset.tier,
            customBrandSkin = preset.skinName,
            isRealDeviceScanned = false
        )
        _uiState.update {
            it.copy(
                deviceForm = customDevice
            )
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _uiState.update { it.copy(currentScreen = destination) }
    }

    fun updateDeviceForm(device: DeviceSpec) {
        _uiState.update { it.copy(deviceForm = device) }
    }

    fun updateGameChoice(game: GameTarget) {
        _uiState.update { it.copy(gameChoice = game) }
    }

    fun updateControlForm(control: ControlSetup) {
        _uiState.update { it.copy(controlForm = control) }
    }

    fun updateGraphicsForm(graphics: GraphicsSetting) {
        _uiState.update { it.copy(graphicsForm = graphics) }
    }

    fun updatePlayStyleForm(style: PlayStyle) {
        _uiState.update { it.copy(playStyleForm = style) }
    }

    fun updateWeaponChoice(weapon: WeaponCategory) {
        _uiState.update { it.copy(weaponChoice = weapon) }
    }

    fun updateCurrentSensForm(current: CurrentSensInputs) {
        _uiState.update { it.copy(currentSensForm = current) }
    }

    fun runAnalysisAndGenerate() {
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.Analyzing,
                analysisProgressIndex = 0
            )
        }

        viewModelScope.launch {
            for (i in 1..4) {
                delay(90)
                _uiState.update { it.copy(analysisProgressIndex = i) }
            }
            delay(60)

            val currentInputs = FormInputs(
                deviceSpec = _uiState.value.deviceForm,
                gameTarget = _uiState.value.gameChoice,
                playStyle = _uiState.value.playStyleForm,
                controlSetup = _uiState.value.controlForm,
                graphicsSetting = _uiState.value.graphicsForm,
                weaponCategory = _uiState.value.weaponChoice,
                currentSensInputs = _uiState.value.currentSensForm
            )

            val result = SensitivityEngine.calculate(currentInputs)
            _uiState.update {
                it.copy(
                    activeResult = result,
                    currentScreen = ScreenDestination.Result
                )
            }
        }
    }

    fun saveCurrentResultAsProfile(title: String) {
        val result = _uiState.value.activeResult ?: return
        viewModelScope.launch {
            val entity = ProfileEntity(
                title = if (title.isBlank()) "${result.game.displayName} Hacker Sens" else title,
                gameName = result.game.displayName,
                generalSens = result.general,
                redDotSens = result.redDot,
                scope2xSens = result.scope2x,
                scope4xSens = result.scope4x,
                sniperSens = result.sniper,
                freeLookSens = result.freeLook,
                recommendedDpi = result.recommendedDpi,
                fireButtonSize = result.fireButtonSizePercent,
                playStyle = _uiState.value.playStyleForm.label,
                headshotRating = result.headshotRating
            )
            repository.saveProfile(entity)
        }
    }

    fun deleteProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun clearAllProfiles() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun loadProfileAsActive(profile: ProfileEntity) {
        val mockInputs = FormInputs(
            deviceSpec = _uiState.value.deviceForm,
            gameTarget = GameTarget.FREE_FIRE_MAX,
            playStyle = _uiState.value.playStyleForm,
            controlSetup = _uiState.value.controlForm,
            graphicsSetting = _uiState.value.graphicsForm,
            weaponCategory = _uiState.value.weaponChoice
        )
        val calculated = SensitivityEngine.calculate(mockInputs)
        val mockResult = calculated.copy(
            general = profile.generalSens,
            redDot = profile.redDotSens,
            scope2x = profile.scope2xSens,
            scope4x = profile.scope4xSens,
            sniper = profile.sniperSens,
            freeLook = profile.freeLookSens,
            recommendedDpi = profile.recommendedDpi,
            fireButtonSizePercent = profile.fireButtonSize,
            headshotRating = profile.headshotRating,
            deviceSummary = "Profile: ${profile.title}",
            analysisNotes = listOf("Profile '${profile.title}' loaded into active lab state.")
        )
        _uiState.update {
            it.copy(
                activeResult = mockResult,
                currentScreen = ScreenDestination.Result
            )
        }
    }

    fun setCompareProfiles(p1: ProfileEntity?, p2: ProfileEntity?) {
        _uiState.update {
            it.copy(
                compareProfile1 = p1,
                compareProfile2 = p2,
                currentScreen = ScreenDestination.CompareProfiles
            )
        }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun toggleAnimations() {
        _uiState.update { it.copy(animationsEnabled = !it.animationsEnabled) }
    }

    fun toggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    }

    fun toggleSounds() {
        _uiState.update { it.copy(soundsEnabled = !it.soundsEnabled) }
    }
}
