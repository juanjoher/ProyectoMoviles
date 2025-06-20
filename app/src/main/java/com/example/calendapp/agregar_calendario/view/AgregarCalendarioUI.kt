package com.example.calendapp.agregar_calendario.view

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.calendapp.R
import com.example.calendapp.agregar_calendario.viewmodel.AgregarCalendarioViewModel
import com.example.calendapp.config.UserViewModel
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCalendarioUI(
    navController: NavHostController,
    viewModel: AgregarCalendarioViewModel = viewModel(),
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val userState by userViewModel.userState.collectAsState()
    val usuariosSugeridos by viewModel.usuariosSugeridos.collectAsState()

    // TimePickerDialogs
    val timePickerInicio = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            viewModel.actualizarHoraInicio(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val timePickerFin = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            viewModel.actualizarHoraFin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(0xFF0A192F))
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text("Panel administrador", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                DrawerItem("Pantalla de inicio", Icons.Default.Home) { 
                    navController.navigate("administrador")
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Agregar usuario", Icons.Default.PersonAdd) { 
                    navController.navigate("agregar_usuario")
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Eliminar usuario", Icons.Default.PersonRemove) { 
                    navController.navigate("eliminar_usuario")
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Editar perfil", Icons.Default.Edit) { 
                    navController.navigate("editar_perfil")
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Cerrar sesión", Icons.Default.Close) { 
                    navController.navigate("login")
                    scope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121C2B))
        ) {
            // Fondo de color específico detrás de la imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(204.dp)
                    .background(Color(0xFF132D46))
                    .align(Alignment.TopCenter)
            )

            // Imagen de fondo encima del fondo de color
            Image(
                painter = painterResource(id = R.drawable.image3),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(204.dp)
                    .align(Alignment.TopCenter)
                    .graphicsLayer { alpha = 0.9f },
                contentScale = ContentScale.Crop
            )

            // Cabecera con el ícono de menú, nombre del usuario y campana de notificaciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono de menú y nombre de usuario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = userState.userName ?: "Usuario Temporal",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Default
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Ícono de notificaciones
                IconButton(onClick = { navController.navigate("notificaciones") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Título en la parte superior centrado
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            ) {
                Text(
                    text = "Nuevo Horario",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Contenido principal
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 220.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo: Nombre del usuario
                    OutlinedTextField(
                        value = viewModel.nombreUsuario.value,
                        onValueChange = { viewModel.actualizarNombreUsuario(it) },
                        label = { Text("Escribe el nombre del usuario", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A3C53),
                            unfocusedContainerColor = Color(0xFF2A3C53),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0077FF),
                            unfocusedIndicatorColor = Color(0xFF0077FF),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            disabledTextColor = Color.White,
                            disabledContainerColor = Color(0xFF2A3C53),
                            disabledIndicatorColor = Color(0xFF0077FF)
                        )
                    )
                    if (viewModel.mostrarErrores.value && viewModel.nombreUsuario.value.isBlank()) {
                        Text(
                            text = "El nombre del usuario es obligatorio",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo: Usuarios asignados
                    OutlinedTextField(
                        value = viewModel.usuariosSeleccionados.value.joinToString(", ") { "${it.nombre} ${it.apellido}" },
                        onValueChange = {},
                        label = { Text("Usuarios asignados", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.mostrarVentanaAsignados.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Ver usuarios asignados",
                                    tint = Color.White
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A3C53),
                            unfocusedContainerColor = Color(0xFF2A3C53),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0077FF),
                            unfocusedIndicatorColor = Color(0xFF0077FF),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            disabledTextColor = Color.White,
                            disabledContainerColor = Color(0xFF2A3C53),
                            disabledIndicatorColor = Color(0xFF0077FF)
                        )
                    )
                    if (viewModel.mostrarErrores.value && viewModel.usuariosSeleccionados.value.isEmpty()) {
                        Text(
                            text = "Debes seleccionar al menos un usuario",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Etiqueta: Franja horaria
                    Text(
                        text = "Ingresa la franja horaria",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campos: Inicio y Fin
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { timePickerInicio.show() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2A3C53),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color(0xFF0077FF))
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text(viewModel.horaInicio.value, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { timePickerFin.show() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2A3C53),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color(0xFF0077FF))
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text(viewModel.horaFin.value, color = Color.White)
                        }
                    }
                    if (viewModel.mostrarErrores.value && (viewModel.horaInicio.value == "00:00" || viewModel.horaFin.value == "00:00")) {
                        Text(
                            text = "Debes seleccionar una hora de inicio y fin",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo: Fecha
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.mostrarCalendario.value = true }
                    ) {
                        OutlinedTextField(
                            value = viewModel.fechasSeleccionadas.value.joinToString(", "),
                            onValueChange = { },
                            label = { Text("Seleccionar fecha", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF2A3C53),
                                unfocusedContainerColor = Color(0xFF2A3C53),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color(0xFF0077FF),
                                unfocusedIndicatorColor = Color(0xFF0077FF),
                                disabledTextColor = Color.White,
                                disabledContainerColor = Color(0xFF2A3C53),
                                disabledIndicatorColor = Color(0xFF0077FF)
                            )
                        )
                    }
                    if (viewModel.mostrarErrores.value && viewModel.fechasSeleccionadas.value.isEmpty()) {
                        Text(
                            text = "Debes seleccionar al menos una fecha",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo: Ubicación
                    OutlinedTextField(
                        value = viewModel.ubicacion.value,
                        onValueChange = { viewModel.actualizarUbicacion(it) },
                        label = { Text("Ubicación", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A3C53),
                            unfocusedContainerColor = Color(0xFF2A3C53),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0077FF),
                            unfocusedIndicatorColor = Color(0xFF0077FF),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    if (viewModel.mostrarErrores.value && viewModel.ubicacion.value.isBlank()) {
                        Text(
                            text = "La ubicación es obligatoria",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo: Descripción
                    OutlinedTextField(
                        value = viewModel.descripcion.value,
                        onValueChange = { viewModel.actualizarDescripcion(it) },
                        label = { Text("Descripción", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A3C53),
                            unfocusedContainerColor = Color(0xFF2A3C53),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0077FF),
                            unfocusedIndicatorColor = Color(0xFF0077FF),
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    if (viewModel.mostrarErrores.value && viewModel.descripcion.value.isBlank()) {
                        Text(
                            text = "La descripción es obligatoria",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botones: Guardar y Cancelar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón Cancelar
                        Button(
                            onClick = {
                                viewModel.limpiarCampos()
                                viewModel.limpiarMensajes()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Se canceló con éxito",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A3C53)
                            )
                        ) {
                            Text("Cancelar", color = Color.White)
                        }

                        // Botón Guardar
                        Button(
                            onClick = {
                                viewModel.guardarCalendario()
                                scope.launch {
                                    viewModel.mensajeExito.value?.let { mensaje ->
                                        snackbarHostState.showSnackbar(
                                            message = mensaje,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0A446D)
                            )
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    }

                    // Mostrar mensajes de error o éxito
                    viewModel.mensajeError.value?.let { mensaje ->
                        Text(
                            text = mensaje,
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    viewModel.mensajeExito.value?.let { mensaje ->
                        Text(
                            text = mensaje,
                            color = Color.Green,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Mostrar sugerencias si hay usuarios sugeridos
                if (viewModel.mostrarVentanaSugerencias.value && usuariosSugeridos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = 300.dp)
                            .zIndex(1f)
                    ) {
                        VentanaSugerenciasUsuarios(
                            usuarios = usuariosSugeridos,
                            onSeleccionar = { index -> viewModel.seleccionarUsuario(index) },
                            onDismiss = { viewModel.mostrarVentanaSugerencias.value = false },
                            viewModel = viewModel
                        )
                    }
                }

                // Ventana de calendario
                if (viewModel.mostrarCalendario.value) {
                    CalendarioDialog(
                        fechasSeleccionadas = viewModel.fechasSeleccionadas.value,
                        onSeleccionarFecha = { fecha -> viewModel.seleccionarFecha(fecha) },
                        onDismiss = { viewModel.mostrarCalendario.value = false }
                    )
                }
            }

            // Ventana de usuarios asignados
            if (viewModel.mostrarVentanaAsignados.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    VentanaUsuariosAsignados(
                            usuarios = viewModel.usuariosSeleccionados.value,
                        onEliminar = { index -> viewModel.eliminarUsuario(index) },
                        onCerrar = { viewModel.mostrarVentanaAsignados.value = false }
                    )
                }
            }

            // Snackbar
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF0A446D),
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
} 