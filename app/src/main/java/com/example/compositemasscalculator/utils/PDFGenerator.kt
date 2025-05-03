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
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PDFGenerator(private val context: Context) {

    fun generateProfessionalReport(calculation: Calculation): File? {
        return try {
            // Создание файла в папке Downloads
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Расчет_${calculation.name}_$timeStamp.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            // Создание PDF документа
            PdfWriter(file).use { writer ->
                PdfDocument(writer).use { pdfDocument ->
                    val document = Document(pdfDocument, PageSize.A4).apply {
                        setMargins(30f, 30f, 30f, 30f)
                    }

                    // Настройка шрифтов
                    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                    val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

                    // Заголовок
                    document.add(
                        Paragraph("ИТОГОВЫЙ РАСЧЕТ МАССЫ")
                            .setFont(boldFont)
                            .setFontSize(18f)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(20f)
                    )

                    // Основная информация
                    addKeyValue(document, "Название:", calculation.name ?: "Не указано", boldFont, normalFont)
                    addKeyValue(document, "Тип:", if (calculation.type == "semi") "Полуфабрикат" else "Изделие", boldFont, normalFont)
                    addKeyValue(document, "Дата:", SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()), boldFont, normalFont)

                    // Разделитель (исправленная версия)
                    document.add(
                        LineSeparator(
                            com.itextpdf.layout.properties.Leading(1f)
                        ).apply {
                            setStrokeColor(ColorConstants.BLACK)
                            setMarginTop(10f)
                            setMarginBottom(10f)
                        }
                    )

                    // Параметры расчета
                    if (calculation.type == "semi") {
                        addKeyValue(document, "Материал (ткань):", "%.2f г".format(calculation.mam ?: 0.0), boldFont, normalFont)
                        addKeyValue(document, "Связующее (смола):", "%.2f г".format(calculation.mc ?: 0.0), boldFont, normalFont)
                        addKeyValue(document, "Компонент A:", "%.2f г".format(calculation.componentA ?: 0.0), boldFont, normalFont)
                        addKeyValue(document, "Компонент B:", "%.2f г".format(calculation.componentB ?: 0.0), boldFont, normalFont)
                        calculation.ratioB?.let {
                            addKeyValue(document, "Соотношение:", "100:%.0f".format(it), boldFont, normalFont)
                        }
                    }

                    // Итоговая масса
                    val totalMass = calculation.totalMass ?: ((calculation.mam ?: 0.0) + (calculation.mc ?: 0.0))
                    document.add(
                        Paragraph("ИТОГОВАЯ МАССА: %.2f г".format(totalMass))
                            .setFont(boldFont)
                            .setFontSize(16f)
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

    private fun addKeyValue(
        document: Document,
        key: String,
        value: String,
        keyFont: com.itextpdf.kernel.font.PdfFont,
        valueFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(
            Paragraph()
                .add(Text("$key ").setFont(keyFont))
                .add(Text(value).setFont(valueFont))
                .setMarginBottom(8f)
        )
    }
}