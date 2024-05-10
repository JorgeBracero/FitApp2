package com.example.fitapp2.vistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.example.fitapp2.modelos.Rutas
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentosConsumidosScreen(navController: NavController, momentoDia: String,
                              refAlimentos: DatabaseReference, refRegAl: DatabaseReference){

    var query by rememberSaveable { mutableStateOf("") }
    var showClear by rememberSaveable { mutableStateOf(false) }
    var alimentos by rememberSaveable { mutableStateOf<List<Alimento>>(emptyList()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ){
        Text(
            text = momentoDia,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            fontSize = TextUnit(30f, TextUnitType.Sp)
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(start = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    showClear = it.trim().isNotEmpty()
                    if (query.isEmpty()) {
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
                    if (showClear) {
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


        //Busqueda de productos, segun el momento del dia
        LaunchedEffect(query) {
            if(query.isNotEmpty()) {
                println(query)
                getAlimentosDia(query, momentoDia, refAlimentos, refRegAl, { alimentosBuscados ->
                    alimentos = alimentosBuscados
                })
            }
        }


        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(start = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            //LISTA DE ALIMENTOS
            if (alimentos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(alimentos) { alimento ->
                        DiseñoAlimento(navController,alimento, refRegAl)
                        Divider()
                    }
                }
            } else {
                Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
            }
        }
    }
}


//Funcion para obtener todos los alimentos de esa seccion del dia
fun getAlimentosDia(
    query: String,
    momentoDia: String,
    refAlimentos: DatabaseReference,
    refRegAl: DatabaseReference,
    callback: (List<Alimento>) -> Unit
) {
    val alimentosTemp = mutableListOf<Alimento>()

    // Listener para obtener los cambios en los datos de alimentos
    refAlimentos.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.forEach { data ->
                val alimento = data.getValue(Alimento::class.java)
                println("Alimento de la vuelta: $alimento")
                alimento?.let {
                    var encontrado = false // Variable para seguir el estado de si se ha encontrado el objeto o no
                    // Por cada alimento, comprobamos el momento del día en el cual se ha consumido
                    refRegAl.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach { reg ->
                                if (encontrado) return@forEach // Si ya se encontró el objeto, salir del bucle
                                val regAlimento = reg.getValue(RegAlimento::class.java)
                                println("Registro alimento ${alimento.idAlimento}: $regAlimento")
                                regAlimento?.let {
                                    if (regAlimento.idAlimento.equals(alimento.idAlimento) && regAlimento.momentoDia.equals(momentoDia)) {
                                        println("Alimento encontrado: $alimento")
                                        alimentosTemp.add(alimento)
                                        println("Lista alimentos actual: $alimentosTemp")
                                        encontrado = true
                                    }
                                }
                            }

                            println("Alimentos temp fuera de change: $alimentosTemp")
                            // Filtramos los alimentos por la búsqueda
                            val alimentosBuscados = alimentosTemp.filter {
                                it.descAlimento.toLowerCase().startsWith(query.toLowerCase()) ||
                                        it.marcaAlimento.toLowerCase().startsWith(query.toLowerCase())
                            }

                            println("Alimentos Buscados: $alimentosBuscados")

                            // Llamamos al callback con la lista filtrada
                            callback(alimentosBuscados)
                            println("se ejecuto el callback")
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            println("Error al obtener los registros de los alimentos: ${databaseError.message}")
                            // Llamamos al callback con una lista vacía en caso de error
                            callback(emptyList())
                        }
                    })
                    // Eliminar el listener de RegAlimentos después de que se complete el onDataChange
                    refRegAl.removeEventListener(this)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("Error al obtener alimentos locales: ${databaseError.message}")
            // Llamamos al callback con una lista vacía en caso de error
            callback(emptyList())
        }
    })
}

@Composable
fun DiseñoAlimento(navController: NavController, alimento: Alimento, refRegAl: DatabaseReference) {
    var cantidad by rememberSaveable { mutableStateOf(1) }
    var cantidadObtenida by rememberSaveable { mutableStateOf(false) }
    var opacidad by rememberSaveable { mutableStateOf(1f) }
    obtenerCantidadAlimentoBD(alimento,refRegAl, { cantidadBD ->
        println("Cantidad antes de la asignacion: $cantidadBD")
        cantidad = cantidadBD
        cantidadObtenida = true
    })

    //Aseguramos de que el valor de cantidad cambia en la BD
    if(cantidadObtenida) {
        //var isLongPressActive = false
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    //Navego a los detalles de ese alimento
                    navController.navigate(Rutas.DetallesScreen.ruta + "/${alimento.idAlimento}")
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress { change, dragAmount ->
                        println("EY TOCON")
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = alimento.descAlimento,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = TextUnit(20f, TextUnitType.Sp)
                )
                Text(
                    text = alimento.marcaAlimento,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                )
            }

            Button(
                onClick = {
                    if (cantidad > 1) {
                        cantidad--

                        //Update en la BD
                        actualizarCantidadBD(alimento, refRegAl, cantidad)
                    }
                },
                modifier = Modifier.graphicsLayer(alpha = opacidad),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar cantidad",
                    tint = Color.White
                )
            }

            Column {
                Text(
                    text = "Cantidad",
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                )

                Text(
                    text = "x$cantidad",
                    onTextLayout = {
                        //Listener para cuando cambia el texto, volvemos el boton de menos mas oscuro
                        if (cantidad == 1) {
                            opacidad = 0.5f
                        }
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(20f, TextUnitType.Sp)
                )

            }


            Button(
                onClick = {
                    if (cantidad == 1) {
                        opacidad = 1f
                    }
                    cantidad++

                    //Update en la BD
                    actualizarCantidadBD(alimento, refRegAl, cantidad)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir cantidad",
                    tint = Color.White
                )
            }
        }
    }
}

private fun actualizarCantidadBD(alimento: Alimento, refRegAl: DatabaseReference, cantidad: Int){
    refRegAl.child(alimento.idAlimento).child("cantidad").setValue(cantidad)
        .addOnSuccessListener {
            //Se ha actualizado correctamente la cantidad
            println("cantidad actualizada")
        }.addOnFailureListener {
            //Ha ocurrido algun error
            println("No se ha actualizado la cantidad bien: ${it.message}")
        }
}

private fun obtenerCantidadAlimentoBD(alimento: Alimento, refRegAl: DatabaseReference,
                                      callback: (Int) -> Unit) {
    var cantidadBD = 1
    refRegAl.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { reg ->
                val regAlimento = reg.getValue(RegAlimento::class.java)
                regAlimento?.let { //Comprobamos que no sea nulo
                    if(regAlimento.idAlimento.equals(alimento.idAlimento)){ //Buscamos el alimento de la vuelta
                        cantidadBD = regAlimento.cantidad
                        println("Cantidad del alimento: $cantidadBD")
                        return@forEach //Sale del bucle
                    }
                }
            }

            //Sale del bucle, y llama al callback
            println("sali del bucle, llamando al callback")
            callback(cantidadBD)
        }

        override fun onCancelled(error: DatabaseError) {
            println("No se ha extraido la cantidad del alimento correctamente")
            callback(1)
        }
    })
}

