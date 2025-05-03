package com.example.compositemasscalculator.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,  // Добавим код материала (A, B, C)
    val name: String,
    val density: Double
)