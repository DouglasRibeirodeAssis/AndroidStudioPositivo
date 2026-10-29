package com.example.habitflow

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Rotas {
    const val TELA_INICIAL = "tela_inicial"
    const val TELA_DASHBOARD = "tela_dashboard"
    const val TELA_CADASTRO_HABITO = "tela_cadastro_habito"
    const val TELA_DETALHE_HABITO = "tela_detalhe_habito/{habitoId}"
    const val TELA_METAS = "tela_metas"
    const val TELA_DETALHE_META = "tela_detalhe_meta/{metaId}"
    const val TELA_PERFIL = "tela_perfil"

    fun criarRotaDetalheHabito(id: Int) = "tela_detalhe_habito/$id"
    fun criarRotaDetalheMeta(id: Int) = "tela_detalhe_meta/$id"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route

    val exibirTopBar = rotaAtual?.startsWith("tela_detalhe") == true || rotaAtual == Rotas.TELA_CADASTRO_HABITO
    val exibirBottomBar = rotaAtual in listOf(
        Rotas.TELA_INICIAL,
        Rotas.TELA_DASHBOARD,
        Rotas.TELA_METAS,
        Rotas.TELA_PERFIL
    )

    Scaffold(
        topBar = {
            if (exibirTopBar) {
                TopAppBar(
                    title = {
                        val titulo = when {
                            rotaAtual?.startsWith("tela_detalhe") == true -> "Detalhes"
                            else -> "Cadastrar Hábito"
                        }
                        Text(titulo)
                    },
                    navigationIcon = {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("← Voltar")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (exibirBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rotaAtual == Rotas.TELA_INICIAL,
                        onClick = {
                            navController.navigate(Rotas.TELA_INICIAL) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("🏠") },
                        label = { Text("Início") }
                    )
                    NavigationBarItem(
                        selected = rotaAtual == Rotas.TELA_DASHBOARD,
                        onClick = {
                            navController.navigate(Rotas.TELA_DASHBOARD) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("📋") },
                        label = { Text("Hábitos") }
                    )
                    NavigationBarItem(
                        selected = rotaAtual == Rotas.TELA_METAS,
                        onClick = {
                            navController.navigate(Rotas.TELA_METAS) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("🎯") },
                        label = { Text("Metas") }
                    )
                    NavigationBarItem(
                        selected = rotaAtual == Rotas.TELA_PERFIL,
                        onClick = {
                            navController.navigate(Rotas.TELA_PERFIL) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("👤") },
                        label = { Text("Perfil") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.TELA_INICIAL,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.TELA_INICIAL) {
                TelaInicial(navController = navController)
            }
            composable(Rotas.TELA_DASHBOARD) {
                TelaDashboard(navController = navController)
            }
            composable(Rotas.TELA_CADASTRO_HABITO) {
                TelaCadastroHabito(navController = navController)
            }
            composable(
                route = Rotas.TELA_DETALHE_HABITO,
                arguments = listOf(navArgument("habitoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("habitoId") ?: 0
                TelaDetalheHabito(habitoId = id, navController = navController)
            }
            composable(Rotas.TELA_METAS) {
                TelaMetas(navController = navController)
            }
            composable(
                route = Rotas.TELA_DETALHE_META,
                arguments = listOf(navArgument("metaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("metaId") ?: 0
                TelaDetalheMeta(metaId = id, navController = navController)
            }
            composable(Rotas.TELA_PERFIL) {
                TelaPerfil(navController = navController)
            }
        }
    }
}