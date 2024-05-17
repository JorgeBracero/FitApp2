package com.example.fitapp2.vistas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.InputStream
import java.net.URL
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDeepLinkRequest
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.BloquearBotonRetroceso
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.metodos.isValidUrl
import com.example.fitapp2.modelos.Rutas
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(
    navController: NavController,
    momentoDia: String,
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController,
    userController: UsuarioController
){
    var query by rememberSaveable { mutableStateOf("") }
    var showClear by rememberSaveable { mutableStateOf(false) }
    var alimentos by remember { mutableStateOf<List<Alimento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // Variable para controlar el estado de carga
    val context = LocalContext.current
    var conexion = isConnectedToNetwork(context)

    //Términos de búsqueda
    val size = 20

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    showClear = it.trim().isNotEmpty()
                    if(query.isEmpty()){
                        isLoading = false
                        alimentos = emptyList()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                label = { Text(text = "Alimentos") },
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
                    if(showClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar busqueda",
                            tint = Color.Black,
                            modifier = Modifier.clickable {
                                query = ""
                                showClear = false //Si limpia el texto lo volvemos a ocultar
                                alimentos = emptyList()
                            }
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Llamada a la Api
        LaunchedEffect(query) {
            if (query.isNotBlank()) {
                //PROCEDIMIENTO CON CONEXION A INTERNET
                if(conexion) {
                    isLoading = true // Mostrar indicador de carga
                    val deferredAlimentos = apiCall(query = query, size = size)
                    val alimentosResult = deferredAlimentos.await() // Espera a que se complete el Deferred y obtiene el resultado
                    alimentos = alimentosResult as List<Alimento>
                    isLoading = false // Ocultar indicador de carga
                }else{
                    println("Estas sin wifi")

                    //Rellenamos la lista de alimentos con los alimentos de la base de datos
                    alimentoController.getAlimentosLocal(query, { alimentosBuscados ->
                        alimentos = alimentosBuscados
                    })
                }
            }
        }


        //Pantalla
        if(isLoading){
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }else{
            if (alimentos.isNotEmpty()) {
                //RecyclerView
                LazyColumn(
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(15.dp)
                ) {
                    //Cargamos el recyclerview con la lista de alimentos
                    items(items = alimentos) { alimento ->
                        val alimentoCorrecto = alimento != null && alimento.imgAlimento != null &&
                                alimento.imgAlimento.isNotEmpty() && alimento.descAlimento != null &&
                                alimento.descAlimento.isNotEmpty() && alimento.marcaAlimento != null &&
                                alimento.marcaAlimento.isNotEmpty() && isValidUrl(alimento.imgAlimento)

                        if(alimentoCorrecto){
                            CardALimento(
                                navController,
                                alimento,
                                momentoDia,
                                alimentoController,
                                regAlimentoController,
                                storeController,
                                userController,
                                conexion
                            ) //Creamos un card para cada uno
                        }
                    }
                }
            }else{
                Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
            }
        }
    }
}


// Llamada a la Api, devuelve la lista de alimentos buscados
fun apiCall(query: String, size: Int): Deferred<List<Alimento?>> {
    return CoroutineScope(Dispatchers.IO).async {
        val userAgent = "com.example.fitapp2 - Android - Version 1.0"
        val alimentosTemp = mutableListOf<Alimento?>()

        if (query.isBlank()) {
            // Si la consulta está en blanco, devuelve una lista vacía
            return@async alimentosTemp
        }

        try {
            val service = ApiServiceFactory.makeService()
            val response = service.getProducts(query, size, userAgent)
            val html = response.string()
            val listaCodigos = extractBarcodesFromHtml(html)

            // Realiza las llamadas de manera concurrente para obtener detalles de productos
            val deferredAlimentos = listaCodigos.map { codigo ->
                async {
                    val alimento = service.getDetailsProduct(codigo, userAgent).alimento
                    alimento
                }
            }

            // Esperar a que todas las llamadas asíncronas se completen
            val alimentos = runBlocking {
                deferredAlimentos.awaitAll()
            }

            // Filtrar alimentos para eliminar duplicados basados en el nombre
            val alimentosFiltrados = alimentos.distinctBy { it!!.descAlimento.toLowerCase() }

            // Espera a que todas las llamadas asíncronas se completen y recopila los resultados
            alimentosTemp.addAll(alimentosFiltrados)
            println("Alimentos Buscados: $alimentosTemp")
        } catch (e: Exception) {
            println("Error al obtener los productos: ${e.message}")
        }

        return@async alimentosTemp
    }
}


private fun extractBarcodesFromHtml(html: String): List<String> {
    val barcodes = mutableListOf<String>()
    val document = Jsoup.parse(html)
    val elements = document.select("div[id=search_results] ul[class=products] li a")
    for (element in elements) {
        val barcode = element.attr("href").split("/")[2]
        barcode?.let { barcodes.add(it) }
    }
    return barcodes
}


//Vista del alimento extraido
@Composable
fun CardALimento(
    navController: NavController,
    alimento: Alimento,
    momentoDia: String,
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController,
    userController: UsuarioController,
    conexion: Boolean
){
    var imgSubida by rememberSaveable { mutableStateOf(false) }
    val email = userController.getAuth().currentUser!!.email
    var alimentoGuardado by rememberSaveable { mutableStateOf(false) }
    var botonBloqueado by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(250.dp)
            .padding(15.dp)
            .clickable {
                //Navega a la pantalla de detalles del producto
                navController.navigate(Rutas.DetallesScreen.ruta + "/${alimento.idAlimento}")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            Text(
                text = "Producto: ${alimento.descAlimento}",
                fontSize = TextUnit(16f, TextUnitType.Sp)
            ) //Nombre del alimento
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            //Imagen del alimento
            if(conexion) {
                ImgAlimentoUrl(url = alimento.imgAlimento) //Imagen del alimento dada una url, con conexion
            }else{
                println("Imagen sin conexion: ${alimento.imgAlimento}")
                ImgAlimentoStorage(img = alimento.imgAlimento, storeController) //Imagen del alimento del storage, local
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Marca: ${alimento.marcaAlimento}",
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                ) //Marca del alimento

                //Si tenemos conexion, podra guardar el usuario el producto en la base de datos
                if(conexion && !alimentoGuardado) {
                    //Boton para guardar el producto en nuestra base de datos en la tabla 'Alimentos'
                    Button(onClick = {
                        imgSubida = true //Se puede subir la imagen
                        /*
                        botonBloqueado = true
                        // Desbloquear el botón después de 5 segundos
                        coroutineScope.launch {
                            delay(5000)
                            botonBloqueado = false
                        }*/
                    }) {
                        Text(text = "Guardar")
                    }
                }
            }
        }
    }

    /*
    if(botonBloqueado){
        // Bloquear el botón de retroceso
        BloquearBotonRetroceso()
    }*/

    //Si la descarga de la imagen ha ido bien, se sigue con el proceso de guardado
    if(imgSubida){
        storeController.subirImagen(alimento, alimentoController)

        //Por ultimo subo el registro de ese alimento, compruebo que ya no tenga uno para ese mismo usuario
        val regAlimento = RegAlimento(idAlimento = alimento.idAlimento, email = email!!,momentoDia = momentoDia, cantidad = 1)
        println(regAlimento)
        regAlimentoController.alimentoConsumidoUsuario(alimento,email, {alimentoConsumido ->
            if(!alimentoConsumido && !alimentoGuardado){ //Si el alimento no ha sido consumido por el usuario, lo añadimos
                regAlimentoController.addRegAlimento(regAlimento)
                alimentoGuardado = true
                println("Añadido correctamente")
            }
        })

        //Navegamos a la pantalla principal
        navController.navigate(Rutas.PrincipalScreen.ruta)
    }
}


//Descargar Imagen del alimento del Firebase Storage
@Composable
fun ImgAlimentoStorage(img: String, storeController: StorageController) {
    val bitmap = storeController.getBitmapImagen(LocalContext.current,img)

    if(bitmap != null) {
        val bitmapPainter = bitmap.asImageBitmap()

        Image(
            bitmap = bitmapPainter,
            contentDescription = null
        )
    }else{
        Text(text = "No se puede cargar la imagen.")
    }
}


//Descargar Imagen del alimento dada una url
@Composable
fun ImgAlimentoUrl(url: String){
    val painter: Painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(true) // Transición de fundido cruzado
            //transformations(CircleCropTransformation()) // Opcional: aplicar transformación para redondear la imagen
        }
    )

    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier.size(120.dp)
    )
}