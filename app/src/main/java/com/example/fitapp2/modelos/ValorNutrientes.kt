package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class ValorNutrientes(
    @SerializedName("carbohydrates") val carbohidratos: Double,
    @SerializedName("energy") val calorias: Double,
    @SerializedName("proteins") val proteinas: Double,
    @SerializedName("salt") val sal: Double,
    @SerializedName("sodium") val sodio: Double,
    @SerializedName("sugars") val azucar: Double
)
