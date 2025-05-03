package com.example.compositemasscalculator.data

import android.content.Context
import androidx.room.Room
import com.example.compositemasscalculator.data.entities.Calculation
import com.example.compositemasscalculator.data.room.AppDatabase

class CalculationRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "calculations_db"
    ).build()

    private val dao = db.calculationDao()

    suspend fun saveCalculation(calculation: Calculation) {
        dao.insert(calculation)
    }

    suspend fun getAllCalculations(): List<Calculation> {
        return dao.getAllCalculations()
    }
}