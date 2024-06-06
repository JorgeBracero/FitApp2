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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.fitapp2.R
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario

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


    if(idUser != null){
        //Obtenemos los datos del usuario actual
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
                            Text(text = context.getString(R.string.txtPerfil))
                        }
                    },
                    navigationIcon = {
                        //Descargamos la imagen del usuario actual y la mostramos
                        storeController.mostrarImagen(context, usuarioActual!!.fotoPerfil, 55.dp)
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        if(showGaleria) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_photo_24),
                                contentDescription = "Galeria",
                                tint = Color.White,
                                modifier = Modifier.clickable {
                                    //Mostramos la galeria
                                    getImage.launch("image/*")
                                    showGaleria = false
                                }.size(45.dp)
                            )
                        }
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
                                    tint = Color.White,
                                    modifier = Modifier.size(45.dp)
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
                                        .clickable {
                                            navController.navigate(Rutas.PrincipalScreen.ruta)
                                        }
                                )
                                Text(text = "Inicio")
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
                                            navController.navigate(Rutas.InformesScreen.ruta)
                                        }

                                    //Navega a Informes
                                )
                                Text(text = context.getString(R.string.txtInformes))
                            }

                            /*
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
                            }*/
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
                    painter = painterResource(id = R.drawable.fondo5),
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    //Boton para editar la foto de perfil al usuario
                                    showGaleria = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
                                    contentDescription = "Editar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                            Text(text = context.getString(R.string.txtEdFoto))
                        }
                    }


                    TarjetaPersonal(context.getString(R.string.txtPeso), navController)
                    Spacer(Modifier.height(10.dp))
                    TarjetaPersonal("Informacion personal", navController)

                    Spacer(Modifier.height(80.dp))

                    //Boton de cierre de sesion
                    Button(
                        onClick = {
                            if (isConnectedToNetwork(context)) {
                                userController.cerrarSesion({ sucess, error ->
                                    if (sucess) {
                                        //Si el cierre de sesion es correcto, navega al login
                                        navController.navigate(Rutas.LoginScreen.ruta)
                                    } else {
                                        //En caso contrario, mostramos el mensaje de error
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                })
                            } else {
                                Toast.makeText(
                                    context,
                                    "Esta accion requiere conexion a Internet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
                onClick = {},
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
                color = Color.White
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