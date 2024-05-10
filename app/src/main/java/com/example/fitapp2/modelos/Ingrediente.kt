package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class Ingrediente(
    @SerializedName("id") val idIng: String,
    @SerializedName("percent_estimate") val porcentaje: Double
) {
    //Constructor firebase
    constructor() : this("", 3.0)
}
