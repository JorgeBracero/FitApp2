package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class Alimento(
    @SerializedName("code") val idAlimento: String,
    @SerializedName("generic_name") val descAlimento: String,
    @SerializedName("brands") val marcaAlimento: String,
    @SerializedName("categories") val catsAlimento: String,
    @SerializedName("energy-kcal") val calAlimento: Int,
    @SerializedName("status") val estAlimento: String,
    @SerializedName("image_front_url") val imgAlimento: String,
    @SerializedName("ingredients_ids_debug") val ingredientes: List<String>
)