package com.example.habitflow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TelaDetalheHabito(habitoId: Int, navController: NavController) {
    val context = LocalContext.current
    var habito by remember { mutableStateOf<HabitoEntity?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(habitoId) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            habito = db.habitoDao().buscarPorId(habitoId)
            carregando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (carregando) {
            Text("Buscando no banco de dados...", fontSize = 16.sp)
        } else if (habito == null) {
            Text("Hábito não encontrado no banco.", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        } else {
            val h = habito!!
            Text(
                text = h.nome,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Categoria: ${h.categoria}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Horário Definido: ${h.horario}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Meta do Hábito: ${h.metaDias} dias consecutivos", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val horasMensaisEstimadas = 7 * 4
            val porcentagemConsistencia = (7.0 / h.metaDias * 100).toInt().coerceAtMost(100)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Análise de Impacto Calculada:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Tempo investido estimado por mês: ~$horasMensaisEstimadas horas")
                    Text(text = "• Progresso da meta semanal: $porcentagemConsistencia%")
                    Text(text = "• Status: Em consolidação no banco Room")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar para Lista de Hábitos")
            }
        }
    }
}