package com.example.compositemasscalculator.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "calculations")
data class Calculation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val mam: Double? = null,
    val mc: Double? = null,
    val componentA: Double? = null,
    val componentB: Double? = null,
    val ratioB: Double? = null,
    val totalMass: Double? = null,
    val date: Date = Date()
)