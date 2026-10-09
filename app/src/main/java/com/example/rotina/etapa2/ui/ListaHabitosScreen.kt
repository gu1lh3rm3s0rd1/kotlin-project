package com.example.rotina.etapa2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaHabitosScreen(
    viewModel: ListaHabitosViewModel,
    aoClicarHabito: (Int) -> Unit,
    aoClicarNovoHabito: () -> Unit,
    aoAbrirVersaoXml: () -> Unit
) {
    // Lê o StateFlow do ViewModel; a coleta pausa quando o app vai para segundo plano
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var menuAberto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus hábitos") },
                actions = {
                    // Menu "três pontinhos": dá acesso à versão original do app, feita com Views/XML
                    Box {
                        IconButton(onClick = { menuAberto = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                        }
                        DropdownMenu(
                            expanded = menuAberto,
                            onDismissRequest = { menuAberto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Versão original (XML)") },
                                onClick = {
                                    menuAberto = false
                                    aoAbrirVersaoXml()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Só mostra o botão quando há lista (no estado vazio já existe um botão no meio da tela)
            if (uiState is ListaUiState.Conteudo) {
                ExtendedFloatingActionButton(
                    onClick = aoClicarNovoHabito,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Novo hábito") },
                    // Sem isto, o leitor de tela (TalkBack) anunciava só "botão", sem o nome
                    modifier = Modifier.semantics { contentDescription = "Novo hábito" }
                )
            }
        }
    ) { paddingInterno ->
        val modifier = Modifier.padding(paddingInterno)

        when (val estado = uiState) {
            is ListaUiState.Carregando -> EstadoCarregando(modifier)

            is ListaUiState.Vazia -> EstadoMensagem(
                titulo = "Nenhum hábito ainda",
                mensagem = "Crie o seu primeiro hábito para começar a acompanhar a sua rotina.",
                textoBotao = "Criar primeiro hábito",
                aoClicarBotao = aoClicarNovoHabito,
                modifier = modifier
            )

            is ListaUiState.Erro -> EstadoMensagem(
                titulo = "Algo deu errado",
                mensagem = estado.mensagem,
                textoBotao = "Tentar novamente",
                aoClicarBotao = { viewModel.carregarHabitos() },
                modifier = modifier
            )

            is ListaUiState.Conteudo -> LazyColumn(
                modifier = modifier,
                // Espaço extra embaixo para o último card não ficar atrás do botão "Novo hábito"
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.habitos, key = { it.id }) { habito ->
                    HabitoCard(
                        habito = habito,
                        aoClicar = { aoClicarHabito(habito.id) },
                        aoMarcarFeito = { viewModel.alternarFeitoHoje(habito) },
                        // Anima a entrada, saída e troca de posição dos itens
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}
