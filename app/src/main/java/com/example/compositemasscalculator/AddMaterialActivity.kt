package com.example.compositemasscalculator

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.databinding.ActivityAddMaterialBinding
import com.example.compositemasscalculator.ui.viewmodels.MaterialViewModel
import kotlinx.coroutines.launch

class AddMaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMaterialBinding
    private lateinit var viewModel: MaterialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MaterialViewModel::class.java]

        binding.btnSaveMaterial.setOnClickListener {
            val code = binding.etMaterialCode.text.toString().trim().uppercase()
            val name = binding.etMaterialName.text.toString().trim()
            val density = binding.etMaterialDensity.text.toString().toDoubleOrNull() ?: 0.0

            if (validateInput(code, name, density)) {
                lifecycleScope.launch {
                    checkAndSaveMaterial(code, name, density)
                }
            }
        }
    }

    private fun validateInput(code: String, name: String, density: Double): Boolean {
        return when {
            code.isEmpty() -> {
                showToast("Введите код материала")
                false
            }
            name.isEmpty() -> {
                showToast("Введите название материала")
                false
            }
            density <= 0 -> {
                showToast("Плотность должна быть больше 0")
                false
            }
            else -> true
        }
    }

    private suspend fun checkAndSaveMaterial(code: String, name: String, density: Double) {
        val exists = viewModel.isMaterialCodeExists(code)
        if (exists) {
            showToast("Материал с кодом '$code' уже существует")
        } else {
            val newMaterial = Material(code = code, name = name, density = density)
            viewModel.addMaterial(newMaterial)
            showToast("Материал сохранен")
            finish()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@AddMaterialActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}