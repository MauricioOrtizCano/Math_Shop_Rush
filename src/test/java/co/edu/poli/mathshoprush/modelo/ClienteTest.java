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
     * R9a: el cliente puede cambiar su pago por uno de menor valor que siga
     * cubriendo el total; el vuelto esperado se recalcula.
     */
    @Test
    void cambiarPagoAceptaUnPagoMenorQueCubreElTotal() {
        Cliente c = GeneradorFijo.cliente(1, 5300, GeneradorFijo.pago(Denominacion.BILLETE_50000), 60);
        c.cambiarPago(GeneradorFijo.pago(Denominacion.BILLETE_5000, Denominacion.MONEDA_1000));
        assertEquals(6000, c.getPago().total());
        assertEquals(700, c.getVueltoEsperado());
        assertFalse(c.isRedondea());
    }

    /**
     * R9a: el nuevo pago no puede ser menor que el total de la compra, y
     * el pago anterior se conserva si el cambio falla.
     */
    @Test
    void cambiarPagoRechazaUnPagoQueNoCubreElTotal() {
        Cliente c = GeneradorFijo.cliente(1, 5300, GeneradorFijo.pago(Denominacion.BILLETE_50000), 60);
        assertThrows(IllegalArgumentException.class,
                () -> c.cambiarPago(GeneradorFijo.pago(Denominacion.BILLETE_5000)));
        assertEquals(50000, c.getPago().total());
    }

    /**
     * R9a: al pedir sencillo el cliente nunca entrega más de lo que ya había
     * pagado.
     */
    @Test
    void cambiarPagoRechazaUnPagoMayorQueElAnterior() {
        Cliente c = GeneradorFijo.cliente(1, 5300,
                GeneradorFijo.pago(Denominacion.BILLETE_5000, Denominacion.MONEDA_500), 60);
        assertThrows(IllegalArgumentException.class,
                () -> c.cambiarPago(GeneradorFijo.pago(Denominacion.BILLETE_5000, Denominacion.MONEDA_1000)));
        assertEquals(5500, c.getPago().total());
    }
}
