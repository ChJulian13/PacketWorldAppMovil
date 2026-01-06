package com.example.packetworldapp.dominio

import android.content.Context
import com.example.packetworldapp.conexion.ConexionAPI
import com.example.packetworldapp.poko.Paquete
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection

object PaqueteImp {
    fun obtenerPaquetes(context: Context, idEnvio: Int, callback: (List<Paquete>?) -> Unit) {
        val url = "${Constantes.URL_API}paquete/obtener-paquetes-envio/$idEnvio"

        ConexionAPI.peticionGET(context, url) { respuesta ->
            if (respuesta.codigo == HttpURLConnection.HTTP_OK) {
                try {
                    val gson = Gson()
                    val tipoLista = object : TypeToken<List<Paquete>>() {}.type
                    val lista: List<Paquete> = gson.fromJson(respuesta.contenido, tipoLista)
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