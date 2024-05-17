package com.example.fitapp2.controladores


import com.example.fitapp2.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsuarioController(db: FirebaseDatabase){
    private val auth = FirebaseAuth.getInstance()
    private val refUsuarios = db.getReference("usuarios")

    //Metodo para registrarse en firebase por correo/contraseña
    fun registrar(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    //Añade o modifica un usuario a realtime database, a la tabla usuarios, con todos sus datos
    fun addOrUpdUsuario(user: Usuario){
        refUsuarios.child(user.uid).setValue(user)
    }

    //Borra un usuario
    fun deleteUsuario(user: Usuario){
        refUsuarios.child(user.uid).removeValue()
    }

    //Obtener los datos personales
    fun obtenerDatosUsuario(uid: String, callback : (Usuario) -> Unit) {
        var userBD = Usuario()

        println(uid)
        //Obtenemos el alimento de la base de datos, dado a ese id
        refUsuarios.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { usuario ->
                    val user = usuario.getValue(Usuario::class.java)
                    user?.let { //Comprobamos que no sea nulo
                        println("Usuario: $user")
                        if(user.uid == uid){ //Buscamos el usuario que tenga ese uid
                            userBD = user
                            println("Usuario extraido de la base de datos: ${userBD.uid}")
                            return@forEach //Sale del bucle una vez lo hemos encontrado
                        }
                    }
                }

                println("UserBD antes del callback: $userBD")
                //Llamamos al callback
                callback(userBD)
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha extraido el usuario correctamente")
                callback(userBD)
            }
        })
    }

    //Modifica la foto de perifl de un usuario
    fun updFotoPerfil(user: Usuario, foto: String){
        refUsuarios.child(user.uid).child("fotoPerfil").setValue(foto)
            .addOnSuccessListener {
                //Se ha actualizado correctamente la cantidad
                println("foto de perfil actualizada")
            }.addOnFailureListener {
                //Ha ocurrido algun error
                println("No se ha actualizado la foto de perfil correctamente: ${it.message}")
            }
    }

    //Metodo de logueo
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                    println("te logueaste correctamente bro")
                }else{
                    callback(false, task.exception?.message)
                }
            }
    }

    //Metodo para cerrar sesion en firebase
    fun cerrarSesion(callback: (Boolean, String?) -> Unit) {
        auth.signOut()
        callback(true, null) //Se cerro sesion con exito
    }

    //Metodo para restablecer tu contraseña en firebase
    fun restablecerContrasenia(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El correo electrónico para restablecer la contraseña se envió exitosamente
                    callback(true, null)
                } else {
                    // Ocurrió un error al enviar el correo electrónico
                    callback(false, task.exception?.message)
                }
            }
    }

    //Metodo para comprobar que el correo dado corresponde a un usuario autenticado en mi aplicacion
    fun usuarioExiste(email: String, callback: (Boolean) -> Unit){
        var usuarioExiste = false
        refUsuarios.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { usuario ->
                    val user = usuario.getValue(Usuario::class.java)
                    user?.let { //Comprobamos que no sea nulo
                        println("Usuario: $user")
                        if(user.email == email){ //Buscamos el usuario que tenga ese correo
                            usuarioExiste = true //Se ha encontrado el correo asociado
                            return@forEach //Sale del bucle una vez lo hemos encontrado
                        }

                        //Eliminar el listener
                        refUsuarios.removeEventListener(this)
                    }
                }

                //Llamamos al callback
                callback(usuarioExiste)
            }

            override fun onCancelled(error: DatabaseError) {
                println("No se ha encontrado ese usuario")
                callback(usuarioExiste)
            }
        })
    }

    //GETTERS
    fun getAuth(): FirebaseAuth{
        return auth
    }

    fun getRefUsuarios(): DatabaseReference {
        return refUsuarios
    }
}