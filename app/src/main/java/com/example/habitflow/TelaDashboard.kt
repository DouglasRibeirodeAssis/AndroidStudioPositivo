package com.example.habitflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CartaoItemHabito(
    habito: HabitoEntity,
    onCliqueItem: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onCliqueItem() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habito.nome,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${habito.categoria} • ${habito.horario}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onRemover) {
                Text("🗑️", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun TelaDashboard(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lista by remember { mutableStateOf<List<HabitoEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            AppDatabase.popularBancoSeVazio(db)
            lista = db.habitoDao().buscarTodos()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gerenciador de Hábitos (Room)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Clique em um hábito para ver detalhes ou na lixeira para deletar do banco.",
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (lista.isEmpty()) {
            Text(
                text = "Nenhum hábito no banco de dados.",
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(lista, key = { it.id }) { habito ->
                    CartaoItemHabito(
                        habito = habito,
                        onCliqueItem = {
                            navController.navigate(Rotas.criarRotaDetalheHabito(habito.id))
                        },
                        onRemover = {
                            scope.launch(Dispatchers.IO) {
                                val db = AppDatabase.getDatabase(context)
                                db.habitoDao().deletar(habito)
                                val atualizada = db.habitoDao().buscarTodos()
                                withContext(Dispatchers.Main) {
                                    lista = atualizada
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate(Rotas.TELA_CADASTRO_HABITO) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Novo Hábito", fontSize = 16.sp)
        }
    }
}