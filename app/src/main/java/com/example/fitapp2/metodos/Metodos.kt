package com.example.fitapp2.metodos

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

//Devuelve si el usuario tiene conexion o no
fun isConnectedToNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}


//Bloqueamos el boton de retroceso cuando guarde algun alimento
@Composable
fun BloquearBotonRetroceso() {
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    DisposableEffect(Unit) {
        val callback = onBackPressedDispatcher?.let {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // No hacer nada cuando se presiona el botón de retroceso
                }
            }
        }

        callback?.let {
            it.isEnabled = true // Habilitar el callback para interceptar las pulsaciones de retroceso
            it.handleOnBackPressed() // Interceptar las pulsaciones de retroceso
            onDispose {
                it.isEnabled = false // Deshabilitar el callback al deshacer el efecto
            }
        }!!
    }
}


