package com.example.fitapp2.vistas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.modelos.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(navController: NavController, userController: UsuarioController){
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo4),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Restablece tu contraseña",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = TextUnit(35f, TextUnitType.Sp),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(7.dp))

            //TextField para el email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Correo",
                        tint = Color.Black
                    )
                },
                modifier = Modifier.padding(6.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(Modifier.height(7.dp))

            //Boton para enviar la peticion
            Button(
                onClick = {
                    if (isConnectedToNetwork(context)) {
                        userController.usuarioExiste(email, { usuarioExiste ->
                            if (usuarioExiste) {
                                //Si el usuario esta autenticado, restablecemos su contraseña
                                userController.restablecerContrasenia(email, { sucess, error ->
                                    if (sucess) {
                                        //Si se restablece la contraseña correctamente, navega al login
                                        navController.navigate(Rutas.LoginScreen.ruta)
                                    } else {
                                        //En caso contrario, mostramos el mensaje de error
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                })
                            } else {
                                Toast.makeText(
                                    context,
                                    "El usuario no esta autenticado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        Toast.makeText(
                            context,
                            "Esta accion requiere conexion a Internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Enviar",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}