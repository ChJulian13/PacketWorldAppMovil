package com.example.packetworldapp.dominio

import android.content.Context
import com.example.packetworldapp.conexion.ConexionAPI
import com.example.packetworldapp.poko.EstatusEnvio
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection

object CatalogoImp {

    fun obtenerEstatusEnvio(context: Context, callback: (List<EstatusEnvio>?) -> Unit) {
        val url = "${Constantes.URL_API}catalogo/obtener-estatus-envios"

        ConexionAPI.peticionGET(context, url) { respuesta ->
            if (respuesta.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val tipoLista = object : TypeToken<List<EstatusEnvio>>() {}.type
                    val lista: List<EstatusEnvio> = gson.fromJson(respuesta.contenido, tipoLista)
                    callback(lista)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }
}