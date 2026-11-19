package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la enumeración {@link Denominacion}.
 */
class DenominacionTest {

    /**
     * Las denominaciones están ordenadas de mayor a menor valor.
     */
    @Test
    void estanOrdenadasDeMayorAMenor() {
        Denominacion[] ds = Denominacion.values();
        assertEquals(10, ds.length);
        for (int i = 0; i < ds.length - 1; i++) {
            assertTrue(ds[i].getValor() > ds[i + 1].getValor());
        }
    }

    /**
     * Distingue billetes de monedas.
     */
    @Test
    void distingueBilletesDeMonedas() {
        assertTrue(Denominacion.BILLETE_2000.esBillete());
        assertFalse(Denominacion.MONEDA_1000.esBillete());
        assertEquals(50000, Denominacion.BILLETE_50000.getValor());
        assertEquals("$5.000", Denominacion.BILLETE_5000.getEtiqueta());
    }
}
