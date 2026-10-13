package com.udb.tienda.ar240329.rs240130.controller

import com.udb.tienda.ar240329.rs240130.model.Producto
import com.udb.tienda.ar240329.rs240130.model.ProductoRepository

class ProductoController(private val repositorio: ProductoRepository = ProductoRepository()) {
    fun obtenerProductos(resultado: (Result<List<Producto>>) -> Unit) {
        repositorio.obtenerProductos(resultado)
    }

    fun obtenerProductoPorId(id: String, resultado: (Result<Producto>) -> Unit) {
        repositorio.obtenerProductoPorId(id, resultado)
    }

    fun crearProducto(producto: Producto, resultado: (Result<Producto>) -> Unit) {
        repositorio.crearProducto(producto, resultado)
    }

    fun actualizarProducto(id: String, producto: Producto, resultado: (Result<Producto>) -> Unit) {
        repositorio.actualizarProducto(id, producto, resultado)
    }

    fun eliminarProducto(id: String, resultado: (Result<Unit>) -> Unit) {
        repositorio.eliminarProducto(id, resultado)
    }

    fun cancelarSolicitud() {
        repositorio.cancelarSolicitud()
    }
}
