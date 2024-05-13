package com.example.fitapp2.controladores

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.fitapp2.modelos.Alimento
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.URL

class StorageController {
    private val storeRef = FirebaseStorage.getInstance().reference



    //Subir Imagen de los productos guardados a storage
    @Composable
    fun subirImagen(alimento: Alimento, alimentoController: AlimentoController) {
        LaunchedEffect(alimento.imgAlimento) {
            withContext(Dispatchers.IO) {
                val refStore = storeRef.child("images").child("${alimento.descAlimento}.jpg")
                val descImg: InputStream = URL(alimento.imgAlimento).openStream()

                // Subo la imagen
                refStore.putStream(descImg).addOnSuccessListener {
                    println("La imagen se subió con éxito")
                    // Como campo al alimento le añadimos la URL
                    alimento.imgAlimento = alimento.descAlimento

                    //Guardo el alimento seleccionado y su registro en la db, a partir de mi ref
                    alimentoController.addAlimento(alimento)
                }.addOnFailureListener { exception ->
                    println("Error al subir la imagen del producto: ${alimento.descAlimento}\n$exception")
                }
            }
        }
    }

    fun borrarImagen(alimento: Alimento){
        // Referencia al archivo que deseas eliminar
        val ref = storeRef.child("images/${alimento.imgAlimento}.jpg")

        // Elimina el archivo
        ref.delete()
            .addOnSuccessListener {
                // La eliminación se realizó con éxito
                println("Archivo eliminado exitosamente.")
            }
            .addOnFailureListener { exception ->
                // Ocurrió un error al intentar eliminar el archivo
                println("Error al eliminar el archivo: $exception")
            }
    }

    fun getBitmapImagen(context: Context, img: String): Bitmap? {
        var imagenFile: File? = null
        var imagenDescargada = false
        descargarImagen(context, img, { localFile, exception ->
            imagenFile = localFile
            imagenDescargada = true
        })

        if(imagenDescargada && imagenFile != null) {
            val bitmap = BitmapFactory.decodeFile(imagenFile!!.absolutePath)
            return bitmap
        }else{
            return null
        }
    }

    fun descargarImagen(context: Context, fileName: String, callback: (File?, Exception?) -> Unit) {
        // Crear un archivo local persistente en el directorio de almacenamiento interno de la aplicación
        val localFile = File(context.filesDir, "${fileName}.jpg")
        // Verificar si el archivo ya existe localmente
        if (localFile.exists()) {
            // Llamar al callback con el archivo local
            callback(localFile, null)
        } else {
            // Si el archivo no existe localmente, descargarlo de Firebase Storage
            val imgRef = storeRef.child("images/${fileName}.jpg")

            imgRef.getFile(localFile)
                .addOnSuccessListener {
                    // Llamar al callback con el archivo local
                    callback(localFile, null)
                }
                .addOnFailureListener { exception ->
                    // Manejar errores de descarga llamando al callback con la excepción
                    println("Fallida ${fileName}")
                    callback(null, exception)
                }
        }
    }

    fun getStoreRef(): StorageReference{
        return storeRef
    }
}