package com.example.packetworldapp.poko

data class Paquete(
    val idPaquete: Int,
    val idEnvio: Int,
    val descripcion: String,
    val peso: Double,
    val alto: Double,
    val ancho: Double,
    val profundidad: Double
) {
    fun getDimensiones(): String {
        return "${alto}x${ancho}x${profundidad} cm"
    }
}