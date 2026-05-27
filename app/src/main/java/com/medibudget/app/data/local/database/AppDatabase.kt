package com.medibudget.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medibudget.app.data.local.dao.HistoryDao
import com.medibudget.app.data.local.dao.HospitalDao
import com.medibudget.app.data.local.dao.MedicineDao
import com.medibudget.app.data.local.dao.SchemeDao
import com.medibudget.app.data.local.entity.EstimationLogEntity
import com.medibudget.app.data.local.entity.HospitalEntity
import com.medibudget.app.data.local.entity.MedicineEntity
import com.medibudget.app.data.local.entity.SchemeEntity
import com.medibudget.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(
    entities = [
        MedicineEntity::class,
        HospitalEntity::class,
        SchemeEntity::class,
        SearchHistoryEntity::class,
        EstimationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun hospitalDao(): HospitalDao
    abstract fun schemeDao(): SchemeDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medibudget_database"
                )
                .addCallback(AppDatabaseCallback(context, scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    prepopulateDatabase(database)
                }
            }
        }

        private suspend fun prepopulateDatabase(db: AppDatabase) {
            val gson = Gson()
            
            // 1. Seed Medicines
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("medicines.json")
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<RawMedicine>>() {}.type
                val rawList: List<RawMedicine> = gson.fromJson(reader, type)
                
                val entities = rawList.map { raw ->
                    MedicineEntity(
                        id = raw.id ?: java.util.UUID.randomUUID().toString(),
                        name = raw.name ?: "",
                        genericName = raw.generic_name ?: "",
                        category = raw.category ?: "General",
                        composition = raw.generic_name ?: "",
                        usesRaw = raw.uses?.joinToString(",") ?: "",
                        sideEffectsRaw = raw.side_effects?.joinToString(",") ?: "",
                        dosage = raw.dosage ?: "",
                        warningsRaw = raw.warnings?.joinToString(",") ?: "",
                        priceRange = raw.price_range ?: "₹10 - ₹50",
                        prescriptionRequired = raw.prescription_required ?: false,
                        manufacturer = raw.manufacturer ?: "Generic",
                        alternativesRaw = raw.alternatives?.joinToString(",") ?: ""
                    )
                }
                db.medicineDao().insertAll(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Seed Hospitals
            try {
                val inputStream = context.assets.open("hospitals.json")
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<RawHospital>>() {}.type
                val rawList: List<RawHospital> = gson.fromJson(reader, type)
                
                val entities = rawList.map { raw ->
                    HospitalEntity(
                        id = raw.id ?: java.util.UUID.randomUUID().toString(),
                        name = raw.name ?: "",
                        city = raw.city ?: "",
                        state = raw.state ?: "",
                        latitude = raw.latitude ?: 0.0,
                        longitude = raw.longitude ?: 0.0,
                        category = raw.category ?: "general",
                        pricingTier = raw.pricing_tier ?: "standard",
                        consultationCost = raw.consultation_cost ?: 100.0,
                        contactPhone = raw.contact_phone
                    )
                }
                db.hospitalDao().insertAll(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Seed Government Schemes
            try {
                val inputStream = context.assets.open("government_schemes.json")
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<RawScheme>>() {}.type
                val rawList: List<RawScheme> = gson.fromJson(reader, type)
                
                val entities = rawList.map { raw ->
                    SchemeEntity(
                        id = raw.id ?: java.util.UUID.randomUUID().toString(),
                        name = raw.name ?: "",
                        description = raw.description ?: "",
                        eligibilityCriteria = raw.eligibility_criteria ?: "",
                        coverageAmount = raw.coverage_amount ?: 0.0,
                        state = raw.state ?: "",
                        isActive = raw.is_active ?: true
                    )
                }
                db.schemeDao().insertAll(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Helper classes for parsing raw json
    private class RawMedicine(
        val id: String?,
        val name: String?,
        val generic_name: String?,
        val category: String?,
        val uses: List<String>?,
        val side_effects: List<String>?,
        val dosage: String?,
        val warnings: List<String>?,
        val price_range: String?,
        val prescription_required: Boolean?,
        val manufacturer: String?,
        val alternatives: List<String>?
    )

    private class RawHospital(
        val id: String?,
        val name: String?,
        val city: String?,
        val state: String?,
        val latitude: Double?,
        val longitude: Double?,
        val category: String?,
        val pricing_tier: String?,
        val consultation_cost: Double?,
        val contact_phone: String?
    )

    private class RawScheme(
        val id: String?,
        val name: String?,
        val description: String?,
        val eligibility_criteria: String?,
        val coverage_amount: Double?,
        val state: String?,
        val is_active: Boolean?
    )
}
