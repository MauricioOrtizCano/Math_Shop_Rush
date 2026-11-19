package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Efectivo}.
 */
class EfectivoTest {

    /**
     * Agregar piezas suma al total y al conteo.
     */
    @Test
    void agregaPiezasYCalculaTotales() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.BILLETE_10000);
        e.agregar(Denominacion.MONEDA_100, 3);
        assertEquals(10300, e.total());
        assertEquals(4, e.totalPiezas());
        assertEquals(3, e.cantidad(Denominacion.MONEDA_100));
        assertFalse(e.estaVacio());
    }

    /**
     * Quitar piezas reduce el efectivo; no se pueden quitar más de las que hay.
     */
    @Test
    void quitaPiezasYValidaExistencias() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.MONEDA_500, 2);
        e.quitar(Denominacion.MONEDA_500);
        assertEquals(1, e.cantidad(Denominacion.MONEDA_500));
        assertThrows(IllegalArgumentException.class, () -> e.quitar(Denominacion.MONEDA_500, 2));
        assertThrows(IllegalArgumentException.class, () -> e.agregar(Denominacion.MONEDA_500, -1));
        e.quitar(Denominacion.MONEDA_500);
        assertTrue(e.estaVacio());
    }

    /**
     * Quitar todo lo de otro efectivo no modifica nada si no alcanza.
     */
    @Test
    void quitarTodoEsAtomico() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.BILLETE_5000);
        Efectivo pedido = new Efectivo();
        pedido.agregar(Denominacion.BILLETE_5000);
        pedido.agregar(Denominacion.MONEDA_50);
        assertFalse(e.contiene(pedido));
        assertThrows(IllegalArgumentException.class, () -> e.quitarTodo(pedido));
        assertEquals(5000, e.total());
    }

    /**
     * La copia es independiente del original.
     */
    @Test
    void copiaEsIndependiente() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.MONEDA_200);
        Efectivo c = e.copia();
        c.agregar(Denominacion.MONEDA_200);
        assertEquals(200, e.total());
        assertEquals(400, c.total());
    }

    /**
     * El monto se forma con la menor cantidad de piezas.
     */
    @Test
    void formaMontoConMenorCantidadDePiezas() {
        Efectivo e = Efectivo.conMenorCantidadDePiezas(4700);
        assertEquals(4700, e.total());
        assertEquals(2, e.cantidad(Denominacion.BILLETE_2000));
        assertEquals(1, e.cantidad(Denominacion.MONEDA_500));
        assertEquals(1, e.cantidad(Denominacion.MONEDA_200));
        assertEquals(4, e.totalPiezas());
        assertThrows(IllegalArgumentException.class, () -> Efectivo.conMenorCantidadDePiezas(75));
        assertTrue(Efectivo.conMenorCantidadDePiezas(0).estaVacio());
    }

    /**
     * Comprueba si un monto se puede formar con las piezas disponibles.
     */
    @Test
    void puedeFormarMontos() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.BILLETE_5000);
        e.agregar(Denominacion.BILLETE_2000);
        assertTrue(e.puedeFormar(0));
        assertTrue(e.puedeFormar(2000));
        assertTrue(e.puedeFormar(7000));
        assertFalse(e.puedeFormar(3000));
        assertFalse(e.puedeFormar(7050));
        assertFalse(e.puedeFormar(7025));
        assertFalse(e.puedeFormar(-50));
    }

    /**
     * Respeta la cantidad limitada de cada denominación.
     */
    @Test
    void puedeFormarRespetaCantidades() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.MONEDA_1000, 2);
        assertTrue(e.puedeFormar(2000));
        assertFalse(e.puedeFormar(3000));

        Efectivo f = new Efectivo();
        f.agregar(Denominacion.BILLETE_2000);
        f.agregar(Denominacion.MONEDA_500, 3);
        assertTrue(f.puedeFormar(3500));
        assertTrue(f.puedeFormar(1500));
        assertTrue(f.puedeFormar(2500));
        assertFalse(f.puedeFormar(4000));
    }
}
