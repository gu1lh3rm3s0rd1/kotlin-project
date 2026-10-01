package com.example.rotina.etapa2.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/*
 * Rotas do Navigation 3. Cada tela é representada por uma classe.
 * Os argumentos da tela (como o id do hábito) ficam dentro da própria rota.
 * O @Serializable permite salvar o back stack quando a tela é recriada (ex.: rotação).
 */

@Serializable
data object ListaRota : NavKey

@Serializable
data class DetalheRota(val habitoId: Int) : NavKey

/** habitoId nulo = criar hábito novo; com valor = editar um hábito existente. */
@Serializable
data class FormularioRota(val habitoId: Int? = null) : NavKey
