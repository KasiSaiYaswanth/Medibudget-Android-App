package com.medibudget.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.medibudget.app.data.local.entity.EstimationLogEntity
import com.medibudget.app.data.local.entity.HospitalEntity
import com.medibudget.app.data.local.entity.MedicineEntity
import com.medibudget.app.data.local.entity.SchemeEntity
import com.medibudget.app.data.repository.AuthRepository
import com.medibudget.app.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isBiometricSet = MutableStateFlow(authRepository.isBiometricsEnabled())
    val isBiometricSet: StateFlow<Boolean> = _isBiometricSet.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success("Welcome back, $email!")
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.resetPassword(email)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }
            result.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Reset password failed")
            }
        }
    }

    fun loginWithBiometrics(): Boolean {
        val savedPw = authRepository.getSavedPassword()
        val email = authRepository.getUserEmail()
        return if (!savedPw.isNullOrEmpty() && !email.isNullOrEmpty() && authRepository.isBiometricsEnabled()) {
            login(email, savedPw)
            true
        } else {
            false
        }
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        authRepository.enableBiometrics(enabled)
        _isBiometricSet.value = enabled
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun getUserEmail(): String {
        return authRepository.getUserEmail() ?: "user@medibudget.com"
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val message: String) : AuthState
    data class Error(val message: String) : AuthState
}

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val healthRepository = HealthRepository(application.applicationContext, viewModelScope)

    // --- Search & Medicine state ---
    val allMedicines: StateFlow<List<MedicineEntity>> = healthRepository.getAllMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchResults = MutableStateFlow<List<MedicineEntity>>(emptyList())
    val searchResults: StateFlow<List<MedicineEntity>> = _searchResults.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // --- Hospital state ---
    val hospitals: StateFlow<List<HospitalEntity>> = healthRepository.getAllHospitals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _gpsCity = MutableStateFlow("Visakhapatnam")
    val gpsCity: StateFlow<String> = _gpsCity.asStateFlow()

    private val _nearbyHospitals = MutableStateFlow<List<HospitalEntity>>(emptyList())
    val nearbyHospitals: StateFlow<List<HospitalEntity>> = _nearbyHospitals.asStateFlow()

    // --- Dynamic cost estimation state ---
    private val _estimationResult = MutableStateFlow<EstimationLogEntity?>(null)
    val estimationResult: StateFlow<EstimationLogEntity?> = _estimationResult.asStateFlow()

    val estimationHistory: StateFlow<List<EstimationLogEntity>> = healthRepository.getEstimationLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Scheme Check ---
    val activeSchemes: StateFlow<List<SchemeEntity>> = healthRepository.getActiveGovernmentSchemes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Chat Assistant flow ---
    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatMessages: StateFlow<List<Pair<String, String>>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- OCR Scanning state ---
    private val _scannedMedicine = MutableStateFlow<MedicineEntity?>(null)
    val scannedMedicine: StateFlow<MedicineEntity?> = _scannedMedicine.asStateFlow()

    init {
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            healthRepository.getSearchHistory().collect { historyEntities ->
                _searchHistory.value = historyEntities.map { it.query }
            }
        }
    }

    fun searchMedicines(query: String) {
        viewModelScope.launch {
            val list = healthRepository.searchMedicines(query)
            _searchResults.value = list
            loadSearchHistory()
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            val cityRes = healthRepository.reverseGeocode(lat, lon)
            cityRes.onSuccess {
                _gpsCity.value = it
            }
            // Fetch nearest hospitals using OSM query boundaries
            val localHospitals = healthRepository.fetchNearbyHospitals(lat, lon)
            _nearbyHospitals.value = localHospitals
        }
    }

    fun calculateEstimate(conditionKey: String, city: String, facility: String) {
        viewModelScope.launch {
            val res = healthRepository.calculateEstimatedCosts(conditionKey, city, facility)
            res.onSuccess {
                _estimationResult.value = it
            }
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.trim().isEmpty()) return
        
        viewModelScope.launch {
            val updatedList = _chatMessages.value.toMutableList().apply {
                add(Pair("user", userText))
            }
            _chatMessages.value = updatedList
            _isChatLoading.value = true

            // Call fallback engine mapping user input matching keywords
            val aiResponse = healthRepository.getAIChatResponse(updatedList)
            
            _chatMessages.value = _chatMessages.value.toMutableList().apply {
                add(Pair("assistant", aiResponse))
            }
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun performOcrMatch(scannedLines: List<String>) {
        viewModelScope.launch {
            // ML Kit OCR parser matching brand name words
            val allMeds = allMedicines.value
            for (line in scannedLines) {
                val match = allMeds.firstOrNull { med ->
                    line.contains(med.name, ignoreCase = true) || 
                    med.name.contains(line, ignoreCase = true)
                }
                if (match != null) {
                    _scannedMedicine.value = match
                    return@launch
                }
            }
            // Fallback match to first medicine for packaging scan demo
            if (allMeds.isNotEmpty()) {
                _scannedMedicine.value = allMeds.first()
            }
        }
    }

    fun clearScannedMedicine() {
        _scannedMedicine.value = null
    }

    fun clearSearchHistoryLogs() {
        viewModelScope.launch {
            healthRepository.clearSearchHistory()
            _searchHistory.value = emptyList()
        }
    }

    fun clearEstimationLogs() {
        viewModelScope.launch {
            healthRepository.clearEstimationsHistory()
        }
    }
}
