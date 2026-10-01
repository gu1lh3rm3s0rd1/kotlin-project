package com.example.rotina.etapa2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetalheUiState {
    data object Carregando : DetalheUiState
    data object NaoEncontrado : DetalheUiState
    data class Conteudo(val habito: Habito) : DetalheUiState
}

class DetalheHabitoViewModel(
    private val repository: HabitoRepository,
    habitoId: Int
) : ViewModel() {

    // Transforma o Flow do banco em um StateFlow de estados da tela.
    // Quando o hábito muda no banco (ex.: marcado como feito), a tela atualiza sozinha.
    val uiState: StateFlow<DetalheUiState> = repository.buscarHabito(habitoId)
        .map { habito ->
            if (habito == null) DetalheUiState.NaoEncontrado else DetalheUiState.Conteudo(habito)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetalheUiState.Carregando
        )

    fun alternarFeitoHoje() {
        val estado = uiState.value
        if (estado is DetalheUiState.Conteudo) {
            viewModelScope.launch {
                repository.alternarFeitoHoje(estado.habito)
            }
        }
    }

    /**
     * Exclui o hábito e só depois chama [aoTerminar] (que volta para a lista).
     * Se voltássemos antes, o ViewModel seria destruído e a exclusão poderia ser cancelada.
     */
    fun excluir(aoTerminar: () -> Unit) {
        val estado = uiState.value
        if (estado is DetalheUiState.Conteudo) {
            viewModelScope.launch {
                repository.excluir(estado.habito)
                aoTerminar()
            }
        }
    }
}
