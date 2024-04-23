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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.fitapp2.R
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Rutas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(navController: NavController, alimentos: List<Alimento?>){
    var text by rememberSaveable { mutableStateOf("") }
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
                value = text,
                onValueChange = {
                    text = it
                    showClear = it.trim().isNotEmpty()
                },
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
                                text = ""
                                showClear = false //Si limpia el texto lo volvemos a ocultar
                            }
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if(alimentos != null && alimentos.size > 0) {
            //RecyclerView
            LazyColumn(
                modifier = Modifier.background(color = Color.White)
                    .padding(10.dp)
            ) {
                //Cargamos el recyclerview con la lista de panes
                items(items = alimentos) { alimento ->
                    CardALimento(alimento) //Creamos un card para cada uno
                }
            }
        }else{
            Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
        }
    }
}


//Vista del alimento extraido
@Composable
fun CardALimento(alimento: Alimento?){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Cyan,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Imagen del alimento
            ImgAlimento(url = alimento!!.imgAlimento) //Imagen del alimento
            Text(text = alimento!!.descAlimento) //Nombre del alimento
            Text(text = alimento.marcaAlimento) //Marca del alimento
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
        modifier = Modifier.size(70.dp)
    )
}