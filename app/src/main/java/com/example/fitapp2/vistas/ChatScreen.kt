package com.example.fitapp2.vistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.modelos.Mensaje
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    uid: String,
    userController: UsuarioController,
    storeController: StorageController
){
    val context = LocalContext.current
    val uidActual = userController.getAuth().currentUser!!.uid
    var mensaje by rememberSaveable { mutableStateOf("") }

    //Lista de mensajes
    var mensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    //Obtenemos los datos del usuario con el cual tenemos la conversacion
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    userController.obtenerDatosUsuario(uid,{
        usuario = it
    })

    //Y obtenemos los datos del usuario actual
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    userController.obtenerDatosUsuario(uidActual,{
        usuarioActual = it
    })

    //Hasta que no obtengamos los usuarios con toda su informacion, el chat no se cargara
    if(usuario != null && usuarioActual != null) {
        //Diseño Pantalla
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        storeController.mostrarImagen(
                            context = context,
                            img = usuario!!.fotoPerfil,
                            size = 50.dp
                        )
                    },
                    title = {
                        Text(
                            text = usuario!!.nombreUsuario,
                            fontSize = TextUnit(20f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically, // Añade esto para centrar verticalmente
                            horizontalArrangement = Arrangement.SpaceBetween // Cambia a SpaceBetween para un mejor ajuste
                        ) {
                            OutlinedTextField(
                                value = mensaje,
                                onValueChange = {
                                    mensaje = it
                                },
                                shape = RoundedCornerShape(5.dp),
                                placeholder = {
                                    Text(
                                        text = "Escriba un mensaje...",
                                        color = Color.White,
                                        fontSize = 14.sp // Ajusta el tamaño del texto si es necesario
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color.Gray,
                                    cursorColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f) // Usa weight para que tome el espacio disponible
                                    .padding(end = 8.dp) // Añade padding al final para separar del botón
                                    .height(56.dp)
                            )
                            Button(
                                onClick = {
                                    // Envia el mensaje al chat, siempre que este tenga contenido
                                    if (mensaje.trim().isNotEmpty()) {
                                        val nuevoMensaje = Mensaje(usuarioActual!!, mensaje, "18:00", false)
                                        mensajes = mensajes + nuevoMensaje // Actualizamos la lista de mensajes
                                        // Limpiamos el mensaje actual
                                        mensaje = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Blue
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically) // Alinea verticalmente el botón
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Enviar mensaje",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fondo3),
                    contentDescription = "Fondo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                //Conversacion
                if (mensajes.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        items(items = mensajes) { mensaje ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Card(
                                    modifier = Modifier.padding(10.dp)
                                        .wrapContentSize(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Gray,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Column {
                                        Text(
                                            text = mensaje.hora,
                                            fontSize = TextUnit(10f, TextUnitType.Sp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = mensaje.mensaje,
                                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = "Visto",
                                                tint = if (mensaje.visto) Color.Blue else Color.Black
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Spacer(Modifier.height(8.dp))
                                //Foto de perfil del usuario actual
                                storeController.mostrarImagen(
                                    context = context,
                                    img = usuarioActual!!.fotoPerfil,
                                    size = 35.dp
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡ESCRIBE TU PRIMER MENSAJE!",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}