package com.example.compositemasscalculator.data.dao

import com.example.compositemasscalculator.data.dao.MaterialDao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.compositemasscalculator.data.entities.Material
import kotlinx.coroutines.flow.Flow
// Добавьте этот импорт

@Dao
interface MaterialDao {
    // Основные CRUD операции
    @Insert
    suspend fun insert(material: Material)

    @Update
    suspend fun update(material: Material)

    @Delete
    suspend fun delete(material: Material)

    // Получение материалов
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<Material>>  // Используем Flow вместо LiveData

    @Query("SELECT * FROM materials WHERE id = :materialId")
    suspend fun getMaterialById(materialId: Int): Material?

    // Дополнительные методы
    @Query("SELECT * FROM materials WHERE code = :code LIMIT 1")
    suspend fun getMaterialByCode(code: String): Material?

    @Query("SELECT COUNT(*) FROM materials")
    suspend fun getMaterialsCount(): Int

    @Query("SELECT * FROM materials WHERE name LIKE :searchQuery ORDER BY name ASC")
    fun searchMaterials(searchQuery: String): Flow<List<Material>>

    @Query("SELECT code FROM materials")
    suspend fun getAllMaterialCodes(): List<String>


}