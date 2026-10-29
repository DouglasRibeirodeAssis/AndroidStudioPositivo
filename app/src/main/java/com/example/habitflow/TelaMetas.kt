package com.example.habitflow

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun CartaoItemMeta(
    meta: MetaEntity,
    onCliqueItem: () -> Unit,
    onToggleConcluida: (Boolean) -> Unit,
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = meta.concluida,
                onCheckedChange = { onToggleConcluida(it) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = meta.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = meta.descricao,
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
fun TelaMetas(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lista by remember { mutableStateOf<List<MetaEntity>>(emptyList()) }

    var novoTitulo by remember { mutableStateOf("") }
    var novaDescricao by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            AppDatabase.popularBancoSeVazio(db)
            lista = db.metaDao().buscarTodos()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lista de Metas (Room Database)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Adicionar Meta ao Banco:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = novoTitulo,
                    onValueChange = { novoTitulo = it },
                    label = { Text("Título da Meta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = novaDescricao,
                    onValueChange = { novaDescricao = it },
                    label = { Text("Descrição detalhada") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (novoTitulo.isNotBlank()) {
                            scope.launch(Dispatchers.IO) {
                                val db = AppDatabase.getDatabase(context)
                                db.metaDao().inserir(MetaEntity(titulo = novoTitulo, descricao = novaDescricao, concluida = false))
                                val atualizada = db.metaDao().buscarTodos()
                                withContext(Dispatchers.Main) {
                                    lista = atualizada
                                    novoTitulo = ""
                                    novaDescricao = ""
                                    Toast.makeText(context, "Meta gravada no banco!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar Meta no Banco")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Metas Cadastradas (${lista.size}):",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (lista.isEmpty()) {
            Text("Nenhuma meta no banco de dados.", fontSize = 14.sp)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(lista, key = { it.id }) { meta ->
                    CartaoItemMeta(
                        meta = meta,
                        onCliqueItem = {
                            navController.navigate(Rotas.criarRotaDetalheMeta(meta.id))
                        },
                        onToggleConcluida = { marcada ->
                            scope.launch(Dispatchers.IO) {
                                val db = AppDatabase.getDatabase(context)
                                db.metaDao().atualizar(meta.copy(concluida = marcada))
                                val atualizada = db.metaDao().buscarTodos()
                                withContext(Dispatchers.Main) {
                                    lista = atualizada
                                }
                            }
                        },
                        onRemover = {
                            scope.launch(Dispatchers.IO) {
                                val db = AppDatabase.getDatabase(context)
                                db.metaDao().deletar(meta)
                                val atualizada = db.metaDao().buscarTodos()
                                withContext(Dispatchers.Main) {
                                    lista = atualizada
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}