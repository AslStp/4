package com.example.compositemasscalculator.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.data.room.AppDatabase
import kotlinx.coroutines.flow.Flow

class MaterialRepository(application: Application) {
    private val materialDao = AppDatabase.getDatabase(application).materialDao()

    // Основной метод получения материалов (Flow)
    fun getMaterialsFlow(): Flow<List<Material>> = materialDao.getAllMaterials()

    // LiveData версия для совместимости
    fun getMaterialsLiveData(): LiveData<List<Material>> = materialDao.getAllMaterials().asLiveData()

    // Добавление материала
    suspend fun insert(material: Material) {
        materialDao.insert(material)
    }

    // Получение материала по коду
    suspend fun getMaterialByCode(code: String): Material? {
        return materialDao.getMaterialByCode(code)
    }

    // Поиск материалов
    fun searchMaterials(query: String): Flow<List<Material>> {
        return materialDao.searchMaterials("%$query%")
    }
}