package com.example.rotina.etapa2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Modelo imutável do hábito, salvo como uma tabela no Room.
 *
 * - `descricao` é opcional (pode ser nula).
 * - `ultimoDiaFeito` guarda o dia em que o hábito foi marcado como feito
 *   (em "epochDay", ou seja, número de dias desde 01/01/1970). Fica nulo
 *   enquanto o hábito nunca foi feito.
 */
@Entity(tableName = "habitos")
data class Habito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String?,
    val metaSemanal: Int,
    val ultimoDiaFeito: Long? = null
) {
    fun estaFeitoHoje(): Boolean = ultimoDiaFeito == LocalDate.now().toEpochDay()
}
