package com.example.fitapp2.modelos

sealed class Rutas(val ruta: String) {
    object LoginScreen: Rutas("LoginScreen")
    object PrincipalScreen: Rutas("PrincipalScreen")
    object PerfilScreen: Rutas("PerfilScreen")
    object DatosInicialesScreen: Rutas("DatosInicialesScreen")
    object BuscarScreen: Rutas("BuscarScreen")
    object InfoPersonalScreen: Rutas("InfoPersonalScreen")
    object DetallesScreen: Rutas("DetallesScreen")
    object AlimentosConsumidosScreen: Rutas("AlimentosConsumidosScreen")
    object AjustesScreen: Rutas("AjustesScreen")
    object PasswordScreen: Rutas("PasswordScreen")
}
