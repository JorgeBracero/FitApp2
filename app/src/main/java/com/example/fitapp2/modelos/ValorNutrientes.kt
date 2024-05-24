package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class ValorNutrientes(
    @SerializedName("carbohydrates") val carbohidratos: Double,
    @SerializedName("energy-kcal_100g") val calorias: Double,
    @SerializedName("proteins") val proteinas: Double,
    @SerializedName("fat") val grasas: Double,
    @SerializedName("salt") val sal: Double,
    @SerializedName("sodium") val sodio: Double,
    @SerializedName("sugars") val azucar: Double
) {
    //Constructor firebase
    constructor() : this(3.0, 3.0,3.0,3.0,3.0,3.0,3.0)
}
