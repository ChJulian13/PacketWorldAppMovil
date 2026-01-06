package com.example.packetworldapp.dominio

import android.content.Context
import com.example.packetworldapp.conexion.ConexionAPI
import com.example.packetworldapp.dto.RSAutenticacionConductor
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import java.net.HttpURLConnection

object InicioSesionImp {
    fun verificarCredenciales(
        context: Context,
        noPersonal: String,
        password: String,
        callback: (RSAutenticacionConductor) -> Unit
    ) {
        val respuestaFinal = RSAutenticacionConductor()
        val parametros = mapOf("noPersonal" to noPersonal, "password" to password)
        val URL = "${Constantes.URL_API}autenticacion/movil"

        ConexionAPI.peticionBody(
            context = context,
            url = URL,
            metodoHTTP = "POST",
            datos = parametros,
            contentType = "application/x-www-form-urlencoded"
        ) { respuestaAPI ->
            if (respuestaAPI.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val resp = gson.fromJson(respuestaAPI.contenido, RSAutenticacionConductor::class.java)
                    callback(resp)
                } catch (e: Exception) {
                    respuestaFinal.error = true
                    respuestaFinal.mensaje = "Lo sentimos hubo un error al obtener la información, intentelo más tarde."
                    callback(respuestaFinal)
                }
            } else {
                respuestaFinal.error = true
                respuestaFinal.mensaje = when (respuestaAPI.codigo) {
                    Constantes.ERROR_MALFORMED_URL -> Constantes.MSJ_ERROR_URL
                    Constantes.ERROR_PETICION -> Constantes.MSJ_ERROR_PETICION
                    HttpURLConnection.HTTP_BAD_REQUEST -> "Datos requeridos para poder realizar la operación solicitada."
                    else -> "Lo sentimos hay problemas para verificar sus credenciales en este momento."
                }
                callback(respuestaFinal)
            }
        }
    }
}
