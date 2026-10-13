package com.udb.tienda.ar240329.rs240130.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.CatalogoController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityCatalogoBinding
import com.udb.tienda.ar240329.rs240130.model.CatalogoProducto

class CatalogoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogoBinding
    private val controlador = CatalogoController()
    private val adaptador = CatalogoAdapter { producto -> importarProducto(producto) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        binding.listaCatalogo.layoutManager = LinearLayoutManager(this)
        binding.listaCatalogo.adapter = adaptador
        binding.buscadorCatalogo.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                cargarProductos(query)
                binding.buscadorCatalogo.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) cargarProductos(null)
                return true
            }
        })
        cargarProductos()
    }

    private fun esProductoDeCafeteria(
        producto: CatalogoProducto
    ): Boolean {
        val texto = "${producto.title} ${producto.description} ${producto.category}"
            .lowercase()

        val palabrasPermitidas = listOf(
            "coffee", "cafe", "tea", "cake", "cookie",
            "bread", "sandwich", "muffin", "chocolate",
            "juice", "milk", "honey", "sugar", "butter",
            "cheese", "egg", "banana", "apple", "croissant",
            "snack", "beverage", "drink", "food", "dessert",
            "groceries"
        )

        return palabrasPermitidas.any { it in texto }
    }

    private fun cargarProductos(texto: String? = null) {
        binding.progresoCatalogo.visibility = View.VISIBLE
        binding.textoEstadoCatalogo.visibility = View.GONE
        controlador.cargarProductos(texto) { resultado ->
            if (isFinishing || isDestroyed) return@cargarProductos
            binding.progresoCatalogo.visibility = View.GONE
            resultado.fold(

                onSuccess = { productos ->
                    val productosCafeteria = productos.filter {
                        esProductoDeCafeteria(it)
                    }

                    adaptador.mostrarProductos(productosCafeteria)

                    if (productosCafeteria.isEmpty()) {
                        binding.textoEstadoCatalogo.text =
                            "No se encontraron alimentos o bebidas."
                        binding.textoEstadoCatalogo.visibility = View.VISIBLE
                    }
                },

                onFailure = { error ->
                    adaptador.mostrarProductos(emptyList())
                    binding.textoEstadoCatalogo.text = error.message ?: "Error al cargar el catálogo."
                    binding.textoEstadoCatalogo.visibility = View.VISIBLE
                    Toast.makeText(this, binding.textoEstadoCatalogo.text, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun importarProducto(producto: CatalogoProducto) {
        val intent = Intent(this, CrearProductoActivity::class.java).apply {
            putExtra(CrearProductoActivity.EXTRA_NOMBRE, producto.title)
            putExtra(CrearProductoActivity.EXTRA_DESCRIPCION, producto.description)
            putExtra(CrearProductoActivity.EXTRA_PRECIO, producto.price)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
