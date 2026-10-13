package com.udb.tienda.ar240329.rs240130.model

data class ReporteInventario(
    val totalProductos: Int,
    val valorTotal: Double,
    val productosStockBajo: List<Producto>
)
