package com.udb.tienda.ar240329.rs240130.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogoService {
    @GET("products")
    fun obtenerProductos(): Call<CatalogoRespuesta>

    @GET("products/search")
    fun buscarProductos(@Query("q") texto: String): Call<CatalogoRespuesta>
}
