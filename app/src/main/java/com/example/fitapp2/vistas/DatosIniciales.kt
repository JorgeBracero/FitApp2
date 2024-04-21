package com.example.fitapp2.vistas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.modelos.Rutas


@Composable
fun DatosInicialesScreen(navController: NavController, usuario: String){
    var pesoTexto by rememberSaveable { mutableStateOf("") }
    var alturaTexto by rememberSaveable { mutableStateOf("") }
    var nombreUsuario by rememberSaveable { mutableStateOf("Invitado") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Introduzca sus datos iniciales, para poder seguir con el login")
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            pesoTexto = Campo("Peso base")
            Text(text = "kg")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            alturaTexto = Campo("Altura")
            Text(text = "m")
        }
        Spacer(modifier = Modifier.height(10.dp))
        //Si es un usuario autenticado, podra tener un nombre de usuario propio, sino sera un Invitado
        if(usuario.equals("user")){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                nombreUsuario = Campo("Nombre de usuario")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        IconButton(
            onClick = {
                //Navega con los parametros ingresados por el usuario a la pantalla principal
                //Controlamos que todos los parametros sean correctos
                try{
                    pesoTexto = pesoTexto.replace(",",".")
                    alturaTexto = alturaTexto.replace(",",".")
                    val pesoUser = pesoTexto.toFloat()
                    val alturaUser = alturaTexto.toFloat()

                    if(pesoUser > 0 && pesoUser < 400 && alturaUser > 0 && alturaUser < 3 && nombreUsuario.trim().isNotEmpty()){
                        navController.navigate(Rutas.PrincipalScreen.ruta + "/$pesoUser/$alturaUser/$nombreUsuario") //Navega a la pantalla principal
                    }else{
                        Toast.makeText(context, "Peso, altura o nombre incorrectos",Toast.LENGTH_SHORT).show()
                    }

                } catch (e: NumberFormatException){
                    Toast.makeText(context, e.message,Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sharp_arrow_circle_right_24),
                contentDescription = "Flecha principal",
                tint = Color.Cyan
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Campo(placeholder: String): String {
    var text by rememberSaveable { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text(text = placeholder) }
    )
    return text
}