package com.udb.tienda.ar240329.rs240130.model

import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogoRepository {
    companion object {
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    private val servicio by lazy { retrofit.create(CatalogoService::class.java) }
    private var llamadaActual: Call<CatalogoRespuesta>? = null

    fun obtenerProductos(texto: String? = null, resultado: (Result<List<CatalogoProducto>>) -> Unit) {
        llamadaActual?.cancel()
        val llamada = if (texto.isNullOrBlank()) servicio.obtenerProductos()
            else servicio.buscarProductos(texto.trim())
        llamadaActual = llamada
        llamada.enqueue(object : Callback<CatalogoRespuesta> {
            override fun onResponse(call: Call<CatalogoRespuesta>, response: Response<CatalogoRespuesta>) {
                if (call.isCanceled) return
                llamadaActual = null
                if (response.isSuccessful) {
                    val productos = response.body()?.productos
                    if (productos == null) resultado(Result.failure(IOException("La respuesta del catálogo está vacía.")))
                    else resultado(Result.success(productos))
                } else {
                    resultado(Result.failure(IOException("Error al consultar el catálogo (HTTP ${response.code()}).")))
                }
            }

            override fun onFailure(call: Call<CatalogoRespuesta>, error: Throwable) {
                if (call.isCanceled) return
                llamadaActual = null
                resultado(Result.failure(IOException("No se pudo conectar con DummyJSON. Revisa tu conexión.", error)))
            }
        })
    }

    fun cancelarSolicitud() {
        llamadaActual?.cancel()
        llamadaActual = null
    }
}
