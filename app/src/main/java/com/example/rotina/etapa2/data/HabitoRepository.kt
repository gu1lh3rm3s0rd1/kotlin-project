package com.example.rotina.etapa2.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Único ponto de acesso aos dados de hábitos.
 * Os ViewModels conversam com o Repository e não sabem que existe um Room por trás.
 */
class HabitoRepository(private val dao: HabitoDao) {

    fun listarHabitos(): Flow<List<Habito>> = dao.listarTodos()

    fun buscarHabito(id: Int): Flow<Habito?> = dao.buscarPorId(id)

    suspend fun salvar(habito: Habito) {
        // id 0 significa hábito novo (o Room gera o id automaticamente)
        if (habito.id == 0) {
            dao.inserir(habito)
        } else {
            dao.atualizar(habito)
        }
    }

    suspend fun excluir(habito: Habito) {
        dao.excluir(habito)
    }

    /** Marca como feito hoje, ou desmarca se já estava feito. */
    suspend fun alternarFeitoHoje(habito: Habito) {
        val novoDia = if (habito.estaFeitoHoje()) null else LocalDate.now().toEpochDay()
        dao.atualizar(habito.copy(ultimoDiaFeito = novoDia))
    }
}
