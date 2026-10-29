package com.example.habitflow

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun FormularioCadastroHabito(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nomeHabito by remember { mutableStateOf("") }
    var categoriaHabito by remember { mutableStateOf("") }
    var horarioHabito by remember { mutableStateOf("") }
    var metaDias by remember { mutableStateOf("21") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = nomeHabito,
            onValueChange = { nomeHabito = it },
            label = { Text("Nome do Hábito") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = categoriaHabito,
            onValueChange = { categoriaHabito = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = horarioHabito,
            onValueChange = { horarioHabito = it },
            label = { Text("Horário Pretendido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = metaDias,
            onValueChange = { metaDias = it },
            label = { Text("Meta de Dias Consecutivos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nomeHabito.isNotBlank() && categoriaHabito.isNotBlank()) {
                    val dias = metaDias.toIntOrNull() ?: 21
                    val novoHabito = HabitoEntity(
                        nome = nomeHabito,
                        categoria = categoriaHabito,
                        horario = if (horarioHabito.isBlank()) "08:00" else horarioHabito,
                        metaDias = dias
                    )

                    scope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(context)
                        db.habitoDao().inserir(novoHabito)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Hábito  gravado no banco!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha nome e categoria!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Hábito no Banco", fontSize = 16.sp)
        }
    }
}

@Composable
fun TelaCadastroHabito(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cadastrar Novo Hábito",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        FormularioCadastroHabito(navController = navController)
    }
}