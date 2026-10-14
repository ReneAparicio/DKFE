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
import com.udb.tienda.ar240329.rs240130.controller.ProductoController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityInventarioBinding
import com.udb.tienda.ar240329.rs240130.model.Producto

open class InventarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventarioBinding
    private val controlador = ProductoController()
    private val adaptador = ProductoAdapter(
        alSeleccionar = { producto -> mostrarOpciones(producto) },
        alEditar = { producto -> abrirFormulario(producto) },
        alEliminar = { producto -> confirmarEliminacion(producto) }
    )
    private var operacionEnCurso = false

    private val formulario = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) cargarProductos()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        binding.listaProductos.layoutManager = LinearLayoutManager(this)
        binding.listaProductos.adapter = adaptador
        binding.botonCrearProducto.setOnClickListener { abrirFormulario() }
        cargarProductos()
    }

    private fun cargarProductos() {
        cambiarEstadoOperacion(true)
        binding.textoEstado.visibility = View.VISIBLE
        binding.textoEstado.setText(R.string.cargando_productos)
        controlador.obtenerProductos { resultado ->
            if (isFinishing || isDestroyed) return@obtenerProductos
            cambiarEstadoOperacion(false)
            resultado.fold(
                onSuccess = { productos ->
                    adaptador.mostrarProductos(productos)
                    binding.textoEstado.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
                    binding.textoEstado.setText(R.string.inventario_vacio)
                },
                onFailure = { error ->
                    val mensaje = error.message ?: getString(R.string.error_cargar_productos)
                    binding.textoEstado.visibility = View.VISIBLE
                    binding.textoEstado.text = mensaje
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun abrirFormulario(producto: Producto? = null) {
        if (operacionEnCurso) return
        if (producto != null && producto.id.isNullOrBlank()) {
            Toast.makeText(this, R.string.producto_sin_id, Toast.LENGTH_LONG).show()
            return
        }
        val pantalla = if (producto == null) CrearProductoActivity::class.java else ActualizarProductoActivity::class.java
        val intent = Intent(this, pantalla)
        if (producto != null) {
            intent.putExtra(CrearProductoActivity.EXTRA_ID, producto.id)
            intent.putExtra(CrearProductoActivity.EXTRA_NOMBRE, producto.nombre)
            intent.putExtra(CrearProductoActivity.EXTRA_DESCRIPCION, producto.descripcion)
            intent.putExtra(CrearProductoActivity.EXTRA_PRECIO, producto.precio)
            intent.putExtra(CrearProductoActivity.EXTRA_STOCK, producto.stock)
            intent.putExtra(CrearProductoActivity.EXTRA_CATEGORIA, producto.categoria)
        }
        formulario.launch(intent)
    }

    private fun mostrarOpciones(producto: Producto) {
        if (operacionEnCurso) return
        AlertDialog.Builder(this)
            .setTitle(producto.nombre)
            .setItems(arrayOf(getString(R.string.modificar), getString(R.string.eliminar))) { _, opcion ->
                if (opcion == 0) abrirFormulario(producto) else confirmarEliminacion(producto)
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun confirmarEliminacion(producto: Producto) {
        if (operacionEnCurso) return
        val id = producto.id
        if (id.isNullOrBlank()) {
            Toast.makeText(this, R.string.producto_sin_id, Toast.LENGTH_LONG).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar_producto)
            .setMessage(getString(R.string.confirmar_eliminar_producto, producto.nombre))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                if (!operacionEnCurso) eliminarProducto(id)
            }
            .show()
    }

    private fun eliminarProducto(id: String) {
        cambiarEstadoOperacion(true)
        controlador.eliminarProducto(id) { resultado ->
            if (isFinishing || isDestroyed) return@eliminarProducto
            cambiarEstadoOperacion(false)
            resultado.fold(
                onSuccess = {
                    adaptador.eliminarProducto(id)
                    binding.textoEstado.visibility = if (adaptador.itemCount == 0) View.VISIBLE else View.GONE
                    binding.textoEstado.setText(R.string.inventario_vacio)
                    Toast.makeText(this, R.string.producto_eliminado, Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(
                        this,
                        error.message ?: getString(R.string.error_eliminar_producto),
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    private fun cambiarEstadoOperacion(enCurso: Boolean) {
        operacionEnCurso = enCurso
        binding.botonCrearProducto.isEnabled = !enCurso
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
