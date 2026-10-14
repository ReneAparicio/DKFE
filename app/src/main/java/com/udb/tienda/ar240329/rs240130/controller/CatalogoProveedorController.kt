package com.udb.tienda.ar240329.rs240130.controller

import com.udb.tienda.ar240329.rs240130.model.CatalogoProveedorRepository
import com.udb.tienda.ar240329.rs240130.model.ProductoProveedor

class CatalogoProveedorController(private val repositorio: CatalogoProveedorRepository = CatalogoProveedorRepository()) {
    fun obtenerProductos(nombre: String, resultado: (Result<List<ProductoProveedor>>) -> Unit) {
        repositorio.obtenerProductos(nombre.trim(), resultado)
    }

    fun cancelarSolicitud() {
        repositorio.cancelarSolicitud()
    }
}
