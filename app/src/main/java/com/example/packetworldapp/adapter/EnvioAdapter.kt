package com.example.packetworldapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.packetworldapp.R
import com.example.packetworldapp.poko.Envio

class EnvioAdapter(
    private var listaEnvios: List<Envio>,
    private val onClick: (Envio) -> Unit
) : RecyclerView.Adapter<EnvioAdapter.EnvioViewHolder>() {

    class EnvioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGuia: TextView = itemView.findViewById(R.id.tvNoGuia)
        val tvDestino: TextView = itemView.findViewById(R.id.tvDireccionDestino)
        val tvEstatus: TextView = itemView.findViewById(R.id.tvEstatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_envio, parent, false)
        return EnvioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnvioViewHolder, position: Int) {
        val envio = listaEnvios[position]
        holder.tvGuia.text = "Guía: ${envio.noGuia}"
        holder.tvDestino.text = envio.destinatarioDireccion
        holder.tvEstatus.text = envio.estatus

        holder.itemView.setOnClickListener { onClick(envio) }
    }

    override fun getItemCount(): Int = listaEnvios.size
}