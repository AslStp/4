package com.example.compositemasscalculator.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.compositemasscalculator.data.entities.Calculation

@Dao
interface CalculationDao {
    @Insert
    suspend fun insert(calculation: Calculation)

    @Query("SELECT * FROM calculations ORDER BY date DESC")
    fun getAllCalculations(): List<Calculation>
}