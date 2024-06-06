package com.example.fitapp2.vistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.CategoriaController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentosConsumidosScreen(
    navController: NavController,
    momentoDia: String,
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController,
    userController: UsuarioController,
    catController: CategoriaController
){
    var query by rememberSaveable { mutableStateOf("") }
    val email = userController.getAuth().currentUser!!.email
    var showClear by rememberSaveable { mutableStateOf(false) }
    var showCategorias by rememberSaveable { mutableStateOf(false) }
    var alimentos by rememberSaveable { mutableStateOf<List<Alimento>>(emptyList()) }
    var categorias by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("Filtrar") }
    //val context = LocalContext.current


    //Rellenamos la lista de categorias
    catController.getListaCategorias({
        categorias = it
    })


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = momentoDia,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = TextUnit(30f, TextUnitType.Sp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "Alimentos del $momentoDia")
                    }
                },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        }
    ){
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo5),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp)
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


                //Boton para filtrar la busqueda por categoria
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.White
                    ),
                    onClick = {
                        //Abre el dialogo para seleccionar una categoria
                        showCategorias = true
                    }
                ) {
                    Text(text = categoriaSeleccionada)
                }


                //Panel categorias
                if (showCategorias) {
                    panelCategorias(
                        categorias,
                        { showCategorias = false },
                        { categoriaSeleccionada = it })
                }


                //Busqueda de productos, segun el momento del dia
                LaunchedEffect(query) {
                    if (query.isNotEmpty()) {
                        println(query)
                        alimentoController.getAlimentosDia(
                            query,
                            momentoDia,
                            categoriaSeleccionada,
                            email!!,
                            regAlimentoController,
                            { alimentosBuscados ->
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
                                DiseñoAlimento(
                                    navController,
                                    alimento,
                                    momentoDia,
                                    alimentoController,
                                    regAlimentoController,
                                    storeController,
                                    userController
                                )
                                Divider()
                            }
                        }
                    } else {
                        Text(text = "No se encontraron alimentos con su criterio de búsqueda.")
                    }
                }
            }
        }
    }
}


//Panel para seleccionar una categoria
@Composable
fun panelCategorias(categorias: List<String>, onDismiss: () -> Unit, callback: (String) -> Unit) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Box(
                modifier = Modifier.size(height = 180.dp, width = 270.dp)
            ) {
                LazyColumn {
                    items(categorias) { cat ->
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    println("Antes de cambiar: $selectedItem")
                                    selectedItem = categorias.indexOf(cat)
                                    println("Despues de actualizar: $selectedItem")
                                    callback(categorias[selectedItem])
                                    onDismiss() //Cerramos el dialog
                                }
                        ) {
                            Text(text = cat)
                            /*Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = selectedItem == categorias.indexOf(cat),
                                onClick = {
                                    println("Antes de cambiar: $selectedItem")
                                    selectedItem = categorias.indexOf(cat)
                                    println("Despues de actualizar: $selectedItem")
                                    callback(categorias[selectedItem])
                                    onDismiss() //Cerramos el dialog
                                }
                            )*/
                        }
                        Divider()
                    }
                }
            }
        },
        containerColor = Color.DarkGray
    )
}

@Composable
fun DiseñoAlimento(
    navController: NavController,
    alimento: Alimento,
    momentoDia: String,
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController,
    userController: UsuarioController
) {
    var cantidad by rememberSaveable { mutableStateOf(1) }
    val email = userController.getAuth().currentUser!!.email
    var cantidadObtenida by rememberSaveable { mutableStateOf(false) }
    var opacidad by rememberSaveable { mutableStateOf(1f) }
    var isHolding by rememberSaveable { mutableStateOf(false) }
    var borrarAlimento by rememberSaveable { mutableStateOf(false) }
    var actMomentoDia by rememberSaveable { mutableStateOf(false) }
    var icon by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }
    regAlimentoController.obtenerCantidadAlimentoBD(alimento, email!!, { cantidadBD ->
        if(cantidadBD != -1) {
            println("Cantidad antes de la asignacion: $cantidadBD")
            cantidad = cantidadBD
            cantidadObtenida = true
        }
    })

    //Aseguramos de que el valor de cantidad cambia en la BD
    if(cantidadObtenida) {
        // Creamos un ScrollState
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            //Metodo para controlar que el usuario esta manteniendo el alimento
                            isHolding = true
                        }
                    )
                }
                .horizontalScroll(state = scrollState),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Column {
                    Text(
                        text = alimento.descAlimento,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                        modifier = Modifier.clickable {
                            //Navego a los detalles de ese alimento
                            navController.navigate(Rutas.DetallesScreen.ruta + "/${alimento.idAlimento}")
                        }
                    )
                    Text(
                        text = alimento.marcaAlimento,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = {
                        if (cantidad > 1) {
                            cantidad--

                            //Update en la BD
                            regAlimentoController.actualizarCantidadBD(alimento, email,cantidad)
                        }
                    },
                    modifier = Modifier.graphicsLayer(alpha = opacidad),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                ) {
                    Text(
                        text = "-",
                        color = Color.White
                    )
                }

                Column {
                    Text(
                        text = "Cantidad",
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )

                    Text(
                        text = "  x$cantidad",
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
                        regAlimentoController.actualizarCantidadBD(alimento, email,cantidad)
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

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    //Panel para poder cambiar el momento del dia
                    //Lo pondremos en forma de textfield
                    Text(text = "MomentoDia")
                    OutlinedTextField(
                        modifier = Modifier.clickable {
                            //Abre el dialogo
                            icon = Icons.Default.KeyboardArrowUp
                            actMomentoDia = true
                        },
                        value = momentoDia,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.clickable {
                                    //Abre el dialogo
                                    icon = Icons.Default.KeyboardArrowUp
                                    actMomentoDia = true
                                }
                            )
                        },
                        readOnly = true
                    )

                    //Boton para eliminar el alimento
                    if (isHolding) {
                        Button(
                            onClick = {
                                //Borra el alimento deseado
                                //Muestra un Panel para eliminarlo
                                borrarAlimento = true
                                isHolding = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Borrar Alimento",
                                tint = Color.White
                            )
                        }
                    }
                }
        }

        if(actMomentoDia){
            panelMomentoDia(navController,momentoDia,email,alimento, regAlimentoController,{actMomentoDia = false},{icon = Icons.Default.KeyboardArrowDown})
        }


        if(borrarAlimento){
            panelBorrarAlimento({ borrarAlimento = false }, {
                //Si el usuario pulsa en aceptar
                //Borramos tanto el registro como la imagen del storage, del alimento correspondiente primero
                //Luego borramos el alimento de la BD, y actualizamos la interfaz y la lista de alimentos

                //Borrar el registro  del alimento para el usuarioActual
                regAlimentoController.deleteRegAlimento(alimento,email!!)

                //Borrar la imagen del Storage
                storeController.borrarImagenAlimento(alimento,email,regAlimentoController)

                //Borrar el alimento
                alimentoController.deleteAlimento(alimento,email,regAlimentoController)

                //Cerrar el dialogo
                borrarAlimento = false

                //Navegamos a la pantalla principal
                navController.navigate(Rutas.PrincipalScreen.ruta)
            })
        }
    }
}

@Composable
private fun panelMomentoDia(navController: NavController,momentoDia: String, email: String, alimento: Alimento, regAlimentoController: RegAlimentoController, onDismiss: () -> Unit, cambiarIcono: () -> Unit){
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    var items: List<String> = emptyList()
    if(momentoDia == "Desayuno"){
        items = listOf(momentoDia,"Almuerzo","Cena")
    }else{
        if(momentoDia == "Almuerzo"){
            items = listOf(momentoDia,"Desayuno","Cena")
        }else{
            items = listOf(momentoDia,"Desayuno","Almuerzo")
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items.forEach { item ->
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItem = items.indexOf(item)
                                if (items[selectedItem] != momentoDia) {
                                    regAlimentoController.actualizarMomentoDiaBD(
                                        alimento,
                                        email,
                                        items[selectedItem]
                                    ) //Actualizamos en la base de datos
                                }
                                cambiarIcono()
                                onDismiss() //Cerramos el dialog
                                //Navegamos a principal
                                navController.navigate(Rutas.PrincipalScreen.ruta)
                            }
                    ) {
                        Text(text = item)
                        /*Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = selectedItem == items.indexOf(item),
                            onClick = {
                                selectedItem = items.indexOf(item)
                                //Actualizamos si modifica el momento del dia
                                if(items[selectedItem] != momentoDia) {
                                    regAlimentoController.actualizarMomentoDiaBD(alimento, email,items[selectedItem]) //Actualizamos en la base de datos
                                }
                                cambiarIcono()
                                onDismiss() //Cerramos el dialog
                                //Navegamos a principal
                                navController.navigate(Rutas.PrincipalScreen.ruta)
                            }
                        )*/
                    }
                    Divider()
                }
            }
        },
        containerColor = Color.DarkGray
    )
}


@Composable
private fun panelBorrarAlimento(onDismiss: () -> Unit, borradoBD: () -> Unit){
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = borradoBD,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ){
                Text(text = "Aceptar")
            }
        },
        title = {
          Text(text = "Eliminar alimento")
        },
        text = {
          Text(text = "¿Estas seguro de eliminar el alimento?")
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ){
                Text(text = "Cancelar")
            }
        },
        containerColor = Color.DarkGray
    )
}

