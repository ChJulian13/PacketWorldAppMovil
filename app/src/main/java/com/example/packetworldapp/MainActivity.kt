package com.example.packetworldapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.packetworldapp.adapter.EnvioAdapter
import com.example.packetworldapp.databinding.ActivityMainBinding
import com.example.packetworldapp.dominio.ColaboradorImp
import com.example.packetworldapp.dominio.EnvioImp
import com.example.packetworldapp.poko.Conductor
import com.example.packetworldapp.poko.Envio
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var idConductor: Int = 0
    private var conductorActual: Conductor? = null

    private val launcherPerfil = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val jsonActualizado = data?.getStringExtra("conductor_actualizado")

            if (jsonActualizado != null) {
                conductorActual = Gson().fromJson(jsonActualizado, Conductor::class.java)
                idConductor = conductorActual!!.idColaborador

                binding.tvTitulo.text = "Hola, ${conductorActual!!.nombre}"
                Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                refrescarDatosUsuario()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.rvEnvios.layoutManager = LinearLayoutManager(this)

        obtenerDatosConductor()

        binding.iconPerfil.setOnClickListener {
            if (conductorActual != null) {
                val intent = Intent(this, PerfilActivity::class.java)
                val gson = Gson()
                val jsonConductor = gson.toJson(conductorActual)
                intent.putExtra("conductor", jsonConductor)

                launcherPerfil.launch(intent)
            } else {
                Toast.makeText(this, "Cargando información...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (idConductor > 0) {
            cargarEnvios()
        }
    }

    private fun obtenerDatosConductor() {
        val jsonConductor = intent.getStringExtra("conductor")
        if (!jsonConductor.isNullOrEmpty()) {
            try {
                conductorActual = Gson().fromJson(jsonConductor, Conductor::class.java)
                if (conductorActual != null) {
                    idConductor = conductorActual!!.idColaborador
                    binding.tvTitulo.text = "Hola, ${conductorActual!!.nombre}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refrescarDatosUsuario() {
        if (idConductor > 0) {
            ColaboradorImp.obtenerDatosPorId(this, idConductor) { conductorNuevo ->
                if (conductorNuevo != null) {
                    conductorActual = conductorNuevo.copy(
                        fotoBase64 = null
                    )

                    binding.tvTitulo.text = "Hola, ${conductorActual!!.nombre}"
                    Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarEnvios() {
        EnvioImp.obtenerEnviosPorConductor(this, idConductor) { listaEnvios, mensajeError ->
            runOnUiThread {
                if (mensajeError != null) {
                    Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
                } else {
                    if (!listaEnvios.isNullOrEmpty()) {
                        configurarAdaptador(listaEnvios)
                    } else {
                        Toast.makeText(this, "No tienes envíos asignados.", Toast.LENGTH_SHORT).show()
                        binding.rvEnvios.adapter = null
                    }
                }
            }
        }
    }

    private fun configurarAdaptador(lista: List<Envio>) {
        val adaptador = EnvioAdapter(lista) { envioSeleccionado ->
            irPantallaDetalle(envioSeleccionado)
        }
        binding.rvEnvios.adapter = adaptador
    }

    private fun irPantallaDetalle(envio: Envio) {
        val intent = Intent(this, DetalleEnvioActivity::class.java)
        intent.putExtra("envio", envio)
        intent.putExtra("idConductor", idConductor)
        startActivity(intent)
    }
}