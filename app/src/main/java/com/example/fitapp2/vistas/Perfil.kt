package com.example.fitapp2.vistas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.fitapp2.R
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario

//VISTA DE PERFIL DE USUARIO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, userController: UsuarioController, storeController: StorageController){
    val context = LocalContext.current
    val idUser by remember { mutableStateOf (userController.getAuth().currentUser!!.uid)}
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var showGaleria by remember { mutableStateOf(false) }

    //Galeria variables
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }

    //Obtenemos los datos del usuario actual siempre que exista
    if(idUser != null){
        LaunchedEffect(Unit) {
            userController.obtenerDatosUsuario(idUser, { userBD ->
                usuarioActual = userBD
            })
        }
    }

    if(usuarioActual != null) {
        //Obtener imagen del usuario
        val getImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                val fileName = storeController.getFileName(context.contentResolver, it)
                val nombreImg = fileName!!.substring(0, fileName!!.lastIndexOf('.'))

                //Borramos la imagen anterior que tuviese, a menos que sea la predeterminada
                //Actualizamos el campo foto perfil del usuario con el nuevo nombre de la imagen
                userController.updFotoPerfil(usuarioActual!!, nombreImg!!)

                nombreImg?.let { name ->
                    storeController.subirImagenGaleria(context,it, name) { url ->
                        uploadedImageUrl = url
                    }
                }
            }
        }


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = context.getString(R.string.txtPerfil),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = TextUnit(23f, TextUnitType.Sp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
                            contentDescription = "Editar foto",
                            tint = Color.White,
                            modifier = Modifier.clickable {
                                showGaleria = true
                            }.size(35.dp)
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            Column(verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally){
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = Color.Cyan,
                                    modifier = Modifier.size(45.dp)
                                )
                                Text(
                                    text = context.getString(R.string.txtPerfil),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Cyan
                                )
                            }

                            Column(verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally){
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Inicio",
                                    tint = Color.White,
                                    modifier = Modifier.size(45.dp)
                                        .clickable {
                                            navController.navigate(Rutas.PrincipalScreen.ruta)
                                        }
                                )
                                Text(
                                    text = "Inicio",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column(verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally){
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Informes",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clickable {
                                            //Navega a Informes
                                            navController.navigate(Rutas.InformesScreen.ruta)
                                        }
                                )
                                Text(
                                    text = context.getString(R.string.txtInformes),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    },
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            }
        ) { innerPadding ->
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
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Aquí puedes colocar el contenido principal de tu pantalla
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            //Descargamos la imagen del usuario actual y la mostramos
                            storeController.mostrarImagen(context, usuarioActual!!.fotoPerfil, 100.dp)
                            Spacer(Modifier.width(12.dp))
                            if(showGaleria) {
                                Button(
                                    onClick = {
                                        //Mostramos la galeria
                                        showGaleria = false
                                        //Mostramos la galeria del movil
                                        getImage.launch("image/*")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Cyan,
                                        contentColor = Color.White
                                    )
                                ){
                                    Text(text = context.getString(R.string.txtEdFoto))
                                }
                            }
                        }
                    }


                    TarjetaPersonal(context.getString(R.string.txtPeso), navController)
                    Spacer(Modifier.height(10.dp))
                    TarjetaPersonal("Informacion personal", navController)

                    Spacer(Modifier.height(80.dp))

                    //Boton de cierre de sesion
                    Button(
                        onClick = {
                            //Si disponemos de conexion, podemos cerrar la sesion
                            if (isConnectedToNetwork(context)) {
                                userController.cerrarSesion() //Cerramos sesion
                                //Al cerrar sesion, navega al login
                                navController.navigate(Rutas.LoginScreen.ruta)
                            } else {
                                Toast.makeText(context, "Esta accion requiere conexion a Internet", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Cerrar sesion",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    //Si el usuario elige una imagen, navega a la ruta principal
                    if (selectedImageUri != null) {
                        navController.navigate(Rutas.PrincipalScreen.ruta)
                    }
                }
            }
        }
    }
}


@Composable
fun TarjetaPersonal(titulo: String,navController: NavController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (titulo.equals("Informacion personal")) {
                    navController.navigate(Rutas.InfoPersonalScreen.ruta) //Navega a la pantalla de info personal
                }else{
                    navController.navigate(Rutas.PesoScreen.ruta) //Navega a la pantalla de Mi Peso
                }
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Button(
                onClick = {
                    if (titulo.equals("Informacion personal")) {
                        navController.navigate(Rutas.InfoPersonalScreen.ruta) //Navega a la pantalla de info personal
                    }else{
                        navController.navigate(Rutas.PesoScreen.ruta) //Navega a la pantalla de Mi Peso
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp)
            ){
                if(titulo.equals("Informacion personal")){
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }else{
                    Image(
                        painter = painterResource(id = R.drawable.peso),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.width(50.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Flechita",
                tint = Color.White
            )
        }
    }
}