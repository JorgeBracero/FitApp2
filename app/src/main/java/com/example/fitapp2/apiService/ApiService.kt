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
import retrofit2.http.Query
import java.lang.reflect.Type


//Interfaz para la gestion de datos de la Api
interface ApiService {

    //Metodo para extraer los alimentos de la api y mostrarlos en nuestro recyclerview
    @GET("api/v3/product/737628064502.json")
    suspend fun getProducts(): Response<AlimentoResponse>
}

//Clase para poder realizar la deserializacion parcial del JSON a nuestro modelo de clase ALimento
class AlimentoDeserializer: JsonDeserializer<Alimento> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Alimento {
        val jsonObject = json!!.asJsonObject
        if(jsonObject != null) {

            println("No es nulo el json")

            // Obtener los campos que te interesan del JSON y asignarlos a las propiedades del objeto Alimento
            val code = jsonObject.getAsJsonPrimitive("code").asString
            println(code)
            val descAlimento = jsonObject.getAsJsonPrimitive("generic_name").asString
            println(descAlimento)
            val categorias = jsonObject.getAsJsonArray("categories_tags").map { it.asString }
            println(categorias)
            val estado = jsonObject.getAsJsonPrimitive("status").asString
            println(categorias)
            val imagen = jsonObject.getAsJsonPrimitive("image_front_url").asString
            println(imagen)
            return Alimento(code,descAlimento, categorias, 10f, estado, imagen)
        }else{
            println("json nulo")
            return Alimento("2","yea", listOf("v"), 2f, "feliz", "img")
        }


    }

}


//Objeto que gestione el servicio de los datos de la Api
object ApiServiceFactory {
    fun makeService(): ApiService {
        val gson = GsonBuilder()
            .registerTypeAdapter(Alimento::class.java, AlimentoDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}