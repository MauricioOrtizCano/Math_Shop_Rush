package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Cliente}.
 */
class ClienteTest {

    /**
     * El vuelto esperado es pago menos total.
     */
    @Test
    void calculaVueltoEsperado() {
        Cliente c = GeneradorFijo.cliente(1, 5300, GeneradorFijo.pago(Denominacion.BILLETE_10000), 60);
        assertEquals(4700, c.getVueltoEsperado());
        assertFalse(c.isRedondea());
    }

    /**
     * La paciencia disminuye y nunca baja de cero.
     */
    @Test
    void consumePaciencia() {
        Cliente c = GeneradorFijo.cliente(1, 1000, GeneradorFijo.pago(Denominacion.BILLETE_2000), 10);
        assertEquals(1.0, c.getFraccionPaciencia(), 1e-9);
        c.consumirPaciencia(4);
        assertEquals(6.0, c.getPacienciaActual(), 1e-9);
        assertFalse(c.sinPaciencia());
        c.consumirPaciencia(100);
        assertEquals(0.0, c.getPacienciaActual(), 1e-9);
        assertTrue(c.sinPaciencia());
    }

    /**
     * El pago debe cubrir la compra y la paciencia debe ser positiva.
     */
    @Test
    void validaPagoYPaciencia() {
        assertThrows(IllegalArgumentException.class,
                () -> GeneradorFijo.cliente(1, 5000, GeneradorFijo.pago(Denominacion.BILLETE_2000), 10));
        assertThrows(IllegalArgumentException.class,
                () -> GeneradorFijo.cliente(1, 1000, GeneradorFijo.pago(Denominacion.BILLETE_2000), 0));
    }

    /**
     * Cambiar el pago exige mantener el mismo valor.
     */
    @Test
    void cambiarPagoMantieneElValor() {
        Cliente c = GeneradorFijo.cliente(1, 1000, GeneradorFijo.pago(Denominacion.BILLETE_2000), 10);
        c.cambiarPago(GeneradorFijo.pago(Denominacion.MONEDA_1000, Denominacion.MONEDA_1000));
        assertEquals(2000, c.getPago().total());
        assertThrows(IllegalArgumentException.class,
                () -> c.cambiarPago(GeneradorFijo.pago(Denominacion.MONEDA_1000)));
    }
}
