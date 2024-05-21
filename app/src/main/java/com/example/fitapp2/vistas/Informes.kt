package com.example.fitapp2.vistas

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.calcularCaloriasDiarias
import com.example.fitapp2.metodos.calcularCaloriasTotalesConsumidas
import com.example.fitapp2.metodos.calcularPromedioDiario
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Rutas
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformesScreen(navController: NavController, alimentoController: AlimentoController, regAlimentoController: RegAlimentoController, userController: UsuarioController){
    val context = LocalContext.current
    val user = userController.getAuth().currentUser
    var fechas by remember { mutableStateOf<List<String?>>(emptyList())}
    var cambiarFecha by remember { mutableStateOf(false)}
    var listaCaloriasMomentosDia by remember { mutableStateOf(MutableList(3) { 0 }) }
    var listaAlimentos by remember { mutableStateOf<List<Alimento>>(emptyList())}

    //Obtenemos las distintas fechas de consumiciones que tiene el usuario
    regAlimentoController.getFechasUsuario(user!!.email!!,{ listaFechas ->
        fechas = listaFechas
    })


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(text = context.getString(R.string.txtInformes), color = Color.White)
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navegar hacia atras",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            navController.navigateUp()
                        }
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
                                modifier = Modifier
                                    .size(45.dp)
                                    .clickable {
                                        navController.navigate(Rutas.PrincipalScreen.ruta)
                                    }
                            )
                            Text(text = "Inicio")
                        }

                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Informes",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)

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
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (fechas != null && fechas.size > 0) { //Si el usuario ha consumido algun alimento
                var caloriasDiarias by remember { mutableStateOf(0) }
                var caloriasConsumidas by remember { mutableStateOf(0) }
                var numAlimentosConsumidos by remember { mutableStateOf(1) }
                var promedioDiario by remember { mutableStateOf(caloriasConsumidas) }

                //Una vez obtenidas las fechas, guardamos la fecha seleccionada actual
                var fechaElegida by remember { mutableStateOf(fechas[0])}

                //Obtenemos los datos de la base de datos
                LaunchedEffect(fechaElegida) {

                    //Obtenemos el numero de alimentos consumidos por ese usuario en esa fecha
                    regAlimentoController.getNumAlimentosFechaUsuario(user.email!!, fechaElegida!!, {
                        numAlimentosConsumidos = it
                    })

                    //Obtenemos las calorias en cada momento del dia
                    //Desayuno
                    regAlimentoController.calcularCaloriasDiariasConsumidasDia(
                        user.email!!,
                        fechaElegida!!,
                        "Desayuno",
                        alimentoController,
                        { listaCaloriasMomentosDia[0] = it }
                    )

                    //Almuerzo
                    regAlimentoController.calcularCaloriasDiariasConsumidasDia(
                        user.email!!,
                        fechaElegida!!,
                        "Almuerzo",
                        alimentoController,
                        { listaCaloriasMomentosDia[1] = it }
                    )

                    //Cena
                    regAlimentoController.calcularCaloriasDiariasConsumidasDia(
                        user.email!!,
                        fechaElegida!!,
                        "Cena",
                        alimentoController,
                        { listaCaloriasMomentosDia[2] = it }
                    )

                    println("ListaCaloriasMomentoDia: $listaCaloriasMomentosDia")

                    //Obtener los alimentos consumidos por el usuario en la fecha especifica
                    regAlimentoController.getAlimentosUsuarioFecha(user.email!!, fechaElegida!!, alimentoController, { alimentos ->
                        listaAlimentos = alimentos
                        println("Lista alimentos: $listaAlimentos")
                    })

                    userController.obtenerDatosUsuario(user.uid, { userBD ->
                        if (userBD.uid.isNotEmpty()) {
                            caloriasDiarias = calcularCaloriasDiarias(userBD) //Calculo las calorias diarias

                            //Calculo las calorias consumidas diarias segund su fecha
                            regAlimentoController.calcularCaloriasDiariasConsumidas(
                                user.email!!, fechaElegida!!,
                                alimentoController, { caloriasConsumidas = it })
                            println("Calorias Diarias: $caloriasDiarias")
                            println("Calorias Consumidas: $caloriasConsumidas")
                        }
                    })
                }

                //Calculamos el promedio diario
                promedioDiario = calcularPromedioDiario(caloriasConsumidas,numAlimentosConsumidos)

                Column(
                    verticalArrangement = Arrangement.Center
                ){
                    Text(text = "Fecha seleccionada")
                    FechaElegida(fechaElegida!!, {
                        cambiarFecha = true
                    })
                }

                Spacer(Modifier.height(10.dp))

                //Cards con las estadisticas del usuario
                CardInforme(context,regAlimentoController,user,caloriasDiarias,caloriasConsumidas,promedioDiario,listaCaloriasMomentosDia,listaAlimentos,false)
                Spacer(Modifier.height(8.dp))
                CardInforme(context,regAlimentoController,user,caloriasDiarias,caloriasConsumidas,promedioDiario,listaCaloriasMomentosDia,listaAlimentos,true)
                //Panel de cambiar fecha
                if(cambiarFecha){
                    panelFecha(fechas,{ cambiarFecha = false },{ fechaElegida = it })
                }
            }else{
                Text(text = "Este usuario aun no ha consumido ningun alimento")
            }
        }
    }
}

//Panel para cambiar la fecha
@Composable
fun panelFecha(fechas: List<String?>, onDismiss: () -> Unit, callback: (String) -> Unit) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {},
            text = {
                LazyColumn(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(fechas) { fecha ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    println("Antes de cambiar: $selectedItem")
                                    selectedItem = fechas.indexOf(fecha)
                                    println("Despues de actualizar: $selectedItem")
                                    onDismiss() //Cerramos el dialog
                                    callback(fechas[selectedItem]!!)
                                }
                        ) {
                            Text(text = fecha!!)
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = selectedItem == fechas.indexOf(fecha),
                                onClick = {
                                    println("Antes de cambiar: $selectedItem")
                                    selectedItem = fechas.indexOf(fecha)
                                    println("Despues de actualizar: $selectedItem")
                                    onDismiss() //Cerramos el dialog
                                    callback(fechas[selectedItem]!!)
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

//Combobox para mostrar la fecha elegida
@Composable
fun FechaElegida(fecha: String, onClick: () -> Unit){
    var icon by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }
    OutlinedTextField(
        value = fecha,
        onValueChange = {},
        trailingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.clickable {
                    //Abre el dialogo
                    icon = Icons.Default.KeyboardArrowUp
                    onClick()
                }
            )
        },
        readOnly = true
    )
}

@Composable
fun CardInforme(
    context: Context,
    regAlimentoController: RegAlimentoController,
    user: FirebaseUser?,
    caloriasDiarias: Int,
    caloriasConsumidas: Int,
    promedioDiario: Int,
    listaCaloriasMomentosDia: List<Int>,
    listaAlimentos: List<Alimento>,
    esInformeAlimentos: Boolean
){
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            if (!esInformeAlimentos) {

                //CONTENIDO INFORME DE CALORIAS
                if (caloriasDiarias != 0) {
                    Text(text = context.getString(R.string.txtCaloriasRes) + "\t\t\t$caloriasDiarias")
                }

                if (caloriasConsumidas != -1) {
                    Text(text = context.getString(R.string.txtCaloriasCon) + "\t\t\t$caloriasConsumidas")
                }

                Text(text = "Promedio Diario $promedioDiario")

                //Bucle para crear el diseño de las calorias para el desayuno, almuerzo y cena
                val momentosDia = listOf("Desayuno", "Almuerzo", "Cena    ")
                var index = 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = "Calorias")
                }
                Divider(color = Color.White)
                momentosDia.forEach { dia ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_square_24),
                            contentDescription = "",
                            tint = Color.Green
                        )
                        Text(text = dia)
                        Spacer(Modifier.width(220.dp))
                        Text(text = "${listaCaloriasMomentosDia[index]}")
                    }
                    Divider(color = Color.White)
                    index++ //Actualizamos la posicion
                }

            }else{
                //CONTENIDO INFORME DE ALIMENTOS TOMADOS
                var totalAlimentos = 0
                Text(text = "Alimentos tomados")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text(text = "Alimentos")
                    Spacer(Modifier.width(70.dp))
                    Text(text = "Cantidad")
                    Text(text = "Calorias")
                }

                Divider(color = Color.White)
                println("Lista alimentos: $listaAlimentos")
                //Recorremos la lista de alimentos consumidos por el usuario en una fecha especifica
                listaAlimentos.forEach { alimento ->
                    //Inicializamos una variable cantidad
                    var cantidad by remember { mutableStateOf(1) }

                    //Obtenemos la cantidad del alimento aqui
                    regAlimentoController.obtenerCantidadAlimentoBD(alimento, user!!.email!!, {cantidadBD ->
                        if(cantidadBD != -1) {
                            println("Cantidad antes de la asignacion: $cantidadBD")
                            cantidad = cantidadBD
                        }
                    })

                    println("Cantidad fuera del foreach: $cantidad")

                    if(cantidad != -1) {
                        totalAlimentos += cantidad
                        var calorias = alimento.nutrientes.calorias * cantidad
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = alimento.descAlimento)
                            Spacer(Modifier.width(50.dp))
                            Text(text = "x$cantidad")
                            Text(text = calorias.toString())
                        }
                        Divider(color = Color.White)
                    }
                }

                //TOTAL CALORIAS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text(text = "Total")
                    Spacer(Modifier.width(70.dp))
                    Text(text = "x$totalAlimentos")
                    Text(text = caloriasConsumidas.toString())
                }
            }
        }
    }
}