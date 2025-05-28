package com.example.calendapp.registerUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendapp.config.FirebaseConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.calendapp.login.LoginViewModel
import android.util.Log


class AddUserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun onFieldChanged(field: String, value: String) {
        _userState.value = _userState.value.copy(
            user = _userState.value.user.copy(
                cedula = if (field == "cedula") value else _userState.value.user.cedula,
                nombre = if (field == "nombre") value else _userState.value.user.nombre,
                apellido = if (field == "apellido") value else _userState.value.user.apellido,
                genero = if (field == "genero") value else _userState.value.user.genero,
                edad = if (field == "edad") value else _userState.value.user.edad,
                telefono = if (field == "telefono") value else _userState.value.user.telefono,
                rol = if (field == "rol") value else _userState.value.user.rol,
                correo = if (field == "correo") value else _userState.value.user.correo,
                contrasena = if (field == "contrasena") value else _userState.value.user.contrasena
            )
        )
    }

    fun createUser() {
        viewModelScope.launch {
            try {
                _userState.value = _userState.value.copy(
                    isLoading = true,
                    error = null
                )

                val user = _userState.value.user

                // Validaciones básicas
                if (user.cedula.isEmpty() || user.nombre.isEmpty() || user.apellido.isEmpty() ||
                    user.genero.isEmpty() || user.edad.isEmpty() || user.telefono.isEmpty() ||
                    user.rol.isEmpty() || user.correo.isEmpty() || user.contrasena.isEmpty()) {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        error = "Todos los campos son obligatorios"
                    )
                    return@launch
                }

                // Crear usuario en Firebase Auth
                val authResult = FirebaseConfig.auth.createUserWithEmailAndPassword(
                    user.correo,
                    user.contrasena
                ).await()

                val uid = authResult.user?.uid
                if (uid != null) {
                    // Guardar información adicional en Firestore
                    val userData = hashMapOf(
                        "cedula" to user.cedula,
                        "nombre" to user.nombre,
                        "apellido" to user.apellido,
                        "genero" to user.genero,
                        "edad" to user.edad,
                        "telefono" to user.telefono,
                        "rol" to user.rol,
                        "correo" to user.correo
                    )

                    FirebaseConfig.firestore
                        .collection(FirebaseConfig.USERS_COLLECTION)
                        .document(uid)
                        .set(userData)
                        .await()
                    // Volver a autenticar al admin usando las credenciales guardadas
                    if (LoginViewModel.currentUserEmail.isNotEmpty() && LoginViewModel.currentUserPassword.isNotEmpty()) {
                        try {
                            FirebaseConfig.auth.signOut()
                            FirebaseConfig.auth.signInWithEmailAndPassword(
                                LoginViewModel.currentUserEmail,
                                LoginViewModel.currentUserPassword
                            ).await()
                            Log.d("AddUserViewModel", "Admin reautenticado exitosamente")
                        } catch (e: Exception) {
                            Log.e("AddUserViewModel", "Error al reautenticar al admin", e)
                        }
                    }
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        error = "Error al crear el usuario"
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isLoading = false,
                    error = when (e) {
                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "La contraseña es muy débil"
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido"
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "El correo electrónico ya está en uso"
                        else -> "Error: ${e.message}"
                    }
                )
            }
        }
    }

    fun resetState() {
        _userState.value = UserState()
    }
} 