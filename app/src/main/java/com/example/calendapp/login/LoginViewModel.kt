package com.example.calendapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState
    // Variables para almacenar las credenciales del usuario actual
    companion object {
        var currentUserEmail: String = ""
        var currentUserPassword: String = ""
    }
    fun onEmailChanged(newEmail: String) {
        _loginState.value = _loginState.value.copy(email = newEmail)
    }

    fun onPasswordChanged(newPassword: String) {
        _loginState.value = _loginState.value.copy(password = newPassword)
    }

    fun login() {
        viewModelScope.launch {
            try {
                _loginState.value = _loginState.value.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )

                val email = _loginState.value.email.trim()
                val password = _loginState.value.password.trim()

                if (email.isEmpty() || password.isEmpty()) {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = "Por favor ingresa email y contraseña"
                    )
                    return@launch
                }
                // Guardar las credenciales del usuario actual
                currentUserEmail = email
                currentUserPassword = password
                Log.d("LoginViewModel", "Credenciales guardadas para el usuario: $email")

                Log.d("LoginViewModel", "Intentando iniciar sesión con email: $email")
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                
                if (user != null) {
                    Log.d("LoginViewModel", "Usuario autenticado, obteniendo datos de Firestore")
                    val userDoc = db.collection("users").document(user.uid).get().await()
                    val nombre = userDoc.getString("nombre") ?: ""
                    val rol = userDoc.getString("rol") ?: ""
                    val cedula = userDoc.getString("cedula") ?: ""
                    
                    Log.d("LoginViewModel", "Datos obtenidos - Nombre: $nombre, Rol: $rol, Cédula: $cedula")
                    
                    if (rol.isEmpty()) {
                        Log.e("LoginViewModel", "Error: usuario no encontrado")
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            error = "Error: usuario no encontrado"
                        )
                        return@launch
                    }

                    if (cedula.isEmpty()) {
                        Log.e("LoginViewModel", "Error: Cédula de usuario no encontrada")
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            error = "Error: Cédula de usuario no encontrada"
                        )
                        return@launch
                    }

                    // Verificar que el rol sea válido
                    if (rol != "admin" && rol != "usuario") {
                        Log.e("LoginViewModel", "Error: Rol de usuario inválido: $rol")
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            error = "Error: Rol de usuario inválido"
                        )
                        return@launch
                    }

                    Log.d("LoginViewModel", "Login exitoso, actualizando estado")
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userName = nombre,
                        userRole = rol,
                        cedula = cedula
                    )
                    Log.d("LoginViewModel", "Estado actualizado - isSuccess: ${_loginState.value.isSuccess}, Cédula: ${_loginState.value.cedula}")
                } else {
                    Log.e("LoginViewModel", "Error: Usuario nulo después de la autenticación")
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = "Error al obtener información del usuario"
                    )
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error durante el login", e)
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = when (e) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Usuario no encontrado"
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                        else -> "Error: ${e.message}"
                    }
                )
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState()
    }
}
