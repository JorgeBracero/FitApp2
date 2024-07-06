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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.isValidUrl
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarUsuariosScreen(
    navController: NavController,
    userController: UsuarioController,
    storeController: StorageController
){
    val uidActual = userController.getAuth().currentUser!!.uid
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var showClear by rememberSaveable { mutableStateOf(false) }
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }

    //Obtenemos los usuarios actuales
    userController.getListaUsuarios(uidActual, {
        usuarios = it
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buscar Usuarios",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = TextUnit(23f, TextUnitType.Sp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ){
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        showClear = it.trim().isNotEmpty()
                        if (query.isEmpty()) {
                            userController.getListaUsuarios(uidActual, {
                                usuarios = it
                            })
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    label = { Text(text = "Busca un usuario") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        cursorColor = Color.Blue
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Busqueda",
                            tint = Color.Black
                        )
                    },
                    trailingIcon = {
                        if (showClear) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar busqueda",
                                tint = Color.Black,
                                modifier = Modifier.clickable {
                                    query = ""
                                    showClear = false //Si limpia el texto lo volvemos a ocultar
                                    userController.getListaUsuarios(uidActual, {
                                        usuarios = it
                                    })
                                }
                            )
                        }
                    }
                )

                //Busqueda en tiempo real
                LaunchedEffect(query) {
                    if (query.isNotEmpty()) {
                         userController.getUsuariosBuscados(uidActual,query,{
                             usuarios = it
                         })
                        println("Usuarios actuales buscados: $usuarios")
                    }else{
                        userController.getListaUsuarios(uidActual, {
                            usuarios = it
                        })
                        println("Usuarios actuales buscados: $usuarios")
                    }
                }

                //Lista de usuarios
                if (usuarios.isNotEmpty()) {
                    //RecyclerView
                    LazyColumn(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        //Cargamos el recyclerview con la lista de usuarios
                        items(items = usuarios) { usuario ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        //Navega a la pantalla de chat con ese usuario
                                        navController.navigate(Rutas.ChatScreen.ruta + "/${usuario.uid}")
                                    }
                            ){
                                storeController.mostrarImagen(
                                    context = context,
                                    img = usuario.fotoPerfil,
                                    size = 65.dp
                                )
                                Spacer(Modifier.width(15.dp))
                                Column {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = usuario.nombreUsuario,
                                        fontSize = TextUnit(22f, TextUnitType.Sp),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}