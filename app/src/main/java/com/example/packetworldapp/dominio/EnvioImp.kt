package com.example.packetworldapp.dominio

import android.content.Context
import com.example.packetworldapp.conexion.ConexionAPI
import com.example.packetworldapp.dto.Respuesta
import com.example.packetworldapp.poko.Envio
import com.example.packetworldapp.poko.EnvioHistorialEstatus
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection

object EnvioImp {
    fun obtenerEnviosPorConductor(
        context: Context,
        idConductor: Int,
        callback: (List<Envio>?, String?) -> Unit
    ) {
        val url = "${Constantes.URL_API}envio/obtener-envios-conductor/$idConductor"

        ConexionAPI.peticionGET(context, url) { respuestaAPI ->
            if (respuestaAPI.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val tipoLista = object : TypeToken<List<Envio>>() {}.type
                    val listaEnvios: List<Envio> = gson.fromJson(respuestaAPI.contenido, tipoLista)

                    callback(listaEnvios, null)
                } catch (e: Exception) {
                    callback(null, "Error al procesar los datos de los envíos.")
                }
            } else {
                val mensajeError = when (respuestaAPI.codigo) {
                    Constantes.ERROR_PETICION -> Constantes.MSJ_ERROR_PETICION
                    Constantes.ERROR_MALFORMED_URL -> Constantes.MSJ_ERROR_URL
                    else -> "No se pudieron obtener los envíos. Código: ${respuestaAPI.codigo}"
                }
                callback(null, mensajeError)
            }
        }
    }

    fun actualizarEstatus(
        context: Context,
        datosActualizacion: EnvioHistorialEstatus,
        callback: (Boolean, String) -> Unit
    ) {
        val url = "${Constantes.URL_API}envio/actualizar-estatus"

        val gson = Gson()
        val jsonBody = gson.toJson(datosActualizacion)

        ConexionAPI.peticionBody(
            context = context,
            url = url,
            metodoHTTP = "POST",
            datos = jsonBody,
            contentType = "application/json"
        ) { respuestaAPI ->

            if (respuestaAPI.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val respuestaServidor = gson.fromJson(respuestaAPI.contenido, Respuesta::class.java)

                    callback(!respuestaServidor.error, respuestaServidor.mensaje)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false, "Error al procesar la respuesta del servidor.")
                }
            } else {
                val msj = when (respuestaAPI.codigo) {
                    Constantes.ERROR_PETICION -> Constantes.MSJ_ERROR_PETICION
                    Constantes.ERROR_MALFORMED_URL -> Constantes.MSJ_ERROR_URL
                    else -> "Error del servidor. Código: ${respuestaAPI.codigo}"
                }
                callback(false, msj)
            }
        }
    }
}