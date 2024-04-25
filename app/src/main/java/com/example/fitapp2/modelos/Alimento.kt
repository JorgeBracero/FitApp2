package com.example.fitapp2.modelos

import com.google.gson.annotations.SerializedName

data class Alimento(
    @SerializedName("code") val idAlimento: String,
    @SerializedName("product_name") val descAlimento: String,
    @SerializedName("brands") val marcaAlimento: String,
    @SerializedName("categories") val catsAlimento: String,
    @SerializedName("product_quantity") val calAlimento: Float,
    @SerializedName("status") val estAlimento: String,
    @SerializedName("image_front_url") val imgAlimento: String,
    @SerializedName("ingredients_ids_debug") val ingredientes: List<String>
)