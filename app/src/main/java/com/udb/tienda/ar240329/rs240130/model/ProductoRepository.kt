package com.udb.tienda.ar240329.rs240130.model

class ProductoRepository {
    private val cliente = ClienteMockApi()
    private val servicio: ProductoService by lazy {
        cliente.crearServicio(ProductoService::class.java)
    }

    fun obtenerProductos(resultado: (Result<List<Producto>>) -> Unit) {
        cliente.ejecutar({ servicio.obtenerProductos() }, resultado)
    }

    fun obtenerProductoPorId(id: String, resultado: (Result<Producto>) -> Unit) {
        cliente.ejecutar(
            { servicio.obtenerProductoPorId(id) },
            resultado,
            mensajeNoEncontrado = "Producto no encontrado"
        )
    }

    fun crearProducto(producto: Producto, resultado: (Result<Producto>) -> Unit) {
        cliente.ejecutar({ servicio.crearProducto(producto) }, resultado)
    }

    fun actualizarProducto(id: String, producto: Producto, resultado: (Result<Producto>) -> Unit) {
        cliente.ejecutar({ servicio.actualizarProducto(id, producto) }, resultado)
    }

    fun eliminarProducto(id: String, resultado: (Result<Unit>) -> Unit) {
        cliente.ejecutar({ servicio.eliminarProducto(id) }, resultado, Unit)
    }

    fun cancelarSolicitud() {
        cliente.cancelarSolicitud()
    }
}
