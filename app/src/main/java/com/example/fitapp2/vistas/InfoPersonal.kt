package com.example.fitapp2.vistas

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.getFloat
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.metodos.round
import com.example.fitapp2.metodos.validarDatos
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPersonalScreen(navController: NavController, userController: UsuarioController){
    val idUser = userController.getAuth().currentUser!!.uid
    val context = LocalContext.current
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var showPanelSexo by rememberSaveable { mutableStateOf(false) }
    var txtPeso by rememberSaveable { mutableStateOf("") }
    var txtEdad by rememberSaveable { mutableStateOf("") }
    var txtAltura by rememberSaveable { mutableStateOf("") }
    var nombreUser by rememberSaveable { mutableStateOf("") }
    var sexoUser by rememberSaveable { mutableStateOf("H") }

    //Obtenemos los datos del usuario
    LaunchedEffect(Unit) {
        userController.obtenerDatosUsuario(idUser, { userBD ->
            usuarioActual = userBD
        })
    }

    if(usuarioActual != null) {
        //Recojo los datos del usuario campo por campo, para mostrarlos asi en el textfield
        //Y poder modificarlos despues si lo desea
        txtPeso = usuarioActual!!.peso.toString()
        txtEdad = usuarioActual!!.edad.toString()
        txtAltura = usuarioActual!!.altura.toString()
        nombreUser = usuarioActual!!.nombreUsuario
        sexoUser = usuarioActual!!.sexo
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Datos Personales",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    navController.navigateUp() //Navega a la pantalla anterior
                                }
                        )
                    }
                )
            },
            containerColor = Color.Black
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fondo),
                    contentDescription = "Fondo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FieldEmail(usuarioActual!!.email)
                    Spacer(modifier = Modifier.height(15.dp))

                    Field(label = "Peso", valor = txtPeso, onChange = { txtPeso = it })
                    Spacer(modifier = Modifier.height(15.dp))

                    Field(label = "Altura", valor = txtAltura, onChange = { txtAltura = it })
                    Spacer(modifier = Modifier.height(15.dp))

                    Field(label = "Edad", valor = txtEdad, onChange = { txtEdad = it })
                    Spacer(modifier = Modifier.height(15.dp))
                    CampoSexo(sexoUser, {
                        //Abre el dialogo
                        showPanelSexo = true
                    })
                    Spacer(modifier = Modifier.height(15.dp))
                    Field(
                        label = "Nombre de usuario",
                        valor = nombreUser,
                        onChange = { nombreUser = it })

                    //Panel sexo
                    if (showPanelSexo) {
                        panelSexo({ showPanelSexo = false }, {
                            sexoUser = it
                        })
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    //Boton para guardar los cambios
                    Button(
                        onClick = {
                            if (isConnectedToNetwork(context)) {
                                //Actualiza los valores para ese usuario en la BD, siempre que tenga conexion
                                if (validarDatos(txtPeso, txtAltura, txtEdad, nombreUser)) {
                                    val nuevoPeso = getFloat(txtPeso).round(1)
                                    val nuevaAltura = getFloat(txtAltura).round(2)
                                    val nuevaEdad = txtEdad.toInt()
                                    val usuarioMod = Usuario(
                                        usuarioActual!!.uid,
                                        usuarioActual!!.email,
                                        nombreUser,
                                        nuevoPeso,
                                        nuevaAltura,
                                        sexoUser,
                                        nuevaEdad,
                                        usuarioActual!!.fotoPerfil
                                    )
                                    userController.addOrUpdUsuario(usuarioMod) //Actualiza ese mismo usuario
                                    Toast.makeText(
                                        context,
                                        "Datos modificados correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Los datos son incorrectos, no se pueden guardar los cambios",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Esta accion requiere conexion a Internet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Guardar cambios",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Field(label: String, valor: String, onChange: (String) -> Unit) {
    var teclado = KeyboardOptions.Default

    if(label == "Peso" || label == "Altura" || label == "Edad"){
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    }

    OutlinedTextField(
        value = valor,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White
        ),
        onValueChange = onChange,
        label = { Text(text = label) },
        keyboardOptions = teclado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldEmail(email: String) {
    OutlinedTextField(
        value = email,
        onValueChange = {},
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White
        ),
        label = { Text(text = "Email") },
        readOnly = true //El email no se puede modificar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSexo(sexo: String, onClick: () -> Unit){
    var icon by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }
    OutlinedTextField(
        value = sexo,
        onValueChange = {},
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White
        ),
        label = {Text(text = "Sexo")},
        trailingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color.Black,
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

//Muestra un dialog para elegir el sexo, luego devuelve la eleccion
@Composable
fun panelSexo(onDismiss: () -> Unit, callback: (String) -> Unit) {
    val items = listOf("H","M")
    var selectedItem by rememberSaveable { mutableStateOf(0) }
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
                                println("Antes de cambiar: $selectedItem")
                                selectedItem = items.indexOf(item)
                                println("Despues de actualizar: $selectedItem")
                                callback(items[selectedItem])
                                onDismiss() //Cerramos el dialog
                            }
                    ) {
                        Text(
                            text = item,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider()
                }
            }
        },
        containerColor = Color.DarkGray
    )
}

