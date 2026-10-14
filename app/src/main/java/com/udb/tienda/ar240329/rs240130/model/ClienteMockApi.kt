package com.udb.tienda.ar240329.rs240130.model

import java.io.IOException
import android.util.Log
import com.udb.tienda.ar240329.rs240130.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClienteMockApi {
    companion object {
        const val URL_BASE_MOCKAPI = "https://6acdadf9b406cb8ec0c7784e.mockapi.io/api/v1/"

        private val retrofit: Retrofit by lazy {
            check(URL_BASE_MOCKAPI.isNotBlank()) {
                "Falta configurar URL_BASE_MOCKAPI en ClienteMockApi."
            }
            val cliente = OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor { mensaje -> Log.d("MockApiHTTP", mensaje) }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()
            Retrofit.Builder()
                .baseUrl(URL_BASE_MOCKAPI)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    private var solicitudActual: Call<*>? = null

    fun <T> crearServicio(tipo: Class<T>): T = retrofit.create(tipo)

    fun cancelarSolicitud() {
        solicitudActual?.cancel()
        solicitudActual = null
    }

    fun <T> ejecutar(
        crearSolicitud: () -> Call<T>,
        resultado: (Result<T>) -> Unit,
        respuestaVacia: T? = null,
        mensajeNoEncontrado: String? = null
    ) {
        cancelarSolicitud()
        val solicitud = try {
            crearSolicitud()
        } catch (error: IllegalStateException) {
            resultado(Result.failure(error))
            return
        } catch (error: IllegalArgumentException) {
            resultado(Result.failure(IllegalArgumentException("La URL base de MockAPI no es válida.", error)))
            return
        }
        solicitudActual = solicitud
        solicitud.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (call.isCanceled) return
                solicitudActual = null
                val datos = response.body() ?: respuestaVacia
                when {
                    !response.isSuccessful -> {
                        response.errorBody()?.close()
                        val mensaje = if (response.code() == 404 && mensajeNoEncontrado != null) {
                            mensajeNoEncontrado
                        } else {
                            "Error HTTP ${response.code()}."
                        }
                        resultado(Result.failure(IOException(mensaje)))
                    }
                    datos == null -> resultado(Result.failure(IOException("El servidor respondió sin datos.")))
                    else -> resultado(Result.success(datos))
                }
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                if (call.isCanceled) return
                solicitudActual = null
                val mensaje = if (error is IOException) {
                    "No se pudo conectar con MockAPI. Revisa tu conexión y la URL configurada."
                } else {
                    "No se pudo interpretar la respuesta del servidor."
                }
                resultado(Result.failure(IOException(mensaje, error)))
            }
        })
    }
}
