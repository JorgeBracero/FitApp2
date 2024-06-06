package com.example.fitapp2.controladores

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitapp2.metodos.isValidUrl
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Categoria
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class StorageController {
    private val storeRef = FirebaseStorage.getInstance().reference

    //Subir Imagen de los productos guardados a storage
    @Composable
    fun subirImagen(
        alimento: Alimento,
        alimentoController: AlimentoController,
        catController: CategoriaController
    ) {
            //Sube la imagen al storage, sino no hace nada
            LaunchedEffect(alimento.imgAlimento) {
                withContext(Dispatchers.IO) {
                    val refStore = storeRef.child("images").child("${alimento.descAlimento}.jpg")

                    //Comprobamos que la url sea valida, si lo es seguimos adelante
                    if (isValidUrl(alimento.imgAlimento)) {
                        val url = URL(alimento.imgAlimento)
                        val descImg: InputStream = url.openStream()

                        // Subo la imagen
                        refStore.putStream(descImg).addOnSuccessListener {
                            println("La imagen se subió con éxito")
                            // Como campo al alimento le añadimos la URL
                            alimento.imgAlimento = alimento.descAlimento

                            //Guardo el alimento seleccionado, su registro en la db y sus categorias, a partir de mi ref
                            alimentoController.obtenerAlimento(alimento.idAlimento, { alimentoBD ->
                                if (alimentoBD.idAlimento.isEmpty()) { //Si el alimento que intento añadir no existe, lo guardo
                                    alimentoController.addAlimento(alimento)

                                    //Recorro las categorias de cada alimento y las añado a la base de datos
                                    alimento.catsAlimento.trim().split(",").forEach { cat ->
                                        val categoria = Categoria(cat.trim()) //Instancio una categoria por cada una
                                        catController.addCategoria(categoria) //Añade la categoria
                                    }
                                }
                            })
                        }.addOnFailureListener { exception ->
                            println("Error al subir la imagen del producto: ${alimento.descAlimento}\n$exception")
                        }
                    }else{
                        println("la url de la imagen no es valida")
                    }
                }
            }
    }

    //Subir imagen de la galeria a Storage
    fun subirImagenGaleria(context: Context,uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val imagesRef = storeRef.child("images/$fileName.jpg")
        val uploadTask = imagesRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }


    //Extrae el nombre de la foto seleccionada en la galeria
    fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if(index >= 0) {
                    fileName = it.getString(index)
                }
            }
        }
        return fileName
    }

    fun borrarImagenAlimento(alimento: Alimento, email: String, regAlimentoController: RegAlimentoController){
        regAlimentoController.alimentoConsumido(alimento, email, { alimentoConsumido ->
            if (!alimentoConsumido) { //Si es un alimento, que no ha sido consumido por otro usuario
                //Podemos borrar la imagen
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
        })
    }

    /*
    fun borrarImagenPerfil(img: String){
        if(img != "Predeterminada") { //La imagen predeterminada de perfil no se borra
            // Referencia al archivo que deseas eliminar
            val ref = storeRef.child("images/$img.jpg")
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
    }
    */

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


    //Mostrar imagen Storage
    @Composable
    fun mostrarImagen(context: Context,img: String, size: Dp){
        val bitmap = getBitmapImagen(context,img)

        if(bitmap != null) {
            val bitmapPainter = bitmap.asImageBitmap()

            Image(
                bitmap = bitmapPainter,
                contentDescription = null,
                modifier = Modifier.size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }else{
            CircularProgressIndicator(color = Color.White)
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