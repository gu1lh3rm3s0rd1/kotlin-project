package com.example.rotina.etapa2.ui

const val TAMANHO_MINIMO_NOME = 3
const val TAMANHO_MAXIMO_NOME = 40
const val TAMANHO_MAXIMO_DESCRICAO = 120

/** Mensagem de erro de cada campo do formulário. `null` = campo sem erro. */
data class ErrosFormulario(
    val nome: String? = null,
    val descricao: String? = null,
    val meta: String? = null
) {
    fun temErro(): Boolean = nome != null || descricao != null || meta != null
}

/**
 * Regras do formulário de hábito. Fica separada da tela para poder ser
 * testada com testes unitários simples (veja ValidacaoHabitoTest).
 */
fun validarFormulario(nome: String, descricao: String, meta: String): ErrosFormulario {
    val nomeLimpo = nome.trim()
    val erroNome = when {
        nomeLimpo.isEmpty() -> "Informe o nome do hábito."
        nomeLimpo.length < TAMANHO_MINIMO_NOME -> "O nome precisa ter pelo menos $TAMANHO_MINIMO_NOME caracteres."
        nomeLimpo.length > TAMANHO_MAXIMO_NOME -> "O nome pode ter no máximo $TAMANHO_MAXIMO_NOME caracteres."
        else -> null
    }

    val erroDescricao = if (descricao.trim().length > TAMANHO_MAXIMO_DESCRICAO) {
        "A descrição pode ter no máximo $TAMANHO_MAXIMO_DESCRICAO caracteres."
    } else {
        null
    }

    // toIntOrNull devolve null se o texto não for um número (ex.: "", "abc")
    val metaNumero = meta.trim().toIntOrNull()
    val erroMeta = when {
        metaNumero == null -> "Informe um número de 1 a 7."
        metaNumero !in 1..7 -> "A meta deve ser de 1 a 7 vezes por semana."
        else -> null
    }

    return ErrosFormulario(nome = erroNome, descricao = erroDescricao, meta = erroMeta)
}
