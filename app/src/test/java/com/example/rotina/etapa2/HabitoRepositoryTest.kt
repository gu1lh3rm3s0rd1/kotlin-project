package com.example.rotina.etapa2

import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HabitoRepositoryTest {

    private val dao = FakeHabitoDao()
    private val repository = HabitoRepository(dao)

    @Test
    fun salvarHabitoNovo_inserePelaPrimeiraVez() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))

        assertEquals(1, dao.salvos.size)
        assertEquals("Ler", dao.salvos.first().nome)
    }

    @Test
    fun salvarHabitoExistente_atualizaSemDuplicar() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val salvo = dao.salvos.first()

        repository.salvar(salvo.copy(nome = "Ler 30 minutos"))

        assertEquals(1, dao.salvos.size)
        assertEquals("Ler 30 minutos", dao.salvos.first().nome)
    }

    @Test
    fun alternarFeitoHoje_marcaEDesmarcaNoBanco() = runTest {
        repository.salvar(Habito(nome = "Meditar", descricao = null, metaSemanal = 4))

        repository.alternarFeitoHoje(dao.salvos.first())
        assertTrue(dao.salvos.first().estaFeitoHoje())
        assertEquals(LocalDate.now().toEpochDay(), dao.salvos.first().ultimoDiaFeito)

        repository.alternarFeitoHoje(dao.salvos.first())
        assertFalse(dao.salvos.first().estaFeitoHoje())
        assertNull(dao.salvos.first().ultimoDiaFeito)
    }

    @Test
    fun habitoFeitoOntem_naoContaComoFeitoHoje() {
        val ontem = LocalDate.now().minusDays(1).toEpochDay()
        val habito = Habito(nome = "Correr", descricao = null, metaSemanal = 2, ultimoDiaFeito = ontem)

        assertFalse(habito.estaFeitoHoje())
    }

    @Test
    fun excluir_removeDoBanco() = runTest {
        repository.salvar(Habito(nome = "Ler", descricao = null, metaSemanal = 3))

        repository.excluir(dao.salvos.first())

        assertTrue(dao.salvos.isEmpty())
    }
}
