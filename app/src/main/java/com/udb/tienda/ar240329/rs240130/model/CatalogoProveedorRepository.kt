package com.udb.tienda.ar240329.rs240130.model

import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogoProveedorRepository {
    companion object {
        private val servicio = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoProveedorService::class.java)
    }

    private var solicitudActual: Call<RespuestaCatalogo>? = null

    fun obtenerProductos(nombre: String, resultado: (Result<List<ProductoProveedor>>) -> Unit) {
        cancelarSolicitud()
        val solicitud = if (nombre.isBlank()) servicio.obtenerProductos() else servicio.buscarProductos(nombre)
        solicitudActual = solicitud
        solicitud.enqueue(object : Callback<RespuestaCatalogo> {
            override fun onResponse(call: Call<RespuestaCatalogo>, response: Response<RespuestaCatalogo>) {
                if (call.isCanceled || solicitudActual !== call) return
                solicitudActual = null
                val productos = response.body()?.products
                if (response.isSuccessful && productos != null) {
                    resultado(Result.success(productos))
                } else {
                    response.errorBody()?.close()
                    resultado(Result.failure(IOException("No se pudo cargar el catálogo. Intenta nuevamente.")))
                }
            }

            override fun onFailure(call: Call<RespuestaCatalogo>, error: Throwable) {
                if (call.isCanceled || solicitudActual !== call) return
                solicitudActual = null
                val mensaje = if (error is IOException) {
                    "No se pudo conectar con DummyJSON. Revisa tu conexión e intenta nuevamente."
                } else {
                    "No se pudo interpretar la respuesta del catálogo."
                }
                resultado(Result.failure(IOException(mensaje, error)))
            }
        })
    }

    fun cancelarSolicitud() {
        solicitudActual?.cancel()
        solicitudActual = null
    }
}
