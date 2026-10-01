package com.example.rotina.etapa1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rotina.databinding.ActivityListaHabitosBinding

/**
 * Etapa 1 — Tela 1.
 * Lista hábitos mockados e navega para o detalhe via Intent explícita,
 * passando o id do hábito clicado.
 */
class ListaHabitosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaHabitosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityListaHabitosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsetsDoSistema(binding.root)

        val habitos = HabitosMockData.listaMock()

        binding.recyclerHabitos.layoutManager = LinearLayoutManager(this)
        binding.recyclerHabitos.adapter = HabitoAdapter(habitos) { habitoSelecionado ->
            val intent = Intent(this, DetalheHabitoActivity::class.java).apply {
                putExtra(EXTRA_HABITO_ID, habitoSelecionado.id)
            }
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_HABITO_ID = "extra_habito_id"
    }
}
