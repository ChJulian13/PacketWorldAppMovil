package com.example.packetworldapp.poko

import java.io.Serializable

data class Envio(
    val idEnvio: Int,
    val noGuia: String,
    val costo: Double,

    val sucursal: String,
    val sucursalDireccion: String,
    val destinatarioDireccion: String,
    val destinatarioNombre: String,
    val destinatarioApellidoPaterno: String,
    val destinatarioApellidoMaterno: String?,

    val cliente: String,
    val clienteTelefono: String,
    val clienteCorreo: String,

    val estatus: String
) : Serializable
