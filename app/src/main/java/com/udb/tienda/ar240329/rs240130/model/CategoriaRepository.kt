package com.udb.tienda.ar240329.rs240130.model

class CategoriaRepository {
    private val cliente = ClienteMockApi()
    private val servicio: CategoriaService by lazy {
        cliente.crearServicio(CategoriaService::class.java)
    }

    fun obtenerCategorias(resultado: (Result<List<Categoria>>) -> Unit) {
        cliente.ejecutar({ servicio.obtenerCategorias() }, resultado)
    }

    fun obtenerCategoriaPorId(id: String, resultado: (Result<Categoria>) -> Unit) {
        cliente.ejecutar({ servicio.obtenerCategoriaPorId(id) }, resultado)
    }

    fun crearCategoria(categoria: Categoria, resultado: (Result<Categoria>) -> Unit) {
        cliente.ejecutar({ servicio.crearCategoria(categoria) }, resultado)
    }

    fun actualizarCategoria(id: String, categoria: Categoria, resultado: (Result<Categoria>) -> Unit) {
        cliente.ejecutar({ servicio.actualizarCategoria(id, categoria) }, resultado)
    }

    fun eliminarCategoria(id: String, resultado: (Result<Unit>) -> Unit) {
        cliente.ejecutar({ servicio.eliminarCategoria(id) }, resultado, Unit)
    }

    fun cancelarSolicitud() {
        cliente.cancelarSolicitud()
    }
}
