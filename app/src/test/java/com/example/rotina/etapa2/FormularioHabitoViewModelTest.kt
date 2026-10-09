package com.example.rotina.etapa2

import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import com.example.rotina.etapa2.ui.FormularioHabitoViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class FormularioHabitoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao = FakeHabitoDao()
    private val repository = HabitoRepository(dao)

    @Test
    fun salvarComCamposInvalidos_mostraErrosENaoSalva() = runTest {
        val viewModel = FormularioHabitoViewModel(repository, habitoId = null)

        viewModel.salvar()

        val estado = viewModel.uiState.value
        assertNotNull(estado.erros.nome)
        assertNotNull(estado.erros.meta)
        assertFalse(estado.salvo)
        assertTrue(dao.salvos.isEmpty())
    }

    @Test
    fun digitarNoCampo_apagaOErroDele() = runTest {
        val viewModel = FormularioHabitoViewModel(repository, habitoId = null)
        viewModel.salvar()

        viewModel.aoMudarNome("Ler")

        assertNull(viewModel.uiState.value.erros.nome)
        assertNotNull(viewModel.uiState.value.erros.meta) // os outros erros continuam
    }

    @Test
    fun salvarHabitoNovo_gravaDadosLimposEDescricaoVaziaComoNula() = runTest {
        val viewModel = FormularioHabitoViewModel(repository, habitoId = null)
        viewModel.aoMudarNome("  Meditar  ")
        viewModel.aoMudarDescricao("   ")
        viewModel.aoMudarMeta("4")

        viewModel.salvar()

        assertTrue(viewModel.uiState.value.salvo)
        val salvo = dao.salvos.single()
        assertEquals("Meditar", salvo.nome)
        assertNull(salvo.descricao)
        assertEquals(4, salvo.metaSemanal)
    }

    @Test
    fun editar_carregaDadosExistentesEMantemFeitoHoje() = runTest {
        val hoje = LocalDate.now().toEpochDay()
        repository.salvar(
            Habito(nome = "Ler", descricao = "Antes de dormir", metaSemanal = 5, ultimoDiaFeito = hoje)
        )
        val id = dao.salvos.first().id

        val viewModel = FormularioHabitoViewModel(repository, habitoId = id)

        val estado = viewModel.uiState.value
        assertTrue(estado.editando)
        assertFalse(estado.carregando)
        assertEquals("Ler", estado.nome)
        assertEquals("Antes de dormir", estado.descricao)
        assertEquals("5", estado.meta)

        viewModel.aoMudarNome("Ler 30 minutos")
        viewModel.salvar()

        val salvo = dao.salvos.single() // não criou um hábito novo
        assertEquals("Ler 30 minutos", salvo.nome)
        assertEquals(hoje, salvo.ultimoDiaFeito) // continua marcado como feito hoje
    }
}
