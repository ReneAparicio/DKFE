package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.udb.tienda.ar240329.rs240130.R
import com.udb.tienda.ar240329.rs240130.controller.ReporteInventarioController
import com.udb.tienda.ar240329.rs240130.databinding.ActivityReporteInventarioBinding
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONException

class ReporteInventarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReporteInventarioBinding
    private val controlador = ReporteInventarioController()
    private val adaptador = StockBajoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReporteInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        binding.listaStockBajo.layoutManager = LinearLayoutManager(this)
        binding.listaStockBajo.adapter = adaptador
        lifecycleScope.launch {
            try {
                val reporte = controlador.obtenerReporte()
                binding.textoTotalProductos.text = getString(R.string.total_productos_reporte, reporte.totalProductos)
                val valor = NumberFormat.getCurrencyInstance(Locale.US).format(reporte.valorTotal)
                binding.textoValorTotal.text = getString(R.string.valor_inventario_reporte, valor)
                binding.resumenReporte.visibility = View.VISIBLE
                adaptador.mostrarProductos(reporte.productosStockBajo)
                binding.textoEstado.visibility = if (reporte.productosStockBajo.isEmpty()) View.VISIBLE else View.GONE
                binding.textoEstado.setText(R.string.sin_stock_bajo)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                mostrarError(getString(R.string.error_conexion_reporte) + " " + error.message.orEmpty())
            } catch (error: JSONException) {
                mostrarError(getString(R.string.respuesta_invalida_reporte))
            } catch (error: IllegalArgumentException) {
                mostrarError(error.message ?: getString(R.string.error_cargar_reporte))
            } catch (error: IllegalStateException) {
                mostrarError(error.message ?: getString(R.string.error_cargar_reporte))
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.resumenReporte.visibility = View.GONE
        binding.textoEstado.text = mensaje
        binding.textoEstado.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        controlador.cancelarSolicitud()
        super.onDestroy()
    }
}
