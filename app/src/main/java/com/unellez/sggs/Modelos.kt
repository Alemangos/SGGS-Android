package com.unellez.sggs

import com.google.gson.annotations.SerializedName

// Modelo principal que representa un trámite y su configuración
data class Tramite(
    @SerializedName("id_tramite")
    val idTramite: Int,

    @SerializedName("departamento")
    val departamento: String,

    @SerializedName("nombre_tramite")
    val nombreTramite: String,

    @SerializedName("campos_requeridos")
    val camposRequeridos: List<CampoRequerido>
)

// Modelo secundario que representa cada campo del formulario dinámico
data class CampoRequerido(
    @SerializedName("id_campo")
    val idCampo: String,

    @SerializedName("tipo")
    val tipo: String, // ej: "texto", "area_texto", "seleccion"

    @SerializedName("etiqueta")
    val etiqueta: String,

    @SerializedName("obligatorio")
    val obligatorio: Boolean
)

// Este es el paquete de datos que enviaremos a Google Sheets
data class SolicitudRequest(
    @SerializedName("cedula_usuario")
    val cedulaUsuario: String,

    @SerializedName("id_tramite")
    val idTramite: Int,

    @SerializedName("respuestas_formulario")
    val respuestasFormulario: Map<String, String>
)

// Esta es la respuesta que esperamos recibir del servidor (Éxito o Error)
data class RespuestaServidor(
    @SerializedName("status")
    val status: String,

    @SerializedName("mensaje")
    val mensaje: String
)

// Molde para leer el historial de solicitudes enviadas
data class HistorialSolicitud(
    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("id_tramite")
    val idTramite: Int,

    @SerializedName("estado")
    val estado: String
)