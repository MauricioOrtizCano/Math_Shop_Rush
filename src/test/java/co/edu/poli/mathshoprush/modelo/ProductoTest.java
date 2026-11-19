package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Producto}.
 */
class ProductoTest {

    /**
     * Un producto válido conserva nombre y precio.
     */
    @Test
    void creaProductoValido() {
        Producto p = new Producto("Pan", 1500);
        assertEquals("Pan", p.getNombre());
        assertEquals(1500, p.getPrecio());
    }

    /**
     * El precio debe ser positivo y el nombre no puede estar vacío.
     */
    @Test
    void rechazaDatosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("Pan", 0));
        assertThrows(IllegalArgumentException.class, () -> new Producto("Pan", -100));
        assertThrows(IllegalArgumentException.class, () -> new Producto(null, 100));
        assertThrows(IllegalArgumentException.class, () -> new Producto("  ", 100));
    }
}
