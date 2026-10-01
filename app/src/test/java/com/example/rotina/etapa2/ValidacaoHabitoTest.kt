package com.example.rotina.etapa2

import com.example.rotina.etapa2.ui.validarFormulario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacaoHabitoTest {

    @Test
    fun formularioValido_naoTemErros() {
        val erros = validarFormulario(nome = "Ler 20 minutos", descricao = "", meta = "5")

        assertFalse(erros.temErro())
    }

    @Test
    fun nomeVazio_mostraErro() {
        val erros = validarFormulario(nome = "   ", descricao = "", meta = "5")

        assertEquals("Informe o nome do hábito.", erros.nome)
    }

    @Test
    fun nomeCurto_mostraErro() {
        val erros = validarFormulario(nome = "Ab", descricao = "", meta = "5")

        assertNotNull(erros.nome)
    }

    @Test
    fun nomeMuitoLongo_mostraErro() {
        val erros = validarFormulario(nome = "a".repeat(41), descricao = "", meta = "5")

        assertNotNull(erros.nome)
    }

    @Test
    fun descricaoMuitoLonga_mostraErro() {
        val erros = validarFormulario(nome = "Meditar", descricao = "a".repeat(121), meta = "3")

        assertNotNull(erros.descricao)
        assertNull(erros.nome)
    }

    @Test
    fun metaQueNaoEhNumero_mostraErro() {
        val erros = validarFormulario(nome = "Meditar", descricao = "", meta = "abc")

        assertEquals("Informe um número de 1 a 7.", erros.meta)
    }

    @Test
    fun metaForaDoIntervalo_mostraErro() {
        assertTrue(validarFormulario("Meditar", "", "0").temErro())
        assertTrue(validarFormulario("Meditar", "", "8").temErro())
    }

    @Test
    fun metaNosLimites_ehValida() {
        assertNull(validarFormulario("Meditar", "", "1").meta)
        assertNull(validarFormulario("Meditar", "", "7").meta)
    }
}
