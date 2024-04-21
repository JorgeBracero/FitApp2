package com.example.fitapp2.apiService

import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.AlimentoResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.lang.reflect.Type

//Interfaz para la gestion de datos de la Api
interface ApiService {

    //Metodo para extraer los alimentos de la api y mostrarlos en nuestro recyclerview
    @GET("api/v3/product/737628064502.json")
    suspend fun getProducts(): AlimentoResponse
}

//Objeto que gestione el servicio de los datos de la Api
object ApiServiceFactory {
    fun makeService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}