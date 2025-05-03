package com.example.compositemasscalculator

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.ui.viewmodels.MaterialViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var spinnerMaterials: Spinner
    private var materialsList: List<Material> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация ViewModel
        materialViewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

        // Инициализация UI элементов

        val btnAddMaterial = findViewById<Button>(R.id.btn_add_material)
        val btnSemi = findViewById<Button>(R.id.btn_semi)
        val btnProduct = findViewById<Button>(R.id.btn_product)
        val btnHistory = findViewById<Button>(R.id.btn_history)

        // Наблюдаем за списком материалов

        // Обработчики кликов (ИСПРАВЛЕННЫЕ)
        btnAddMaterial.setOnClickListener {
            val intent = Intent(this@MainActivity, AddMaterialActivity::class.java)
            startActivity(intent)
        }

        btnSemi.setOnClickListener {
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra("isSemiProduct", true)
            startActivity(intent)
        }

        btnProduct.setOnClickListener {
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra("isSemiProduct", false)
            startActivity(intent)
        }

        btnHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            materialsList.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaterials.adapter = adapter
    }
}