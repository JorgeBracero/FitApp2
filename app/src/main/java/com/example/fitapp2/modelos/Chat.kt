package com.example.fitapp2.modelos

data class Chat(
    val usuario1: Usuario,
    val usuario2: Usuario,
    val mensajes: List<Mensaje>
){
    constructor(): this(Usuario(),Usuario(), emptyList())
}
