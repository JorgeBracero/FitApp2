package com.example.fitapp2.metodos

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario
import com.example.fitapp2.vistas.CampoSexo
import com.example.fitapp2.vistas.panelSexo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDate
import kotlin.math.pow
import kotlin.math.roundToInt

//Devuelve si el usuario tiene conexion o no
fun isConnectedToNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

//Obtener la fecha del sistema
fun obtenerFechaDelSistema(): String {
    val fechaActual = LocalDate.now()
    return fechaActual.toString()
}

//Bloqueamos el boton de retroceso cuando guarde algun alimento
@Composable
fun BloquearBotonRetroceso() {
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    DisposableEffect(Unit) {
        val callback = onBackPressedDispatcher?.let {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // No hacer nada cuando se presiona el botón de retroceso
                }
            }
        }

        callback?.let {
            it.isEnabled = true // Habilitar el callback para interceptar las pulsaciones de retroceso
            it.handleOnBackPressed() // Interceptar las pulsaciones de retroceso
            onDispose {
                it.isEnabled = false // Deshabilitar el callback al deshacer el efecto
            }
        }!!
    }
}


//Funcion para mostrar los terminos de politica y servicio de nuestra app
@Composable
fun Terminos(context: Context, callback: (Boolean) -> Unit) {
    var checked by rememberSaveable { mutableStateOf(false) }
    var showPanel by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
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
                    Button(
                        modifier = Modifier.fillMaxWidth(),
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
                },
                title = { Text(text = context.getString(R.string.titTerminos)) },
                text = { Text(
                    text = context.getString(R.string.ckTexto),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.verticalScroll(state = scrollState)
                ) },
                containerColor = Color.White,
                titleContentColor = Color.Black,
                textContentColor = Color.Black
            )
        }
    }

    callback(checked)
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


//Diseño Login
@Composable
fun LoginCard(
    context: Context,
    navController: NavController,
    userController: UsuarioController,
    isLogin: Boolean,
    checkedTerminos: Boolean
){
    var txtEmail by rememberSaveable { mutableStateOf("") }
    var txtPassword by rememberSaveable { mutableStateOf("") }
    var txtPeso by rememberSaveable { mutableStateOf("") }
    var txtAltura by rememberSaveable { mutableStateOf("") }
    var txtEdad by rememberSaveable { mutableStateOf("") }
    var txtSexo by rememberSaveable { mutableStateOf("H") }
    var nombreUser by rememberSaveable { mutableStateOf("") }
    var showPanelSexo by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            //CONTENIDO
            Text(
                text = if(isLogin) "LOGIN" else "SIGN UP",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = TextUnit(25f, TextUnitType.Sp)
            )

            Spacer(Modifier.height(8.dp))

            //Logo
            Image(
                painter = painterResource(id = R.drawable.fitlogo),
                contentDescription = "LogoApp",
                modifier = Modifier
                    .size(90.dp)
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

            if(!isLogin){ //En caso de que se trate de un registro
                nombreUser = CampoRegistro(label = "Nombre de usuario*")
                Spacer(Modifier.height(7.dp))
                txtPeso = CampoRegistro(label = "Peso kg*")
                Spacer(Modifier.height(7.dp))
                txtAltura = CampoRegistro(label = "Altura m*")
                Spacer(Modifier.height(7.dp))
                txtEdad = CampoRegistro(label = "Edad*")
                Spacer(Modifier.height(7.dp))
                CampoSexo(txtSexo, {
                    //Abre el dialogo
                    showPanelSexo = true
                })
            }

            if(showPanelSexo){
                panelSexo({showPanelSexo = false},{
                    txtSexo = it
                })
            }

            Spacer(Modifier.height(7.dp))

            //Boton de inicio de sesion/registro
            Button(
                onClick = {
                    //Necesitas haber validado antes los terminos de la aplicacion
                    if(checkedTerminos) {
                        //Logica para el registro/login de firebase, necesitas conexion wifi
                        if (!isLogin) {
                            if (txtEmail.contains("@") && txtPassword.length >= 6 &&
                                validarDatos(txtPeso, txtAltura, txtEdad, nombreUser)) {
                                if (isConnectedToNetwork(context)) {

                                    userController.registrar(txtEmail, txtPassword) { success, error ->
                                        if (success) {
                                            // Registro exitoso
                                            //Comprobamos que los demas datos son correctos
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
                                                "Predeterminada",
                                                emptyList()
                                            )

                                            println("Altura: $alturaUser")
                                            println("Peso: $pesoUser")
                                            println("Uid: $uidUser")
                                            println("Usuario: $usuario")

                                            userController.addOrUpdUsuario(usuario)
                                            navController.navigate(Rutas.PrincipalScreen.ruta)

                                        } else {
                                            // Error en el registro, muestra el mensaje de error
                                            Toast.makeText(context, "Este usuario ya esta autenticado", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Esta accion requiere conexion a Internet", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                //Si estos datos no son correctos
                                Toast.makeText(context, "Peso, altura o nombre incorrectos", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            if (txtEmail.contains("@") && txtPassword.length >= 6) {
                                userController.login(txtEmail, txtPassword) { success, error ->
                                    if (success) {
                                        // Login exitoso
                                        navController.navigate(Rutas.PrincipalScreen.ruta)
                                    } else {
                                        // Error en el login, muestra el mensaje de error
                                        Toast.makeText(context, "El usuario no existe", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }else{
                                //Si estos datos no son correctos
                                Toast.makeText(context, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(context, "Acepte los terminos de politica y servicio", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = if(!isLogin) "Registrate" else "Inicia sesion",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Spacer(Modifier.height(7.dp))

            //Olvidaste tu contraseña??
            if(isLogin){
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Color.Blue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable {
                        //Navega a la pantalla de ForgotPassword
                        navController.navigate(Rutas.PasswordScreen.ruta)
                    }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "¿No tienes una cuenta?. Registrate aqui",
                    color = Color.Blue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable {
                        //Navega a la pantalla de ForgotPassword
                        navController.navigate(Rutas.RegistroScreen.ruta)
                    }
                )
            }
        }
    }
}

fun getFloat(txt: String): Float {
    val txtFormateado = txt.replace(",",".")
    val valor = txtFormateado.toFloat()
    return valor
}

fun validarDatos(pesoTexto: String, alturaTexto: String, edadTexto: String, nombreUsuario: String): Boolean {
    //Navega con los parametros ingresados por el usuario a la pantalla principal
    //Controlamos que todos los parametros sean correctos
    var datosCorrectos = true
    try{
        val pesoUser = getFloat(pesoTexto)
        val alturaUser = getFloat(alturaTexto)
        val edadUser = edadTexto.toInt()

        if(edadUser > 0 && edadUser <= 120 && pesoUser > 0 && pesoUser < 400 && alturaUser > 0 && alturaUser < 3 && nombreUsuario.trim().isNotEmpty()){
            println("REGISTRO VALIDADO CORRECTAMENTE")
        }else{
            datosCorrectos = false
        }
    } catch (e: NumberFormatException){
        datosCorrectos = false
    }
    return datosCorrectos
}

//Redondea floats
fun Float.round(decimals: Int): Float {
    val factor = 10.0.pow(decimals.toDouble()).toFloat()
    return (this * factor).roundToInt() / factor
}

//Validar una URL
fun isValidUrl(urlString: String): Boolean {
    return try {
        URL(urlString)
        true
    } catch (e: MalformedURLException) {
        false
    }
}

//Indice de masa corporal, para saber en que categoria se encuentra esa persona
fun calcularIMC(usuario: Usuario): Float {
    return (usuario.peso/(usuario.altura * usuario.altura)).round(1)
}

//Tasa metabolica basal, calcula las calorías diarias necesarias para que el cuerpo funcione con normalidad segun la persona
fun calcularTMB(usuario: Usuario): Double {
    var tmb = 66.5 + (usuario.peso * 13.8) + (5 * (usuario.altura * 100)) - (6.8 * usuario.edad)
    if(usuario.sexo == "M"){
        tmb = 665 + (usuario.peso * 9.6) + (1.8 * (usuario.altura * 100)) - (4.7 * usuario.edad)
    }
    return tmb
}

//Nos devuelve la categoria segun la clasificacion del IMC, a la cual pertenece una persona
fun categoriaIMC(usuario: Usuario): String {
    var categoria = "Infrapeso"
    val imc = calcularIMC(usuario) //sacamos el IMC de ese usuario
    if(imc >= 18.5 && imc <= 24.9){
        categoria = "Peso normal"
    }else{
        if(imc >= 25 && imc <= 29.9){
            categoria = "Sobrepeso"
        }else{
            if(imc >= 30){
                categoria = "Obesidad"
            }
        }
    }
    return categoria
}

//Calorias diarias que debe tomar una persona para bajar de peso en funcion de su sexo, peso, altura, edad...
fun calcularCaloriasDiarias(usuario: Usuario): Int {
    return (calcularTMB(usuario) * 1.2).roundToInt() //Damos por hecho que la persona realiza algo de actividad fisica, pero con poca frecuencia
    //aunque esta no la controlamos en la app
}

//Calorias diarias consumidas de una persona
fun calcularCaloriasTotalesConsumidas(
    usuario: Usuario,
    regAlimentoController: RegAlimentoController,
    alimentoController: AlimentoController,
    callback: (Int) -> Unit
) {
    regAlimentoController.calcularCaloriasTotales(usuario.email, alimentoController, { calorias ->
        if(calorias != -1) {
            callback(calorias)
        }
    })
}


//Calcular promedio diario
fun calcularPromedioDiario(caloriasConsumidas: Int, numAlimentos: Int): Int {
    return caloriasConsumidas/numAlimentos
}

//Genera una clave aleatoria dada por firebase, a partir de una referencia
fun generarKey(ref: DatabaseReference, callback: (String?) -> Unit) {
    // Obtener los datos de alimentos
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Incrementar el último valor de alimento en 1 para obtener el nuevo valor
            val key = ref.push().key
            callback(key)
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error al obtener los datos de alimentos: $error")
            callback(null)
        }
    })
}


