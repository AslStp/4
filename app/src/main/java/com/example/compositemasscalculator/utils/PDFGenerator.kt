package com.example.compositemasscalculator.utils

import android.content.Context
import android.os.Environment
import com.example.compositemasscalculator.data.entities.Calculation
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PDFGenerator(private val context: Context) {

    fun generateReport(calculation: Calculation): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "CompositeReport_${calculation.name}_$timeStamp.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            PdfWriter(file).use { writer ->
                PdfDocument(writer).use { pdfDocument ->
                    val document = Document(pdfDocument, PageSize.A4)
                    document.setMargins(40f, 40f, 40f, 40f)

                    // Шрифты
                    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                    val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

                    // Заголовок
                    document.add(
                        Paragraph("ИТОГОВЫЙ РАСЧЁТ МАССЫ")
                            .setFont(boldFont)
                            .setFontSize(18f)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(20f)
                    )

                    // Таблица с параметрами
                    val table = Table(2)
                        .setWidth(400f)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)

                    // Добавление строк
                    addTableRow(table, "Название:", calculation.name ?: "N/A", boldFont, normalFont)
                    addTableRow(table, "Тип:", if (calculation.type == "semi") "Полуфабрикат" else "Изделие", boldFont, normalFont)

                    if (calculation.type == "semi") {
                        addTableRow(table, "Материал:", formatMass(calculation.mam), boldFont, normalFont)
                        addTableRow(table, "Связующее:", formatMass(calculation.mc), boldFont, normalFont)
                        addTableRow(table, "Компонент A:", formatMass(calculation.componentA), boldFont, normalFont)
                        addTableRow(table, "Компонент B:", formatMass(calculation.componentB), boldFont, normalFont)
                        calculation.ratioB?.let {
                            addTableRow(table, "Соотношение:", "100:${"%.0f".format(it)}", boldFont, normalFont)
                        }
                    }

                    document.add(table)

                    // Итог
                    val totalMass = calculation.totalMass ?: ((calculation.mam ?: 0.0) + (calculation.mc ?: 0.0))
                    document.add(
                        Paragraph("ИТОГОВАЯ МАССА: ${"%.2f".format(totalMass)} г")
                            .setFont(boldFont)
                            .setFontSize(14f)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(20f)
                    )

                    document.close()
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addTableRow(
        table: Table,
        label: String,
        value: String,
        labelFont: com.itextpdf.kernel.font.PdfFont,
        valueFont: com.itextpdf.kernel.font.PdfFont
    ) {
        table.addCell(
            Cell().add(Paragraph(label).setFont(labelFont))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(8f)
        )
        table.addCell(
            Cell().add(Paragraph(value).setFont(valueFont))
                .setPadding(8f)
        )
    }

    private fun formatMass(value: Double?): String {
        return if (value != null) "%.2f г".format(value) else "N/A"
    }
}