package com.udb.tienda.ar240329.rs240130.model

data class RespuestaCatalogo(
    val products: List<ProductoProveedor>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
