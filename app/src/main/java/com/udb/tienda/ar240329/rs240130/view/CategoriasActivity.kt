package com.udb.tienda.ar240329.rs240130.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.CategoriaController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityCategoriasBinding
import com.udb.tienda.ar240329.rs240130.model.Categoria

class CategoriasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriasBinding
    private val controlador = CategoriaController()
    private val adaptador = CategoriaAdapter(
        alEditar = { categoria -> abrirFormulario(categoria) },
        alEliminar = { categoria -> confirmarEliminacion(categoria) }
    )
    private var operacionEnCurso = false

    private val formulario = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) cargarCategorias()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        binding.listaCategorias.layoutManager = LinearLayoutManager(this)
        binding.listaCategorias.adapter = adaptador
        binding.botonCrearCategoria.setOnClickListener { abrirFormulario() }
        cargarCategorias()
    }

    private fun cargarCategorias() {
        cambiarEstadoOperacion(true)
        binding.textoEstado.visibility = View.VISIBLE
        binding.textoEstado.setText(R.string.cargando_categorias)
        controlador.obtenerCategorias { resultado ->
            if (isFinishing || isDestroyed) return@obtenerCategorias
            cambiarEstadoOperacion(false)
            resultado.fold(
                onSuccess = { categorias ->
                    adaptador.mostrarCategorias(categorias)
                    binding.textoEstado.visibility = if (categorias.isEmpty()) View.VISIBLE else View.GONE
                    binding.textoEstado.setText(R.string.categorias_vacias)
                },
                onFailure = { error ->
                    val mensaje = error.message ?: getString(R.string.error_cargar_categorias)
                    binding.textoEstado.visibility = View.VISIBLE
                    binding.textoEstado.text = mensaje
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun abrirFormulario(categoria: Categoria? = null) {
        if (operacionEnCurso) return
        if (categoria != null && categoria.id.isNullOrBlank()) {
            Toast.makeText(this, R.string.categoria_sin_id, Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, CrearCategoriaActivity::class.java)
        if (categoria != null) {
            intent.putExtra(CrearCategoriaActivity.EXTRA_ID, categoria.id)
            intent.putExtra(CrearCategoriaActivity.EXTRA_NOMBRE, categoria.nombre)
            intent.putExtra(CrearCategoriaActivity.EXTRA_DESCRIPCION, categoria.descripcion)
        }
        formulario.launch(intent)
    }

    private fun confirmarEliminacion(categoria: Categoria) {
        if (operacionEnCurso) return
        val id = categoria.id
        if (id.isNullOrBlank()) {
            Toast.makeText(this, R.string.categoria_sin_id, Toast.LENGTH_LONG).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar_categoria)
            .setMessage(getString(R.string.confirmar_eliminar_categoria, categoria.nombre))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                if (!operacionEnCurso) eliminarCategoria(id)
            }
            .show()
    }

    private fun eliminarCategoria(id: String) {
        cambiarEstadoOperacion(true)
        controlador.eliminarCategoria(id) { resultado ->
            if (isFinishing || isDestroyed) return@eliminarCategoria
            cambiarEstadoOperacion(false)
            resultado.fold(
                onSuccess = {
                    adaptador.eliminarCategoria(id)
                    binding.textoEstado.visibility = if (adaptador.itemCount == 0) View.VISIBLE else View.GONE
                    binding.textoEstado.setText(R.string.categorias_vacias)
                    Toast.makeText(this, R.string.categoria_eliminada, Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(
                        this,
                        error.message ?: getString(R.string.error_eliminar_categoria),
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    private fun cambiarEstadoOperacion(enCurso: Boolean) {
        operacionEnCurso = enCurso
        binding.botonCrearCategoria.isEnabled = !enCurso
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
