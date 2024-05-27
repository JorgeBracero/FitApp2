package com.example.fitapp2.vistas

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.CategoriaController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.example.fitapp2.modelos.Rutas
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    navController: NavController,
    storeController: StorageController,
    alimentoController: AlimentoController,
    catController: CategoriaController,
    regAlimentoController: RegAlimentoController,
    userController: UsuarioController
){
    val context = LocalContext.current
    val email = userController.getAuth().currentUser!!.email
    var qrResult by remember { mutableStateOf<String?>(null) }
    var alimento by remember { mutableStateOf<Alimento?>(null) }
    var showProducto by remember { mutableStateOf(false) }
    var momentoDia by remember { mutableStateOf("Desayuno") }
    var icon by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }
    var elegirMomentoDia by remember { mutableStateOf(false) }
    var guardarProducto by remember { mutableStateOf(false) }
    val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract(), onResult = { result ->
        qrResult = result.contents
        println("QR: $qrResult")
    })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(text = context.getString(R.string.txtPrincipal), color = Color.White)
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clickable {
                                        //Navega al perfil del usuario
                                        navController.navigate(route = Rutas.PerfilScreen.ruta)
                                    }
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
                        }
                    }
                },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                containerColor = Color(0xFF33B2A8),
                onClick = {
                    //Mostramos el scanner qr
                    var options = ScanOptions()
                    options.setPrompt("Enfoque a un codigo de barras de un alimento")
                    options.setBarcodeImageEnabled(true) //Para que pueda escanear codigos de barras
                    options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES) // Solo escanear códigos de barras de productos
                    scanLauncher.launch(options)
                }
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_qr_code_2_24),
                        contentDescription = "Escaner qr",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Seleccione un momento del dia, para añadir, eliminar o buscar alimentos")
            Spacer(Modifier.height(10.dp))
            TarjetaDia(context.getString(R.string.txtDesayuno), R.drawable.desayuno,navController)
            TarjetaDia(context.getString(R.string.txtAlmuerzo), R.drawable.almuerzo,navController)
            TarjetaDia(context.getString(R.string.txtCena), R.drawable.cena,navController)

            //Si encuentra un codigo de barras, procedemos a validarlo
            //Una vez tenemos el codigo de barras, buscamos el producto en la api
            //Si el producto existe en la Api, le solicitamos al usuario si desea guardarlo
            //Si lo desea guardar se almacena en la base de datos
            //En caso contrario, indicamos que no existe y se acaba el proceso
            LaunchedEffect(qrResult){
                if(qrResult != null) {
                    val deferredAlimento = buscarProductoApi(qrResult!!)
                    val alimentoResult = deferredAlimento.await() // Espera a que se complete el Deferred y obtiene el resultado
                    alimento = alimentoResult
                    println("Alimento recogido: $alimento")
                    showProducto = true
                }
            }


            if(showProducto){
                AlertDialog(
                    onDismissRequest = {showProducto = false},
                    confirmButton = {
                        if(alimento != null) {
                            Button(
                                onClick = {
                                    //Lo guarda en la Base de datos
                                    //Si el alimento cumple todos estos requisitos, se puede guardar
                                    if(alimento!!.catsAlimento.isNotEmpty() && alimento!!.imgAlimento.isNotEmpty()
                                        && alimento!!.ingredientes[0].idIng.isNotEmpty()
                                        && (alimento!!.nutrientes.calorias != 0.0
                                                && !alimento!!.descAlimento.toLowerCase().contains("agua"))) {
                                        //El agua es el unico alimento que no tiene calorias
                                        guardarProducto = true //En este caso se puede guardar
                                    }else{ //En caso contrario
                                        Toast.makeText(context,"El alimento esta incompleto",Toast.LENGTH_SHORT).show()
                                    }
                                    showProducto = false //Cierra el dialog
                                }
                            ) {
                                Text(text = "Guardar")
                            }
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                //Lo guarda en la Base de datos
                                showProducto = false //Cierra el dialog
                            }
                        ) {
                            Text(text = if(alimento == null) "Salir" else "Cancelar")
                        }
                    },
                    title = {
                        Text(
                            text = if(alimento == null) "Error"
                            else alimento!!.descAlimento
                        )
                    },
                    text = {
                        if(alimento == null) {
                            Text(
                                text = "No se ha encontrado el producto"
                            )
                        }

                        if(alimento != null) {
                            OutlinedTextField(
                                label = {Text(text = "Elige el momento del dia en el cual lo quieres guardar:")},
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
                                            elegirMomentoDia = true
                                        }
                                    )
                                },
                                readOnly = true
                            )
                        }
                    }
                )
            }

            if(elegirMomentoDia){
                var selectedItem by remember {mutableStateOf(0)}
                val items: List<String> = listOf("Desayuno","Almuerzo","Cena")
                AlertDialog(
                    onDismissRequest = {elegirMomentoDia = false},
                    confirmButton = {},
                    text = {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            items.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = items.indexOf(item)
                                            icon = Icons.Default.KeyboardArrowDown
                                            momentoDia = items[selectedItem]
                                            elegirMomentoDia = false
                                        }
                                ) {
                                    Text(text = item)
                                    Spacer(modifier = Modifier.weight(1f))
                                    RadioButton(
                                        selected = selectedItem == items.indexOf(item),
                                        onClick = {
                                            selectedItem = items.indexOf(item)
                                            icon = Icons.Default.KeyboardArrowDown
                                            momentoDia = items[selectedItem]
                                            elegirMomentoDia = false //Cerramos el dialog
                                        }
                                    )
                                }
                                Divider()
                            }
                        }
                    },
                    containerColor = Color.DarkGray
                )
            }

            if(guardarProducto){
                storeController.subirImagen(alimento!!, alimentoController, catController)

                //Por ultimo subo el registro de ese alimento, compruebo que ya no tenga uno para ese mismo usuario
                val regAlimento = RegAlimento(idAlimento = alimento!!.idAlimento, email = email!!,momentoDia = momentoDia, cantidad = 1)
                println(regAlimento)
                regAlimentoController.alimentoConsumidoUsuario(alimento!!,email, {alimentoConsumido ->
                    if(!alimentoConsumido){ //Si el alimento no ha sido consumido por el usuario, lo añadimos
                        regAlimentoController.addRegAlimento(regAlimento)
                        println("Añadido correctamente")
                    }
                })
            }
        }
    }
}

//Metodo para buscar el producto a ese codigo de barras asociado en la api
fun buscarProductoApi(qrCode: String): Deferred<Alimento?> {
    return CoroutineScope(Dispatchers.IO).async {
        val userAgent = "com.example.fitapp2 - Android - Version 1.0"
        var alimentoTemp: Alimento? = null

        if (qrCode.isBlank()) {
            // Si la consulta está en blanco, devuelve una lista vacía
            return@async alimentoTemp
        }

        try {
            val service = ApiServiceFactory.makeService()
            // Realiza una llamada a la api, para buscar el producto
            val deferredAlimento = async {
                    val alimento = service.getDetailsProduct(qrCode, userAgent).alimento
                    alimento
            }


            // Esperar a que todas las llamadas asíncronas se completen
            val alimento = runBlocking {
                deferredAlimento.await()
            }

            // Espera a que todas las llamadas asíncronas se completen y recopila los resultados
            if(alimento != null) {
                alimentoTemp = alimento
                println("Alimento: $alimentoTemp")
            }else{
                println("Alimento no encontrado en la api")
            }
        } catch (e: Exception) {
            var error = "El servidor se encuentra en mantenimiento, no se pueden recuperar datos"
            if(e.message == "timeout"){
                error = "Refresca la busqueda, se le acabo el tiempo"
            }

            println(error)
        }

        return@async alimentoTemp
    }
}

@Composable
fun TarjetaDia(momentoDia: String, idImg: Int,navController: NavController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                //Iriamos a la pantalla para modificar los alimentos o consultarlos de ese dia
                navController.navigate(Rutas.AlimentosConsumidosScreen.ruta + "/$momentoDia")
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = idImg),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = momentoDia,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir",
                    tint = Color.Cyan,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            //Navegamos a la pantalla BuscarScreen, dependiendo del momento del dia
                            //Identificamos el momento del dia pasandole un parametro
                            navController.navigate(Rutas.BuscarScreen.ruta + "/$momentoDia")
                        }
                )
            }
        }
    }
}