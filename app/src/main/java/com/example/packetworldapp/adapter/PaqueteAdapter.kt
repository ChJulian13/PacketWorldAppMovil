package com.example.packetworldapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.packetworldapp.databinding.ItemPaqueteBinding
import com.example.packetworldapp.poko.Paquete

class PaqueteAdapter(private val lista: List<Paquete>) :
    RecyclerView.Adapter<PaqueteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPaqueteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaqueteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvDescripcionPaquete.text = item.descripcion
        holder.binding.tvPeso.text = "Peso: ${item.peso} kg"
        holder.binding.tvDimensiones.text = "Dim: ${item.getDimensiones()}"
    }

    override fun getItemCount(): Int = lista.size
}