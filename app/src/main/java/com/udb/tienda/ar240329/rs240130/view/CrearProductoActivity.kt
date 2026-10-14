package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.ProductoController
import com.udb.tienda.ar240329.rs240130.controller.CategoriaController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityCrearProductoBinding
import com.udb.tienda.ar240329.rs240130.model.Producto

open class CrearProductoActivity : AppCompatActivity() {
    protected open val modoEdicion = false
    companion object {
        const val EXTRA_ID = "producto_id"
        const val EXTRA_NOMBRE = "producto_nombre"
        const val EXTRA_DESCRIPCION = "producto_descripcion"
        const val EXTRA_PRECIO = "producto_precio"
        const val EXTRA_STOCK = "producto_stock"
        const val EXTRA_CATEGORIA = "producto_categoria"
        private const val ESTADO_CATEGORIA = "categoria_seleccionada"
    }

    private lateinit var binding: ActivityCrearProductoBinding
    private val controlador = ProductoController()
    private val controladorCategorias = CategoriaController()
    private var nombresCategorias = emptyList<String>()
    private var categoriaAnterior: String? = null
    private var guardando = false
    private val idProducto: String?
        get() = if (modoEdicion) intent.getStringExtra(EXTRA_ID) else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCrearProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
            vista.setPadding(barras.left, barras.top, barras.right, maxOf(barras.bottom, teclado.bottom))
            insets
        }
        if ((modoEdicion && idProducto.isNullOrBlank()) || (!modoEdicion && intent.hasExtra(EXTRA_ID))) {
            Toast.makeText(this, R.string.producto_sin_id, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        binding.textoTitulo.setText(
            if (idProducto == null) R.string.crear_producto else R.string.editar_producto
        )
        if (savedInstanceState == null && (idProducto != null || intent.hasExtra(EXTRA_NOMBRE))) {
            binding.campoNombre.setText(intent.getStringExtra(EXTRA_NOMBRE))
            binding.campoDescripcion.setText(intent.getStringExtra(EXTRA_DESCRIPCION))
            binding.campoPrecio.setText(intent.getDoubleExtra(EXTRA_PRECIO, 0.0).toString())
            if (idProducto != null) {
                binding.campoStock.setText(intent.getIntExtra(EXTRA_STOCK, 0).toString())
            } else {
                binding.campoStock.setText("0")
            }
        }
        binding.botonGuardarProducto.setOnClickListener { guardarProducto() }
        categoriaAnterior = if (savedInstanceState == null) {
            intent.getStringExtra(EXTRA_CATEGORIA)
        } else {
            savedInstanceState.getString(ESTADO_CATEGORIA)
        }
        cargarCategorias(categoriaAnterior)
    }

    private fun cargarCategorias(categoriaAnterior: String?) {
        controladorCategorias.obtenerCategorias { resultado ->
            if (isFinishing || isDestroyed) return@obtenerCategorias
            resultado.fold(
                onSuccess = { categorias ->
                    nombresCategorias = categorias.filter { !it.nombre.isNullOrBlank() }
                        .map { it.nombre.trim() }.distinct()
                    val opciones = listOf(getString(R.string.seleccionar_categoria)) + nombresCategorias
                    val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.selectorCategoria.adapter = adaptador
                    binding.selectorCategoria.isEnabled = nombresCategorias.isNotEmpty()
                    binding.botonGuardarProducto.isEnabled = nombresCategorias.isNotEmpty() && !guardando
                    val posicion = categoriaAnterior?.trim()?.let { nombresCategorias.indexOf(it) } ?: -1
                    binding.selectorCategoria.setSelection(if (posicion >= 0) posicion + 1 else 0)
                    when {
                        nombresCategorias.isEmpty() -> {
                            binding.textoEstadoCategorias.setText(R.string.registrar_categoria_primero)
                            binding.textoEstadoCategorias.visibility = View.VISIBLE
                        }
                        !categoriaAnterior.isNullOrBlank() && posicion < 0 -> {
                            binding.textoEstadoCategorias.setText(R.string.categoria_no_disponible)
                            binding.textoEstadoCategorias.visibility = View.VISIBLE
                        }
                        else -> binding.textoEstadoCategorias.visibility = View.GONE
                    }
                },
                onFailure = { error ->
                    val mensaje = error.message ?: getString(R.string.error_cargar_categorias)
                    binding.textoEstadoCategorias.text = mensaje
                    binding.textoEstadoCategorias.visibility = View.VISIBLE
                    binding.selectorCategoria.isEnabled = false
                    binding.botonGuardarProducto.isEnabled = false
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun guardarProducto() {
        if (guardando) return
        val campos = listOf(
            binding.campoNombre,
            binding.campoDescripcion,
            binding.campoPrecio,
            binding.campoStock
        )
        var hayCamposVacios = false
        campos.forEach { campo ->
            campo.error = null
            if (campo.text.toString().trim().isEmpty()) {
                campo.error = getString(R.string.campo_obligatorio)
                hayCamposVacios = true
            }
        }
        if (hayCamposVacios) return

        val categoria = nombresCategorias.getOrNull(binding.selectorCategoria.selectedItemPosition - 1)
        if (categoria.isNullOrBlank()) {
            Toast.makeText(this, R.string.seleccionar_categoria, Toast.LENGTH_LONG).show()
            return
        }

        val precio = binding.campoPrecio.text.toString().trim().replace(',', '.').toDoubleOrNull()
        val stock = binding.campoStock.text.toString().trim().toIntOrNull()
        val precioValido = precio != null && precio.isFinite() && precio > 0
        val stockValido = stock != null && stock >= 0
        if (!precioValido) binding.campoPrecio.error = getString(R.string.precio_invalido)
        if (!stockValido) binding.campoStock.error = getString(R.string.stock_invalido)
        if (!precioValido || !stockValido) return

        val producto = Producto(
            id = idProducto,
            nombre = binding.campoNombre.text.toString().trim(),
            descripcion = binding.campoDescripcion.text.toString().trim(),
            precio = precio ?: return,
            stock = stock ?: return,
            categoria = categoria
        )
        guardando = true
        binding.botonGuardarProducto.isEnabled = false
        binding.selectorCategoria.isEnabled = false
        val resultado: (Result<Producto>) -> Unit = { respuesta ->
            if (!isFinishing && !isDestroyed) {
                guardando = false
                binding.botonGuardarProducto.isEnabled = true
                binding.selectorCategoria.isEnabled = true
                respuesta.fold(
                    onSuccess = {
                        Toast.makeText(this, R.string.producto_guardado, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this,
                            error.message ?: getString(R.string.error_guardar_producto),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
        val id = idProducto
        if (id == null) {
            controlador.crearProducto(producto, resultado)
        } else {
            controlador.actualizarProducto(id, producto, resultado)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            ESTADO_CATEGORIA,
            if (nombresCategorias.isEmpty()) categoriaAnterior
            else nombresCategorias.getOrNull(binding.selectorCategoria.selectedItemPosition - 1)
        )
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        controladorCategorias.cancelarSolicitud()
        super.onDestroy()
    }
}
