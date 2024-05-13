package com.example.fitapp2.modelos

import androidx.compose.runtime.Composable
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitapp2.controladores.AlimentoController
import com.example.fitapp2.controladores.RegAlimentoController
import com.example.fitapp2.controladores.StorageController
import com.example.fitapp2.vistas.AlimentosConsumidosScreen
import com.example.fitapp2.vistas.BuscarScreen
import com.example.fitapp2.vistas.DatosInicialesScreen
import com.example.fitapp2.vistas.DetallesScreen
import com.example.fitapp2.vistas.InfoPersonalScreen
import com.example.fitapp2.vistas.LoginScreen
import com.example.fitapp2.vistas.PerfilScreen
import com.example.fitapp2.vistas.PrincipalScreen
import com.google.firebase.database.DatabaseReference

//Gestor de navegacion entre las pantallas de la app
@Composable
fun Navigation(
    alimentoController: AlimentoController,
    regAlimentoController: RegAlimentoController,
    storeController: StorageController
){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Rutas.LoginScreen.ruta
    ){
        //LOGIN
        composable(route = Rutas.LoginScreen.ruta){
            LoginScreen(navController)
        }

        //INICIO
        composable(route = Rutas.PrincipalScreen.ruta + "/{peso}/{altura}/{nombre}",
            arguments = listOf(
                navArgument(name = "peso"){ type = NavType.FloatType },
                navArgument(name = "altura"){ type = NavType.FloatType },
                navArgument(name = "nombre"){ type = NavType.StringType }
            )){
            it.arguments?.let { it1 ->
                PrincipalScreen(navController, it1.getFloat("peso"), it.arguments!!.getFloat("altura"),
                    it.arguments!!.getString("nombre",""))
            }
        }

        //PERFIL
        composable(route = Rutas.PerfilScreen.ruta + "/{peso}/{altura}/{nombre}",
            arguments = listOf(
                navArgument(name = "peso"){ type = NavType.FloatType },
                navArgument(name = "altura"){ type = NavType.FloatType },
                navArgument(name = "nombre"){ type = NavType.StringType }
            )){
            it.arguments?.let { it1 ->
                PerfilScreen(
                    navController,
                    it1.getFloat("peso"),
                    it.arguments!!.getFloat("altura"),
                    it.arguments!!.getString("nombre","")
                )
            }
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
                storeController
            ) }
        }

        //DATOS PERSONALES
        composable(route = Rutas.InfoPersonalScreen.ruta + "/{peso}/{altura}/{nombre}",
            arguments = listOf(
                navArgument(name = "peso"){ type = NavType.FloatType },
                navArgument(name = "altura"){ type = NavType.FloatType },
                navArgument(name = "nombre"){ type = NavType.StringType }
            )){
            it.arguments?.let { it1 ->
                InfoPersonalScreen(
                    navController,
                    it1.getFloat("peso"),
                    it.arguments!!.getFloat("altura"),
                    it.arguments!!.getString("nombre","")
                )
            }
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
                DetallesScreen(navController, alimentoController, id)
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
                AlimentosConsumidosScreen(navController, it,alimentoController,regAlimentoController,storeController)
            }
        }
    }
}