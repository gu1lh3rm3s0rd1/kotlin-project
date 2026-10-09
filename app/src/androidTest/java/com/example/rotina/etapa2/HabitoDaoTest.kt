package com.example.rotina.etapa2

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoDao
import com.example.rotina.etapa2.data.RotinaDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testa as consultas do Room de verdade, usando um banco temporário em memória
 * (é apagado no fim do teste e não mexe nos dados do app).
 */
@RunWith(AndroidJUnit4::class)
class HabitoDaoTest {

    private lateinit var database: RotinaDatabase
    private lateinit var dao: HabitoDao

    @Before
    fun criarBanco() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RotinaDatabase::class.java).build()
        dao = database.habitoDao()
    }

    @After
    fun fecharBanco() {
        database.close()
    }

    @Test
    fun inserir_listaEmOrdemAlfabeticaComIdGerado() = runBlocking {
        dao.inserir(Habito(nome = "Meditar", descricao = null, metaSemanal = 4))
        dao.inserir(Habito(nome = "Beber água", descricao = "Garrafa por perto", metaSemanal = 7))

        val habitos = dao.listarTodos().first()

        assertEquals(listOf("Beber água", "Meditar"), habitos.map { it.nome })
        assertTrue(habitos.all { it.id > 0 })
        assertNull(habitos.last().descricao)
    }

    @Test
    fun atualizar_alteraOHabitoNoBanco() = runBlocking {
        dao.inserir(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val salvo = dao.listarTodos().first().first()

        dao.atualizar(salvo.copy(nome = "Ler 30 minutos", ultimoDiaFeito = 100L))

        val atualizado = dao.buscarPorId(salvo.id).first()
        assertEquals("Ler 30 minutos", atualizado?.nome)
        assertEquals(100L, atualizado?.ultimoDiaFeito)
    }

    @Test
    fun excluir_removeDoBanco() = runBlocking {
        dao.inserir(Habito(nome = "Ler", descricao = null, metaSemanal = 3))
        val salvo = dao.listarTodos().first().first()

        dao.excluir(salvo)

        assertTrue(dao.listarTodos().first().isEmpty())
        assertNull(dao.buscarPorId(salvo.id).first())
    }
}
