package com.example.rotina.etapa2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rotina.etapa2.data.Habito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheHabitoScreen(
    viewModel: DetalheHabitoViewModel,
    aoVoltar: () -> Unit,
    aoEditar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // rememberSaveable mantém o diálogo aberto mesmo se a tela girar
    var mostrarDialogoExcluir by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (uiState is DetalheUiState.Conteudo) {
                        IconButton(onClick = aoEditar) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar hábito")
                        }
                        IconButton(onClick = { mostrarDialogoExcluir = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir hábito")
                        }
                    }
                }
            )
        }
    ) { paddingInterno ->
        val modifier = Modifier.padding(paddingInterno)

        when (val estado = uiState) {
            is DetalheUiState.Carregando -> EstadoCarregando(modifier)

            is DetalheUiState.NaoEncontrado -> EstadoMensagem(
                titulo = "Hábito não encontrado",
                mensagem = "Este hábito pode ter sido excluído.",
                textoBotao = "Voltar para a lista",
                aoClicarBotao = aoVoltar,
                modifier = modifier
            )

            is DetalheUiState.Conteudo -> ConteudoDetalhe(
                habito = estado.habito,
                aoMarcarFeito = { viewModel.alternarFeitoHoje() },
                modifier = modifier
            )
        }
    }

    if (mostrarDialogoExcluir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExcluir = false },
            title = { Text("Excluir hábito?") },
            text = { Text("Essa ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoExcluir = false
                    viewModel.excluir(aoTerminar = aoVoltar)
                }) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoExcluir = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ConteudoDetalhe(
    habito: Habito,
    aoMarcarFeito: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feito = habito.estaFeitoHoje()

    // verticalScroll evita que o conteúdo seja cortado com fonte grande ou tela pequena
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = habito.nome, style = MaterialTheme.typography.headlineSmall)

        // `descricao` é opcional: só mostra quando existe
        if (!habito.descricao.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = habito.descricao,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Meta: ${habito.metaSemanal}x por semana",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = if (feito) "Status: feito hoje ✓" else "Status: pendente",
            style = MaterialTheme.typography.titleMedium,
            color = if (feito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (feito) {
            OutlinedButton(onClick = aoMarcarFeito, modifier = Modifier.fillMaxWidth()) {
                Text("Desmarcar feito hoje")
            }
        } else {
            Button(onClick = aoMarcarFeito, modifier = Modifier.fillMaxWidth()) {
                Text("Marcar como feito hoje")
            }
        }
    }
}
