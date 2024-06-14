package com.example.fitapp2.controladores

import com.example.fitapp2.metodos.generarKey
import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.Categoria
import com.example.fitapp2.modelos.RegAlimento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//CONTROLADOR PARA LAS CATEGORIAS
class CategoriaController(db: FirebaseDatabase){
    private val refCategorias = db.getReference("categorias")

    //Añade una nueva categoria
    fun addCategoria(cat: Categoria){
        existeCategoria(cat, { existeCategoria ->
            if(!existeCategoria){ //Si no existe la añade
                generarKey(refCategorias,{ key ->
                    if(key != null) {
                        refCategorias.child(key).setValue(cat)
                    }
                })
            }
        })
    }

    //Comprueba que una categoria exista o no
    private fun existeCategoria(cat: Categoria, callback: (Boolean) -> Unit){
        var existeCategoria = false
        refCategorias.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Hay resultados en la consulta
                    //Recorremos todos los usuarios, y comprobamos si alguno ha consumido el alimento en cuestion
                    //En caso de que ningun otro usuario haya consumido este alimento, si se puede borrar
                    snapshot.children.forEach { child ->
                        val categoria = child.getValue(Categoria::class.java)
                        if (categoria != null && categoria.nomCategoria == cat.nomCategoria) {
                            //En este caso, significa que esta categoria ya esta añadida
                            existeCategoria = true
                        }
                    }
                }

                //Llamamos al callback
                callback(existeCategoria)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener la categoria: $error")
                callback(existeCategoria)
            }
        })
    }

    //Devuelve una lista con todas las categorias de la BD
    fun getListaCategorias(callback: (MutableList<String>) -> Unit){
        val categorias = mutableListOf<String>()
        refCategorias.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Hay resultados en la consulta
                    //Recorremos todos los usuarios, y comprobamos si alguno ha consumido el alimento en cuestion
                    //En caso de que ningun otro usuario haya consumido este alimento, si se puede borrar
                    categorias.add("Filtrar")
                    snapshot.children.forEach { child ->
                        val categoria = child.getValue(Categoria::class.java)
                        if (categoria != null) {
                            categorias.add(categoria.nomCategoria)
                        }
                    }
                }

                //Llamamos al callback
                callback(categorias)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener la categoria: $error")
                callback(categorias)
            }
        })
    }
}