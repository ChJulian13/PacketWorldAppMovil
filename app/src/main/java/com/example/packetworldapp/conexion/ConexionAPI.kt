package com.example.packetworldapp.conexion

import android.content.Context
import com.example.packetworldapp.poko.RespuestaHTTP
import com.example.packetworldapp.util.Constantes
import com.koushikdutta.ion.Ion

object ConexionAPI {
    fun peticionGET(context: Context, url: String, callback: (RespuestaHTTP) -> Unit) {
        val respuesta = RespuestaHTTP()
        Ion.with(context)
            .load(url)
            .asString()
            .withResponse()
            .setCallback { e, result ->
                if (e != null) {
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = e.message
                } else if (result != null) {
                    respuesta.codigo = result.getHeaders().code()
                    respuesta.contenido = result.getResult()
                } else {
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = "Respuesta nula"
                }
                callback(respuesta)
            }
    }

    fun peticionBody(
        context: Context,
        url: String,
        metodoHTTP: String,
        datos: Any,
        contentType: String,
        callback: (RespuestaHTTP) -> Unit
    ) {
        val respuesta = RespuestaHTTP()
        val builder = Ion.with(context).load(metodoHTTP, url)

        builder.setHeader("Content-Type", contentType)

        if (datos is Map<*, *>) {
            for ((k, v) in datos) {
                builder.setBodyParameter(k.toString(), v.toString())
            }
        } else if (datos is String) {
            builder.setStringBody(datos)
        } else if (datos is ByteArray) {
            builder.setByteArrayBody(datos)
        }

        builder.asString().withResponse().setCallback { e, result ->
            if (e != null) {
                respuesta.codigo = Constantes.ERROR_PETICION
                respuesta.contenido = e.message
            } else if (result != null) {
                respuesta.codigo = result.headers.code()
                respuesta.contenido = result.result
            } else {
                respuesta.codigo = Constantes.ERROR_PETICION
                respuesta.contenido = "Respuesta nula"
            }
            callback(respuesta)
        }
    }

    fun peticionSinBody(
        context: Context,
        url: String,
        metodoHTTP: String,
        callback: (RespuestaHTTP) -> Unit
    ) {
        val respuesta = RespuestaHTTP()
        Ion.with(context)
            .load(metodoHTTP, url)
            .asString()
            .withResponse()
            .setCallback { e, result ->
                if (e != null) {
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = e.message
                } else if (result != null) {
                    respuesta.codigo = result.getHeaders().code()
                    respuesta.contenido = result.getResult()
                } else {
                    respuesta.codigo = Constantes.ERROR_PETICION
                    respuesta.contenido = "Respuesta nula"
                }
                callback(respuesta)
            }
    }
}