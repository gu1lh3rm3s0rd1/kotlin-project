package com.example.rotina.etapa2.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioHabitoScreen(
    viewModel: FormularioHabitoViewModel,
    aoVoltar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Quando o ViewModel avisar que salvou, mostra a confirmação e volta para a tela anterior
    LaunchedEffect(uiState.salvo) {
        if (uiState.salvo) {
            Toast.makeText(context, "Hábito salvo!", Toast.LENGTH_SHORT).show()
            aoVoltar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.editando) "Editar hábito" else "Novo hábito") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        // safeDrawing inclui o teclado: assim os campos não ficam escondidos atrás dele
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingInterno ->
        if (uiState.carregando) {
            EstadoCarregando(Modifier.padding(paddingInterno))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = uiState.nome,
                onValueChange = viewModel::aoMudarNome,
                label = { Text("Nome *") },
                isError = uiState.erros.nome != null,
                supportingText = {
                    Text(uiState.erros.nome ?: "${uiState.nome.trim().length}/$TAMANHO_MAXIMO_NOME")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.descricao,
                onValueChange = viewModel::aoMudarDescricao,
                label = { Text("Descrição (opcional)") },
                isError = uiState.erros.descricao != null,
                supportingText = {
                    Text(uiState.erros.descricao ?: "${uiState.descricao.trim().length}/$TAMANHO_MAXIMO_DESCRICAO")
                },
                minLines = 2,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.meta,
                onValueChange = viewModel::aoMudarMeta,
                label = { Text("Meta semanal (1 a 7) *") },
                isError = uiState.erros.meta != null,
                supportingText = {
                    Text(uiState.erros.meta ?: "Quantas vezes por semana você quer fazer o hábito")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = viewModel::salvar,
                enabled = !uiState.salvando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.salvando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Salvar")
                }
            }
        }
    }
}
