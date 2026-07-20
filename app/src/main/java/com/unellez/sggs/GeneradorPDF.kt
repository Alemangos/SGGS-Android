package com.unellez.sggs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

fun generarComprobantePDF(context: Context, nombreTramite: String, cedula: String) {
    // 1. Creamos el documento PDF
    val pdfDocument = PdfDocument()

    // 2. Configuramos la página (Tamaño estándar A4 aproximado)
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val pagina = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = pagina.canvas
    val paint = Paint()

    // 3. Dibujamos los textos en el lienzo (X, Y)
    paint.textSize = 24f
    paint.isFakeBoldText = true
    canvas.drawText("UNELLEZ - VIPI", 50f, 80f, paint)

    paint.textSize = 16f
    paint.isFakeBoldText = false
    canvas.drawText("Comprobante de Solicitud de Trámite", 50f, 120f, paint)

    paint.textSize = 14f
    canvas.drawText("Trámite: $nombreTramite", 50f, 160f, paint)
    canvas.drawText("Cédula: $cedula", 50f, 190f, paint)
    canvas.drawText("Estado: PENDIENTE", 50f, 220f, paint)

    // 4. Cerramos la edición de la página
    pdfDocument.finishPage(pagina)

    // 5. Guardamos el archivo en la carpeta de Descargas del teléfono
    try {
        val directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val archivoPDF = File(directorioDescargas, "Comprobante_${cedula}.pdf")

        pdfDocument.writeTo(FileOutputStream(archivoPDF))
        // Aquí podrías agregar un Toast para avisarle al usuario que se guardó

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        pdfDocument.close()
    }
}