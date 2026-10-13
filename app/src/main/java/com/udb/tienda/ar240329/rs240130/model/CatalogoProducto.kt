package com.udb.tienda.ar240329.rs240130.model

import com.google.gson.annotations.SerializedName

data class CatalogoRespuesta(
    @SerializedName("products") val productos: List<CatalogoProducto> = emptyList(),
    @SerializedName("total") val total: Int = 0,
    @SerializedName("skip") val skip: Int = 0,
    @SerializedName("limit") val limit: Int = 0
)

data class CatalogoProducto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val thumbnail: String
)
