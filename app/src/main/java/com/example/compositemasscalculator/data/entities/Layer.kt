package com.example.compositemasscalculator.data.entities

import com.example.compositemasscalculator.data.entities.Material
data class Layer(
    val material: Material,
    val area: Double,
    val layersCount: Int
)