package com.example.compositemasscalculator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.compositemasscalculator.data.entities.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Insert
    suspend fun insert(material: Material)

    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<Material>>
}