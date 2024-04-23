package com.example.fitapp2.apiService

import com.example.fitapp2.modelos.AlimentoResponse
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

//Interfaz para la gestion de datos de la Api
interface ApiService {

    //Metodo para obtener a partir de una consulta los alimentos buscados segun un filtro, procesa el doc HTML
    @GET("cgi/search.pl")
    suspend fun getProducts(
        @Query("search_terms") query: String,
        @Query("page_size") size: Int,
        @Header("User-Agent") userAgent: String
    ): ResponseBody


    //Metodo que procesa el JSON del producto con todos sus detalles a partir de su codigo de barras
    @GET("api/v2/product/{barcode}.json")
    suspend fun getDetailsProduct(
        @Path("barcode") barcode: String,
        @Header("User-Agent") userAgent: String
    ): AlimentoResponse
}


//Objeto que gestione el servicio de los datos de la Api
object ApiServiceFactory {
    fun makeService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create()) //Para procesar el JSON
            .build()
            .create(ApiService::class.java)
    }
}