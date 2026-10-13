package com.udb.tienda.ar240329.rs240130.controller

import com.udb.tienda.ar240329.rs240130.model.Categoria
import com.udb.tienda.ar240329.rs240130.model.CategoriaRepository

class CategoriaController(private val repositorio: CategoriaRepository = CategoriaRepository()) {
    fun obtenerCategorias(resultado: (Result<List<Categoria>>) -> Unit) {
        repositorio.obtenerCategorias(resultado)
    }

    fun obtenerCategoriaPorId(id: String, resultado: (Result<Categoria>) -> Unit) {
        repositorio.obtenerCategoriaPorId(id, resultado)
    }

    fun crearCategoria(categoria: Categoria, resultado: (Result<Categoria>) -> Unit) {
        repositorio.crearCategoria(categoria, resultado)
    }

    fun actualizarCategoria(id: String, categoria: Categoria, resultado: (Result<Categoria>) -> Unit) {
        repositorio.actualizarCategoria(id, categoria, resultado)
    }

    fun eliminarCategoria(id: String, resultado: (Result<Unit>) -> Unit) {
        repositorio.eliminarCategoria(id, resultado)
    }

    fun cancelarSolicitud() {
        repositorio.cancelarSolicitud()
    }
}
