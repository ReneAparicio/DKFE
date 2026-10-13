package com.udb.tienda.ar240329.rs240130.model

data class Producto(
    val id: String? = null,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String
)
