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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.fitapp2.apiService.ApiServiceFactory
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Navigation
import com.example.fitapp2.ui.theme.FitApp2Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            FitApp2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Base de datos
                    val db = FirebaseDatabase.getInstance()
                    // Activo la persistencia
                    db.setPersistenceEnabled(true)

                    // Saco las referencias a mis tablas
                    val refAlimentos = db.getReference("alimentos") // Saco la ref a mi tabla alimentos

                    // En caso que sea de manera local
                    refAlimentos.keepSynced(true) // Sincroniza los datos

                    val refRegAl = db.getReference("regAlimentos") // Saco ref a mi tabla regAlimentos
                    Navigation(refAlimentos, refRegAl)
                }
            }
        }
    }
}


