package com.udb.tienda.ar240329.rs240130.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.databinding.ItemStockBajoBinding
import com.udb.tienda.ar240329.rs240130.model.Producto

class StockBajoAdapter : RecyclerView.Adapter<StockBajoAdapter.StockBajoViewHolder>() {
    private val productos = mutableListOf<Producto>()

    fun mostrarProductos(nuevosProductos: List<Producto>) {
        val cantidadAnterior = productos.size
        productos.clear()
        notifyItemRangeRemoved(0, cantidadAnterior)
        productos.addAll(nuevosProductos)
        notifyItemRangeInserted(0, productos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockBajoViewHolder {
        val binding = ItemStockBajoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockBajoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockBajoViewHolder, position: Int) {
        holder.mostrarProducto(productos[position])
    }

    override fun getItemCount() = productos.size

    class StockBajoViewHolder(private val binding: ItemStockBajoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun mostrarProducto(producto: Producto) {
            binding.textoNombre.text = producto.nombre
            binding.textoStock.text = binding.root.context.getString(R.string.stock_disponible, producto.stock)
        }
    }
}
