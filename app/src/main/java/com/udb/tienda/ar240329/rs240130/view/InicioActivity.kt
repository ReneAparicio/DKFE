package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.content.Intent
import com.udb.tienda.ar240329.rs240130.databinding.ActivityMainBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val densidad = resources.displayMetrics.density
        val alturaEncabezado = (resources.configuration.screenHeightDp * 0.38f).coerceIn(260f, 320f)
        binding.encabezadoInicio.layoutParams.height = (alturaEncabezado * densidad).toInt()
        binding.botonCatalogoProveedor.setOnClickListener {
            startActivity(Intent(this, CatalogoProveedorActivity::class.java))
        }
        binding.botonInventario.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.botonCategorias.setOnClickListener {
            startActivity(Intent(this, CategoriasActivity::class.java))
        }
        binding.botonBuscarProductoPorId.setOnClickListener {
            startActivity(Intent(this, BuscarProductoActivity::class.java))
        }
        binding.botonReporteInventario.setOnClickListener {
            startActivity(Intent(this, ReporteInventarioActivity::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { vista, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.tarjetaLogo.translationY = systemBars.top / 2f
            val anchoDisponible = resources.displayMetrics.widthPixels - systemBars.left - systemBars.right
            val parametrosTarjeta = binding.tarjetaLogo.layoutParams
            parametrosTarjeta.width = minOf(anchoDisponible - (48 * densidad).toInt(), (360 * densidad).toInt())
            binding.tarjetaLogo.layoutParams = parametrosTarjeta
            insets
        }
    }
}
