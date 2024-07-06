package com.example.fitapp2.modelos

data class Usuario(
    val uid: String,
    val email: String,
    val nombreUsuario: String,
    val peso: Float,
    val altura: Float,
    val sexo: String,
    val edad: Int,
    val fotoPerfil: String,
    val chats: List<Chat>
){
    // Constructor sin argumentos requerido por Firebase Realtime Database
    constructor() : this("", "",  "", 40f, 1.5f, "H",1,"", emptyList())
}