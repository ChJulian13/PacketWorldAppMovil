package com.example.packetworldapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworldapp.databinding.ActivityLoginBinding
import com.example.packetworldapp.dominio.InicioSesionImp
import com.example.packetworldapp.dto.RSAutenticacionConductor
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            val noPersonal = binding.etNoPersonal.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (noPersonal.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingrese número de personal y contraseña",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                verificarCredenciales(noPersonal, password)
            }
        }
    }

    private fun verificarCredenciales(noPersonal: String, password: String) {
        InicioSesionImp.verificarCredenciales(
            context = this,
            noPersonal = noPersonal,
            password = password
        ) { respuesta: RSAutenticacionConductor ->

            runOnUiThread {
                if (!respuesta.error) {
                    Toast.makeText(
                        this,
                        "Bienvenido ${respuesta.conductor?.nombre}",
                        Toast.LENGTH_LONG
                    ).show()
                    irPantallaInicio(Gson().toJson(respuesta.conductor))
                } else {
                    Toast.makeText(
                        this,
                        respuesta.mensaje,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun irPantallaInicio(json: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("conductor", json)
        startActivity(intent)
        finish()
    }
}
