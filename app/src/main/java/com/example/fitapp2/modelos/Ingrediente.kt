package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Ingrediente(
    @SerializedName("id") val idIng: String,
    @SerializedName("percent_estimate") val porcentaje: Double
): Serializable {
    //Constructor firebase
    constructor() : this("", 3.0)
}
