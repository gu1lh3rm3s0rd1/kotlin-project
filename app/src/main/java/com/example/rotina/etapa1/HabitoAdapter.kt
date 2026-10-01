package com.example.rotina.etapa1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rotina.R
import com.example.rotina.databinding.ItemHabitoBinding

class HabitoAdapter(
    private val habitos: List<HabitoMock>,
    private val aoClicarHabito: (HabitoMock) -> Unit
) : RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder>() {

    inner class HabitoViewHolder(val binding: ItemHabitoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val binding = ItemHabitoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HabitoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = habitos[position]
        with(holder.binding) {
            textNomeHabito.text = habito.nome
            textMetaHabito.text = root.context.getString(R.string.meta_semanal, habito.metaSemanal)
            imageStatusHabito.visibility = if (habito.feitoHoje) View.VISIBLE else View.INVISIBLE
            root.setOnClickListener { aoClicarHabito(habito) }
        }
    }

    override fun getItemCount(): Int = habitos.size
}
