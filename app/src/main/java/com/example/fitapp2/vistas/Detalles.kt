package com.example.fitapp2.vistas

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.fitapp2.modelos.Alimento

@Composable
fun DetallesScreen(navController: NavController, alimento: Alimento){
    Text(text = "Hola alimento: ${alimento.descAlimento}")
}