package com.example.rotina.etapa1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rotina.R
import com.example.rotina.databinding.ActivityDetalheHabitoBinding

/**
 * Etapa 1 — Tela 2.
 * Recebida via Intent explícita (ListaHabitosActivity.EXTRA_HABITO_ID).
 * O botão "Marcar como feito hoje" é a interação obrigatória que
 * atualiza a interface sem trocar de tela.
 */
class DetalheHabitoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheHabitoBinding
    private var feitoHoje: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheHabitoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsetsDoSistema(binding.root)

        val habitoId = intent.getStringExtra(ListaHabitosActivity.EXTRA_HABITO_ID)
        val habito = HabitosMockData.listaMock().firstOrNull { it.id == habitoId }

        if (habito == null) {
            binding.textNomeDetalhe.text = getString(R.string.habito_nao_encontrado)
            binding.buttonMarcarFeito.isEnabled = false
            return
        }

        // Após rotação, restaura o que o usuário marcou em vez do valor do mock
        feitoHoje = savedInstanceState?.getBoolean(ESTADO_FEITO_HOJE) ?: habito.feitoHoje

        binding.textNomeDetalhe.text = habito.nome
        // `descricao` é opcional — trata o caso nulo sem quebrar a UI
        binding.textDescricaoDetalhe.text = habito.descricao ?: ""
        binding.textDescricaoDetalhe.visibility =
            if (habito.descricao.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.textMetaDetalhe.text = getString(R.string.meta_semanal, habito.metaSemanal)

        atualizarStatus()

        binding.buttonMarcarFeito.setOnClickListener {
            feitoHoje = !feitoHoje
            atualizarStatus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ESTADO_FEITO_HOJE, feitoHoje)
    }

    private fun atualizarStatus() {
        binding.textStatusDetalhe.text = if (feitoHoje) {
            getString(R.string.status_feito)
        } else {
            getString(R.string.status_pendente)
        }
        binding.buttonMarcarFeito.text = if (feitoHoje) {
            getString(R.string.ja_feito_hoje)
        } else {
            getString(R.string.marcar_feito_hoje)
        }
    }

    companion object {
        private const val ESTADO_FEITO_HOJE = "estado_feito_hoje"
    }
}
