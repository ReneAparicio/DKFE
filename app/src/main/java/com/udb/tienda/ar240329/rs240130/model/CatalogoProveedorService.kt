package com.udb.tienda.ar240329.rs240130.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogoProveedorService {
    @GET("products")
    fun obtenerProductos(@Query("limit") limite: Int = 0): Call<RespuestaCatalogo>

    @GET("products/search")
    fun buscarProductos(@Query("q") nombre: String, @Query("limit") limite: Int = 0): Call<RespuestaCatalogo>
}
