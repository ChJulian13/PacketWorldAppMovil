package com.example.packetworldapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.packetworldapp.databinding.ActivityPerfilBinding
import com.example.packetworldapp.dominio.ColaboradorImp
import com.example.packetworldapp.poko.Conductor
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private var conductor: Conductor? = null
    private var fotoPerfilBytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonConductor = intent.getStringExtra("conductor")
        if (jsonConductor != null) {
            try {
                conductor = Gson().fromJson(jsonConductor, Conductor::class.java)
                llenarCamposTexto()
                descargarFotoActualDelServidor()

                bloquearCampos(true)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        configurarBotones()
        configurarClickFoto()
    }

    private fun descargarFotoActualDelServidor() {
        if (conductor != null) {
            ColaboradorImp.obtenerFotoPerfil(this, conductor!!.idColaborador) { base64Servidor ->
                if (!base64Servidor.isNullOrEmpty()) {
                    try {
                        conductor = conductor!!.copy(fotoBase64 = base64Servidor)
                        val imgBytes = Base64.decode(base64Servidor, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                        binding.ivFotoPerfil.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.ivFotoPerfil.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                } else {
                    conductor = conductor!!.copy(fotoBase64 = null)
                    binding.ivFotoPerfil.setImageResource(android.R.drawable.ic_menu_camera)
                }
            }
        }
    }

    private fun configurarClickFoto() {
        binding.ivFotoPerfil.setOnClickListener {
            if (binding.btnGuardarCambios.visibility == View.VISIBLE) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                seleccionarFotoLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Habilita la edición primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val seleccionarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            if (data != null) {
                fotoPerfilBytes = uriToByteArray(data)
                if (fotoPerfilBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(fotoPerfilBytes, 0, fotoPerfilBytes!!.size)
                    binding.ivFotoPerfil.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun llenarCamposTexto() {
        conductor?.let {
            binding.etNombre.setText(it.nombre)
            binding.etPaterno.setText(it.apellidoPaterno)
            binding.etMaterno.setText(it.apellidoMaterno)
            binding.etNoPersonal.setText(it.noPersonal)
            binding.etCorreo.setText(it.correo)
            binding.etCurp.setText(it.curp)
            binding.etLicencia.setText(it.numeroLicencia)
        }
    }

    private fun bloquearCampos(bloquear: Boolean) {
        val accion = !bloquear
        binding.etNombre.isEnabled = accion
        binding.etPaterno.isEnabled = accion
        binding.etMaterno.isEnabled = accion
        binding.etCorreo.isEnabled = accion
        binding.etCurp.isEnabled = accion
        binding.etLicencia.isEnabled = accion

        binding.btnIrCambiarPass.visibility = if (accion) View.VISIBLE else View.GONE

        binding.ivFotoPerfil.alpha = if(bloquear) 0.7f else 1.0f
        binding.etNoPersonal.isEnabled = false
    }

    private fun configurarBotones() {
        binding.btnHabilitarEdicion.setOnClickListener {
            bloquearCampos(false)
            binding.btnHabilitarEdicion.visibility = View.GONE
            binding.btnGuardarCambios.visibility = View.VISIBLE
            binding.etNombre.requestFocus()
        }

        binding.btnGuardarCambios.setOnClickListener {
            if (validarCamposGenerales()) {
                guardarCambios()
            }
        }

        binding.btnIrCambiarPass.setOnClickListener {
            val intent = Intent(this, CambiarPasswordActivity::class.java)
            intent.putExtra("idColaborador", conductor!!.idColaborador)
            startActivity(intent)
        }
    }

    private fun validarCamposGenerales(): Boolean {
        var esValido = true
        if (binding.etNombre.text.isNullOrEmpty()) { binding.etNombre.error = "Campo requerido"; esValido = false }
        if (binding.etPaterno.text.isNullOrEmpty()) { binding.etPaterno.error = "Campo requerido"; esValido = false }
        if (binding.etCorreo.text.isNullOrEmpty()) { binding.etCorreo.error = "Campo requerido"; esValido = false }
        if (binding.etLicencia.text.isNullOrEmpty()) { binding.etLicencia.error = "Campo requerido"; esValido = false }
        return esValido
    }

    private fun guardarCambios() {
        binding.btnGuardarCambios.isEnabled = false
        if (fotoPerfilBytes != null) {
            ColaboradorImp.subirFotoPerfil(this, conductor!!.idColaborador, fotoPerfilBytes!!) { exito, mensaje ->
                if (exito) {
                    descargarFotoYGuardarTexto()
                } else {
                    binding.btnGuardarCambios.isEnabled = true
                    Toast.makeText(this, "Error foto: $mensaje", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            actualizarDatosTexto()
        }
    }

    private fun descargarFotoYGuardarTexto() {
        ColaboradorImp.obtenerFotoPerfil(this, conductor!!.idColaborador) { nuevaBase64 ->
            if (nuevaBase64 != null) {
                conductor = conductor!!.copy(fotoBase64 = nuevaBase64)
            }
            actualizarDatosTexto()
        }
    }

    private fun actualizarDatosTexto() {
        val conductorEditado = conductor!!.copy(
            nombre = binding.etNombre.text.toString(),
            apellidoPaterno = binding.etPaterno.text.toString(),
            apellidoMaterno = binding.etMaterno.text.toString(),
            correo = binding.etCorreo.text.toString(),
            curp = binding.etCurp.text.toString(),
            numeroLicencia = binding.etLicencia.text.toString()
        )

        ColaboradorImp.editarColaborador(this, conductorEditado) { exito, mensaje ->
            binding.btnGuardarCambios.isEnabled = true
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

            if (exito) {
                conductor = conductorEditado
                bloquearCampos(true)
                binding.btnHabilitarEdicion.visibility = View.VISIBLE
                binding.btnGuardarCambios.visibility = View.GONE
                fotoPerfilBytes = null

                setResult(RESULT_OK)
            }
        }
    }
}