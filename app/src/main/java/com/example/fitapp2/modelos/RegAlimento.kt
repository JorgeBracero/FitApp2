package com.example.fitapp2.modelos

//CLASE QUE LLEVA EL REGISTRO DE LOS ALIMENTOS CONSUMIDOS
data class RegAlimento(
    val idAlimento: String,
    val momentoDia: String,
    val cantidad: Int
){
    //Constructor sin argumentos necesario para Firebase
    constructor(): this("","",1)
}
