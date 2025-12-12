package com.example.packetworldapp.poko

data class Conductor(
    val idColaborador: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val curp: String,
    val correo: String,
    val noPersonal: String,
    val fotoBase64: String?,
    val idRol: Int,
    val idSucursal: Int,
    val numeroLicencia: String

)