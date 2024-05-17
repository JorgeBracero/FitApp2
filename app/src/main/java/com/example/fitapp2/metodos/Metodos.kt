package com.example.fitapp2.metodos

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.modelos.Usuario
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDate
import kotlin.math.pow
import kotlin.math.roundToInt

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

//Obtener la fecha del sistema
fun obtenerFechaDelSistema(): String {
    val fechaActual = LocalDate.now()
    return fechaActual.toString()
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


fun getFloat(txt: String): Float {
    val txtFormateado = txt.replace(",",".")
    val valor = txtFormateado.toFloat()
    return valor
}

fun validarDatos(pesoTexto: String, alturaTexto: String, edadTexto: String, nombreUsuario: String): Boolean {
    //Navega con los parametros ingresados por el usuario a la pantalla principal
    //Controlamos que todos los parametros sean correctos
    var datosCorrectos = true
    try{
        val pesoUser = getFloat(pesoTexto)
        val alturaUser = getFloat(alturaTexto)
        val edadUser = edadTexto.toInt()

        if(edadUser > 0 && edadUser <= 120 && pesoUser > 0 && pesoUser < 400 && alturaUser > 0 && alturaUser < 3 && nombreUsuario.trim().isNotEmpty()){
            println("REGISTRO VALIDADO CORRECTAMENTE")
        }else{
            datosCorrectos = false
        }
    } catch (e: NumberFormatException){
        datosCorrectos = false
    }
    return datosCorrectos
}

//Redondea floats
fun Float.round(decimals: Int): Float {
    val factor = 10.0.pow(decimals.toDouble()).toFloat()
    return (this * factor).roundToInt() / factor
}

//Validar una URL
fun isValidUrl(urlString: String): Boolean {
    return try {
        URL(urlString)
        true
    } catch (e: MalformedURLException) {
        false
    }
}

//Indice de masa corporal, para saber en que categoria se encuentra esa persona
fun calcularIMC(usuario: Usuario): Float {
    return (usuario.peso/(usuario.altura * usuario.altura)).round(1)
}

//Tasa metabolica basal, calcula las calorías diarias necesarias para que el cuerpo funcione con normalidad segun la persona
fun calcularTMB(usuario: Usuario): Double {
    var tmb = 66.5 + (usuario.peso * 13.8) + (5 * (usuario.altura * 100)) - (6.8 * usuario.edad)
    if(usuario.sexo == "M"){
        tmb = 665 + (usuario.peso * 9.6) + (1.8 * (usuario.altura * 100)) - (4.7 * usuario.edad)
    }
    return tmb
}

//Nos devuelve la categoria segun la clasificacion del IMC, a la cual pertenece una persona
fun categoriaIMC(usuario: Usuario): String {
    var categoria = "Infrapeso"
    val imc = calcularIMC(usuario) //sacamos el IMC de ese usuario
    if(imc >= 18.5 && imc <= 24.9){
        categoria = "Peso normal"
    }else{
        if(imc >= 25 && imc <= 29.9){
            categoria = "Sobrepeso"
        }else{
            if(imc >= 30){
                categoria = "Obesidad"
            }
        }
    }
    return categoria
}

//Calorias diarias que debe tomar una persona para bajar de peso en funcion de su sexo, peso, altura, edad...
fun calcularCaloriasDiarias(usuario: Usuario): Int {
    return (calcularTMB(usuario) * 1.2).roundToInt() //Damos por hecho que la persona realiza algo de actividad fisica, pero con poca frecuencia
    //aunque esta no la controlamos en la app
}

//Calorias diarias consumidas de una persona
fun calcularCaloriasDiariasConsumidas(
    usuario: Usuario,
    regAlimentoController: RegAlimentoController,
    alimentoController: AlimentoController,
    callback: (Int) -> Unit
) {
    regAlimentoController.calcularCalorias(usuario.email, alimentoController, { calorias ->
        callback(calorias)
    })
}


