package com.udb.tienda.ar240329.rs240130.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoService {
    @GET("producto")
    fun obtenerProductos(): Call<List<Producto>>

    @GET("producto/{id}")
    fun obtenerProductoPorId(@Path("id") id: String): Call<Producto>

    @POST("producto")
    fun crearProducto(@Body producto: Producto): Call<Producto>

    @PUT("producto/{id}")
    fun actualizarProducto(@Path("id") id: String, @Body producto: Producto): Call<Producto>

    @DELETE("producto/{id}")
    fun eliminarProducto(@Path("id") id: String): Call<Unit>
}
