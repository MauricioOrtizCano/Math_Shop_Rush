package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Compra}.
 */
class CompraTest {

    /**
     * El total es la suma de los precios.
     */
    @Test
    void calculaTotal() {
        Compra c = new Compra(List.of(new Producto("Pan", 1500), new Producto("Leche", 2500)));
        assertEquals(4000, c.getTotal());
        assertEquals(2, c.getProductos().size());
    }

    /**
     * La compra necesita al menos un producto.
     */
    @Test
    void rechazaListaVaciaONula() {
        assertThrows(IllegalArgumentException.class, () -> new Compra(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Compra(null));
    }

    /**
     * La lista de productos expuesta no se puede modificar.
     */
    @Test
    void listaEsInmutable() {
        Compra c = new Compra(List.of(new Producto("Pan", 1500)));
        assertThrows(UnsupportedOperationException.class,
                () -> c.getProductos().add(new Producto("Arroz", 100)));
    }
}
