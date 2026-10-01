package com.example.rotina.etapa1

/**
 * Modelo imutável usado apenas na Etapa 1 (dados mockados, sem Room).
 * `descricao` é opcional — tratado com segurança na tela de detalhe.
 */
data class HabitoMock(
    val id: String,
    val nome: String,
    val descricao: String?,
    val metaSemanal: Int,
    val feitoHoje: Boolean
)

object HabitosMockData {
    fun listaMock(): List<HabitoMock> = listOf(
        HabitoMock(
            id = "1",
            nome = "Beber 2L de água",
            descricao = "Manter garrafa por perto durante o dia.",
            metaSemanal = 7,
            feitoHoje = true
        ),
        HabitoMock(
            id = "2",
            nome = "Ler 20 minutos",
            descricao = null,
            metaSemanal = 5,
            feitoHoje = false
        ),
        HabitoMock(
            id = "3",
            nome = "Exercitar-se",
            descricao = "Academia ou caminhada de 30 min.",
            metaSemanal = 3,
            feitoHoje = false
        ),
        HabitoMock(
            id = "4",
            nome = "Meditar",
            descricao = null,
            metaSemanal = 4,
            feitoHoje = false
        )
    )
}
