package com.example.rotina.etapa2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rotina.etapa2.data.Habito

/**
 * Card de um hábito, reutilizado em cada item da lista.
 * O Checkbox já tem área de toque de 48dp, que é o mínimo recomendado.
 */
@Composable
fun HabitoCard(
    habito: Habito,
    aoClicar: () -> Unit,
    aoMarcarFeito: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feito = habito.estaFeitoHoje()

    Card(onClick = aoClicar, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = habito.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Meta: ${habito.metaSemanal}x por semana",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(
                checked = feito,
                onCheckedChange = { aoMarcarFeito() },
                // Texto lido pelo leitor de tela (TalkBack)
                modifier = Modifier.semantics {
                    contentDescription = if (feito) {
                        "${habito.nome}: feito hoje"
                    } else {
                        "${habito.nome}: pendente"
                    }
                }
            )
        }
    }
}

@Composable
fun EstadoCarregando(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Carregando...", style = MaterialTheme.typography.bodyLarge)
    }
}

/** Mensagem centralizada com um botão. Usada para os estados de lista vazia e de erro. */
@Composable
fun EstadoMensagem(
    titulo: String,
    mensagem: String,
    textoBotao: String,
    aoClicarBotao: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = aoClicarBotao) {
            Text(text = textoBotao)
        }
    }
}
