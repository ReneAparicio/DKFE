package com.udb.tienda.ar240329.rs240130.controller

import com.udb.tienda.ar240329.rs240130.model.CatalogoProducto
import com.udb.tienda.ar240329.rs240130.model.CatalogoRepository

class CatalogoController(private val repositorio: CatalogoRepository = CatalogoRepository()) {
    fun cargarProductos(texto: String? = null, resultado: (Result<List<CatalogoProducto>>) -> Unit) {
        repositorio.obtenerProductos(texto, resultado)
    }

    fun cancelarSolicitud() = repositorio.cancelarSolicitud()
}
