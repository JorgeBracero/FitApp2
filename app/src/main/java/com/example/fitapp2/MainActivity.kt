package com.example.fitapp2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Navigation
import com.example.fitapp2.ui.theme.FitApp2Theme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    //private var signInLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //PROBAR SI SE CONECTA BIEN A LA API O NO
        val service = ApiServiceFactory.makeService()

        //CORRUTINA
        lifecycleScope.launch {
            try {
                val response = service.getProducts()
                if (response.isSuccessful) {
                    val alimentoResponse = response.body()
                    val alimento = alimentoResponse?.alimento
                    if(alimento != null){
                        println("Nombre: ${alimento.descAlimento}\n" +
                                "")
                    }
                } else {
                    // Manejar errores de la API
                    println("Error en la solicitud: ${response.code()}")
                }
            } catch (e: Exception) {
                // Manejar errores de red o de la aplicación
                e.printStackTrace()
            }
        }


        /*
        // Registrar el ActivityResultLauncher
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Aquí puedes manejar el resultado del inicio de sesión de Google
            // Por ejemplo, puedes autenticar al usuario con Firebase si el inicio de sesión fue exitoso
        }*/

        setContent {
            FitApp2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Navigation()
                    //Main(this@MainActivity,signInLauncher)
                }
            }
        }
    }
}

/*
@Composable
fun Main(activity: Activity,signInLauncher: ActivityResultLauncher<Intent>?) {
    Button(
        onClick = {
            val googleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN)
            signInLauncher?.launch(googleSignInClient.signInIntent)
        }
    ) {
        Text(text = "Iniciar sesión con Google")
    }
}*/

