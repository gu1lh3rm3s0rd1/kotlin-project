package com.example.rotina.etapa2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Estado da tela de formulário. Os campos ficam como texto (String)
 * porque é o que o usuário digita; a conversão para número é feita ao salvar.
 */
data class FormularioUiState(
    val nome: String = "",
    val descricao: String = "",
    val meta: String = "",
    val erros: ErrosFormulario = ErrosFormulario(),
    val editando: Boolean = false,
    val carregando: Boolean = false,
    val salvando: Boolean = false,
    val salvo: Boolean = false
)

/**
 * O estado fica no ViewModel, então o que foi digitado não se perde ao girar a tela.
 * habitoId nulo = hábito novo; com valor = edição.
 */
class FormularioHabitoViewModel(
    private val repository: HabitoRepository,
    habitoId: Int?
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FormularioUiState(editando = habitoId != null, carregando = habitoId != null)
    )
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    // Guarda o hábito que está sendo editado, para não perder o "feito hoje" ao salvar
    private var habitoOriginal: Habito? = null

    init {
        if (habitoId != null) {
            carregarHabito(habitoId)
        }
    }

    private fun carregarHabito(id: Int) {
        viewModelScope.launch {
            // first() pega só o valor atual do banco (não fica observando)
            val habito = repository.buscarHabito(id).first()
            habitoOriginal = habito
            _uiState.value = _uiState.value.copy(
                nome = habito?.nome ?: "",
                descricao = habito?.descricao ?: "",
                meta = habito?.metaSemanal?.toString() ?: "",
                carregando = false
            )
        }
    }

    // Ao digitar, atualiza o campo e apaga o erro dele
    fun aoMudarNome(novoNome: String) {
        _uiState.value = _uiState.value.copy(
            nome = novoNome,
            erros = _uiState.value.erros.copy(nome = null)
        )
    }

    fun aoMudarDescricao(novaDescricao: String) {
        _uiState.value = _uiState.value.copy(
            descricao = novaDescricao,
            erros = _uiState.value.erros.copy(descricao = null)
        )
    }

    fun aoMudarMeta(novaMeta: String) {
        _uiState.value = _uiState.value.copy(
            meta = novaMeta,
            erros = _uiState.value.erros.copy(meta = null)
        )
    }

    fun salvar() {
        val estado = _uiState.value
        if (estado.salvando) return

        val erros = validarFormulario(estado.nome, estado.descricao, estado.meta)
        if (erros.temErro()) {
            _uiState.value = estado.copy(erros = erros)
            return
        }

        _uiState.value = estado.copy(salvando = true)

        viewModelScope.launch {
            val habito = Habito(
                id = habitoOriginal?.id ?: 0,
                nome = estado.nome.trim(),
                // Descrição em branco é salva como null (campo opcional)
                descricao = estado.descricao.trim().ifBlank { null },
                metaSemanal = estado.meta.trim().toInt(),
                ultimoDiaFeito = habitoOriginal?.ultimoDiaFeito
            )
            repository.salvar(habito)
            _uiState.value = _uiState.value.copy(salvando = false, salvo = true)
        }
    }
}
