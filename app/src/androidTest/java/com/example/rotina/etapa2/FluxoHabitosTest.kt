package com.example.rotina.etapa2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rotina.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes de interface: abrem o app de verdade e clicam nos botões como um usuário.
 * Atenção: apagam os hábitos salvos no aparelho onde rodam.
 */
@RunWith(AndroidJUnit4::class)
class FluxoHabitosTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Before
    fun limparBanco() {
        val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as RotinaApplication
        app.database.clearAllTables()
    }

    /** Espera até um texto aparecer na tela (as operações no banco são assíncronas). */
    private fun esperarTexto(texto: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasText(texto))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun esperarDescricao(descricao: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasContentDescription(descricao))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun fluxoCompleto_criarValidarMarcarEditarExcluir() {
        val cenario = ActivityScenario.launch(MainActivity::class.java)

        // 1. Lista vazia
        esperarTexto("Nenhum hábito ainda")
        composeRule.onNodeWithText("Criar primeiro hábito").performClick()

        // 2. Validação: salvar vazio mostra os erros e não sai da tela
        composeRule.onNodeWithText("Salvar").performClick()
        composeRule.onNodeWithText("Informe o nome do hábito.").assertIsDisplayed()
        composeRule.onNodeWithText("Informe um número de 1 a 7.").assertIsDisplayed()

        // 3. Meta fora do limite
        composeRule.onNodeWithText("Nome *").performTextInput("Beber água")
        composeRule.onNodeWithText("Descrição (opcional)").performTextInput("Garrafa por perto")
        composeRule.onNodeWithText("Meta semanal (1 a 7) *").performTextInput("9")
        composeRule.onNodeWithText("Salvar").performClick()
        composeRule.onNodeWithText("A meta deve ser de 1 a 7 vezes por semana.").assertIsDisplayed()

        // 4. O que foi digitado sobrevive à recriação da tela (igual à rotação)
        cenario.recreate()
        esperarTexto("Beber água")
        composeRule.onNodeWithText("Garrafa por perto").assertIsDisplayed()

        // 5. Corrige e salva: volta para a lista com o hábito
        composeRule.onNodeWithText("Meta semanal (1 a 7) *").performTextReplacement("7")
        composeRule.onNodeWithText("Salvar").performClick()
        esperarTexto("Meta: 7x por semana")

        // 6. Segundo hábito, sem descrição
        composeRule.onNodeWithContentDescription("Novo hábito").performClick()
        composeRule.onNodeWithText("Nome *").performTextInput("Ler 20 minutos")
        composeRule.onNodeWithText("Meta semanal (1 a 7) *").performTextInput("5")
        composeRule.onNodeWithText("Salvar").performClick()
        esperarTexto("Meta: 5x por semana")

        // 7. Checkbox da lista marca o hábito
        composeRule.onNodeWithContentDescription("Ler 20 minutos: pendente").performClick()
        esperarDescricao("Ler 20 minutos: feito hoje")

        // 8. Detalhe abre o hábito certo, já marcado, e sem descrição
        composeRule.onNodeWithText("Ler 20 minutos").performClick()
        esperarTexto("Status: feito hoje ✓")
        composeRule.onNodeWithText("Garrafa por perto").assertDoesNotExist()

        // 9. Desmarcar no detalhe e voltar: a lista mostra a mudança
        composeRule.onNodeWithText("Desmarcar feito hoje").performClick()
        esperarTexto("Status: pendente")
        composeRule.onNodeWithContentDescription("Voltar").performClick()
        esperarDescricao("Ler 20 minutos: pendente")

        // 10. Editar carrega os dados existentes e salva a alteração
        composeRule.onNodeWithText("Beber água").performClick()
        esperarTexto("Garrafa por perto")
        composeRule.onNodeWithContentDescription("Editar hábito").performClick()
        esperarTexto("Editar hábito")
        composeRule.onNodeWithText("Garrafa por perto").assertIsDisplayed()
        composeRule.onNodeWithText("Nome *").performTextReplacement("Beber 3L de água")
        composeRule.onNodeWithText("Salvar").performClick()
        esperarTexto("Beber 3L de água") // voltou para o detalhe atualizado

        // 11. Excluir pede confirmação; cancelar não apaga
        composeRule.onNodeWithContentDescription("Excluir hábito").performClick()
        composeRule.onNodeWithText("Cancelar").performClick()
        composeRule.onNodeWithText("Beber 3L de água").assertIsDisplayed()

        // 12. Confirmar a exclusão volta para a lista sem o hábito
        composeRule.onNodeWithContentDescription("Excluir hábito").performClick()
        composeRule.onNodeWithText("Excluir").performClick()
        esperarTexto("Meus hábitos")
        composeRule.onNodeWithText("Beber 3L de água").assertDoesNotExist()
        composeRule.onNodeWithText("Ler 20 minutos").assertIsDisplayed()

        cenario.close()
    }

    @Test
    fun versaoXml_abrePeloMenuNavegaEVolta() {
        val cenario = ActivityScenario.launch(MainActivity::class.java)
        esperarTexto("Meus hábitos")

        // Abre a versão em Views/XML pelo menu "Mais opções"
        composeRule.onNodeWithContentDescription("Mais opções").performClick()
        composeRule.onNodeWithText("Versão original (XML)").performClick()

        // Lista XML com os dados mockados
        onView(withText("Meus hábitos (versão XML)")).check(matches(isDisplayed()))
        onView(withText("Meditar")).check(matches(isDisplayed()))

        // Clicar no item abre o detalhe do hábito certo (id passado pela Intent)
        onView(withText("Meditar")).perform(click())
        onView(withId(R.id.textNomeDetalhe)).check(matches(withText("Meditar")))
        onView(withId(R.id.textMetaDetalhe)).check(matches(withText("Meta: 4x por semana")))
        onView(withId(R.id.textStatusDetalhe)).check(matches(withText("Pendente")))

        // O botão atualiza a tela
        onView(withId(R.id.buttonMarcarFeito)).perform(click())
        onView(withId(R.id.textStatusDetalhe)).check(matches(withText("Feito hoje")))

        // Voltar duas vezes retorna para a tela principal do app
        pressBack()
        onView(withText("Meus hábitos (versão XML)")).check(matches(isDisplayed()))
        pressBack()
        esperarTexto("Meus hábitos")
        composeRule.onNodeWithContentDescription("Mais opções").assertIsDisplayed()

        cenario.close()
    }
}
