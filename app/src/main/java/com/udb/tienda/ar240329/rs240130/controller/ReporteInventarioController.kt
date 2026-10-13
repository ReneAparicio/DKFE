package com.udb.tienda.ar240329.rs240130.controller

import com.udb.tienda.ar240329.rs240130.model.ReporteInventario
import com.udb.tienda.ar240329.rs240130.model.ReporteInventarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException

class ReporteInventarioController(
    private val repositorio: ReporteInventarioRepository = ReporteInventarioRepository()
) {
    suspend fun obtenerReporte(): ReporteInventario = withContext(Dispatchers.IO) {
        val productos = repositorio.obtenerProductos()
        val valorTotal = productos.sumOf { it.precio * it.stock }
        if (!valorTotal.isFinite()) throw JSONException("El valor del inventario no es válido.")
        ReporteInventario(
            totalProductos = productos.size,
            valorTotal = valorTotal,
            productosStockBajo = productos.filter { it.stock < 5 }
        )
    }

    fun cancelarSolicitud() {
        repositorio.cancelarSolicitud()
    }
}
