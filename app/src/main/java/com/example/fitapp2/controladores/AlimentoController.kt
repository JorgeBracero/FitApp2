package com.example.fitapp2.controladores

import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.example.fitapp2.controladores.RegAlimentoController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AlimentoController(db: FirebaseDatabase){
    private val refAlimentos = db.getReference("alimentos")

    //Sincroniza los datos localmente
    fun copiaLocal(){
        refAlimentos.keepSynced(true)
    }

    //Añade un alimento
    fun addAlimento(alimento: Alimento){
        refAlimentos.child(alimento.idAlimento).setValue(alimento)
    }

    //Borra un alimento
    fun deleteAlimento(alimento: Alimento, email: String, regAlimentoController: RegAlimentoController){
        regAlimentoController.alimentoConsumido(alimento, email, { alimentoConsumido ->
            if(!alimentoConsumido){ //Si el alimento no ha sido consumido por otro usuario, se puede eliminar
                refAlimentos.child(alimento.idAlimento).removeValue()
            }
        })
    }

    //Obtiene un alimento a partir de su clave
    fun obtenerAlimento(id: String, callback : (Alimento) -> Unit) {
        var alimentoBD = Alimento()

        //Obtenemos el alimento de la base de datos, dado a ese id
        refAlimentos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { alimento ->
                    val al = alimento.getValue(Alimento::class.java)
                    al?.let { //Comprobamos que no sea nulo
                        println("Alimento: $al")
                        if(al.idAlimento == id){ //Buscamos el alimento que tenga ese id
                            alimentoBD = al
                            println("Alimento extraido de la base de datos: ${alimentoBD.idAlimento}")
                            return@forEach //Sale del bucle una vez lo hemos encontrado
                        }
                    }
                }

                //Llamamos al callback
                callback(alimentoBD)
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha extraido el alimento correctamente")
                callback(alimentoBD)
            }
        })
    }

    //Funcion para obtener todos los alimentos en cache de la base de datos
    fun getAlimentosLocal(query: String, callback: (List<Alimento>) -> Unit) {
        val alimentosTemp = mutableListOf<Alimento>()

        refAlimentos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { data ->
                    val alimento = data.getValue(Alimento::class.java)
                    alimento?.let {
                        alimentosTemp.add(it)
                    }
                }

                // Filtramos los alimentos por la búsqueda
                val alimentosBuscados = alimentosTemp.filter {
                    it.descAlimento.toLowerCase().startsWith(query.toLowerCase()) ||
                            it.marcaAlimento.toLowerCase().startsWith(query.toLowerCase())
                }

                // Llamamos al callback con la lista filtrada
                callback(alimentosBuscados)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el caso de error
                println("Error al obtener alimentos locales: ${databaseError.message}")
                // Llamamos al callback con una lista vacía en caso de error
                callback(emptyList())
            }
        })
    }


    //Funcion para obtener todos los alimentos de esa seccion del dia
    fun getAlimentosDia(
        query: String,
        momentoDia: String,
        email: String,
        regAlimentoController: RegAlimentoController,
        callback: (List<Alimento>) -> Unit
    ) {
        val alimentosTemp = mutableListOf<Alimento>()

        // Listener para obtener los cambios en los datos de alimentos
        refAlimentos.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { data ->
                    val alimento = data.getValue(Alimento::class.java)
                    println("Alimento de la vuelta: $alimento")
                    alimento?.let {
                        var encontrado = false // Variable para seguir el estado de si se ha encontrado el objeto o no
                        // Por cada alimento, comprobamos el momento del día en el cual se ha consumido
                        regAlimentoController.getRefRegAl().addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { reg ->
                                    if (encontrado) return@forEach // Si ya se encontró el objeto, salir del bucle
                                    val regAlimento = reg.getValue(RegAlimento::class.java)
                                    println("Registro alimento ${alimento.idAlimento}: $regAlimento")
                                    regAlimento?.let {
                                        if (regAlimento.idAlimento == alimento.idAlimento &&
                                                regAlimento.momentoDia == momentoDia && regAlimento.email == email) {
                                            println("Alimento encontrado: $alimento")
                                            alimentosTemp.add(alimento)
                                            println("Lista alimentos actual: $alimentosTemp")
                                            encontrado = true
                                        }
                                    }
                                }

                                println("Alimentos temp fuera de change: $alimentosTemp")
                                // Filtramos los alimentos por la búsqueda
                                val alimentosBuscados = alimentosTemp.filter {
                                    it.descAlimento.toLowerCase().startsWith(query.toLowerCase()) ||
                                            it.marcaAlimento.toLowerCase().startsWith(query.toLowerCase())
                                }

                                println("Alimentos Buscados: $alimentosBuscados")

                                // Llamamos al callback con la lista filtrada
                                callback(alimentosBuscados)
                                println("se ejecuto el callback")
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                println("Error al obtener los registros de los alimentos: ${databaseError.message}")
                                // Llamamos al callback con una lista vacía en caso de error
                                callback(emptyList())
                            }
                        })
                        // Eliminar el listener de RegAlimentos después de que se complete el onDataChange
                        regAlimentoController.getRefRegAl().removeEventListener(this)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al obtener alimentos locales: ${databaseError.message}")
                // Llamamos al callback con una lista vacía en caso de error
                callback(emptyList())
            }
        })
    }

    //Devuelve la ref a mi tabla 'alimentos'
    fun getRefAlimentos(): DatabaseReference {
        return refAlimentos
    }
}