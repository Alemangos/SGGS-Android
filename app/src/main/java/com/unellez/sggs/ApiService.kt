package com.unellez.sggs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

// 1. Definimos las acciones que podemos hacer con la API
interface ApiService {
    @GET("macros/s/AKfycbx62r9gmd2ti6PKuXQKZdRjfLnc-rf8Li5ZZn-ZDsPTyrF_LtmurRAuSuwzze-GaQKq8Q/exec")
    suspend fun obtenerTramites(): List<Tramite>

    @POST("macros/s/AKfycbx62r9gmd2ti6PKuXQKZdRjfLnc-rf8Li5ZZn-ZDsPTyrF_LtmurRAuSuwzze-GaQKq8Q/exec")
    suspend fun enviarSolicitud(@Body solicitud: SolicitudRequest): RespuestaServidor

    // ESTA ES LA QUE PROBABLEMENTE FALTA:
    @GET("macros/s/AKfycbx62r9gmd2ti6PKuXQKZdRjfLnc-rf8Li5ZZn-ZDsPTyrF_LtmurRAuSuwzze-GaQKq8Q/exec")
    suspend fun obtenerHistorial(@Query("cedula") cedula: String): List<HistorialSolicitud>
}

// 2. Creamos el objeto que gestionará la conexión a internet
object RetrofitClient {
    // La URL base siempre debe terminar en barra diagonal (/)
    private const val BASE_URL = "https://script.google.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Agregamos Gson para que traduzca el JSON a nuestras Data Classes automáticamente
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}