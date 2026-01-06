package com.example.packetworldapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.packetworldapp.adapter.PaqueteAdapter
import com.example.packetworldapp.databinding.ActivityDetalleEnvioBinding
import com.example.packetworldapp.dominio.CatalogoImp
import com.example.packetworldapp.dominio.EnvioImp
import com.example.packetworldapp.dominio.PaqueteImp
import com.example.packetworldapp.poko.Envio
import com.example.packetworldapp.poko.EnvioHistorialEstatus
import com.example.packetworldapp.poko.EstatusEnvio

class DetalleEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEnvioBinding
    private var envioActual: Envio? = null
    private var idConductor: Int = 0

    private var estatusSeleccionado: EstatusEnvio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        envioActual = intent.getSerializableExtra("envio") as? Envio
        idConductor = intent.getIntExtra("idConductor", 0)

        if (envioActual != null) {
            inicializarUI()
        } else {
            Toast.makeText(this, "Error al cargar el envío", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun inicializarUI() {
        with(binding) {
            tvGuiaDetalle.text = "Guía: ${envioActual?.noGuia}"
            tvOrigen.text = envioActual?.sucursal
            tvDestinatario.text = "${envioActual?.destinatarioNombre} ${envioActual?.destinatarioApellidoPaterno}"
            tvDireccion.text = envioActual?.destinatarioDireccion

            tvClienteNombre.text = envioActual?.cliente
            tvClienteTelefono.text = "Tel: ${envioActual?.clienteTelefono}"
            tvClienteCorreo.text = envioActual?.clienteCorreo
        }

        binding.rvPaquetes.layoutManager = LinearLayoutManager(this)
        cargarPaquetes()

        cargarCatalogoEstatus()

        binding.btnGuardar.setOnClickListener {
            validarYGuardar()
        }
    }

    private fun cargarPaquetes() {
        PaqueteImp.obtenerPaquetes(this, envioActual!!.idEnvio) { listaPaquetes ->
            if (!listaPaquetes.isNullOrEmpty()) {
                val adaptador = PaqueteAdapter(listaPaquetes)
                binding.rvPaquetes.adapter = adaptador
                binding.rvPaquetes.visibility = View.VISIBLE
                binding.tvSinPaquetes.visibility = View.GONE
            } else {
                binding.rvPaquetes.visibility = View.GONE
                binding.tvSinPaquetes.visibility = View.VISIBLE
            }
        }
    }

    private fun cargarCatalogoEstatus() {
        CatalogoImp.obtenerEstatusEnvio(this) { listaEstatus ->
            if (listaEstatus != null && listaEstatus.isNotEmpty()) {

                val estatusPermitidos = listOf(
                    "En tránsito",
                    "Detenido",
                    "Entregado",
                    "Cancelado"
                )

                val listaFiltrada = listaEstatus.filter { estatusServer ->
                    estatusPermitidos.any { permitido ->
                        estatusServer.nombre.trim().equals(permitido, ignoreCase = true)
                    }
                }

                if (listaFiltrada.isNotEmpty()) {
                    val adaptador = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        listaFiltrada
                    )

                    binding.actvEstatus.setAdapter(adaptador)

                    binding.actvEstatus.setOnItemClickListener { parent, _, position, _ ->
                        estatusSeleccionado = parent.getItemAtPosition(position) as EstatusEnvio
                        binding.tilEstatus.error = null
                    }
                } else {
                    Toast.makeText(this, "No se encontraron estatus válidos para conductor.", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "No se pudieron cargar los estatus", Toast.LENGTH_SHORT).show()
                binding.btnGuardar.isEnabled = false
            }
        }
    }

    private fun validarYGuardar() {
        if (estatusSeleccionado == null) {
            binding.tilEstatus.error = "Debes seleccionar un estatus de la lista."
            return
        } else {
            binding.tilEstatus.error = null
        }

        val comentario = binding.etComentario.text.toString().trim()

        val nombreEstatus = estatusSeleccionado!!.nombre.uppercase()
        val esCritico = nombreEstatus.contains("DETENIDO") || nombreEstatus.contains("CANCELADO")

        if (esCritico && comentario.isEmpty()) {
            binding.etComentario.error = "El comentario es obligatorio para el estatus ${estatusSeleccionado!!.nombre}."
            binding.etComentario.requestFocus()
            return
        } else {
            binding.etComentario.error = null
        }

        enviarActualizacion(estatusSeleccionado!!.idEstatusEnvio, comentario)
    }

    private fun enviarActualizacion(idEstatus: Int, comentario: String) {
        val datos = EnvioHistorialEstatus(
            idEnvio = envioActual!!.idEnvio,
            idColaborador = idConductor,
            idEstatusEnvio = idEstatus,
            comentario = comentario
        )

        binding.btnGuardar.isEnabled = false

        EnvioImp.actualizarEstatus(this, datos) { exito, mensaje ->
            binding.btnGuardar.isEnabled = true
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            if (exito) {
                finish()
            }
        }
    }
}