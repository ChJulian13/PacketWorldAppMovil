package com.example.packetworldapp.util

import android.util.Patterns
import java.util.regex.Pattern

object Validaciones {
    private val PATRON_SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")

    private val PATRON_LICENCIA = Pattern.compile("^[A-Z0-9\\-]{5,20}$")


    fun esVacio(texto: String?): Boolean {
        return texto.isNullOrEmpty() || texto.trim().isEmpty()
    }

    fun esSoloLetras(texto: String): Boolean {
        return PATRON_SOLO_LETRAS.matcher(texto).matches()
    }

    fun esCorreoValido(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    fun esCurpValida(curp: String): Boolean {
        return curp.length == 18 && curp.matches(Regex("^[A-Z0-9]+$"))
    }

    fun esLicenciaValida(licencia: String): Boolean {
        return PATRON_LICENCIA.matcher(licencia).matches()
    }

    fun limpiarTextoMayusculas(texto: String): String {
        return texto.trim().uppercase()
    }
}