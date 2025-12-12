package com.example.packetworldapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.packetworldapp.databinding.ActivityLoginBinding
import com.example.packetworldapp.dto.RSAutenticacionConductor
import com.example.packetworldapp.util.Constantes
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

        binding.btnIngresar.setOnClickListener {
            verificarCredenciales()
        }

    }

    fun verificarCredenciales() {
        if (sonCamposValidos()) {
            consumirAPI(binding.etNoPersonal.text.toString(), binding.etPassword.text.toString())
        }
    }

    fun sonCamposValidos() : Boolean {
        var valido = true
        if (binding.etNoPersonal.text.toString().isEmpty()) {
            binding.tilNoPersonal.error = "Número de personal obligatorio"
            valido = false
        }

        if (binding.etPassword.text.toString().isEmpty()) {
            binding.tilPassword.error = "Contraseña obligatoria"
            valido = false
        }

        return valido
    }

    fun consumirAPI(noPersonal : String, password : String) {
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(true)
        Ion.with(this@LoginActivity)
            .load("POST", "${Constantes().URL_API}autenticacion/conductor")
            .setBodyParameter("noPersonal", noPersonal)
            .setBodyParameter("password", password)
            .asString()
            .setCallback {e, result ->
                if (e == null) {
                    serializarRespuesta(result)
                } else {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun serializarRespuesta(json : String) {
        Log.e("WS", json)
        try {
            val gson : Gson = Gson()
            val respuestaLogin = gson.fromJson(json, RSAutenticacionConductor::class.java)
            if (!respuestaLogin.error) {
                Toast.makeText(this@LoginActivity, "Bienvenido(a) ${respuestaLogin.conductor!!.nombre}", Toast.LENGTH_SHORT).show()
                irPantallaInicio(json)
            } else {
                Toast.makeText(this@LoginActivity, "Error: ${respuestaLogin.mensaje}", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception) {
            Toast.makeText(this@LoginActivity, "Lo sentimos, hubo un error en la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun irPantallaInicio(json : String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("conductor", json)
        startActivity(intent)
        finish()
    }
}
