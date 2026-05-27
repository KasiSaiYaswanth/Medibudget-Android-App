package com.medibudget.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medibudget.app.data.local.entity.EstimationLogEntity
import com.medibudget.app.data.local.entity.HospitalEntity
import com.medibudget.app.data.local.entity.MedicineEntity
import com.medibudget.app.data.local.entity.SchemeEntity
import com.medibudget.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity?

    @Query("SELECT DISTINCT category FROM medicines")
    suspend fun getCategories(): List<String>

    @Query("SELECT * FROM medicines WHERE category = :category")
    suspend fun getMedicinesByCategory(category: String): List<MedicineEntity>

    @Query("SELECT * FROM medicines WHERE name LIKE '%' || :query || '%' OR genericName LIKE '%' || :query || '%' OR composition LIKE '%' || :query || '%'")
    suspend fun searchMedicines(query: String): List<MedicineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicines: List<MedicineEntity>)
}

@Dao
interface HospitalDao {
    @Query("SELECT * FROM hospitals")
    fun getAllHospitals(): Flow<List<HospitalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hospitals: List<HospitalEntity>)
}

@Dao
interface SchemeDao {
    @Query("SELECT * FROM government_schemes WHERE isActive = 1")
    fun getActiveSchemes(): Flow<List<SchemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schemes: List<SchemeEntity>)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("SELECT * FROM cost_estimations ORDER BY date DESC")
    fun getEstimations(): Flow<List<EstimationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimation(log: EstimationLogEntity)

    @Query("DELETE FROM cost_estimations")
    suspend fun clearEstimations()
}
