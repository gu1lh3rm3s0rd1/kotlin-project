package com.example.rotina.etapa2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/** Os quatro estados possíveis da tela de lista. */
sealed interface ListaUiState {
    data object Carregando : ListaUiState
    data object Vazia : ListaUiState
    data class Conteudo(val habitos: List<Habito>) : ListaUiState
    data class Erro(val mensagem: String) : ListaUiState
}

class ListaHabitosViewModel(private val repository: HabitoRepository) : ViewModel() {

    // Só o ViewModel altera o estado; a tela apenas lê (fluxo unidirecional)
    private val _uiState = MutableStateFlow<ListaUiState>(ListaUiState.Carregando)
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    private var jobCarregamento: Job? = null

    init {
        carregarHabitos()
    }

    fun carregarHabitos() {
        // Se já existe uma leitura em andamento, cancela antes de começar outra
        jobCarregamento?.cancel()
        _uiState.value = ListaUiState.Carregando

        // viewModelScope cancela a coroutine sozinho quando o ViewModel é destruído
        jobCarregamento = viewModelScope.launch {
            repository.listarHabitos()
                .catch {
                    _uiState.value = ListaUiState.Erro("Não foi possível carregar os hábitos.")
                }
                .collect { habitos ->
                    _uiState.value = if (habitos.isEmpty()) {
                        ListaUiState.Vazia
                    } else {
                        ListaUiState.Conteudo(habitos)
                    }
                }
        }
    }

    fun alternarFeitoHoje(habito: Habito) {
        viewModelScope.launch {
            repository.alternarFeitoHoje(habito)
        }
    }
}
