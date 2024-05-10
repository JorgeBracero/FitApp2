package com.example.fitapp2.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.modelos.Alimento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesScreen(navController: NavController, refAlimentos: DatabaseReference, id: String){
    //Recuperamos el alimento asociado a ese id
    var alimento by remember { mutableStateOf<Alimento?>(null) }

    // Lanzamos la carga del alimento al entrar en la pantalla
    LaunchedEffect(Unit) {
        obtenerAlimento(refAlimentos, id) { alimentoBD ->
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
                            fontWeight = FontWeight.ExtraBold
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Nos aseguramos que el alimento no es nulo
                Text(text = "Hola alimento: broder")
            }
        }
    }else{
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Cargando detalles...")
            CircularProgressIndicator(color = Color.White)
        }
    }
}

private fun obtenerAlimento(refAlimentos: DatabaseReference, id: String, callback : (Alimento) -> Unit) {
    var alimentoBD = Alimento()

    //Obtenemos el alimento de la base de datos, dado a ese id
    refAlimentos.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { alimento ->
                val al = alimento.getValue(Alimento::class.java)
                al?.let { //Comprobamos que no sea nulo
                    println("Alimento: $al")
                    if(al.idAlimento == id){ //Buscamos el alimento que tenga ese id
                        alimentoBD = al
                        println("Alimento extraido de la base de datos: ${alimentoBD.idAlimento}")
                        return@forEach //Sale del bucle una vez lo hemos encontrado
                    }
                }
            }

            //Llamamos al callback
            callback(alimentoBD)
        }

        override fun onCancelled(error: DatabaseError) {
            println("No se ha extraido el alimento correctamente")
            callback(alimentoBD)
        }
    })
}