package com.example.habitflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CabecalhoBoasVindas() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Text(text = "⚡", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "HabitFlow 3.0",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Dados persistentes no banco de dados Room.",
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CartaoResumoInicial() {
    val context = LocalContext.current
    var totalHabitos by remember { mutableIntStateOf(0) }
    var totalMetas by remember { mutableIntStateOf(0) }
    var metasConcluidas by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            AppDatabase.popularBancoSeVazio(db)
            val habitos = db.habitoDao().buscarTodos()
            val metas = db.metaDao().buscarTodos()
            totalHabitos = habitos.size
            totalMetas = metas.size
            metasConcluidas = metas.count { it.concluida }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumo do Banco de Dados:",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "• Hábitos Gravados: $totalHabitos")
            Text(text = "• Metas Cadastradas: $totalMetas")
            Text(text = "• Metas Concluídas: $metasConcluidas")
        }
    }
}

@Composable
fun TelaInicial(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        CabecalhoBoasVindas()

        Spacer(modifier = Modifier.height(20.dp))

        CartaoResumoInicial()

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate(Rotas.TELA_DASHBOARD) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ver Meus Hábitos",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}