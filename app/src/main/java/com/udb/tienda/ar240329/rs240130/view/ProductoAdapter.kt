package com.udb.tienda.ar240329.rs240130.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.databinding.ItemProductoBinding
import com.udb.tienda.ar240329.rs240130.model.Producto

class ProductoAdapter(
    private val alSeleccionar: (Producto) -> Unit,
    private val alEditar: (Producto) -> Unit,
    private val alEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {
    private val productos = mutableListOf<Producto>()

    fun eliminarProducto(id: String) {
        val posicion = productos.indexOfFirst { it.id == id }
        if (posicion >= 0) {
            productos.removeAt(posicion)
            notifyItemRemoved(posicion)
        }
    }

    fun mostrarProductos(nuevosProductos: List<Producto>) {
        val cantidadAnterior = productos.size
        productos.clear()
        notifyItemRangeRemoved(0, cantidadAnterior)
        productos.addAll(nuevosProductos)
        notifyItemRangeInserted(0, productos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.mostrarProducto(productos[position], alSeleccionar, alEditar, alEliminar)
    }

    override fun getItemCount() = productos.size

    class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun mostrarProducto(producto: Producto, alSeleccionar: (Producto) -> Unit, alEditar: (Producto) -> Unit, alEliminar: (Producto) -> Unit) {
            val contexto = binding.root.context
            binding.textoNombre.text = producto.nombre
            binding.textoDescripcion.text = producto.descripcion
            binding.textoId.text = contexto.getString(R.string.producto_id, producto.id.orEmpty())
            binding.textoPrecio.text = contexto.getString(R.string.producto_precio, producto.precio)
            binding.textoStock.text = contexto.getString(R.string.producto_stock, producto.stock)
            binding.textoCategoria.text = contexto.getString(R.string.producto_categoria, producto.categoria)
            binding.root.setOnClickListener { alSeleccionar(producto) }
            binding.botonEditarProducto.setOnClickListener { alEditar(producto) }
            binding.botonEliminarProducto.setOnClickListener { alEliminar(producto) }
        }
    }
}
