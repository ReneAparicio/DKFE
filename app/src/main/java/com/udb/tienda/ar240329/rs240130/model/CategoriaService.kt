package com.udb.tienda.ar240329.rs240130.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoriaService {
    @GET("categoria")
    fun obtenerCategorias(): Call<List<Categoria>>

    @GET("categoria/{id}")
    fun obtenerCategoriaPorId(@Path("id") id: String): Call<Categoria>

    @POST("categoria")
    fun crearCategoria(@Body categoria: Categoria): Call<Categoria>

    @PUT("categoria/{id}")
    fun actualizarCategoria(@Path("id") id: String, @Body categoria: Categoria): Call<Categoria>

    @DELETE("categoria/{id}")
    fun eliminarCategoria(@Path("id") id: String): Call<Unit>
}
