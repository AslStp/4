package com.example.compositemasscalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compositemasscalculator.data.entities.Calculation
import java.text.SimpleDateFormat

class CalculationAdapter(
    private val calculations: List<Calculation>,
    private val dateFormat: SimpleDateFormat
) : RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder>() {

    inner class CalculationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvMam: TextView = itemView.findViewById(R.id.tvMam)
        val tvMc: TextView = itemView.findViewById(R.id.tvMc)
        val tvComponents: TextView = itemView.findViewById(R.id.tvComponents)
        val tvTotalMass: TextView = itemView.findViewById(R.id.tvTotalMass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calculation, parent, false)
        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val calculation = calculations[position]

        // Общие поля для всех типов
        holder.tvName.text = calculation.name ?: "Без названия"
        holder.tvType.text = if (calculation.type == "semi") "Полуфабрикат" else "Изделие"
        holder.tvDate.text = dateFormat.format(calculation.date)
        holder.tvTotalMass.text = "Итоговая масса: ${formatMass(calculation.totalMass)} г"

        if (calculation.type == "semi") {
            // Полный набор данных для полуфабриката
            holder.tvMam.visibility = View.VISIBLE
            holder.tvMc.visibility = View.VISIBLE
            holder.tvComponents.visibility = View.VISIBLE

            holder.tvMam.text = "Материал: ${formatMass(calculation.mam)} г"
            holder.tvMc.text = "Связующее: ${formatMass(calculation.mc)} г"
            holder.tvComponents.text = buildComponentsString(calculation)
        } else {
            // Только итоговая масса для изделия
            holder.tvMam.visibility = View.GONE
            holder.tvMc.visibility = View.GONE
            holder.tvComponents.visibility = View.GONE
        }
    }

    private fun formatMass(value: Double?): String {
        return value?.let { "%.2f".format(it) } ?: "N/A"
    }

    private fun buildComponentsString(calculation: Calculation): String {
        return buildString {
            append("Компоненты: ")
            append("A=${formatMass(calculation.componentA)} г, ")
            append("B=${formatMass(calculation.componentB)} г")
            calculation.ratioB?.let {
                append(" (100:${"%.0f".format(it)})")
            }
        }
    }

    override fun getItemCount(): Int = calculations.size
}