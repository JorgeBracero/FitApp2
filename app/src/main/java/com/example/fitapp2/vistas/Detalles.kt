package com.example.fitapp2.vistas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.modelos.Alimento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesScreen(
    navController: NavController,
    alimentoController: AlimentoController,
    storeController: StorageController,
    id: String
){
    //Recuperamos el alimento asociado a ese id
    var alimento by remember { mutableStateOf<Alimento?>(null) }
    val context = LocalContext.current

    // Lanzamos la carga del alimento al entrar en la pantalla
    LaunchedEffect(Unit) {
        alimentoController.obtenerAlimento(id) { alimentoBD ->
            alimento = alimentoBD
        }
    }

    //Hasta que no recupere el objeto de la base de datos, no muestra nada
    if(alimento != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = alimento!!.descAlimento,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
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
        ) {
            // Creamos un ScrollState
            val scrollState = rememberScrollState()
            //Activamos el desplazamiento vertical
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.DarkGray)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    storeController.mostrarImagen(context, alimento!!.imgAlimento)
                    Column {
                        Text(text = "Alimento: ${alimento!!.descAlimento}")
                        Text(text = "Marca: ${alimento!!.marcaAlimento}")
                    }
                }

                Text(text = "Categorias", textAlign = TextAlign.Start)
                //Mostramos cada una de las categorias por separado
                alimento!!.catsAlimento.trim().split(",").forEach { cat ->
                    Card(
                        modifier = Modifier.padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = cat)
                    }
                }

                Divider(color = Color.White)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = "Ingredientes", textAlign = TextAlign.Start)
                    Text(text = "Nutrientes", textAlign = TextAlign.Start)
                }

                //Ingredientes
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    //Recorremos cada uno de los ingredientes, y los mostramos junto a su porcentaje asociado
                    alimento!!.ingredientes.forEach { ing ->
                        val idFormateado = ing.idIng.substring(ing.idIng.indexOf(":") + 1)
                        if(ing.porcentaje > 0) {
                            Text(text = "$idFormateado: ${ing.porcentaje}%")
                        }
                    }
                }

                //Nutrientes
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    //Recorremos cada uno de los ingredientes, y los mostramos junto a su porcentaje asociado
                    Text(text = "Azucar: ${alimento!!.nutrientes.azucar} g")
                    Text(text = "Sal: ${alimento!!.nutrientes.sal} g")
                    Text(text = "Carbohidratos: ${alimento!!.nutrientes.carbohidratos} g")
                    Text(text = "Proteinas: ${alimento!!.nutrientes.proteinas} g")
                    Text(text = "Sodio: ${alimento!!.nutrientes.sodio} g")
                }

                //Calorias
                Text(text = "Calorias Totales: ${alimento!!.nutrientes.calorias} cal")
            }
        }
    }
}