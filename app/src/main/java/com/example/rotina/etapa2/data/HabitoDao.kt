package com.example.rotina.etapa2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Consultas do Room. As leituras devolvem Flow: sempre que a tabela muda,
 * o Flow emite a lista nova e a tela se atualiza sozinha.
 * As escritas são `suspend` para rodarem dentro de uma coroutine.
 */
@Dao
interface HabitoDao {

    @Query("SELECT * FROM habitos ORDER BY nome")
    fun listarTodos(): Flow<List<Habito>>

    @Query("SELECT * FROM habitos WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Habito?>

    @Insert
    suspend fun inserir(habito: Habito)

    @Update
    suspend fun atualizar(habito: Habito)

    @Delete
    suspend fun excluir(habito: Habito)
}
