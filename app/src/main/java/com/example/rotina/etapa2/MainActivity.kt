package com.example.rotina.etapa2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rotina.etapa2.ui.Navegacao
import com.example.rotina.etapa2.ui.RotinaTema

/** Etapa 2 — ponto de entrada da versão em Jetpack Compose. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repository = (application as RotinaApplication).repository

        setContent {
            RotinaTema {
                Navegacao(repository)
            }
        }
    }
}
