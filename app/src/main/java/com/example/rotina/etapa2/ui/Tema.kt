package com.example.rotina.etapa2.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Tema do app. As cores padrão do Material 3 já têm bom contraste
 * entre texto e fundo, tanto no modo claro quanto no escuro.
 */
@Composable
fun RotinaTema(content: @Composable () -> Unit) {
    val cores = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = cores, content = content)
}
