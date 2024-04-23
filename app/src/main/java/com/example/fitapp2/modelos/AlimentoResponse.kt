package com.example.fitapp2.modelos

import androidx.compose.foundation.pager.PageSize
import com.google.gson.annotations.SerializedName

data class AlimentoResponse(
    @SerializedName("product") val alimento: Alimento?
)
