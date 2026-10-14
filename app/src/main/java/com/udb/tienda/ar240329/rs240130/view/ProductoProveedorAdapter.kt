package com.udb.tienda.ar240329.rs240130.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.databinding.ItemProductoProveedorBinding
import com.udb.tienda.ar240329.rs240130.model.ProductoProveedor

class ProductoProveedorAdapter(
    private val alImportar: (ProductoProveedor) -> Unit
) : RecyclerView.Adapter<ProductoProveedorAdapter.ProductoViewHolder>() {
    private val productos = mutableListOf<ProductoProveedor>()

    fun mostrarProductos(nuevosProductos: List<ProductoProveedor>) {
        val cantidadAnterior = productos.size
        productos.clear()
        notifyItemRangeRemoved(0, cantidadAnterior)
        productos.addAll(nuevosProductos)
        notifyItemRangeInserted(0, productos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        return ProductoViewHolder(ItemProductoProveedorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.mostrarProducto(productos[position], alImportar)
    }

    override fun getItemCount() = productos.size

    override fun onViewRecycled(holder: ProductoViewHolder) {
        holder.cancelarImagen()
        super.onViewRecycled(holder)
    }

    class ProductoViewHolder(private val binding: ItemProductoProveedorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun mostrarProducto(producto: ProductoProveedor, alImportar: (ProductoProveedor) -> Unit) {
            val contexto = binding.root.context
            binding.textoNombre.text = producto.title
            binding.textoDescripcion.text = producto.description
            binding.textoPrecio.text = contexto.getString(R.string.precio_proveedor, producto.price)
            binding.textoCategoria.text = contexto.getString(R.string.producto_categoria, producto.category)
            binding.imagenProducto.contentDescription = producto.title
            binding.botonImportar.setOnClickListener { alImportar(producto) }
            binding.root.setOnClickListener { alImportar(producto) }
            Picasso.get().load(producto.thumbnail?.takeIf { it.isNotBlank() })
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .fit().centerInside().into(binding.imagenProducto)
        }

        fun cancelarImagen() {
            Picasso.get().cancelRequest(binding.imagenProducto)
        }
    }
}
