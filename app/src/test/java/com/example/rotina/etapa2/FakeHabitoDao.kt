package com.example.rotina.etapa2

import com.example.rotina.etapa2.data.Habito
import com.example.rotina.etapa2.data.HabitoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * DAO "de mentira" para os testes: guarda os hábitos numa lista em memória
 * em vez de usar o banco Room. Funciona igual ao DAO real: as leituras são
 * Flow e emitem de novo sempre que a lista muda.
 */
class FakeHabitoDao : HabitoDao {

    private val habitos = MutableStateFlow<List<Habito>>(emptyList())
    private var proximoId = 1

    /** Quando true, a leitura da lista falha (para testar o estado de erro). */
    var falharAoListar = false

    /** Lista atual "salva no banco", para conferir nos testes. */
    val salvos: List<Habito> get() = habitos.value

    override fun listarTodos(): Flow<List<Habito>> {
        if (falharAoListar) {
            return flow { throw IllegalStateException("Erro simulado no banco") }
        }
        return habitos.map { lista -> lista.sortedBy { it.nome } }
    }

    override fun buscarPorId(id: Int): Flow<Habito?> =
        habitos.map { lista -> lista.find { it.id == id } }

    override suspend fun inserir(habito: Habito) {
        habitos.value = habitos.value + habito.copy(id = proximoId++)
    }

    override suspend fun atualizar(habito: Habito) {
        habitos.value = habitos.value.map { if (it.id == habito.id) habito else it }
    }

    override suspend fun excluir(habito: Habito) {
        habitos.value = habitos.value.filter { it.id != habito.id }
    }
}
