package com.task.shopmarket;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestUnitMarket {

    private CarritoDeCompras carrito;
    private Inventario inventario;
    private Producto manzana;
    private Producto platano;

    @Before
    public void setUp() {
        carrito = new CarritoDeCompras();
        inventario = new Inventario();
        manzana = new Producto("Manzana", 1.0);
        platano = new Producto("Plátano", 0.5);
        inventario.agregarProducto(manzana, 10);
        inventario.agregarProducto(platano, 20);
    }

    @Test
    public void testAgregarProductoCarrito() {
        carrito.agregarProducto(manzana);
        assertEquals(1, carrito.contarProductos());
        assertTrue(carrito.obtenerProductos().contains(manzana));
    }

    @Test
    public void testEliminarProductoCarrito() {
        carrito.agregarProducto(manzana);
        carrito.eliminarProducto(manzana);
        assertEquals(0, carrito.contarProductos());
        assertFalse(carrito.obtenerProductos().contains(manzana));
    }

    @Test
    public void testCalcularTotalCarrito() {
        carrito.agregarProducto(manzana);
        carrito.agregarProducto(platano);
        double totalEsperado = 1.5;
        assertEquals(totalEsperado, carrito.calcularTotal(), 0.0);
    }

    @Test
    public void testAplicarDescuento() {
        carrito.agregarProducto(manzana);
        carrito.agregarProducto(platano);
        carrito.aplicarDescuento(0.1); // 10% de descuento
        double totalEsperado = 1.35; // 1.5 - 10%
        assertEquals(totalEsperado, carrito.calcularTotal(), 0.0);
    }

    @Test
    public void testVerificarDisponibilidadProducto() {
        assertTrue(inventario.estaProductoDisponible(manzana));
        inventario.reducirCantidadProducto(manzana, 10);
        assertFalse(inventario.estaProductoDisponible(manzana));
    }

    // Clases y métodos
    static class CarritoDeCompras {
        private List<Producto> productos = new ArrayList<>();
        private double descuento = 0.0;

        public void agregarProducto(Producto producto) {
            productos.add(producto);
        }

        public void eliminarProducto(Producto producto) {
            productos.remove(producto);
        }

        public int contarProductos() {
            return productos.size();
        }

        public List<Producto> obtenerProductos() {
            return new ArrayList<>(productos);
        }

        public double calcularTotal() {
            return productos.stream().mapToDouble(Producto::getPrecio).sum() * (1 - descuento);
        }

        public void aplicarDescuento(double descuento) {
            this.descuento = descuento;
        }
    }

    static class Inventario {
        private Map<Producto, Integer> productos = new HashMap<>();

        public void agregarProducto(Producto producto, int cantidad) {
            productos.put(producto, productos.getOrDefault(producto, 0) + cantidad);
        }

        public void reducirCantidadProducto(Producto producto, int cantidad) {
            if (productos.containsKey(producto)) {
                int nuevaCantidad = productos.get(producto) - cantidad;
                if (nuevaCantidad <= 0) {
                    productos.remove(producto);
                } else {
                    productos.put(producto, nuevaCantidad);
                }
            }
        }

        public boolean estaProductoDisponible(Producto producto) {
            return productos.containsKey(producto) && productos.get(producto) > 0;
        }
    }

    static class Producto {
        private String nombre;
        private double precio;

        public Producto(String nombre, double precio) {
            this.nombre = nombre;
            this.precio = precio;
        }

        public String getNombre() {
            return nombre;
        }

        public double getPrecio() {
            return precio;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Producto producto = (Producto) obj;
            return Double.compare(producto.precio, precio) == 0 && nombre.equals(producto.nombre);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nombre, precio);
        }
    }
}
