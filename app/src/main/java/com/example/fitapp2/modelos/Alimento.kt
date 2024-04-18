package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class Alimento(
    @SerializedName("code") val idAlimento: String,
    @SerializedName("generic_name") val descAlimento: String,
    @SerializedName("brands") val marcaAlimento: String,
    @SerializedName("categories_tags") val catsAlimento: List<String>,
    @SerializedName("energy-kcal") val calAlimento: Float,
    @SerializedName("status") val estAlimento: String,
    @SerializedName("image_front_url") val imgAlimento: String
)