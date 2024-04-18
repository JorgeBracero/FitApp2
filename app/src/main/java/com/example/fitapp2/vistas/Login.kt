package com.example.fitapp2.vistas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavController
import com.example.fitapp2.R
import com.example.fitapp2.modelos.Rutas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun LoginScreen(navController: NavController){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(150.dp))
        Image(
            painter = painterResource(id = R.drawable.fitlogo),
            contentDescription = "LogoApp",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        //Variables
        val txtLogin = context.getString(R.string.btLog)
        val txtInvitado = context.getString(R.string.btInvitado)
        val idGoogle = R.drawable.logogoogle
        val idInvitado = R.drawable.invitado

        //Cards
        Tarjeta(txtLogin,idGoogle,context,navController)
        Spacer(modifier = Modifier.height(10.dp))
        Tarjeta(txtInvitado,idInvitado,context,navController)
        Spacer(modifier = Modifier.height(18.dp))
        Idiomas(context)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ){
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
        Text(text = context.getString(R.string.txtIdiomas))
        Spacer(modifier = Modifier.width(10.dp))
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
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color.White
            )
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

/*
@Composable
fun GoogleSignInButton(context: Context) {
    val startGoogleSignIn = rememberGoogleSignInLauncher(context) { account ->
        if (account != null) {
            signInWithGoogleAccount(account)
            //navController.navigate(Rutas.DatosInicialesScreen.ruta)
        } else {
            Log.i("SignIn", "El inicio de sesión con Google fue cancelado o falló")
        }
    }

    LaunchedEffect(Unit) {
        startGoogleSignIn()
    }
}

//Funcion acceso a Google
fun rememberGoogleSignInLauncher(context: Context, onSignInResult: (GoogleSignInAccount?) -> Unit): () -> Unit {
    return {
        val activityResultLauncher = (context as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = try {
                    task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    null
                }
                // Llama al callback con la cuenta obtenida
                onSignInResult(account)
            } else {
                // El inicio de sesión fue cancelado o falló, llama al callback con null
                onSignInResult(null)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }
}

// Función para iniciar sesión con la cuenta de Google en Firebase Authentication
fun signInWithGoogleAccount(account: GoogleSignInAccount) {
    val auth = FirebaseAuth.getInstance()
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // El inicio de sesión con Google fue exitoso
                Log.d("ExitoLogueo", "todo gucci")
                val user = auth.currentUser

            } else {
                // El inicio de sesión con Google falló
                Log.w("ERROR LOGUEO", "fallo tu inicio de sesion")
                // Manejar el error
            }
        }
}*/




