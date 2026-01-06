package com.example.packetworldapp.dominio

import android.content.Context
import com.example.packetworldapp.conexion.ConexionAPI
import com.example.packetworldapp.dto.Respuesta
import com.example.packetworldapp.poko.Conductor
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import java.net.HttpURLConnection

object ColaboradorImp {

    fun editarColaborador(
        context: Context,
        conductor: Conductor,
        callback: (Boolean, String) -> Unit
    ) {
        val url = "${Constantes.URL_API}colaborador/editar"
        val gson = Gson()
        val jsonBody = gson.toJson(conductor)

        ConexionAPI.peticionBody(
            context, url, "PUT", jsonBody, "application/json"
        ) { respuestaAPI ->
            if (respuestaAPI.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val respuesta = gson.fromJson(respuestaAPI.contenido, Respuesta::class.java)
                    callback(!respuesta.error, respuesta.mensaje)
                } catch (e: Exception) {
                    callback(false, "Error al procesar la respuesta.")
                }
            } else {
                callback(false, "Error al actualizar: ${respuestaAPI.codigo}")
            }
        }
    }

    fun subirFotoPerfil(
        context: Context,
        idColaborador: Int,
        fotoBytes: ByteArray,
        callback: (Boolean, String) -> Unit
    ) {
        val url = "${Constantes.URL_API}colaborador/subir-fotografia/$idColaborador"

        ConexionAPI.peticionBody(
            context,
            url,
            "PUT",
            fotoBytes,
            "application/octet-stream"
        ) { respuestaAPI ->
            if (respuestaAPI.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val respuestaServidor = gson.fromJson(respuestaAPI.contenido, Respuesta::class.java)
                    callback(!respuestaServidor.error, respuestaServidor.mensaje)
                } catch (e: Exception) {
                    callback(false, "Error al interpretar respuesta")
                }
            } else {
                callback(false, "Error al subir imagen: ${respuestaAPI.codigo}")
            }
        }
    }

    fun obtenerFotoPerfil(context: Context, idColaborador: Int, callback: (String?) -> Unit) {
        val url = "${Constantes.URL_API}colaborador/obtener-fotografia/$idColaborador"

        ConexionAPI.peticionGET(context, url) { respuesta ->
            if (respuesta.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val colaborador = gson.fromJson(respuesta.contenido, Conductor::class.java)
                    callback(colaborador.fotoBase64)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun obtenerDatosPorId(context: Context, idColaborador: Int, callback: (Conductor?) -> Unit) {
        val url = "${Constantes.URL_API}colaborador/obtener/$idColaborador"

        ConexionAPI.peticionGET(context, url) { respuesta ->
            if (respuesta.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val conductor = gson.fromJson(respuesta.contenido, Conductor::class.java)
                    callback(conductor)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun cambiarPassword(
        context: Context,
        idColaborador: Int,
        passActual: String,
        passNueva: String,
        callback: (Boolean, String) -> Unit
    ) {
        val url = "${Constantes.URL_API}colaborador/cambiar-password"

        val parametros = mapOf(
            "idColaborador" to idColaborador,
            "passwordActual" to passActual,
            "passwordNueva" to passNueva
        )

        ConexionAPI.peticionBody(
            context,
            url,
            "PUT",
            parametros,
            "application/x-www-form-urlencoded"
        ) { respuestaHTTP ->

            if (respuestaHTTP.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val respuestaServidor = gson.fromJson(respuestaHTTP.contenido, Respuesta::class.java)
                    callback(!respuestaServidor.error, respuestaServidor.mensaje)
                } catch (e: Exception) {
                    callback(false, "Error al procesar la respuesta del servidor")
                }
            } else {
                callback(false, "Error de red: ${respuestaHTTP.codigo}")
            }
        }
    }
}