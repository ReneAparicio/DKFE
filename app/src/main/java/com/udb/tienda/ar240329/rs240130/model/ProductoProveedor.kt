package com.udb.tienda.ar240329.rs240130.model

data class ProductoProveedor(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val thumbnail: String?
)
