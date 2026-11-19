package co.edu.poli.mathshoprush.modelo;

import java.util.List;

/**
 * Compra de un cliente: la lista de productos que lleva al mostrador.
 * Es inmutable.
 */
public final class Compra {

    /**
     * Productos de la compra. Lista inmutable y nunca vacía.
     */
    private final List<Producto> productos;

    /**
     * Suma de los precios de los productos (respuesta de la suma).
     */
    private final int total;

    /**
     * Crea una compra.
     *
     * @param productos productos comprados, no nulos ni vacíos
     * @throws IllegalArgumentException si la lista es nula o vacía
     */
    public Compra(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("La compra debe tener al menos un producto");
        }
        this.productos = List.copyOf(productos);
        int suma = 0;
        for (Producto p : this.productos) {
            suma += p.getPrecio();
        }
        this.total = suma;
    }

    /**
     * Devuelve los productos de la compra.
     *
     * @return lista inmutable de productos
     */
    public List<Producto> getProductos() {
        return productos;
    }

    /**
     * Devuelve el total que debe pagar el cliente.
     *
     * @return suma de los precios, en pesos
     */
    public int getTotal() {
        return total;
    }
}
