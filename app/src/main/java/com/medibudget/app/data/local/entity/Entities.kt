package com.medibudget.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val genericName: String,
    val category: String,
    val composition: String,
    val usesRaw: String, // Comma-separated or JSON
    val sideEffectsRaw: String, // Comma-separated or JSON
    val dosage: String,
    val warningsRaw: String, // Comma-separated or JSON
    val priceRange: String,
    val prescriptionRequired: Boolean,
    val manufacturer: String,
    val alternativesRaw: String // Comma-separated brand names
)

@Entity(tableName = "hospitals")
data class HospitalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val pricingTier: String,
    val consultationCost: Double,
    val contactPhone: String?
)

@Entity(tableName = "government_schemes")
data class SchemeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val eligibilityCriteria: String,
    val coverageAmount: Double,
    val state: String,
    val isActive: Boolean
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val timestamp: Long
)

@Entity(tableName = "cost_estimations")
data class EstimationLogEntity(
    @PrimaryKey val id: String,
    val date: String,
    val condition: String,
    val city: String,
    val hospitalType: String,
    val consultation: Double,
    val tests: Double,
    val medicines: Double,
    val treatment: Double,
    val total: Double,
    val cityMultiplier: Double,
    val hospitalMultiplier: Double
)
