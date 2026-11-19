package co.edu.poli.mathshoprush.modelo;

/**
 * Producto de la tienda con un nombre y un precio en pesos. Es inmutable.
 */
public final class Producto {

    /**
     * Nombre del producto, por ejemplo "Pan".
     */
    private final String nombre;

    /**
     * Precio del producto en pesos. Siempre es mayor que cero.
     */
    private final int precio;

    /**
     * Crea un producto.
     *
     * @param nombre nombre no vacío del producto
     * @param precio precio en pesos, mayor que cero
     * @throws IllegalArgumentException si el nombre es nulo o vacío, o el
     *                                  precio no es positivo
     */
    public Producto(String nombre, int precio) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.nombre = nombre;
        this.precio = precio;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el precio del producto.
     *
     * @return precio en pesos
     */
    public int getPrecio() {
        return precio;
    }

    /**
     * Representación en texto del producto.
     *
     * @return texto con nombre y precio
     */
    @Override
    public String toString() {
        return nombre + " (" + FormatoDinero.formatear(precio) + ")";
    }
}
