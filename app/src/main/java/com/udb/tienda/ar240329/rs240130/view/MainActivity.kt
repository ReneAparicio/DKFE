package com.udb.tienda.ar240329.rs240130.view

import android.os.Bundle
import android.content.Intent
import com.udb.tienda.ar240329.rs240130.databinding.ActivityMainBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.botonCatalogoProveedor.setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))
        }
        binding.botonInventario.setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
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
            vista.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
