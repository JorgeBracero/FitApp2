package com.example.fitapp2.controladores

import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.roundToInt

class RegAlimentoController(db: FirebaseDatabase){
    private val refRegAl = db.getReference("regAlimentos")

    fun addRegAlimento(regAlimento: RegAlimento){
        generarKey({ key ->
            if(key != null) {
                refRegAl.child(key).setValue(regAlimento)
            }
        })
    }

    //Genera una clave aleatoria dada por firebase
    private fun generarKey(callback: (String?) -> Unit) {
        // Obtener los datos de alimentos
        refRegAl.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Incrementar el último valor de alimento en 1 para obtener el nuevo valor
                val key = refRegAl.push().key
                callback(key)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener los datos de alimentos: $error")
                callback(null)
            }
        })
    }

    //Obtener la key de un alimento concreto de un usuario especifico
    private fun obtenerKeyAlimento(alimento: Alimento, emailUser: String, callback: (String?) -> Unit) {
        //Parte de una consulta de un alimento especifico a buscar, ya solo itero sobre los usuarios
        val query = refRegAl.orderByChild("idAlimento").equalTo(alimento.idAlimento)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var claveAlimento: String? = null

                // Iterar sobre los resultados de la consulta
                snapshot.children.forEach { child ->
                    val regAlimento = child.getValue(RegAlimento::class.java)
                    if (regAlimento != null && regAlimento.email == emailUser) {
                        // Si se encuentra un alimento con el emailUser correspondiente, obtener su clave
                        claveAlimento = child.key
                        return@forEach //Sale del foreach
                    }
                }

                // Llamar al callback con la clave del alimento (o null si no se encontró)
                callback(claveAlimento)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener la clave del alimento: $error")
                callback(null)
            }
        })
    }

    fun alimentoConsumido(alimento: Alimento, email: String, callback: (Boolean) -> Unit){
        var alimentoConsumido = false
        val query = refRegAl.orderByChild("idAlimento").equalTo(alimento.idAlimento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Hay resultados en la consulta
                    //Recorremos todos los usuarios, y comprobamos si alguno ha consumido el alimento en cuestion
                    //En caso de que ningun otro usuario haya consumido este alimento, si se puede borrar
                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null && regAlimento.email != email) {
                             //En este caso, significa que otro usuario ha consumido este alimento
                             //Por lo tanto afirmamos que el alimento si lo consumio otro usuario y no se puede borrar
                             alimentoConsumido = true
                        }
                    }
                }

                //Llamamos al callback
                callback(alimentoConsumido)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(alimentoConsumido)
            }
        })
    }


    //Comprueba si el alimento ya ha sido consumido por el usuario
    fun alimentoConsumidoUsuario(alimento: Alimento, email: String, callback: (Boolean) -> Unit){
        var alimentoConsumido = false
        val query = refRegAl.orderByChild("idAlimento").equalTo(alimento.idAlimento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Hay resultados en la consulta
                    //Recorremos todos los usuarios, y comprobamos si alguno ha consumido el alimento en cuestion
                    //En caso de que ningun otro usuario haya consumido este alimento, si se puede borrar
                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null && regAlimento.email == email) {
                            //En este caso, significa que el usuario ya ha consumido este alimento
                            //No lo guardamos
                            alimentoConsumido = true
                        }
                    }
                }

                //Llamamos al callback
                callback(alimentoConsumido)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(alimentoConsumido)
            }
        })
    }

    //Metodo para calcular las calorias consumidas por una persona
    fun calcularCalorias(email: String, alimentoController: AlimentoController, callback: (Int) -> Unit) {
        var caloriasTotales = 0
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Hay resultados en la consulta
                    //Recorremos todos los usuarios, y comprobamos si alguno ha consumido el alimento en cuestion
                    //En caso de que ningun otro usuario haya consumido este alimento, si se puede borrar
                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null) {
                            alimentoController.obtenerAlimento(regAlimento.idAlimento, { alimentoBD ->
                                if(alimentoBD != null){
                                    //Almacenamos las calorias de cada uno de los alimentos que haya consumido
                                    //Las redondeamos a 0 decimales
                                    println("entre PELOTUDOOOOOOOOOOO")
                                    caloriasTotales = caloriasTotales + (alimentoBD.nutrientes.calorias).roundToInt()
                                    println("Calorias Totales: $caloriasTotales")
                                }
                            })
                        }
                    }
                }

                //Llamamos al callback
                callback(caloriasTotales)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(-1)
            }
        })
    }

    fun deleteRegAlimento(alimento: Alimento, email: String){
        obtenerKeyAlimento(alimento,email, { key ->
            if(key != null) {
                refRegAl.child(key).removeValue()
            }
        })
    }

    fun actualizarCantidadBD(alimento: Alimento, email: String, cantidad: Int){
        obtenerKeyAlimento(alimento,email, {key ->
            if(key != null) {
                refRegAl.child(key).child("cantidad").setValue(cantidad)
                    .addOnSuccessListener {
                        //Se ha actualizado correctamente la cantidad
                        println("cantidad actualizada")
                    }.addOnFailureListener {
                        //Ha ocurrido algun error
                        println("No se ha actualizado la cantidad bien: ${it.message}")
                    }
            }
        })
    }

    fun actualizarMomentoDiaBD(alimento: Alimento, email: String, momentoDia: String){
        obtenerKeyAlimento(alimento,email, {key ->
            if(key != null) {
                refRegAl.child(key).child("momentoDia").setValue(momentoDia)
                    .addOnSuccessListener {
                        //Se ha actualizado correctamente la cantidad
                        println("momentoDia actualizado")
                    }.addOnFailureListener {
                        //Ha ocurrido algun error
                        println("No se ha actualizado el momentoDia bien: ${it.message}")
                    }
            }
        })
    }

    fun obtenerCantidadAlimentoBD(alimento: Alimento, email: String, callback: (Int) -> Unit) {
        var cantidadBD = 1
        refRegAl.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { reg ->
                    val regAlimento = reg.getValue(RegAlimento::class.java)
                    regAlimento?.let { //Comprobamos que no sea nulo
                        if(regAlimento.idAlimento == alimento.idAlimento && regAlimento.email == email){ //Buscamos el alimento de la vuelta
                            cantidadBD = regAlimento.cantidad
                            println("Cantidad del alimento: $cantidadBD")
                            return@forEach //Sale del bucle
                        }
                    }
                }

                //Sale del bucle, y llama al callback
                println("sali del bucle, llamando al callback")
                callback(cantidadBD)
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha extraido la cantidad del alimento correctamente")
                callback(-1)
            }
        })
    }

    fun getRefRegAl(): DatabaseReference{
        return refRegAl
    }
}