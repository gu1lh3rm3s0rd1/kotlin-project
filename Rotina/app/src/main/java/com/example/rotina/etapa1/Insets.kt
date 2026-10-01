package com.example.rotina.etapa1

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Com targetSdk 35+ o Android desenha a tela atrás das barras do sistema
 * (edge-to-edge). Soma os insets ao padding original da view para que o
 * conteúdo não fique escondido sob a status bar / barra de navegação.
 */
fun aplicarInsetsDoSistema(view: View) {
    val esquerda = view.paddingLeft
    val topo = view.paddingTop
    val direita = view.paddingRight
    val base = view.paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        v.setPadding(
            esquerda + barras.left,
            topo + barras.top,
            direita + barras.right,
            base + barras.bottom
        )
        insets
    }
}
