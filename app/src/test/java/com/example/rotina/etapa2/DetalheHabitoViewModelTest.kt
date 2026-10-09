package com.example.rotina.etapa2

import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import com.example.rotina.etapa2.ui.DetalheHabitoViewModel
import com.example.rotina.etapa2.ui.DetalheUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheHabitoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao = FakeHabitoDao()
    private val repository = HabitoRepository(dao)

    @Test
    fun idInexistente_mostraNaoEncontrado() = runTest {
        val viewModel = DetalheHabitoViewModel(repository, habitoId = 99)
        // O estado só é calculado enquanto alguém observa (como a tela faz)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(DetalheUiState.NaoEncontrado, viewModel.uiState.value)
    }

    @Test
    fun marcarFeito_atualizaATelaEOBanco() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val viewModel = DetalheHabitoViewModel(repository, habitoId = dao.salvos.first().id)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.alternarFeitoHoje()

        val estado = viewModel.uiState.value as DetalheUiState.Conteudo
        assertTrue(estado.habito.estaFeitoHoje())
        assertTrue(dao.salvos.first().estaFeitoHoje())
    }

    @Test
    fun excluir_removeDoBancoESoDepoisAvisaParaVoltar() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val viewModel = DetalheHabitoViewModel(repository, habitoId = dao.salvos.first().id)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        var voltou = false

        viewModel.excluir(aoTerminar = {
            // Quando a tela volta, o hábito já precisa ter sido apagado
            assertTrue(dao.salvos.isEmpty())
            voltou = true
        })

        assertTrue(voltou)
    }
}
