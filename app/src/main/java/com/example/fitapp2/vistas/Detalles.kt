package com.example.fitapp2.vistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
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
                            Text(
                                text = alimento!!.descAlimento,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = TextUnit(23f, TextUnitType.Sp)
                            )
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fondo),
                    contentDescription = "Fondo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                //Activamos el desplazamiento vertical
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(state = scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        storeController.mostrarImagen(context, alimento!!.imgAlimento, 85.dp)
                        Column {
                            Text(
                                text = "${alimento!!.descAlimento}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = TextUnit(22f, TextUnitType.Sp)
                            )
                            Text(
                                text = "${alimento!!.marcaAlimento}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = TextUnit(18f, TextUnitType.Sp)
                            )
                        }
                    }

                    Text(
                        text = "Categorias",
                        textAlign = TextAlign.Start,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(20f, TextUnitType.Sp)
                    )
                    //Mostramos cada una de las categorias por separado
                    alimento!!.catsAlimento.trim().split(",").forEach { cat ->
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Blue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = cat,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = TextUnit(17f, TextUnitType.Sp)
                            )
                        }
                    }

                    Divider(color = Color.White)


                    //Ingredientes
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ingredientes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                        Spacer(Modifier.height(8.dp))
                        //Recorremos cada uno de los ingredientes, y los mostramos junto a su porcentaje asociado
                        alimento!!.ingredientes.forEach { ing ->
                            val idFormateado = ing.idIng.substring(ing.idIng.indexOf(":") + 1)
                            if (ing.porcentaje > 0) {
                                Text(
                                    text = "$idFormateado: ${ing.porcentaje.toInt()}%",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = TextUnit(17f, TextUnitType.Sp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Divider(color = Color.White)
                        Spacer(Modifier.height(6.dp))

                        //Nutrientes
                        //Mostramos todos los datos
                        Text(
                            text = "Nutrientes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Azucar: ${alimento!!.nutrientes.azucar.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                        Text(
                            text = "Sal: ${alimento!!.nutrientes.sal.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                        Text(
                            text = "Carbohidratos: ${alimento!!.nutrientes.carbohidratos.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                        Text(
                            text = "Proteinas: ${alimento!!.nutrientes.proteinas.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                        Text(
                            text = "Grasas: ${alimento!!.nutrientes.grasas.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                        Text(
                            text = "Sodio: ${alimento!!.nutrientes.sodio.toInt()} g",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    //Calorias
                    Text(
                        text = "Calorias 100g: ${alimento!!.nutrientes.calorias.toInt()} cal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(18f, TextUnitType.Sp)
                    )
                }
            }
        }
    }
}