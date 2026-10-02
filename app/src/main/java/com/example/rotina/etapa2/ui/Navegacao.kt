package com.example.rotina.etapa2.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.rotina.etapa1.ListaHabitosActivity
import com.example.rotina.etapa2.data.HabitoRepository

/**
 * Navegação do app com Navigation 3.
 * O back stack é uma lista de rotas: adicionar uma rota abre a tela,
 * remover a última volta para a tela anterior.
 */
@Composable
fun Navegacao(repository: HabitoRepository) {
    val backStack = rememberNavBackStack(ListaRota)
    val context = LocalContext.current

    // Volta para a tela anterior. Nunca remove a última tela:
    // o NavDisplay não aceita um back stack vazio (o app fecharia com erro).
    fun voltar() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { voltar() },
        // Faz cada tela ter o seu próprio ViewModel, que é descartado quando a tela sai do back stack
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ListaRota> {
                val viewModel = viewModel { ListaHabitosViewModel(repository) }
                ListaHabitosScreen(
                    viewModel = viewModel,
                    aoClicarHabito = { id -> backStack.add(DetalheRota(id)) },
                    aoClicarNovoHabito = { backStack.add(FormularioRota()) },
                    // Abre a versão feita com Views/XML por meio de uma Intent explícita
                    aoAbrirVersaoClassica = {
                        context.startActivity(Intent(context, ListaHabitosActivity::class.java))
                    }
                )
            }

            entry<DetalheRota> { rota ->
                val viewModel = viewModel { DetalheHabitoViewModel(repository, rota.habitoId) }
                DetalheHabitoScreen(
                    viewModel = viewModel,
                    aoVoltar = { voltar() },
                    aoEditar = { backStack.add(FormularioRota(rota.habitoId)) }
                )
            }

            entry<FormularioRota> { rota ->
                val viewModel = viewModel { FormularioHabitoViewModel(repository, rota.habitoId) }
                FormularioHabitoScreen(
                    viewModel = viewModel,
                    aoVoltar = { voltar() }
                )
            }
        }
    )
}
