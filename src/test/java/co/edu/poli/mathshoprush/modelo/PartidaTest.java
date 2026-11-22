package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Partida}: reglas R17 a R20.
 */
class PartidaTest {

    /**
     * R19: la primera jornada abre con la base inicial.
     */
    @Test
    void primeraJornadaUsaLaBaseInicial() {
        Partida p = new Partida(new GeneradorFijo());
        assertTrue(p.esPrimeraJornada());
        Jornada j = p.iniciarJornada(null);
        assertEquals(Partida.baseInicial().total(), j.getCaja().total());
        assertEquals(1, p.getNivel());
        assertEquals(0, p.getSaldoCajaFuerte(), "La caja fuerte empieza en $0");
    }

    /**
     * No se puede cerrar ni abrir una jornada fuera de orden.
     */
    @Test
    void controlaElOrdenDeLasJornadas() {
        Partida p = new Partida(new GeneradorFijo());
        assertThrows(IllegalStateException.class, p::finalizarJornada);
        Jornada j = p.iniciarJornada(null);
        assertThrows(IllegalStateException.class, p::finalizarJornada);
        assertThrows(IllegalStateException.class, () -> p.iniciarJornada(null));
        j.terminarAnticipadamente();
        p.finalizarJornada();
        assertThrows(IllegalStateException.class, p::finalizarJornada);
    }

    /**
     * R17/R18: si no se cumple la meta se repite el nivel y la caja se
     * deposita en la caja fuerte.
     */
    @Test
    void sinMetaSeRepiteElNivelYSeDepositaLaCaja() {
        Partida p = new Partida(new GeneradorFijo());
        Jornada j = p.iniciarJornada(null);
        j.terminarAnticipadamente();
        ResumenJornada r = p.finalizarJornada();
        assertFalse(r.isAprobada());
        assertEquals(1, p.getNivel());
        assertEquals(1, p.getJornadasJugadas());
        assertEquals(Partida.baseInicial().total(), p.getSaldoCajaFuerte());
    }

    /**
     * R18/R20: desde la segunda jornada la base se retira de la caja fuerte,
     * y al cerrar se vuelve a depositar todo el efectivo de la caja.
     */
    @Test
    void segundaJornadaRetiraLaBaseDeLaCajaFuerte() {
        Partida p = new Partida(new GeneradorFijo());
        p.iniciarJornada(null).terminarAnticipadamente();
        p.finalizarJornada();
        int saldo = p.getSaldoCajaFuerte();

        Efectivo base = new Efectivo();
        base.agregar(Denominacion.MONEDA_1000, 5);
        Jornada j2 = p.iniciarJornada(base);
        assertEquals(5000, j2.getCaja().total());
        assertEquals(saldo - 5000, p.getSaldoCajaFuerte());
        j2.terminarAnticipadamente();
        p.finalizarJornada();
        assertEquals(saldo, p.getSaldoCajaFuerte(), "La base vuelve a la caja fuerte al cerrar");

        Efectivo demasiado = new Efectivo();
        demasiado.agregar(Denominacion.BILLETE_50000, 5);
        assertThrows(IllegalArgumentException.class, () -> p.iniciarJornada(demasiado));
    }

    /**
     * R17: al cumplir la meta se avanza de nivel; al superar el nivel 4 se
     * completa la partida.
     */
    @Test
    void superarLosCuatroNivelesCompletaLaPartida() {
        Denominacion b50 = Denominacion.BILLETE_50000;
        Denominacion b20 = Denominacion.BILLETE_20000;
        int[] metas = { 20000, 50000, 90000, 150000 };
        Cliente[] clientes = {
                GeneradorFijo.cliente(1, 20000, GeneradorFijo.pago(b20), 1000),
                GeneradorFijo.cliente(2, 50000, GeneradorFijo.pago(b50), 1000),
                GeneradorFijo.cliente(3, 90000, GeneradorFijo.pago(b50, b20, b20), 1000),
                GeneradorFijo.cliente(4, 150000, GeneradorFijo.pago(b50, b50, b50), 1000) };
        Partida p = new Partida(new GeneradorFijo(clientes));
        for (int i = 0; i < 4; i++) {
            assertEquals(i + 1, p.getNivel());
            Jornada j = p.iniciarJornada(i == 0 ? null : new Efectivo());
            j.avanzar(1.0);
            j.responderTotal(metas[i]);
            j.terminarAnticipadamente();
            ResumenJornada r = p.finalizarJornada();
            assertTrue(r.isAprobada(), "nivel " + (i + 1));
        }
        assertTrue(p.isCompletada());
        assertEquals(4, p.getNivel());
        assertThrows(IllegalStateException.class, () -> p.iniciarJornada(new Efectivo()));
    }
}
