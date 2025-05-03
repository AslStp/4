package com.example.compositemasscalculator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.compositemasscalculator.data.entities.Calculation
import com.example.compositemasscalculator.data.CalculationRepository
import com.example.compositemasscalculator.utils.PDFGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var repository: CalculationRepository
    private lateinit var pdfGenerator: PDFGenerator
    private val decimalFormat = DecimalFormat("#.##")
    private var currentCalculation: Calculation? = null

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) exportCurrentToPdf()
        else showPermissionDeniedMessage()
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            Environment.isExternalStorageManager()) {
            exportCurrentToPdf()
        } else {
            showPermissionDeniedMessage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        repository = CalculationRepository(applicationContext)
        pdfGenerator = PDFGenerator(this)

        val itemName = intent.getStringExtra("itemName") ?: ""
        val isSemiProduct = intent.getBooleanExtra("isSemiProduct", false)

        displayResults(itemName, isSemiProduct)
        setupButtonListeners(itemName, isSemiProduct)
    }

    private fun displayResults(itemName: String, isSemiProduct: Boolean) {
        val tvResultDetails = findViewById<TextView>(R.id.tv_result_details)
        currentCalculation = if (isSemiProduct) createSemiProductCalculation(itemName)
        else createProductCalculation(itemName)

        tvResultDetails.text = currentCalculation?.let { formatCalculationForDisplay(it) } ?: ""
    }

    private fun createSemiProductCalculation(name: String): Calculation {
        val mam = intent.getDoubleExtra("mam", 0.0)
        val ratioB = intent.getDoubleExtra("ratio_b", 32.0)
        val mc = mam

        return Calculation(
            name = name,
            type = "semi",
            mam = mam,
            mc = mc,
            componentA = mc * 100 / (100 + ratioB),
            componentB = mc * ratioB / (100 + ratioB),
            ratioB = ratioB,
            totalMass = mam + mc
        )
    }

    private fun createProductCalculation(name: String): Calculation {
        return Calculation(
            name = name,
            type = "product",
            totalMass = intent.getDoubleExtra("totalMass", 0.0)
        )
    }

    private fun formatCalculationForDisplay(calculation: Calculation): String {
        return if (calculation.type == "semi") {
            """
                Название: ${calculation.name}
                Тип: Полуфабрикат
                
                Материал: ${decimalFormat.format(calculation.mam)} г
                Связующее: ${decimalFormat.format(calculation.mc)} г
                Соотношение: 100:${decimalFormat.format(calculation.ratioB ?: 32.0)}
                
                Компоненты:
                - A: ${decimalFormat.format(calculation.componentA)} г
                - B: ${decimalFormat.format(calculation.componentB)} г
                
                Итог: ${decimalFormat.format(calculation.totalMass)} г
            """.trimIndent()
        } else {
            """
                Название: ${calculation.name}
                Тип: Изделие
                
                Итоговая масса: ${decimalFormat.format(calculation.totalMass)} г
            """.trimIndent()
        }
    }

    private fun setupButtonListeners(itemName: String, isSemiProduct: Boolean) {
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.btn_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                saveCalculation(itemName, isSemiProduct)
            }
        }

        findViewById<Button>(R.id.btn_export_pdf).setOnClickListener {
            checkAndRequestStoragePermission()
        }
    }

    private suspend fun saveCalculation(name: String, isSemiProduct: Boolean) {
        try {
            val calculation = if (isSemiProduct) createSemiProductCalculation(name)
            else createProductCalculation(name)

            repository.saveCalculation(calculation)
            currentCalculation = calculation

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ResultActivity, "Расчет сохранен", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ResultActivity,
                    "Ошибка сохранения: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkAndRequestStoragePermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (Environment.isExternalStorageManager()) {
                    exportCurrentToPdf()
                } else {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:$packageName")
                        manageStorageLauncher.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        manageStorageLauncher.launch(intent)
                    }
                }
            }

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                exportCurrentToPdf()
            }

            else -> {
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun exportCurrentToPdf() {
        currentCalculation?.let { calculation ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val pdfFile = pdfGenerator.generateProfessionalReport(calculation)
                    withContext(Dispatchers.Main) {
                        if (pdfFile != null) {
                            Toast.makeText(
                                this@ResultActivity,
                                "PDF сохранен: ${pdfFile.absolutePath}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@ResultActivity,
                                "Ошибка при создании PDF",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ResultActivity,
                            "Ошибка: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(
                this,
                "Нет данных для экспорта",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Для экспорта PDF необходимо разрешение на доступ к хранилищу",
            Toast.LENGTH_LONG
        ).show()
    }
}