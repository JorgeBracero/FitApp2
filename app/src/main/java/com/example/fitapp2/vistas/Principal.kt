package com.example.fitapp2.vistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.modelos.Rutas


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController, peso: Float?, altura: Float?, nombre: String?){
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(text = context.getString(R.string.txtPrincipal), color = Color.White)
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Busqueda",
                        tint = Color.White,
                        modifier = Modifier.clickable {

                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)
                                    .clickable {
                                        //Navega a la pantalla principal
                                        navController.navigate(route = Rutas.PerfilScreen.ruta + "/$peso/$altura/$nombre")
                                    }
                            )
                            Text(text = context.getString(R.string.txtPerfil))
                        }

                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)
                            )
                            Text(text = "Inicio")
                        }

                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Informes",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)

                                //Navega a Informes
                            )
                            Text(text = context.getString(R.string.txtInformes))
                        }

                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_photo_24),
                                contentDescription = "Album",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)

                                //Navega al album de fotos
                            )
                            Text(text = "Album")
                        }
                    }
                },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Aquí puedes colocar el contenido principal de tu pantalla
            Text(text = context.getString(R.string.txtCaloriasRes) + "\t\t\t0")
            Text(text = context.getString(R.string.txtCaloriasCon) + "\t\t\t0")
            Spacer(modifier = Modifier.height(10.dp))
            TarjetaDia(context.getString(R.string.txtDesayuno), R.drawable.desayuno,navController)
            TarjetaDia(context.getString(R.string.txtAlmuerzo), R.drawable.almuerzo,navController)
            TarjetaDia(context.getString(R.string.txtCena), R.drawable.cena,navController)
            Spacer(modifier = Modifier.height(20.dp))

            if(peso != null && altura != null && nombre != null){
                Text(text = "Usuario: $nombre")
                Text(text = "Peso: $peso kg")
                Text(text = "Altura: $altura m")
            }
        }
    }
}


@Composable
fun TarjetaDia(texto: String, idImg: Int,navController: NavController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                //Iriamos a la pantalla para modificar los alimentos o consultarlos de ese dia

            },
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = idImg),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = texto,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir",
                    tint = Color.Cyan,
                    modifier = Modifier.size(40.dp)
                        .clickable {
                            //Navegamos a la pantalla BuscarScreen, dependiendo del momento del dia
                            //Identificamos el momento del dia pasandole un parametro
                            navController.navigate(Rutas.BuscarScreen.ruta + "/$texto")
                        }
                )
            }
        }
    }
}