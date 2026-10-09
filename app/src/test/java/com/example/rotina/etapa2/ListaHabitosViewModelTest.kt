package com.example.rotina.etapa2

import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import com.example.rotina.etapa2.ui.ListaHabitosViewModel
import com.example.rotina.etapa2.ui.ListaUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ListaHabitosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao = FakeHabitoDao()
    private val repository = HabitoRepository(dao)

    @Test
    fun semHabitos_mostraListaVazia() = runTest {
        val viewModel = ListaHabitosViewModel(repository)

        assertEquals(ListaUiState.Vazia, viewModel.uiState.value)
    }

    @Test
    fun comHabitos_mostraConteudoEmOrdemAlfabetica() = runTest {
        repository.salvar(Habito(nome = "Meditar", descricao = null, metaSemanal = 4))
        repository.salvar(Habito(nome = "Beber água", descricao = null, metaSemanal = 7))

        val viewModel = ListaHabitosViewModel(repository)

        val estado = viewModel.uiState.value as ListaUiState.Conteudo
        assertEquals(listOf("Beber água", "Meditar"), estado.habitos.map { it.nome })
    }

    @Test
    fun habitoCriadoDepois_apareceNaListaSozinho() = runTest {
        val viewModel = ListaHabitosViewModel(repository)
        assertEquals(ListaUiState.Vazia, viewModel.uiState.value)

        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))

        assertTrue(viewModel.uiState.value is ListaUiState.Conteudo)
    }

    @Test
    fun checkbox_marcaEDesmarcaEPersiste() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val viewModel = ListaHabitosViewModel(repository)

        val antes = (viewModel.uiState.value as ListaUiState.Conteudo).habitos.first()
        viewModel.alternarFeitoHoje(antes)

        val depois = (viewModel.uiState.value as ListaUiState.Conteudo).habitos.first()
        assertTrue(depois.estaFeitoHoje())
        assertTrue(dao.salvos.first().estaFeitoHoje()) // gravado no "banco"

        viewModel.alternarFeitoHoje(depois)

        assertFalse((viewModel.uiState.value as ListaUiState.Conteudo).habitos.first().estaFeitoHoje())
        assertFalse(dao.salvos.first().estaFeitoHoje())
    }

    @Test
    fun falhaAoCarregar_mostraErro_eTentarNovamenteRecupera() = runTest {
        dao.falharAoListar = true
        val viewModel = ListaHabitosViewModel(repository)

        assertTrue(viewModel.uiState.value is ListaUiState.Erro)

        dao.falharAoListar = false
        viewModel.carregarHabitos()

        assertEquals(ListaUiState.Vazia, viewModel.uiState.value)
    }
}
