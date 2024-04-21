package com.example.fitapp2


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Navigation
import com.example.fitapp2.ui.theme.FitApp2Theme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    //private var signInLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    // Declarar MutableState para almacenar el estado del alimento
                    var alimentoState by rememberSaveable { mutableStateOf<Alimento?>(null) }

                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            try {
                                val service = ApiServiceFactory.makeService()
                                val response = service.getProducts()
                                // Actualizar el estado del alimento cuando se reciba la respuesta de la API
                                alimentoState = response.alimento
                                println("AlimentoLifeCycle: ${alimentoState}")
                            } catch (e: Exception) {
                                println("Error al obtener el producto: ${e.message}")
                            }
                        }
                    }

                    // Observar el estado del alimento y mostrar la vista correspondiente
                    alimentoState?.let { alimento ->
                        Navigation(alimento)
                    }
                }
            }
        }
    }
}


