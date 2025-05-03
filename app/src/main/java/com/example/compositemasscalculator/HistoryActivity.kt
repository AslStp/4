package com.example.compositemasscalculator

import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compositemasscalculator.data.CalculationRepository
import com.example.compositemasscalculator.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var repository: CalculationRepository
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = CalculationRepository(applicationContext)

        setupRecyclerView()
        loadCalculations()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            setHasFixedSize(true)
        }
    }

    private fun loadCalculations() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val calculations = repository.getAllCalculations()

                withContext(Dispatchers.Main) {
                    if (calculations.isNotEmpty()) {
                        binding.recyclerView.adapter = CalculationAdapter(calculations, dateFormat)
                        showContentView()
                    } else {
                        showEmptyState()
                    }
                }
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Error loading calculations", e)
                withContext(Dispatchers.Main) {
                    showErrorState()
                }
            }
        }
    }

    private fun showContentView() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyStateTextView.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateTextView.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateTextView.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
    }
}