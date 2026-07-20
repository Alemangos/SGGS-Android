package com.unellez.sggs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unellez.sggs.ui.theme.SGGSTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.derivedStateOf


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SGGSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Ahora llamamos a nuestro gestor de navegación en lugar de una pantalla fija
                    AppNavegacion(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var tramiteSeleccionado by remember { mutableStateOf<Tramite?>(null) }

    NavHost(navController = navController, startDestination = "lista", modifier = modifier) {

        // Ruta 1: Catálogo de trámites
        composable("lista") {
            PantallaTramites(
                onTramiteClick = { tramite ->
                    tramiteSeleccionado = tramite
                    navController.navigate("formulario")
                },

                onHistorialClick = {
                    navController.navigate("historial")
                }
            )
        }

        // Ruta 2: Formulario dinámico (se mantiene igual)
        composable("formulario") {
            tramiteSeleccionado?.let { tramite ->
                PantallaFormulario(
                    tramite = tramite,
                    onBackClick = {
                        tramiteSeleccionado = null
                        navController.popBackStack()
                    }
                )
            }
        }

        // NUEVA RUTA 3: La pantalla de historial
        composable("historial") {
            PantallaHistorial(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTramites(onTramiteClick: (Tramite) -> Unit, onHistorialClick: () -> Unit) {
    var listaTramites by remember { mutableStateOf<List<Tramite>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            listaTramites = RetrofitClient.apiService.obtenerTramites()
            cargando = false
        } catch (e: Exception) {
            mensajeError = "Error al conectar: ${e.localizedMessage}"
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trámites VIPI") },
                actions = {
                    // Botón superior derecho para ir al historial
                    TextButton(onClick = onHistorialClick) {
                        Text("Mi Historial")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                cargando -> CircularProgressIndicator()
                mensajeError.isNotEmpty() -> Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listaTramites) { tramite ->
                            TarjetaTramite(tramite = tramite, onClick = { onTramiteClick(tramite) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(onBackClick: () -> Unit) {
    var cedulaBusqueda by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf<List<HistorialSolicitud>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("← Atrás") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = cedulaBusqueda,
                onValueChange = { cedulaBusqueda = it },
                label = { Text("Ingresa tu Cédula") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (cedulaBusqueda.isNotBlank()) {
                        cargando = true
                        mensaje = ""
                        coroutineScope.launch {
                            try {
                                historial = RetrofitClient.apiService.obtenerHistorial(cedulaBusqueda)
                                if (historial.isEmpty()) {
                                    mensaje = "No se encontraron trámites para esta cédula."
                                }
                            } catch (e: Exception) {
                                mensaje = "Error de red: ${e.localizedMessage}"
                            } finally {
                                cargando = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Historial")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resultados
            when {
                cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                mensaje.isNotEmpty() -> Text(text = mensaje, color = MaterialTheme.colorScheme.primary)
                historial.isNotEmpty() -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(historial) { solicitud ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Trámite ID: ${solicitud.idTramite}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Fecha: ${solicitud.fecha}", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "Estado: ${solicitud.estado}",
                                        color = if (solicitud.estado == "Pendiente") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaTramite(tramite: Tramite, onClick: () -> Unit) {
    Card(
        // ¡Aquí está la magia! El modificador "clickable" hace que la tarjeta reaccione al toque
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tramite.nombreTramite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Departamento: ${tramite.departamento}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- NUEVA PANTALLA (Esqueleto) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(tramite: Tramite, onBackClick: () -> Unit) {
    val respuestas = remember { mutableStateMapOf<String, String>() }
    val coroutineScope = rememberCoroutineScope()
    var enviando by remember { mutableStateOf(false) }
    var mensajeResultado by remember { mutableStateOf("") }

    // NUEVA VARIABLE: Controla si mostramos la ventana de éxito
    var mostrarDialogoExito by remember { mutableStateOf(false) }

    val formularioValido by remember {
        derivedStateOf {
            val camposObligatorios = tramite.camposRequeridos.filter { it.obligatorio }
            camposObligatorios.all { campo ->
                val texto = respuestas[campo.idCampo]
                !texto.isNullOrBlank()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("← Atrás") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Trámite: ${tramite.nombreTramite}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tramite.camposRequeridos) { campo ->
                    val textoActual = respuestas[campo.idCampo] ?: ""
                    val mostrarError = campo.obligatorio && textoActual.isBlank()

                    OutlinedTextField(
                        value = textoActual,
                        onValueChange = { nuevoTexto -> respuestas[campo.idCampo] = nuevoTexto },
                        label = { Text(campo.etiqueta + if (campo.obligatorio) " *" else "") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = campo.tipo == "texto",
                        isError = mostrarError,
                        supportingText = {
                            if (mostrarError) {
                                Text("Este campo es obligatorio")
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    enviando = true
                    mensajeResultado = ""
                    coroutineScope.launch {
                        try {
                            val cedula = respuestas["cedula"] ?: "Sin Cédula"
                            val solicitud = SolicitudRequest(
                                cedulaUsuario = cedula,
                                idTramite = tramite.idTramite,
                                respuestasFormulario = respuestas.toMap()
                            )
                            val respuesta = RetrofitClient.apiService.enviarSolicitud(solicitud)

                            // LÓGICA NUEVA: Si el servidor responde con éxito, abrimos la ventana
                            if (respuesta.status == "success") {
                                mostrarDialogoExito = true
                            } else {
                                mensajeResultado = respuesta.mensaje
                            }

                        } catch (e: Exception) {
                            mensajeResultado = "Error al enviar: ${e.localizedMessage}"
                        } finally {
                            enviando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !enviando && formularioValido
            ) {
                if (enviando) {
                    Text("Enviando...")
                } else {
                    Text("Enviar Solicitud")
                }
            }

            if (mensajeResultado.isNotEmpty()) {
                Text(
                    text = mensajeResultado,
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- NUEVO: LA VENTANA EMERGENTE (ALERT DIALOG) ---
        if (mostrarDialogoExito) {
            AlertDialog(
                onDismissRequest = { /* No hacemos nada para obligar a presionar Entendido */ },
                title = {
                    Text(text = "¡Solicitud Exitosa!", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Tu solicitud para ${tramite.nombreTramite} ha sido enviada correctamente al sistema de la universidad.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarDialogoExito = false
                            respuestas.clear() // Limpiamos la cédula y otros datos por seguridad
                            onBackClick() // Ejecutamos la acción de retroceder al menú principal
                        }
                    ) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}