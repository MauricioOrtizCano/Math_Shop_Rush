package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.poli.mathshoprush.modelo.Jornada.PasoAtencion;
import co.edu.poli.mathshoprush.modelo.Jornada.ResultadoTotal;
import co.edu.poli.mathshoprush.modelo.Jornada.ResultadoVuelto;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link Jornada}: reglas R1 a R16.
 */
class JornadaTest {

    /**
     * Total de la compra de los clientes de prueba.
     */
    private static final int TOTAL = 5300;

    /**
     * Pago de los clientes de prueba (un billete de $10.000, vuelto $4.700).
     *
     * @return efectivo con un billete de $10.000
     */
    private static Efectivo pagoDiez() {
        return GeneradorFijo.pago(Denominacion.BILLETE_10000);
    }

    /**
     * Crea un cliente de prueba con total $5.300 y paga con $10.000.
     *
     * @param numero número del cliente
     * @return cliente de prueba
     */
    private static Cliente clienteBase(int numero) {
        return GeneradorFijo.cliente(numero, TOTAL, pagoDiez(), 60);
    }

    /**
     * Crea una jornada de nivel 1 con la base inicial.
     *
     * @param clientes clientes que llegarán
     * @return jornada sin avanzar
     */
    private Jornada nueva(Cliente... clientes) {
        return nueva(Partida.baseInicial(), Partida.reservaInicialCajaFuerte(), clientes);
    }

    /**
     * Crea una jornada de nivel 1.
     *
     * @param caja       caja inicial
     * @param cajaFuerte caja fuerte
     * @param clientes   clientes que llegarán
     * @return jornada sin avanzar
     */
    private Jornada nueva(Efectivo caja, Efectivo cajaFuerte, Cliente... clientes) {
        return new Jornada(ConfiguracionNivel.deNivel(1), caja, cajaFuerte, new GeneradorFijo(clientes));
    }

    /**
     * Crea una jornada y deja al primer cliente en el mostrador.
     *
     * @param cliente cliente que llega
     * @return jornada con el cliente ya presente
     */
    private Jornada conCliente(Cliente cliente) {
        Jornada j = nueva(cliente);
        j.avanzar(1.0);
        return j;
    }

    /**
     * El primer cliente llega al abrir la tienda.
     */
    @Test
    void llegaUnCliente() {
        Jornada j = nueva(clienteBase(1));
        assertNull(j.getClienteActual());
        j.avanzar(1.0);
        assertNotNull(j.getClienteActual());
        assertEquals(PasoAtencion.CALCULAR_TOTAL, j.getPaso());
        assertFalse(j.isTotalVisible());
    }

    /**
     * R2/R6: un total correcto pasa al vuelto y el pago entra a la caja.
     */
    @Test
    void totalCorrectoPasaAlVuelto() {
        Jornada j = conCliente(clienteBase(1));
        assertEquals(ResultadoTotal.CORRECTO, j.responderTotal(TOTAL));
        assertEquals(100, j.getReputacion());
        assertEquals(PasoAtencion.ENTREGAR_VUELTO, j.getPaso());
        assertTrue(j.isTotalVisible());
        assertEquals(Partida.baseInicial().total() + 10000, j.getCaja().total());
    }

    /**
     * R3/R13: un total incorrecto resta 10 de reputación y se continúa.
     */
    @Test
    void totalIncorrectoRestaReputacion() {
        Jornada j = conCliente(clienteBase(1));
        assertEquals(ResultadoTotal.INCORRECTO, j.responderTotal(5000));
        assertEquals(90, j.getReputacion());
        assertEquals(1, j.getErrores());
        assertEquals(PasoAtencion.ENTREGAR_VUELTO, j.getPaso());
    }

    /**
     * R8/R10/R11: vuelto exacto con la menor cantidad de piezas da bono.
     */
    @Test
    void vueltoCorrectoConMenorCantidadDePiezasDaBono() {
        Jornada j = conCliente(clienteBase(1));
        j.responderTotal(TOTAL);
        assertTrue(j.ponerEnMostrador(Denominacion.BILLETE_2000));
        assertTrue(j.ponerEnMostrador(Denominacion.BILLETE_2000));
        assertTrue(j.ponerEnMostrador(Denominacion.MONEDA_500));
        assertTrue(j.ponerEnMostrador(Denominacion.MONEDA_200));
        assertEquals(4700, j.getMostrador().total());
        assertEquals(ResultadoVuelto.CORRECTO_CON_BONO, j.confirmarVuelto());
        assertEquals(TOTAL + Jornada.BONO_VUELTO_MINIMO, j.getGanancias());
        assertEquals(1, j.getClientesAtendidos());
        assertEquals(1, j.getBonificaciones());
        assertNull(j.getClienteActual());
        assertEquals(Partida.baseInicial().total() + 10000 - 4700, j.getCaja().total());
    }

    /**
     * R8: cualquier combinación exacta es válida, pero sin bono si usa más piezas.
     */
    @Test
    void vueltoCorrectoSinBono() {
        Jornada j = conCliente(clienteBase(1));
        j.responderTotal(TOTAL);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        j.ponerEnMostrador(Denominacion.MONEDA_200);
        j.ponerEnMostrador(Denominacion.MONEDA_200);
        j.ponerEnMostrador(Denominacion.MONEDA_200);
        j.ponerEnMostrador(Denominacion.MONEDA_100);
        assertEquals(ResultadoVuelto.CORRECTO, j.confirmarVuelto());
        assertEquals(TOTAL, j.getGanancias());
        assertEquals(0, j.getBonificaciones());
    }

    /**
     * R13: un vuelto incorrecto resta 20, el cliente se va sin comprar y el
     * efectivo queda como estaba.
     */
    @Test
    void vueltoIncorrectoRestaReputacionYDevuelveEfectivo() {
        Jornada j = conCliente(clienteBase(1));
        j.responderTotal(TOTAL);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        assertEquals(ResultadoVuelto.INCORRECTO, j.confirmarVuelto());
        assertEquals(80, j.getReputacion());
        assertEquals(0, j.getGanancias());
        assertEquals(0, j.getClientesAtendidos());
        assertNull(j.getClienteActual());
        assertTrue(j.getMostrador().estaVacio());
        assertEquals(Partida.baseInicial().total(), j.getCaja().total());
    }

    /**
     * Solo se mueve efectivo cuando toca dar el vuelto y hay piezas.
     */
    @Test
    void controlaElMovimientoDeEfectivo() {
        Jornada j = conCliente(clienteBase(1));
        assertFalse(j.ponerEnMostrador(Denominacion.MONEDA_500));
        j.responderTotal(TOTAL);
        assertFalse(j.ponerEnMostrador(Denominacion.BILLETE_50000));
        int antes = j.getCaja().cantidad(Denominacion.MONEDA_500);
        assertTrue(j.ponerEnMostrador(Denominacion.MONEDA_500));
        assertEquals(antes - 1, j.getCaja().cantidad(Denominacion.MONEDA_500));
        assertEquals(500, j.getMostrador().total());
        assertTrue(j.quitarDelMostrador(Denominacion.MONEDA_500));
        assertFalse(j.quitarDelMostrador(Denominacion.MONEDA_500));
        assertEquals(antes, j.getCaja().cantidad(Denominacion.MONEDA_500));
    }

    /**
     * Un pago exacto completa la venta sin entregar vuelto.
     */
    @Test
    void pagoExactoCompletaLaVenta() {
        Cliente c = GeneradorFijo.cliente(1, TOTAL, Efectivo.conMenorCantidadDePiezas(TOTAL), 60);
        Jornada j = conCliente(c);
        j.responderTotal(TOTAL);
        assertEquals(TOTAL, j.getGanancias());
        assertEquals(1, j.getClientesAtendidos());
        assertNull(j.getClienteActual());
    }

    /**
     * R5: el cliente que redondea deja un vuelto múltiplo de $1.000.
     */
    @Test
    void clienteQueRedondea() {
        Efectivo pago = GeneradorFijo.pago(Denominacion.BILLETE_10000,
                Denominacion.MONEDA_100, Denominacion.MONEDA_100, Denominacion.MONEDA_100);
        Cliente c = new Cliente(1, new Compra(java.util.List.of(new Producto("Pan", TOTAL))),
                pago, true, 60);
        assertTrue(c.isRedondea());
        assertEquals(5000, c.getVueltoEsperado());
    }

    /**
     * R9a: sin efectivo para el vuelto se puede pedir sencillo.
     */
    @Test
    void pedirSencilloPermiteDarElVuelto() {
        Efectivo pobre = new Efectivo();
        pobre.agregar(Denominacion.MONEDA_1000);
        Jornada j = nueva(pobre, Partida.reservaInicialCajaFuerte(), clienteBase(1));
        j.avanzar(1.0);
        j.responderTotal(TOTAL);
        assertFalse(j.puedeDarVuelto());
        j.pedirSencillo();
        assertTrue(j.puedeDarVuelto());
        assertEquals(1000 + 10000, j.getCaja().total());
        assertEquals(60 - Jornada.FRACCION_PACIENCIA_SENCILLO * 60,
                j.getClienteActual().getPacienciaActual(), 1e-9);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        j.ponerEnMostrador(Denominacion.MONEDA_500);
        j.ponerEnMostrador(Denominacion.MONEDA_200);
        assertEquals(ResultadoVuelto.CORRECTO_CON_BONO, j.confirmarVuelto());
    }

    /**
     * No se puede pedir sencillo si la caja tiene efectivo suficiente.
     */
    @Test
    void noSePideSencilloSiHayEfectivo() {
        Jornada j = conCliente(clienteBase(1));
        j.responderTotal(TOTAL);
        assertTrue(j.puedeDarVuelto());
        assertThrows(IllegalStateException.class, j::pedirSencillo);
    }

    /**
     * R9b: sacar de la caja fuerte pasa el efectivo a la caja y consume tiempo.
     */
    @Test
    void cajaFuertePasaEfectivoYConsumeTiempo() {
        Efectivo fuerte = Partida.reservaInicialCajaFuerte();
        Jornada j = nueva(Partida.baseInicial(), fuerte, clienteBase(1));
        double antes = j.getSegundosTranscurridos();
        Efectivo pedido = GeneradorFijo.pago(Denominacion.BILLETE_5000);
        j.sacarDeCajaFuerte(pedido);
        assertEquals(Partida.baseInicial().total() + 5000, j.getCaja().total());
        assertEquals(1, fuerte.cantidad(Denominacion.BILLETE_5000));
        assertEquals(antes + Jornada.SEGUNDOS_CAJA_FUERTE, j.getSegundosTranscurridos(), 1e-6);
        Efectivo demasiado = GeneradorFijo.pago(Denominacion.BILLETE_50000);
        assertThrows(IllegalArgumentException.class, () -> j.sacarDeCajaFuerte(demasiado));
    }

    /**
     * R13/R15: el cliente se va si se acaba su paciencia y la reputación baja 15.
     */
    @Test
    void clienteSeVaSiSeAgotaLaPaciencia() {
        Jornada j = conCliente(GeneradorFijo.cliente(1, TOTAL, pagoDiez(), 10));
        assertEquals(1, j.avanzar(10));
        assertEquals(85, j.getReputacion());
        assertEquals(1, j.getClientesPerdidos());
        assertNull(j.getClienteActual());
    }

    /**
     * Los clientes de la fila también pierden paciencia y se atienden en orden.
     */
    @Test
    void laFilaSeAtiendeEnOrden() {
        Cliente c1 = GeneradorFijo.cliente(1, TOTAL, Efectivo.conMenorCantidadDePiezas(TOTAL), 60);
        Cliente c2 = GeneradorFijo.cliente(2, TOTAL, Efectivo.conMenorCantidadDePiezas(TOTAL), 60);
        Jornada j = nueva(c1, c2);
        j.avanzar(1.0);
        j.avanzar(1.0);
        assertEquals(2, j.getClientesEnCola().size());
        assertEquals(1, j.getClienteActual().getNumero());
        j.responderTotal(TOTAL);
        assertEquals(2, j.getClienteActual().getNumero());
        assertEquals(PasoAtencion.CALCULAR_TOTAL, j.getPaso());
    }

    /**
     * Nunca hay más de cuatro clientes en la tienda.
     */
    @Test
    void laFilaTieneUnMaximo() {
        Cliente[] cs = new Cliente[6];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = GeneradorFijo.cliente(i + 1, TOTAL, pagoDiez(), 1000);
        }
        Jornada j = nueva(cs);
        for (int i = 0; i < 10; i++) {
            j.avanzar(1.0);
        }
        assertEquals(Jornada.MAXIMO_CLIENTES, j.getClientesEnCola().size());
    }

    /**
     * R16: el reloj simulado va de las 8:00 a las 18:00.
     */
    @Test
    void relojSimulado() {
        Jornada j = nueva(GeneradorFijo.cliente(1, TOTAL, pagoDiez(), 1000));
        assertEquals("08:00", j.getHoraTexto());
        j.avanzar(120.0);
        assertEquals("13:00", j.getHoraTexto());
        assertEquals(0.5, j.getProgreso(), 1e-9);
    }

    /**
     * La jornada termina al acabarse el tiempo y se resume con la meta sin cumplir.
     */
    @Test
    void terminaPorTiempo() {
        Jornada j = nueva(GeneradorFijo.cliente(1, TOTAL, pagoDiez(), 1000));
        j.avanzar(ConfiguracionNivel.DURACION_JORNADA_SEGUNDOS);
        assertTrue(j.isTerminada());
        assertEquals("18:00", j.getHoraTexto());
        ResumenJornada r = j.getResumen();
        assertEquals(0, r.getGanancias());
        assertFalse(r.isMetaCumplida());
        assertFalse(r.isAprobada());
        assertFalse(r.isPerdidaPorReputacion());
        assertEquals(100, r.getReputacionFinal());
    }

    /**
     * R14: si la reputación llega a cero la jornada termina y se pierde.
     */
    @Test
    void terminaPorReputacion() {
        Cliente[] cs = new Cliente[5];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = clienteBase(i + 1);
        }
        Jornada j = nueva(cs);
        for (int i = 0; i < 5; i++) {
            j.avanzar(1.0);
            j.responderTotal(TOTAL);
            j.confirmarVuelto();
        }
        assertTrue(j.isTerminada());
        assertTrue(j.isPerdidaPorReputacion());
        assertEquals(0, j.getReputacion());
        assertFalse(j.getResumen().isAprobada());
    }

    /**
     * Al terminar se devuelve el efectivo y no se admiten más acciones.
     */
    @Test
    void alTerminarDevuelveElEfectivoYBloquea() {
        Jornada j = conCliente(clienteBase(1));
        j.responderTotal(TOTAL);
        j.ponerEnMostrador(Denominacion.BILLETE_2000);
        j.terminarAnticipadamente();
        assertTrue(j.isTerminada());
        assertEquals(Partida.baseInicial().total(), j.getCaja().total());
        assertTrue(j.getMostrador().estaVacio());
        assertEquals(0, j.avanzar(5));
        assertThrows(IllegalStateException.class, () -> j.responderTotal(1));
        assertThrows(IllegalStateException.class, j::confirmarVuelto);
    }

    /**
     * Las acciones fuera de orden se rechazan.
     */
    @Test
    void rechazaAccionesFueraDeOrden() {
        Jornada j = nueva(clienteBase(1));
        assertThrows(IllegalStateException.class, () -> j.responderTotal(TOTAL));
        j.avanzar(1.0);
        assertThrows(IllegalStateException.class, j::confirmarVuelto);
        j.responderTotal(TOTAL);
        assertThrows(IllegalStateException.class, () -> j.responderTotal(TOTAL));
        assertThrows(IllegalArgumentException.class, () -> j.avanzar(-1));
    }
}
