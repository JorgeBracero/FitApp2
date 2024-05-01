package com.example.fitapp2.vistas

import android.content.Context
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
import com.google.firebase.database.FirebaseDatabase
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
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(navController: NavController, momentoDia: String,refAlimentos: DatabaseReference, refRegAl: DatabaseReference){
    var query by rememberSaveable { mutableStateOf("") }
    var alimentos by remember { mutableStateOf<List<Alimento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // Variable para controlar el estado de carga
    val context = LocalContext.current

    //Términos de búsqueda
    val size = 20

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            var showClear by rememberSaveable { mutableStateOf(false) }
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
                    cursorColor = Color.Blue,
                    textColor = Color.Black
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
                if(isConnectedToNetwork(context)) {
                    isLoading = true // Mostrar indicador de carga
                    val deferredAlimentos = apiCall(query = query, size = size)
                    val alimentosResult = deferredAlimentos.await() // Espera a que se complete el Deferred y obtiene el resultado
                    alimentos = alimentosResult as List<Alimento>
                    isLoading = false // Ocultar indicador de carga
                }else{
                    println("Estas sin wifi")

                    //Rellenamos la lista de alimentos con los alimentos de la base de datos
                    getAlimentosLocal(query,refAlimentos, { alimentosBuscados ->
                        alimentos = alimentosBuscados
                    })
                    println("Alimentos sin wifi: $alimentos")
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
                                alimento.marcaAlimento.isNotEmpty()
                        if(alimentoCorrecto){
                            CardALimento(alimento,momentoDia,refAlimentos,refRegAl) //Creamos un card para cada uno
                        }
                    }
                }
            }else{
                Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
            }
        }
    }
}

//Funcion para obtener todos los alimentos en cache de la base de datos
fun getAlimentosLocal(query: String, refAlimentos: DatabaseReference, callback: (List<Alimento>) -> Unit) {
    val alimentosTemp = mutableListOf<Alimento>()

    refAlimentos.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.forEach { data ->
                val alimento = data.getValue(Alimento::class.java)
                alimento?.let {
                    alimentosTemp.add(it)
                }
            }

            // Filtramos los alimentos por la búsqueda
            val alimentosBuscados = alimentosTemp.filter { it.descAlimento.toLowerCase().contains(query.toLowerCase()) }

            // Llamamos al callback con la lista filtrada
            callback(alimentosBuscados)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Manejar el caso de error
            println("Error al obtener alimentos locales: ${databaseError.message}")
            // Llamamos al callback con una lista vacía en caso de error
            callback(emptyList())
        }
    })
}

//Funcion para comprobar si el usuario tiene conexion a Internet
fun isConnectedToNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

// Llamada a la Api, devuelve la lista de alimentos como un Deferred
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
fun CardALimento(alimento: Alimento, momentoDia: String, refAlimentos: DatabaseReference, refRegAl: DatabaseReference){
    var imgSubida by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(250.dp)
            .padding(15.dp),
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
            ImgAlimento(url = alimento.imgAlimento) //Imagen del alimento
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Marca: ${alimento.marcaAlimento}",
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                ) //Marca del alimento

                //Boton para guardar el producto en nuestra base de datos en la tabla 'Alimentos'
                Button(onClick = {
                    imgSubida = true //Se puede subir la imagen
                }) {
                    Text(text = "Guardar")
                }
            }
        }
    }

    //Si la descarga de la imagen ha ido bien, se sigue con el proceso de guardado
    if(imgSubida){
        subirImagen(alimento,refAlimentos)

        //Por ultimo subo el registro de ese alimento
        refRegAl.child(alimento.idAlimento).setValue(RegAlimento(alimento.idAlimento, momentoDia,1))
    }
}


//Subir Imagen de los productos guardados a storage
@Composable
fun subirImagen(alimento: Alimento, refAlimentos: DatabaseReference) {
    LaunchedEffect(alimento.imgAlimento) {
        withContext(Dispatchers.IO) {
            val store = FirebaseStorage.getInstance()
            val refStore = store.reference.child("images").child("${alimento.descAlimento}.jpg")
            val descImg: InputStream = URL(alimento.imgAlimento).openStream()

            // Subo la imagen
            refStore.putStream(descImg).addOnSuccessListener {
                println("La imagen se subió con éxito")
                // Como campo al alimento le añadimos la URL
                alimento.imgAlimento = alimento.descAlimento


                //Guardo el alimento seleccionado y su registro en la db, a partir de mi ref
                refAlimentos.child(alimento.idAlimento).setValue(alimento)
            }.addOnFailureListener { exception ->
                println("Error al subir la imagen del producto: ${alimento.descAlimento}\n$exception")
            }
        }
    }
}


//Imagen del alimento
@Composable
fun ImgAlimento(url: String){
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