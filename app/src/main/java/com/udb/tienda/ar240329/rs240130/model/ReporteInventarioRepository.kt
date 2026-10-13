package com.udb.tienda.ar240329.rs240130.model

import android.util.Log
import java.io.IOException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ReporteInventarioRepository {
    private val cliente = OkHttpClient()

    @Volatile
    private var solicitudActual: Call? = null

    suspend fun obtenerProductos(): List<Producto> {
        val contexto = currentCoroutineContext()
        contexto.ensureActive()
        val urlBase = ClienteMockApi.URL_BASE_MOCKAPI
        check(urlBase.isNotBlank()) {
            "Falta configurar URL_BASE_MOCKAPI en ClienteMockApi."
        }
        require(urlBase.endsWith('/')) { "La URL base de MockAPI debe terminar en /." }
        val url = urlBase.toHttpUrl().resolve("producto")
            ?: throw IllegalArgumentException("La URL base de MockAPI no es válida.")
        val request = Request.Builder().url(url).get().build()
        val solicitud = cliente.newCall(request)
        solicitudActual = solicitud
        try {
            contexto.ensureActive()
            Log.d("ReporteInventario", "${request.method} ${request.url}")
            return solicitud.execute().use { respuesta ->
                Log.d("ReporteInventario", "${request.method} ${request.url} HTTP ${respuesta.code}")
                contexto.ensureActive()
                if (!respuesta.isSuccessful) {
                    throw IOException("Error HTTP ${respuesta.code} al obtener el inventario.")
                }
                val contenido = respuesta.body?.string()
                    ?: throw JSONException("La respuesta no contiene datos.")
                val registros = JSONArray(contenido)
                val productos = mutableListOf<Producto>()
                for (posicion in 0 until registros.length()) {
                    contexto.ensureActive()
                    val registro: JSONObject = registros.getJSONObject(posicion)
                    val nombre = registro.get("nombre") as? String
                        ?: throw JSONException("El nombre del producto no es válido.")
                    val precio = registro.getDouble("precio")
                    val stock = registro.getDouble("stock")
                    if (nombre.isBlank() || !precio.isFinite() || precio < 0 ||
                        !stock.isFinite() || stock < 0 || stock > Int.MAX_VALUE || stock % 1.0 != 0.0
                    ) {
                        throw JSONException("Un producto contiene datos inválidos.")
                    }
                    productos.add(
                        Producto(
                            id = if (registro.isNull("id")) null else registro.getString("id"),
                            nombre = nombre,
                            descripcion = registro.getString("descripcion"),
                            precio = precio,
                            stock = stock.toInt(),
                            categoria = registro.getString("categoria")
                        )
                    )
                }
                productos
            }
        } finally {
            solicitud.cancel()
            solicitudActual = null
        }
    }

    fun cancelarSolicitud() {
        solicitudActual?.cancel()
    }
}
