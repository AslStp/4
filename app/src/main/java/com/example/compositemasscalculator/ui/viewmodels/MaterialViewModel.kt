package com.example.compositemasscalculator.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.data.repositories.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MaterialViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MaterialRepository(application)

    // Основной поток материалов (Flow версия)
    val materialsFlow: Flow<List<Material>> = repository.getMaterialsFlow()

    // LiveData версия для совместимости с UI
    val materialsLiveData: LiveData<List<Material>> = repository.getMaterialsLiveData()


    // Добавление нового материала
    fun addMaterial(material: Material) = viewModelScope.launch {
        repository.insert(material)
    }

    // Получение материала по коду
    suspend fun getMaterialByCode(code: String): Material? {
        return repository.getMaterialByCode(code)
    }

    // Поиск материалов
    fun searchMaterials(query: String): Flow<List<Material>> {
        return repository.searchMaterials("%$query%")
    }

    // Проверка существования материала с таким кодом
    suspend fun isMaterialCodeExists(code: String): Boolean {
        return repository.getMaterialByCode(code) != null
    }

}