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
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitApp2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Encabezado para las peticiones GET
                    val userAgent = "com.example.fitapp2 - Android - Version 1.0"

                    // Declarar MutableState para almacenar el estado del alimento
                    val alimentosState = remember { mutableStateOf<List<Alimento?>>(emptyList()) }

                    //Términos de búsqueda
                    val query = "pan"
                    val size = 3

                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            try {
                                val service = ApiServiceFactory.makeService()
                                println("Service: $service")
                                val response = service.getProducts(query, size, userAgent) //Recogemos el doc html
                                println("Response: $response")
                                val html = response.string()
                                println("HTML: $html")
                                val listaCodigos = extractBarcodesFromHtml(html) //Recogemos el codigo de cada producto
                                println("Codigos: $listaCodigos")

                                //Una vez tenemos los codigos de barras de cada producto procesado
                                //Extraemos el alimento de cada uno con sus detalles definidos en su JSON
                                val alimentosTemp = mutableListOf<Alimento?>()
                                listaCodigos.forEach { codigo ->
                                    val alimento =
                                        service.getDetailsProduct(codigo, userAgent).alimento //Extraemos el alimento
                                    //que corresponde con ese codigo de barras, y lo añadimos a la lista
                                    alimentosTemp.add(alimento)
                                }

                                println("AlimentosLifeCycle: ${alimentosTemp.size}")

                                // Actualizar la lista de alimentos después de obtener todos los detalles
                                alimentosState.value = alimentosTemp
                            } catch (e: Exception) {
                                println("Error al obtener los productos: ${e.message}")
                            }
                        }
                    }

                    // Pasamos por parametro el estado de la lista de alimentos
                    Navigation(alimentosState.value)
                }
            }
        }
    }
}

private fun extractBarcodesFromHtml(html: String): List<String> {
    val barcodes = mutableListOf<String>()
    val document = Jsoup.parse(html)
    val elements = document.select("div[id=search_results] ul[class=products] li a")
    for (element in elements) {
        val barcode = element.attr("href").split("/")[2]
        barcode?.let { barcodes.add(it) }
    }
    return barcodes
}


