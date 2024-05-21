package com.example.fitapp2.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.calcularIMC
import com.example.fitapp2.metodos.calcularTMB
import com.example.fitapp2.metodos.categoriaIMC
import com.example.fitapp2.modelos.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesoScreen(navController: NavController, userController: UsuarioController) {
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    val idUser = userController.getAuth().currentUser!!.uid

    //Obtenemos los datos del usuario
    LaunchedEffect(Unit) {
        userController.obtenerDatosUsuario(idUser, { userBD ->
            usuarioActual = userBD
        })
    }

    if(usuarioActual != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = "Informacion sobre el peso", color = Color.White)
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navegar hacia atras",
                            tint = Color.White,
                            modifier = Modifier.clickable {
                                navController.navigateUp()
                            }
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Peso Actual: ${usuarioActual!!.peso}")
                Text(text = "IMC: ${calcularIMC(usuarioActual!!)}")
                Text(text = "TMB: ${calcularTMB(usuarioActual!!)}")
                Text(text = "Categoria IMC: ${categoriaIMC(usuarioActual!!)}")
            }
        }
    }
}