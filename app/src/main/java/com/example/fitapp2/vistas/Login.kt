package com.example.fitapp2.vistas


import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.example.fitapp2.metodos.LoginCard
import com.example.fitapp2.metodos.Terminos
import com.example.fitapp2.metodos.getFloat
import com.example.fitapp2.metodos.isConnectedToNetwork
import com.example.fitapp2.metodos.round
import com.example.fitapp2.metodos.validarDatos
import com.example.fitapp2.modelos.Rutas
import com.example.fitapp2.modelos.Usuario
import java.util.Locale


@Composable
fun LoginScreen(navController: NavController, userController: UsuarioController){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var idiomaActual by remember { mutableStateOf("es") }
    var checkedTerminos by rememberSaveable { mutableStateOf(false) }
    ProvideUpdatedLocale(idiomaActual,{
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Image(
                painter = painterResource(id = R.drawable.fondo),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .verticalScroll(state = scrollState)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FitApp",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = TextUnit(35f, TextUnitType.Sp),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                //Cards
                LoginCard(context, navController, userController, true,checkedTerminos)
                Spacer(modifier = Modifier.height(10.dp))
                //Tarjeta(txtInvitado, idInvitado, context, navController)
                Spacer(modifier = Modifier.height(150.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Divider()
                    Terminos(context, {
                        checkedTerminos = it
                    })
                }
            }
        }
    })
}

//Funcion para mostrar un combobox de los idiomas de la app
@Composable
fun Idiomas(context: Context, callback: (String) -> Unit) {
    var idioma = "es"
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
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = index
                                    icon = Icons.Default.KeyboardArrowDown
                                    if(items[selectedItem].first == "Ingles") {
                                        idioma = "en"
                                    }else{
                                        if(items[selectedItem].first == "Frances") {
                                            idioma = "fr"
                                        }else{
                                            idioma = "es"
                                        }
                                    }
                                    showPanel = false
                                }
                        ) {
                            Image(
                                painter = painterResource(id = imageId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = text)
                            /*Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    icon = Icons.Default.KeyboardArrowDown
                                    if(items[selectedItem].first == "Ingles") {
                                        idioma = "en"
                                    }else{
                                        if(items[selectedItem].first == "Frances") {
                                            idioma = "fr"
                                        }else{
                                            idioma = "es"
                                        }
                                    }
                                    showPanel = false
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
    callback(idioma)
}

//Metodo para cambiar el idioma de la app
fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
}

@Composable
fun ProvideUpdatedLocale(languageCode: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val updatedContext = remember(languageCode) {
        setLocale(context, languageCode)
        context
    }
    CompositionLocalProvider(LocalContext provides updatedContext) {
        content()
    }
}