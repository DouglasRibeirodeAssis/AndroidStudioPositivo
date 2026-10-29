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
fun TelaDetalheMeta(metaId: Int, navController: NavController) {
    val context = LocalContext.current
    var meta by remember { mutableStateOf<MetaEntity?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(metaId) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            meta = db.metaDao().buscarPorId(metaId)
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
        } else if (meta == null) {
            Text("Meta não encontrada no banco.", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        } else {
            val m = meta!!
            Text(
                text = m.titulo,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Descrição: ${m.descricao}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    val statusText = if (m.concluida) "Concluída ✅" else "Em Andamento ⏳"
                    Text(text = "Status no Banco: $statusText", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val pontosGanho = if (m.concluida) 100 else 25
            val progressoEstimado = if (m.concluida) 100 else 40

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Métricas da Meta (Room):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Recompensa: $pontosGanho pontos")
                    Text(text = "• Progresso: $progressoEstimado%")
                    Text(text = "• Persistência: Dados gravados no SQLite local")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar para Lista de Metas")
            }
        }
    }
}