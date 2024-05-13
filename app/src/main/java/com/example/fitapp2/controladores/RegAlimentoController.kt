package com.example.fitapp2.controladores

import com.example.fitapp2.modelos.Alimento
import com.example.fitapp2.modelos.RegAlimento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegAlimentoController(db: FirebaseDatabase){
    private val refRegAl = db.getReference("regAlimentos")

    fun addRegAlimento(alimento: Alimento, regAlimento: RegAlimento){
        refRegAl.child(alimento.idAlimento).setValue(regAlimento)
    }

    fun deleteRegAlimento(alimento: Alimento){
        refRegAl.child(alimento.idAlimento).removeValue()
    }

    fun actualizarCantidadBD(alimento: Alimento, cantidad: Int){
        refRegAl.child(alimento.idAlimento).child("cantidad").setValue(cantidad)
            .addOnSuccessListener {
                //Se ha actualizado correctamente la cantidad
                println("cantidad actualizada")
            }.addOnFailureListener {
                //Ha ocurrido algun error
                println("No se ha actualizado la cantidad bien: ${it.message}")
            }
    }

    fun actualizarMomentoDiaBD(alimento: Alimento, momentoDia: String){
        refRegAl.child(alimento.idAlimento).child("momentoDia").setValue(momentoDia)
            .addOnSuccessListener {
                //Se ha actualizado correctamente la cantidad
                println("momentoDia actualizado")
            }.addOnFailureListener {
                //Ha ocurrido algun error
                println("No se ha actualizado el momentoDia bien: ${it.message}")
            }
    }

    fun obtenerCantidadAlimentoBD(alimento: Alimento, callback: (Int) -> Unit) {
        var cantidadBD = 1
        refRegAl.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { reg ->
                    val regAlimento = reg.getValue(RegAlimento::class.java)
                    regAlimento?.let { //Comprobamos que no sea nulo
                        if(regAlimento.idAlimento.equals(alimento.idAlimento)){ //Buscamos el alimento de la vuelta
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
                callback(1)
            }
        })
    }

    fun getRefRegAl(): DatabaseReference{
        return refRegAl
    }
}