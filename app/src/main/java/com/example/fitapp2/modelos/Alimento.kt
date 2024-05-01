package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class Alimento(
    @SerializedName("code") val idAlimento: String,
    @SerializedName("product_name") val descAlimento: String,
    @SerializedName("brands") val marcaAlimento: String,
    @SerializedName("categories") val catsAlimento: String,
    @SerializedName("nutriments") val nutrientes: ValorNutrientes,
    @SerializedName("ingredients") val ingredientes: List<Ingrediente>,
    @SerializedName("image_front_url") var imgAlimento: String
){
    // Constructor sin argumentos requerido por Firebase Realtime Database
    constructor() : this("", "", "", "", ValorNutrientes(), listOf(Ingrediente()), "")
}