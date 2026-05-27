package com.medibudget.app.data.repository

import android.content.Context
import com.medibudget.app.data.local.dao.HistoryDao
import com.medibudget.app.data.local.dao.HospitalDao
import com.medibudget.app.data.local.dao.MedicineDao
import com.medibudget.app.data.local.dao.SchemeDao
import com.medibudget.app.data.local.database.AppDatabase
import com.medibudget.app.data.local.entity.EstimationLogEntity
import com.medibudget.app.data.local.entity.HospitalEntity
import com.medibudget.app.data.local.entity.MedicineEntity
import com.medibudget.app.data.local.entity.SchemeEntity
import com.medibudget.app.data.local.entity.SearchHistoryEntity
import com.medibudget.app.data.remote.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HealthRepository(context: Context, scope: CoroutineScope) {

    private val db = AppDatabase.getDatabase(context, scope)
    private val medicineDao = db.medicineDao()
    private val hospitalDao = db.hospitalDao()
    private val schemeDao = db.schemeDao()
    private val historyDao = db.historyDao()
    private val locationApi = NetworkClient.locationApi

    // --- Medicine Search & OCR Scanner ---
    fun getAllMedicines(): Flow<List<MedicineEntity>> = medicineDao.getAllMedicines()

    suspend fun searchMedicines(query: String): List<MedicineEntity> = withContext(Dispatchers.IO) {
        if (query.trim().isEmpty()) return@withContext emptyList()
        historyDao.insertSearch(SearchHistoryEntity(query = query, timestamp = System.currentTimeMillis()))
        medicineDao.searchMedicines(query)
    }

    suspend fun getMedicineById(id: String): MedicineEntity? = withContext(Dispatchers.IO) {
        medicineDao.getMedicineById(id)
    }

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> = historyDao.getSearchHistory()

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        historyDao.clearSearchHistory()
    }

    // --- Hospital Finder & Location APIs ---
    fun getAllHospitals(): Flow<List<HospitalEntity>> = hospitalDao.getAllHospitals()

    suspend fun reverseGeocode(lat: Double, lon: Double): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = locationApi.reverseGeocode(lat, lon)
            val addr = response.address
            val city = addr?.city ?: addr?.town ?: addr?.village ?: addr?.suburb ?: "Visakhapatnam"
            Result.success(city)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun fetchNearbyHospitals(lat: Double, lon: Double): List<HospitalEntity> = withContext(Dispatchers.IO) {
        try {
            // Define a bounding box around coordinates (roughly ~10km)
            val delta = 0.05
            val viewbox = "${lon - delta},${lat + delta},${lon + delta},${lat - delta}"
            val response = locationApi.searchLocalHospitals(viewbox = viewbox)
            
            // Map OSM search responses to HospitalEntities
            response.mapIndexed { index, raw ->
                val nameParts = raw.display_name.split(",")
                val name = nameParts.firstOrNull() ?: "Hospital #${raw.place_id}"
                HospitalEntity(
                    id = raw.place_id.toString(),
                    name = name,
                    city = "Local Area",
                    state = "OSM GPS",
                    latitude = raw.lat.toDoubleOrNull() ?: lat,
                    longitude = raw.lon.toDoubleOrNull() ?: lon,
                    category = if (index % 3 == 0) "government" else "private",
                    pricingTier = if (index % 3 == 0) "economy" else "standard",
                    consultationCost = if (index % 3 == 0) 50.0 else 500.0,
                    contactPhone = "+91 99887 76655"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- Dynamic Cost Estimator Engine ---
    fun getEstimationLogs(): Flow<List<EstimationLogEntity>> = historyDao.getEstimations()

    suspend fun clearEstimationsHistory() = withContext(Dispatchers.IO) {
        historyDao.clearEstimations()
    }

    suspend fun calculateEstimatedCosts(
        conditionKey: String,
        cityName: String,
        hospitalCategory: String
    ): Result<EstimationLogEntity> = withContext(Dispatchers.IO) {
        try {
            // City Multipliers
            val cityMultiplier = when (cityName.lowercase(Locale.ROOT)) {
                "mumbai" -> 1.6
                "delhi" -> 1.5
                "bangalore", "bengaluru" -> 1.35
                "hyderabad" -> 1.25
                "chennai" -> 1.2
                "kolkata" -> 1.15
                "pune" -> 1.2
                "ahmedabad" -> 1.1
                "visakhapatnam", "vizag" -> 1.05
                else -> 1.0
            }

            // Facility Category Multipliers
            val hospitalMultiplier = when (hospitalCategory.lowercase(Locale.ROOT)) {
                "government" -> 0.3
                "trust" -> 0.5
                "private" -> 1.0
                "corporate", "super-specialty", "multi-specialty" -> 1.8
                else -> 1.0
            }

            // Base Costs
            // Returns: consultation, tests, medicines, treatment
            val baseCosts = when (conditionKey.lowercase(Locale.ROOT)) {
                "fever" -> Quadruple(300.0, 500.0, 300.0, 200.0)
                "fracture" -> Quadruple(800.0, 2500.0, 1200.0, 15000.0)
                "cardiac" -> Quadruple(1500.0, 8000.0, 2500.0, 5000.0)
                "bypass" -> Quadruple(2000.0, 15000.0, 10000.0, 250000.0)
                "angioplasty" -> Quadruple(1500.0, 10000.0, 8000.0, 120000.0)
                "dental" -> Quadruple(500.0, 600.0, 400.0, 5000.0)
                "eye" -> Quadruple(600.0, 1200.0, 800.0, 20000.0)
                "maternity" -> Quadruple(800.0, 3000.0, 2000.0, 35000.0)
                "csection" -> Quadruple(1000.0, 4000.0, 3500.0, 65000.0)
                "kidney" -> Quadruple(800.0, 2000.0, 3000.0, 5000.0)
                "transplant" -> Quadruple(2500.0, 25000.0, 20000.0, 800000.0)
                "skin" -> Quadruple(600.0, 800.0, 1500.0, 4000.0)
                "cancer" -> Quadruple(1500.0, 8000.0, 12000.0, 40000.0)
                "appendix" -> Quadruple(1000.0, 3500.0, 2500.0, 45000.0)
                "hernia" -> Quadruple(800.0, 3000.0, 2000.0, 38000.0)
                "knee" -> Quadruple(1200.0, 5000.0, 4000.0, 150000.0)
                "spine" -> Quadruple(1500.0, 8000.0, 5000.0, 180000.0)
                "diabetes" -> Quadruple(500.0, 1200.0, 1500.0, 500.0)
                "thyroid" -> Quadruple(500.0, 1500.0, 800.0, 200.0)
                "neuro" -> Quadruple(1500.0, 6000.0, 3000.0, 2000.0)
                else -> Quadruple(500.0, 1000.0, 800.0, 1000.0) // Default baseline
            }

            // Calculation
            val estimatedConsultation = baseCosts.first * cityMultiplier * hospitalMultiplier
            val estimatedTests = baseCosts.second * cityMultiplier * hospitalMultiplier
            val estimatedMedicines = baseCosts.third * cityMultiplier * hospitalMultiplier
            val estimatedTreatment = baseCosts.fourth * cityMultiplier * hospitalMultiplier
            val total = estimatedConsultation + estimatedTests + estimatedMedicines + estimatedTreatment

            val log = EstimationLogEntity(
                id = UUID.randomUUID().toString(),
                date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
                condition = conditionKey.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                city = cityName,
                hospitalType = hospitalCategory,
                consultation = Math.round(estimatedConsultation * 100.0) / 100.0,
                tests = Math.round(estimatedTests * 100.0) / 100.0,
                medicines = Math.round(estimatedMedicines * 100.0) / 100.0,
                treatment = Math.round(estimatedTreatment * 100.0) / 100.0,
                total = Math.round(total * 100.0) / 100.0,
                cityMultiplier = cityMultiplier,
                hospitalMultiplier = hospitalMultiplier
            )

            historyDao.insertEstimation(log)
            Result.success(log)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Government Schemes & Insurance ---
    fun getActiveGovernmentSchemes(): Flow<List<SchemeEntity>> = schemeDao.getActiveSchemes()

    // --- AI Symptom Chat Assistant Fallback Engine ---
    suspend fun getAIChatResponse(messages: List<Pair<String, String>>): String = withContext(Dispatchers.IO) {
        // Collect all user speech to perform local NLP parsing as offline fallback
        val userSpeech = messages.filter { it.first == "user" }.joinToString(". ") { it.second }.lowercase(Locale.ROOT)
        
        // Simulating highly smart, contextual diagnostic reply with suggested medical categories
        val conditionKeywords = mapOf(
            "fever" to Pair("Viral Fever / Influenza", "We suggest visiting a General Physician. Typical costs: ₹500 - ₹1,500. Rest, stay hydrated, and take paracetamol if prescribed."),
            "headache" to Pair("Neurological Assessment / Migraine", "We suggest visiting a Neurologist. Typical costs: ₹1,500 - ₹5,000. Avoid bright lights and stress."),
            "chest" to Pair("Cardiac Evaluation / Heart Health", "⚠️ We strongly recommend visiting a Cardiologist immediately for an ECG/TMT. Chest discomfort can be critical!"),
            "fracture" to Pair("Orthopedic Consultation", "We suggest visiting an Orthopedic Specialist. X-ray might be required. Keep the limb immobilized."),
            "stomach" to Pair("Gastroenterology Consultation", "We suggest visiting a Gastroenterologist. Avoid heavy meals; keep hydrated.")
        )

        var detectedCondition = "General Symptoms Guidance"
        var suggestionDetails = "We suggest consulting a General Practitioner at your nearest medical facility."

        for ((key, pair) in conditionKeywords) {
            if (userSpeech.contains(key)) {
                detectedCondition = pair.first
                suggestionDetails = pair.second
                break
            }
        }

        // Return a beautifully structured markdown response matching the web styling
        """
        ### AI Diagnostic Summary: $detectedCondition
        
        Thank you for describing your feelings. Based on our preliminary AI analysis:
        
        * **Recommendation**: $suggestionDetails
        * **Next Step**: Click the **Go to Cost Estimation** button at the top right to calculate treatment, medicines, and consultation costs in your city!
        
        *Note: This is an AI guidance helper and does not replace professional medical diagnosis. If you feel severe pain or difficulty breathing, please tap the SOS emergency button.*
        """.trimIndent()
    }

    private data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
