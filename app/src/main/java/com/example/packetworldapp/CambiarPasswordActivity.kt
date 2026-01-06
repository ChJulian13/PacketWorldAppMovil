package com.example.packetworldapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworldapp.databinding.ActivityCambiarPasswordBinding
import com.example.packetworldapp.dominio.ColaboradorImp

class CambiarPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCambiarPasswordBinding
    private var idColaborador: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idColaborador = intent.getIntExtra("idColaborador", 0)

        if (idColaborador == 0) {
            Toast.makeText(this, "Error al cargar identificador", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnGuardarPass.setOnClickListener {
            validarYGuardar()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun validarYGuardar() {
        val passActual = binding.etPassActual.text.toString()
        val passNueva = binding.etPassNueva.text.toString()
        val passRepetir = binding.etPassRepetir.text.toString()

        if (passActual.isEmpty() || passNueva.isEmpty() || passRepetir.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (passNueva != passRepetir) {
            binding.tilPassRepetir.error = "Las contraseñas nuevas no coinciden"
            return
        } else {
            binding.tilPassRepetir.error = null
        }

        if (passActual == passNueva) {
            binding.tilPassNueva.error = "La nueva contraseña debe ser diferente"
            return
        }

        binding.btnGuardarPass.isEnabled = false

        ColaboradorImp.cambiarPassword(this, idColaborador, passActual, passNueva) { exito, mensaje ->
            binding.btnGuardarPass.isEnabled = true
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

            if (exito) {
                finish()
            }
        }
    }
}