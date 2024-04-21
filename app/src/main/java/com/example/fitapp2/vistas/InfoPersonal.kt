package com.example.fitapp2.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.modelos.Rutas

@Composable
fun InfoPersonalScreen(navController: NavController, peso: Float, altura: Float, nombre: String){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "Datos Personales")
        }

        Field("Peso",peso.toString())
        Spacer(modifier = Modifier.height(15.dp))
        Field("Altura",altura.toString())
        Spacer(modifier = Modifier.height(15.dp))
        Field("Nombre de usuario",nombre)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Field(label: String, valor: String){
    OutlinedTextField(
        value = valor,
        onValueChange = {

        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = ""
            )
        },
        readOnly = true,
        label = { Text(text = label) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            cursorColor = Color.Blue,
            textColor = Color.Black
        )
    )
}

