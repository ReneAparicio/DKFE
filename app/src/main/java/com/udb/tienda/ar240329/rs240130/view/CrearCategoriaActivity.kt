package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.CategoriaController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityCrearCategoriaBinding
import com.udb.tienda.ar240329.rs240130.model.Categoria

class CrearCategoriaActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "categoria_id"
        const val EXTRA_NOMBRE = "categoria_nombre"
        const val EXTRA_DESCRIPCION = "categoria_descripcion"
    }

    private lateinit var binding: ActivityCrearCategoriaBinding
    private val controlador = CategoriaController()
    private var guardando = false
    private val idCategoria: String?
        get() = intent.getStringExtra(EXTRA_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCrearCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
            vista.setPadding(barras.left, barras.top, barras.right, maxOf(barras.bottom, teclado.bottom))
            insets
        }
        if (intent.hasExtra(EXTRA_ID) && idCategoria.isNullOrBlank()) {
            Toast.makeText(this, R.string.categoria_sin_id, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        binding.textoTitulo.setText(
            if (idCategoria == null) R.string.crear_categoria else R.string.editar_categoria
        )
        if (savedInstanceState == null && idCategoria != null) {
            binding.campoNombre.setText(intent.getStringExtra(EXTRA_NOMBRE))
            binding.campoDescripcion.setText(intent.getStringExtra(EXTRA_DESCRIPCION))
        }
        binding.botonGuardarCategoria.setOnClickListener { guardarCategoria() }
    }

    private fun guardarCategoria() {
        if (guardando) return
        val campos = listOf(binding.campoNombre, binding.campoDescripcion)
        var hayCamposVacios = false
        campos.forEach { campo ->
            campo.error = null
            if (campo.text.toString().trim().isEmpty()) {
                campo.error = getString(R.string.campo_obligatorio)
                hayCamposVacios = true
            }
        }
        if (hayCamposVacios) return
        val categoria = Categoria(
            id = idCategoria,
            nombre = binding.campoNombre.text.toString().trim(),
            descripcion = binding.campoDescripcion.text.toString().trim()
        )
        guardando = true
        binding.botonGuardarCategoria.isEnabled = false
        val resultado: (Result<Categoria>) -> Unit = { respuesta ->
            if (!isFinishing && !isDestroyed) {
                guardando = false
                binding.botonGuardarCategoria.isEnabled = true
                respuesta.fold(
                    onSuccess = {
                        Toast.makeText(this, R.string.categoria_guardada, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this,
                            error.message ?: getString(R.string.error_guardar_categoria),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
        val id = idCategoria
        if (id == null) {
            controlador.crearCategoria(categoria, resultado)
        } else {
            controlador.actualizarCategoria(id, categoria, resultado)
        }
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
