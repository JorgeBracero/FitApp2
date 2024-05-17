package com.example.fitapp2.modelos

import com.example.fitapp2.metodos.obtenerFechaDelSistema

//CLASE QUE LLEVA EL REGISTRO DE LOS ALIMENTOS CONSUMIDOS
data class RegAlimento(
    val idAlimento: String,
    val email: String,
    val momentoDia: String,
    val fecha: String = obtenerFechaDelSistema(),
    val cantidad: Int
){
    //Constructor sin argumentos necesario para Firebase
    constructor(): this(idAlimento = "",email = "",momentoDia = "", cantidad = 1)
}
