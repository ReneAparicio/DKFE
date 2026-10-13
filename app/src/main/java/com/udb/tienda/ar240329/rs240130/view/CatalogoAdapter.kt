package com.udb.tienda.ar240329.rs240130.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udb.tienda.ar240329.rs240130.databinding.ItemCatalogoProductoBinding
import com.udb.tienda.ar240329.rs240130.model.CatalogoProducto

class CatalogoAdapter(
    private val alSeleccionar: (CatalogoProducto) -> Unit
) : RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder>() {
    private val productos = mutableListOf<CatalogoProducto>()

    fun mostrarProductos(datos: List<CatalogoProducto>) {
        productos.clear()
        productos.addAll(datos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogoViewHolder {
        val binding = ItemCatalogoProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatalogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogoViewHolder, position: Int) {
        holder.bind(productos[position], alSeleccionar)
    }

    override fun getItemCount() = productos.size

    class CatalogoViewHolder(private val binding: ItemCatalogoProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: CatalogoProducto, alSeleccionar: (CatalogoProducto) -> Unit) {
            binding.textoNombreCatalogo.text = producto.title
            binding.textoDescripcionCatalogo.text = producto.description
            binding.textoPrecioCatalogo.text = "$" + String.format(java.util.Locale.US, "%.2f", producto.price)
            binding.textoCategoriaCatalogo.text = "Categoría: ${producto.category}"
            Picasso.get().load(producto.thumbnail).fit().centerCrop()
                .into(binding.imagenProductoCatalogo)
            binding.root.setOnClickListener { alSeleccionar(producto) }
            binding.botonImportarProducto.setOnClickListener { alSeleccionar(producto) }
        }
    }
}
