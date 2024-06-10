package com.example.fitapp2.controladores

import com.example.fitapp2.metodos.generarKey
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
        generarKey(refRegAl,{ key ->
            if(key != null) {
                refRegAl.child(key).setValue(regAlimento)
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

    //Metodo que indica si un alimento ha sido consumido ya o no
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

    // Método para calcular las calorías totales consumidas por una persona
    fun calcularCaloriasTotales(email: String, alimentoController: AlimentoController, callback: (Int) -> Unit) {
        var caloriasTotales = 0
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener { // Cambiamos a addListenerForSingleValueEvent para obtener los datos una vez
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val childCount = snapshot.childrenCount
                    var processedCount = 0

                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null) {
                            alimentoController.obtenerAlimento(regAlimento.idAlimento) { alimentoBD ->
                                if (alimentoBD != null) {
                                    // Almacenamos las calorías de cada uno de los alimentos que haya consumido
                                    // Las redondeamos a 0 decimales
                                    caloriasTotales += ((alimentoBD.nutrientes.calorias).roundToInt() * regAlimento.cantidad)
                                }
                                processedCount++
                                // Verificamos si todos los alimentos han sido procesados
                                if (processedCount == childCount.toInt()) {
                                    callback(caloriasTotales)
                                }
                            }
                        } else {
                            processedCount++
                            if (processedCount == childCount.toInt()) {
                                callback(caloriasTotales)
                            }
                        }
                    }
                } else {
                    callback(caloriasTotales)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(-1)
            }
        })
    }


    // Método para calcular los alimentos consumidos por una persona en una fecha
    fun getNumAlimentosFechaUsuario(email: String, fecha: String, callback: (Int) -> Unit) {
        var numAlimentos = 0
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener { // Cambiamos a addListenerForSingleValueEvent para obtener los datos una vez
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null && regAlimento.fecha == fecha) {
                             numAlimentos++
                        }
                    }
                }

                callback(numAlimentos)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(0)
            }
        })
    }


    // Método para calcular las calorías diarias consumidas por una persona
    fun calcularCaloriasDiariasConsumidas(email: String, fecha: String, alimentoController: AlimentoController, callback: (Int) -> Unit) {
        var caloriasConsumidasDiarias = 0

        //Filtramos la busqueda para un usuario
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener { // Cambiamos a addListenerForSingleValueEvent para obtener los datos una vez
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val childCount = snapshot.childrenCount
                    var processedCount = 0

                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null && regAlimento.fecha == fecha) { //Si ese registro corresponde a la fecha elegida
                            alimentoController.obtenerAlimento(regAlimento.idAlimento) { alimentoBD ->
                                if (alimentoBD != null) {
                                    // Almacenamos las calorías de cada uno de los alimentos que haya consumido
                                    // Las redondeamos a 0 decimales
                                    caloriasConsumidasDiarias += ((alimentoBD.nutrientes.calorias).roundToInt() * regAlimento.cantidad)
                                }
                                processedCount++
                                // Verificamos si todos los alimentos han sido procesados
                                if (processedCount == childCount.toInt()) {
                                    callback(caloriasConsumidasDiarias)
                                }
                            }
                        } else {
                            processedCount++
                            if (processedCount == childCount.toInt()) {
                                callback(caloriasConsumidasDiarias)
                            }
                        }
                    }
                } else {
                    callback(caloriasConsumidasDiarias)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(-1)
            }
        })
    }


    // Método para calcular las calorías diarias consumidas por una persona en un momento del dia
    fun calcularCaloriasDiariasConsumidasDia(email: String, fecha: String, momentoDia: String, alimentoController: AlimentoController, callback: (Int) -> Unit) {
        var caloriasConsumidasDiarias = 0

        //Filtramos la busqueda para un usuario
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener { // Cambiamos a addListenerForSingleValueEvent para obtener los datos una vez
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val childCount = snapshot.childrenCount
                    var processedCount = 0

                    snapshot.children.forEach { child ->
                        val regAlimento = child.getValue(RegAlimento::class.java)
                        if (regAlimento != null && regAlimento.fecha == fecha && regAlimento.momentoDia == momentoDia) { //Si ese registro corresponde a la fecha elegida
                            alimentoController.obtenerAlimento(regAlimento.idAlimento) { alimentoBD ->
                                if (alimentoBD != null) {
                                    // Almacenamos las calorías de cada uno de los alimentos que haya consumido
                                    // Las redondeamos a 0 decimales
                                    caloriasConsumidasDiarias += ((alimentoBD.nutrientes.calorias).roundToInt() * regAlimento.cantidad)
                                }
                                processedCount++
                                // Verificamos si todos los alimentos han sido procesados
                                if (processedCount == childCount.toInt()) {
                                    callback(caloriasConsumidasDiarias)
                                }
                            }
                        } else {
                            processedCount++
                            if (processedCount == childCount.toInt()) {
                                callback(caloriasConsumidasDiarias)
                            }
                        }
                    }
                } else {
                    callback(caloriasConsumidasDiarias)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener el alimento: $error")
                callback(-1)
            }
        })
    }

    //Obtener las fechas en las cuales un usuario ha consumido alimentos
    fun getFechasUsuario(email: String, callback: (List<String?>) -> Unit) {
        var listaFechas: List<String?> = emptyList()
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { reg ->
                    val regAlimento = reg.getValue(RegAlimento::class.java)
                    regAlimento?.let { //Comprobamos que no sea nulo
                        if(regAlimento != null){ //Buscamos el alimento de la vuelta
                            listaFechas = listaFechas + regAlimento.fecha
                        }
                    }
                }

                //Sale del bucle, y llama al callback
                listaFechas = listaFechas.distinctBy { it } //Obtenemos las fechas que nos interesan
                println("fechas del usuario cogidas")
                callback(listaFechas)
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha extraido la cantidad del alimento correctamente")
                callback(listaFechas)
            }
        })
    }

    //Obtener los nutrientes totales de los alimentos consumidos por un usuario en una fecha
    fun getTotalNutrientes(email: String, fecha: String, alimentoController: AlimentoController, callback: (List<Int>) -> Unit) {
        var listaNutrientes = MutableList(7){0}

        //Recogemos los alimentos consumidos en esa fecha por ese usuario, y extraemos
        //La cantidad de cada uno de esos alimentos
        getAlimentosUsuarioFecha(email,fecha,alimentoController,{ listaAlimentos ->
            if(listaAlimentos != null && listaAlimentos.size > 0){
                //Recorremos cada alimento, y extraemos la cantidad consumida
                listaAlimentos.forEach { alimento ->
                    println("Alimento a extraer sus nutrientes")
                    if(alimento != null && alimento.idAlimento.isNotEmpty()){
                        //Obtenemos la cantidad consumida de ese alimento
                        obtenerCantidadAlimentoBD(alimento,email,{ cantidadBD ->
                            if(cantidadBD != -1){
                                listaNutrientes[0] += (alimento.nutrientes.calorias * cantidadBD).toInt()
                                listaNutrientes[1] += (alimento.nutrientes.proteinas * cantidadBD).toInt()
                                listaNutrientes[2] += (alimento.nutrientes.carbohidratos * cantidadBD).toInt()
                                listaNutrientes[3] += (alimento.nutrientes.azucar * cantidadBD).toInt()
                                listaNutrientes[4] += (alimento.nutrientes.grasas * cantidadBD).toInt()
                                listaNutrientes[5] += (alimento.nutrientes.sal * cantidadBD).toInt()
                                listaNutrientes[6] += (alimento.nutrientes.sodio * cantidadBD).toInt()
                            }
                        })
                    }
                    println("Nutrientes por el momento: $listaNutrientes")
                }

                //Una vez terminado el proceso, llamamos al callback
                callback(listaNutrientes)
            }
        })
    }

    //Obtener los alimentos de un usuario en una fecha especifica
    fun getAlimentosUsuarioFecha(email: String, fecha: String, alimentoController: AlimentoController, callback: (List<Alimento>) -> Unit) {
        val listaAlimentos: MutableList<Alimento> = mutableListOf()
        val query = refRegAl.orderByChild("email").equalTo(email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pendingTasks = snapshot.children.count()
                if (pendingTasks == 0) {
                    callback(listaAlimentos)
                    return
                }

                var completedTasks = 0

                snapshot.children.forEach { child ->
                    val regAlimento = child.getValue(RegAlimento::class.java)
                    println("RegAlimento: $regAlimento")
                    if (regAlimento != null && regAlimento.fecha == fecha) {
                        alimentoController.obtenerAlimento(regAlimento.idAlimento) { alimentoBD ->
                            if (alimentoBD != null) {
                                println("Alimento a añadir a la lista: $alimentoBD")
                                // Añadir el alimento a la lista
                                listaAlimentos.add(alimentoBD)
                            }
                            completedTasks++
                            // Llamar al callback solo cuando todas las tareas hayan completado
                            if (completedTasks == pendingTasks) {
                                println("alimentos del usuario obtenidos en una fecha concreta")
                                callback(listaAlimentos)
                            }
                        }
                    } else {
                        completedTasks++
                        // Llamar al callback solo cuando todas las tareas hayan completado
                        if (completedTasks == pendingTasks) {
                            println("alimentos del usuario obtenidos en una fecha concreta")
                            callback(listaAlimentos)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha extraido la cantidad del alimento correctamente")
                callback(listaAlimentos)
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