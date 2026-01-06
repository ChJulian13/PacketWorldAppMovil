package com.example.packetworldapp.poko

data class EstatusEnvio(
    val idEstatusEnvio: Int,
    val nombre: String
) {
    override fun toString(): String {
        return nombre
    }
}