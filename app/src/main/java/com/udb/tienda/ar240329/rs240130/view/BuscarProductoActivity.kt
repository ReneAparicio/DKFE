package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.ProductoController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityBuscarProductoBinding

class BuscarProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuscarProductoBinding
    private val controlador = ProductoController()
    private var buscando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBuscarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
            vista.setPadding(barras.left, barras.top, barras.right, maxOf(barras.bottom, teclado.bottom))
            insets
        }
        binding.botonBuscar.setOnClickListener { buscarProducto() }
    }

    private fun buscarProducto() {
        if (buscando) return
        binding.resultadoProducto.visibility = View.GONE
        binding.textoEstado.visibility = View.GONE
        binding.campoId.error = null
        val id = binding.campoId.text.toString().trim()
        if (id.isEmpty()) {
            binding.campoId.error = getString(R.string.ingresar_id_producto)
            binding.campoId.requestFocus()
            return
        }
        buscando = true
        binding.botonBuscar.isEnabled = false
        binding.campoId.isEnabled = false
        binding.textoEstado.setText(R.string.buscando_producto)
        binding.textoEstado.visibility = View.VISIBLE
        controlador.obtenerProductoPorId(id) { resultado ->
            if (isFinishing || isDestroyed) return@obtenerProductoPorId
            buscando = false
            binding.botonBuscar.isEnabled = true
            binding.campoId.isEnabled = true
            resultado.fold(
                onSuccess = { producto ->
                    binding.textoEstado.visibility = View.GONE
                    binding.textoNombre.text = producto.nombre
                    binding.textoDescripcion.text = producto.descripcion
                    binding.textoPrecio.text = getString(R.string.producto_precio, producto.precio)
                    binding.textoStock.text = getString(R.string.producto_stock, producto.stock)
                    binding.textoCategoria.text = getString(R.string.producto_categoria, producto.categoria)
                    binding.resultadoProducto.visibility = View.VISIBLE
                },
                onFailure = { error ->
                    binding.textoEstado.text = error.message ?: getString(R.string.error_buscar_producto)
                    binding.textoEstado.visibility = View.VISIBLE
                }
            )
        }
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
