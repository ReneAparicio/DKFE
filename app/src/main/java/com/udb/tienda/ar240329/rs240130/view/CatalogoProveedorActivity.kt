package com.udb.tienda.ar240329.rs240130.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.CatalogoProveedorController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityCatalogoProveedorBinding
import com.udb.tienda.ar240329.rs240130.model.ProductoProveedor

class CatalogoProveedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogoProveedorBinding
    private val controlador = CatalogoProveedorController()
    private val adaptador = ProductoProveedorAdapter { producto -> abrirFormulario(producto) }
    private val manejador = Handler(Looper.getMainLooper())
    private val buscar = Runnable { cargarProductos() }
    private var productosActuales = emptyList<ProductoProveedor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatalogoProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        binding.listaProductos.layoutManager = LinearLayoutManager(this)
        binding.listaProductos.adapter = adaptador
        binding.filtroAlimentos.setOnCheckedChangeListener { _, seleccionado ->
            binding.filtroAlimentos.setText(if (seleccionado) R.string.filtro_alimentos else R.string.filtro_todos)
            if (binding.progresoCatalogo.visibility != View.VISIBLE && binding.botonReintentar.visibility != View.VISIBLE) {
                mostrarProductos()
            }
        }
        binding.buscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                manejador.removeCallbacks(buscar)
                cargarProductos()
                binding.buscador.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                manejador.removeCallbacks(buscar)
                controlador.cancelarSolicitud()
                mostrarCarga()
                manejador.postDelayed(buscar, 400)
                return true
            }
        })
        binding.botonReintentar.setOnClickListener { cargarProductos() }
        cargarProductos()
    }

    private fun abrirFormulario(producto: ProductoProveedor) {
        val intent = Intent(this, CrearProductoActivity::class.java)
        intent.putExtra(CrearProductoActivity.EXTRA_NOMBRE, producto.title)
        intent.putExtra(CrearProductoActivity.EXTRA_DESCRIPCION, producto.description)
        intent.putExtra(CrearProductoActivity.EXTRA_PRECIO, producto.price)
        startActivity(intent)
    }

    private fun mostrarCarga() {
        binding.progresoCatalogo.visibility = View.VISIBLE
        binding.listaProductos.visibility = View.GONE
        binding.botonReintentar.visibility = View.GONE
        binding.textoEstado.visibility = View.VISIBLE
        binding.textoEstado.setText(R.string.cargando_catalogo)
    }

    private fun cargarProductos() {
        mostrarCarga()
        controlador.obtenerProductos(binding.buscador.query.toString()) { resultado ->
            if (isFinishing || isDestroyed) return@obtenerProductos
            binding.progresoCatalogo.visibility = View.GONE
            resultado.fold(
                onSuccess = { productos ->
                    productosActuales = productos
                    mostrarProductos()
                },
                onFailure = { error ->
                    binding.textoEstado.text = error.message ?: getString(R.string.error_cargar_catalogo)
                    binding.botonReintentar.visibility = View.VISIBLE
                }
            )
        }
    }

    private fun mostrarProductos() {
        val productos = if (binding.filtroAlimentos.isChecked) {
            productosActuales.filter { it.category == "groceries" }
        } else {
            productosActuales
        }
        adaptador.mostrarProductos(productos)
        binding.listaProductos.scrollToPosition(0)
        binding.listaProductos.visibility = if (productos.isEmpty()) View.GONE else View.VISIBLE
        binding.textoEstado.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
        binding.textoEstado.setText(R.string.catalogo_sin_resultados)
    }

    override fun onDestroy() {
        manejador.removeCallbacks(buscar)
        controlador.cancelarSolicitud()
        binding.listaProductos.adapter = null
        super.onDestroy()
    }
}
