package com.example.compositemasscalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.ui.viewmodels.MaterialViewModel

class InputActivity : AppCompatActivity() {

    private lateinit var spinnerMaterial: Spinner
    private lateinit var etArea: EditText
    private lateinit var etLayers: EditText
    private lateinit var etComponentB: EditText
    private lateinit var btnAddLayer: Button
    private lateinit var btnCalculate: Button
    private lateinit var componentBContainer: View
    private lateinit var viewModel: MaterialViewModel
    private val layers = mutableListOf<Layer>()
    private var ratioB: Double = 32.0
    private var isSemiProduct: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        isSemiProduct = intent.getBooleanExtra("isSemiProduct", false)
        viewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

        initViews()
        setupMaterialSpinner()
        setupButtons()
        setupComponentBVisibility()
    }

    private fun initViews() {
        spinnerMaterial = findViewById(R.id.spinner_material)
        etArea = findViewById(R.id.et_area)
        etLayers = findViewById(R.id.et_layers)
        etComponentB = findViewById(R.id.et_component_b)
        componentBContainer = findViewById(R.id.component_b_container)
        btnAddLayer = findViewById(R.id.btn_add_layer)
        btnCalculate = findViewById(R.id.btn_calculate)
        btnCalculate.isEnabled = false
        btnCalculate.alpha = 0.5f
    }

    private fun setupComponentBVisibility() {
        componentBContainer.visibility = if (isSemiProduct) View.VISIBLE else View.GONE
    }

    private fun setupMaterialSpinner() {
        viewModel.materialsLiveData.observe(this) { materials ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                materials.map { "${it.name} (${it.density} г/м²)" }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMaterial.adapter = adapter
        }
    }

    private fun setupButtons() {
        btnAddLayer.setOnClickListener {
            try {
                val selectedMaterial = viewModel.materialsLiveData.value?.get(spinnerMaterial.selectedItemPosition)
                val area = etArea.text.toString().toDoubleOrNull()
                val layersCount = etLayers.text.toString().toIntOrNull()

                if (area == null || layersCount == null) {
                    Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (isSemiProduct) {
                    ratioB = etComponentB.text.toString().toDoubleOrNull() ?: 32.0
                    if (ratioB <= 0) {
                        Toast.makeText(this, "Значение B должно быть больше 0", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                selectedMaterial?.let { material ->
                    layers.add(Layer(material, area, layersCount))
                    Toast.makeText(this, "Слой добавлен!", Toast.LENGTH_SHORT).show()
                    btnCalculate.isEnabled = true
                    btnCalculate.alpha = 1f
                    etArea.text.clear()
                    etLayers.text.clear()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }

        btnCalculate.setOnClickListener {
            try {
                val itemName = findViewById<EditText>(R.id.et_item_name)?.text?.toString()
                if (itemName.isNullOrBlank()) {
                    Toast.makeText(this, "Введите название детали", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (layers.isEmpty()) {
                    Toast.makeText(this, "Добавьте хотя бы один слой", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val mam = layers.sumOf { it.area * it.layersCount * it.material.density }
                val mc = if (isSemiProduct) mam else mam * 0.4 / 0.6

                startActivity(Intent(this, ResultActivity::class.java).apply {
                    putExtra("itemName", itemName)
                    putExtra("isSemiProduct", isSemiProduct)
                    putExtra("mam", mam)

                    if (isSemiProduct) {
                        putExtra("ratio_b", etComponentB.text.toString().toDoubleOrNull() ?: 32.0)
                    } else {
                        putExtra("totalMass", mam + mc) // Изменено с mc на totalMass
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка расчета: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

data class Layer(
    val material: Material,
    val area: Double,
    val layersCount: Int
)