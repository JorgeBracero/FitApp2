package com.example.fitapp2.vistas


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.metodos.getFloat
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.metodos.round
import com.example.fitapp2.metodos.validarDatos
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario


@Composable
fun LoginScreen(navController: NavController, userController: UsuarioController){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Idiomas(context)
            }
            Text(
                text = "FitApp",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = TextUnit(35f, TextUnitType.Sp),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            //Variables
            val txtLogin = context.getString(R.string.btLog)
            val txtInvitado = context.getString(R.string.btInvitado)
            val idGoogle = R.drawable.logogoogle
            val idInvitado = R.drawable.invitado

            //Cards
            LoginCard(context, navController, userController)
            Spacer(modifier = Modifier.height(10.dp))
            Tarjeta(txtInvitado, idInvitado, context, navController)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Divider()
                Terminos(context)
            }
    }
}


//Funcion que muestra cada uno de los Cards para inicar sesion en la app
@Composable
fun Tarjeta(texto: String, idImg: Int, context: Context, navController: NavController) {
    var showPanel by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (texto.equals(context.getString(R.string.btInvitado))) {
                    showPanel = true
                } else {
                    navController.navigate(Rutas.DatosInicialesScreen.ruta + "/user")
                }
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
                text = texto,
                color = Color.White
            )
        }

    }

    if(showPanel){
        AlertDialog(
            onDismissRequest = { showPanel = false },
            confirmButton = {
                Button(
                    onClick = {
                        showPanel = false
                        //Navega con parametro
                        navController.navigate(Rutas.DatosInicialesScreen.ruta + "/guest")
                    }
                ) {
                    Text(text = context.getString(R.string.btPanelInvitado))
                }
            },
            title = { Text(text = context.getString(R.string.titPanelInvitado)) },
            text = { Text(text = context.getString(R.string.msgPanelInvitado)) }
        )
    }
}

//Diseño Login
@Composable
fun LoginCard(context: Context,navController: NavController,userController: UsuarioController){
    var txtEmail by rememberSaveable { mutableStateOf("") }
    var txtPassword by rememberSaveable { mutableStateOf("") }
    var txtPeso by rememberSaveable { mutableStateOf("") }
    var txtAltura by rememberSaveable { mutableStateOf("") }
    var txtEdad by rememberSaveable { mutableStateOf("") }
    var txtSexo by rememberSaveable { mutableStateOf("H") }
    var nombreUser by rememberSaveable { mutableStateOf("") }
    var showPanelSexo by rememberSaveable { mutableStateOf(false) }
    var reg by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                //Boton registro
                Button(
                    onClick = {
                        //Cambia el contenido del Card al del registro
                        reg = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "REGISTRO",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Spacer(Modifier.width(191.dp))

                //Boton login
                Button(
                    onClick = {
                        //Cambia el contenido del Card al del Login
                        reg = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "LOGIN",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            //CONTENIDO
            //Logo
            Image(
                painter = painterResource(id = R.drawable.fitlogo),
                contentDescription = "LogoApp",
                modifier = Modifier.size(90.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black
                        )
            )
            Spacer(Modifier.height(9.dp))

            Text(
                text = "Los campos (*) son obligatorios",
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    fontSize = TextUnit(15f, TextUnitType.Sp)
                )
            )

            //El email y la contraseña van a estar siempre
            txtEmail = CampoEmail()
            Spacer(Modifier.height(7.dp))
            txtPassword = CampoContrasenia()

            if(reg){ //En caso de que se trate de un registro
                nombreUser = CampoRegistro(label = "Nombre de usuario*")
                Spacer(Modifier.height(7.dp))
                txtPeso = CampoRegistro(label = "Peso kg*")
                Spacer(Modifier.height(7.dp))
                txtAltura = CampoRegistro(label = "Altura m*")
                Spacer(Modifier.height(7.dp))
                txtEdad = CampoRegistro(label = "Edad*")
                Spacer(Modifier.height(7.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Sexo",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                    CampoSexo(txtSexo, {
                        //Abre el dialogo
                        showPanelSexo = true
                    })
                }
            }

            if(showPanelSexo){
                panelSexo({showPanelSexo = false},{
                    txtSexo = it
                })
            }

            //Boton de inicio de sesion/registro
            Button(
                onClick = {
                    //Logica para el registro/login de firebase, necesitas conexion wifi
                    if(isConnectedToNetwork(context)) {
                        if (reg) {
                            userController.registrar(txtEmail, txtPassword) { success, error ->
                                if (success) {
                                    // Registro exitoso
                                    //Comprobamos que los demas datos son correctos
                                    val todoOk = validarDatos(txtPeso, txtAltura, txtEdad, nombreUser)
                                    println(todoOk)
                                    if (todoOk) {
                                        //Navega a la pantalla principal
                                        val pesoUser = getFloat(txtPeso).round(1)
                                        val alturaUser = getFloat(txtAltura).round(2)
                                        val edadUser = txtEdad.toInt()

                                        //Añadimos el nuevo usuario a nuestra tabla
                                        //Extraemos primero el uid de nuestro usuario registrado
                                        val uidUser = userController.getAuth().currentUser?.uid
                                        val usuario = Usuario(
                                            uidUser!!,
                                            txtEmail,
                                            nombreUser,
                                            pesoUser,
                                            alturaUser,
                                            txtSexo,
                                            edadUser,
                                            "Predeterminada"
                                        )

                                        println("Altura: $alturaUser")
                                        println("Peso: $pesoUser")
                                        println("Uid: $uidUser")
                                        println("Usuario: $usuario")

                                        userController.addOrUpdUsuario(usuario)
                                        navController.navigate(Rutas.PrincipalScreen.ruta)
                                    } else {
                                        //Si estos datos no son correctos
                                        Toast.makeText(
                                            context,
                                            "Peso, altura o nombre incorrectos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Error en el registro, muestra el mensaje de error
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            userController.login(txtEmail, txtPassword) { success, error ->
                                if (success) {
                                    // Login exitoso
                                    navController.navigate(Rutas.PrincipalScreen.ruta)
                                } else {
                                    // Error en el login, muestra el mensaje de error
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(
                            context,
                            "Esta accion requiere conexion a Internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = if(reg) "Registrate" else "Inicia sesion",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Spacer(Modifier.height(7.dp))

            //Olvidaste tu contraseña??
            if(!reg){
                Text(
                    text = AnnotatedString(text = "¿Olvidaste tu contraseña?. Pulsa aqui"),
                    style = TextStyle(
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.clickable {
                        //Navega a la pantalla de ForgotPassword
                        navController.navigate(Rutas.PasswordScreen.ruta)
                    }
                )
            }
        }
    }
}


//Campos de peso altura y nombre usuario del Login
@Composable
fun CampoRegistro(label: String): String {
    var text by rememberSaveable { mutableStateOf("") }
    var teclado = KeyboardOptions.Default

    if(label == "Peso kg*" || label == "Altura m*" || label == "Edad*"){
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        )
    }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(text = label) },
        keyboardOptions = teclado,
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(4.dp)
    )
    return text
}

//Campos de peso altura y nombre usuario del Login
@Composable
fun CampoEmail(): String {
    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(text = "Email*") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = Color.Black
            )
        },
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(4.dp)
    )
    return text
}

//Campos de peso altura y nombre usuario del Login
@Composable
fun CampoContrasenia(): String {
    var text by rememberSaveable { mutableStateOf("") }
    var iconId by rememberSaveable { mutableStateOf(R.drawable.baseline_visibility_off_24) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
        },
        label = { Text(text = "Contraseña*") },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (passwordVisible) KeyboardType.Text else KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            if(text.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "Contraseña Visible o no",
                    tint = Color.Black,
                    modifier = Modifier.clickable {
                        if (iconId == R.drawable.baseline_visibility_24) {
                            iconId = R.drawable.baseline_visibility_off_24
                            passwordVisible = false
                        } else {
                            iconId = R.drawable.baseline_visibility_24
                            passwordVisible = true
                        }
                    }
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(4.dp)
    )
    return text
}

//Funcion para mostrar un combobox de los idiomas de la app
@Composable
fun Idiomas(context: Context) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    val items = listOf(
        context.getString(R.string.txtEspañol) to R.drawable.bespania,
        context.getString(R.string.txtIngles) to R.drawable.binglaterra,
        context.getString(R.string.txtFrances) to R.drawable.bfrancia
    )
    var icon by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }
    var showPanel by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    icon = Icons.Default.KeyboardArrowUp
                    showPanel = true
                },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Image(
                    painter = painterResource(id = items[selectedItem].second),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showPanel) {
        AlertDialog(
            onDismissRequest = {
                showPanel = false
                icon = Icons.Default.KeyboardArrowDown
            },
            confirmButton = {},
            text = {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    items.forEachIndexed { index, (text, imageId) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = index
                                    showPanel = false
                                    icon = Icons.Default.KeyboardArrowDown
                                }
                        ) {
                            Image(
                                painter = painterResource(id = imageId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = text)
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    icon = Icons.Default.KeyboardArrowDown
                                    showPanel = false
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
}


//Funcion para mostrar los terminos de politica y servicio de nuestra app
@Composable
fun Terminos(context: Context) {
    var checked by rememberSaveable { mutableStateOf(false) }
    var showPanel by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = !checked },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue,
                checkmarkColor = Color.White,
                uncheckedColor = Color.DarkGray
            )
        )
        Text(
            text = context.getString(R.string.ckTerminos),
            modifier = Modifier.clickable {
                showPanel = true
            }
        )

        if(showPanel){
            AlertDialog(
                onDismissRequest = { showPanel = false },
                confirmButton = {
                    Row(modifier = Modifier.fillMaxWidth()){
                        Button(
                            onClick = {
                                showPanel = false
                                checked = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = context.getString(R.string.btPanelInvitado))
                        }
                    }
                },
                title = { Text(text = context.getString(R.string.titTerminos)) },
                text = { Text(text = context.getString(R.string.ckTerminos)) }
            )
        }
    }
}