package com.example.fitapp2.modelos

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.CategoriaController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.controladores.UsuarioController
import com.example.fitapp2.vistas.AlimentosConsumidosScreen
import com.example.fitapp2.vistas.BuscarScreen
import com.example.fitapp2.vistas.DatosInicialesScreen
import com.example.fitapp2.vistas.DetallesScreen
import com.example.fitapp2.vistas.InfoPersonalScreen
import com.example.fitapp2.vistas.InformesScreen
import com.example.fitapp2.vistas.LoginScreen
import com.example.fitapp2.vistas.PasswordScreen
import com.example.fitapp2.vistas.PerfilScreen
import com.example.fitapp2.vistas.PesoScreen
import com.example.fitapp2.vistas.PrincipalScreen

//Gestor de navegacion entre las pantallas de la app
@Composable
fun Navigation(
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController,
    userController: UsuarioController,
    catController: CategoriaController
){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Rutas.LoginScreen.ruta
    ){
        //LOGIN
        composable(route = Rutas.LoginScreen.ruta){
            val userActual = userController.getAuth().currentUser //Cogemos el usuario actual
            if(userActual != null) { //Si ya esta logueado, navega directamente a la pantalla principal
                PrincipalScreen(
                    navController,
                    storeController,
                    alimentoController,
                    catController,
                    regAlimentoController,
                    userController
                )
            }else{ //En caso contrario carga la pantalla de logueo
                LoginScreen(navController, userController)
            }
        }

        //INICIO
        composable(route = Rutas.PrincipalScreen.ruta){
            PrincipalScreen(
                navController,
                storeController,
                alimentoController,
                catController,
                regAlimentoController,
                userController
            )
        }

        //PERFIL
        composable(route = Rutas.PerfilScreen.ruta){
            PerfilScreen(navController,userController,storeController)
        }

        //DATOS INICIALES
        composable(route = Rutas.DatosInicialesScreen.ruta + "/{usuario}",
            arguments = listOf(navArgument(name = "usuario") {
                type = NavType.StringType
            })) {
            it.arguments?.let { it1 ->
                DatosInicialesScreen(
                    navController,
                    it1.getString("usuario", "")
                ) }
        }

        //BUSCAR
        composable(route = Rutas.BuscarScreen.ruta + "/{momentoDia}",
            arguments = listOf(navArgument(name = "momentoDia") {
                type = NavType.StringType
            })){
            it.arguments?.let { it1 -> BuscarScreen(
                navController,
                it1.getString("momentoDia", ""),
                alimentoController,
                regAlimentoController,
                storeController,
                userController,
                catController
            ) }
        }

        //DATOS PERSONALES
        composable(route = Rutas.InfoPersonalScreen.ruta){
            InfoPersonalScreen(navController,userController)
        }

        //DETALLES
        composable(route = Rutas.DetallesScreen.ruta + "/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    type = NavType.StringType
                }
            )){
            val id = it.arguments?.getString("id")
            id?.let {
                DetallesScreen(navController, alimentoController, storeController,id)
            }
        }


        //ALIMENTOS CONSUMIDOS
        composable(route = Rutas.AlimentosConsumidosScreen.ruta + "/{momentoDia}",
            arguments = listOf(
                navArgument(name = "momentoDia"){
                    type = NavType.StringType
                }
            )){
            val momentoDia = it.arguments?.getString("momentoDia")
            momentoDia?.let {
                AlimentosConsumidosScreen(
                    navController,
                    it,
                    alimentoController,
                    regAlimentoController,
                    storeController,
                    userController,
                    catController
                )
            }
        }

        //AJUSTES DE CUENTA
        composable(route = Rutas.PesoScreen.ruta){
            PesoScreen(navController, userController)
        }

        //RESTABLECER CONTRASEÑA
        composable(route = Rutas.PasswordScreen.ruta){
            PasswordScreen(navController, userController)
        }

        //INFORMES
        composable(route = Rutas.InformesScreen.ruta){
            InformesScreen(navController,alimentoController,regAlimentoController,userController)
        }
    }
}