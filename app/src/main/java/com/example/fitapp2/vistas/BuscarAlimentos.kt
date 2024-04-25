package com.example.fitapp2.vistas

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.fitapp2.R
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Rutas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(navController: NavController){
    var query by rememberSaveable { mutableStateOf("") }
    var alimentos by remember { mutableStateOf<List<Alimento?>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // Variable para controlar el estado de carga

    //Términos de búsqueda
    val size = 3

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
                    if(showClear == false){
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
                isLoading = true // Mostrar indicador de carga
                val deferredAlimentos = apiCall(query = query, size = size)
                val alimentosResult = deferredAlimentos.await() // Espera a que se complete el Deferred y obtiene el resultado
                alimentos = alimentosResult
                isLoading = false // Ocultar indicador de carga
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
                        CardALimento(alimento) //Creamos un card para cada uno
                    }
                }
            }else{
                Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
            }
        }
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

            // Espera a que todas las llamadas asíncronas se completen y recopila los resultados
            alimentosTemp.addAll(deferredAlimentos.awaitAll())
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
fun CardALimento(alimento: Alimento?){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(200.dp)
            .padding(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Imagen del alimento
            if(alimento != null) {
                ImgAlimento(url = alimento.imgAlimento) //Imagen del alimento
                Text(
                    text = alimento.descAlimento,
                    fontSize = TextUnit(25f, TextUnitType.Sp)
                ) //Nombre del alimento
                Text(
                    text = alimento.marcaAlimento,
                    fontSize = TextUnit(25f, TextUnitType.Sp)
                ) //Marca del alimento
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