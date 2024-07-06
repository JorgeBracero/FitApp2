package com.example.fitapp2.modelos

data class Mensaje(
    val usuario: Usuario,
    val mensaje: String,
    val hora: String,
    val visto: Boolean
){
    constructor(): this(Usuario(),"","",false)
}
