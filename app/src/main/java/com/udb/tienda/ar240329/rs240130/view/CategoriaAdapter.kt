package com.udb.tienda.ar240329.rs240130.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.databinding.ItemCategoriaBinding
import com.udb.tienda.ar240329.rs240130.model.Categoria

class CategoriaAdapter(
    private val alEditar: (Categoria) -> Unit,
    private val alEliminar: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {
    private val categorias = mutableListOf<Categoria>()

    fun mostrarCategorias(nuevasCategorias: List<Categoria>) {
        val cantidadAnterior = categorias.size
        categorias.clear()
        notifyItemRangeRemoved(0, cantidadAnterior)
        categorias.addAll(nuevasCategorias)
        notifyItemRangeInserted(0, categorias.size)
    }

    fun eliminarCategoria(id: String) {
        val posicion = categorias.indexOfFirst { it.id == id }
        if (posicion >= 0) {
            categorias.removeAt(posicion)
            notifyItemRemoved(posicion)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.mostrarCategoria(categorias[position], alEditar, alEliminar)
    }

    override fun getItemCount() = categorias.size

    class CategoriaViewHolder(private val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun mostrarCategoria(categoria: Categoria, alEditar: (Categoria) -> Unit, alEliminar: (Categoria) -> Unit) {
            binding.textoNombre.text = categoria.nombre
            binding.textoDescripcion.text = categoria.descripcion
            binding.textoId.text = binding.root.context.getString(R.string.categoria_id, categoria.id.orEmpty())
            binding.root.setOnClickListener { alEditar(categoria) }
            binding.botonEliminarCategoria.setOnClickListener { alEliminar(categoria) }
        }
    }
}
