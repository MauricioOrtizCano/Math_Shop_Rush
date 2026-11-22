package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link CajaFuerte}: reglas R18 a R20.
 * clase {@link Efectivo}
 */
class CajaFuerteTest {
    /**
     * Una caja fuerte nueva inicia con saldo de $0.
     */
    @Test
    void naceEnCero() {
        CajaFuerte cf = new CajaFuerte();
        assertEquals(0, cf.getSaldo(), "La caja fuerte debe iniciar en $0");
    }

    /**
     * No se puede depositar un monto negativo.
     */
    @Test
    void depositarMontoNegativoFalla() {
        CajaFuerte cf = new CajaFuerte();
        assertThrows(IllegalArgumentException.class, () -> cf.depositar(-1000));
    }

    /**
     * No se puede depositar un monto que no sea múltiplo de $50, porque no
     * existe esa cantidad en billetes y monedas colombianos.
     */
    @Test
    void depositarMontoNoMultiploDe50Falla() {
        CajaFuerte cf = new CajaFuerte();
        assertThrows(IllegalArgumentException.class, () -> cf.depositar(1230));
    }

    /**
     * Cada depósito se suma al saldo existente, no lo reemplaza (R18).
     */
    @Test
    void depositarSumaAlSaldo() {
        CajaFuerte cf = new CajaFuerte();
        cf.depositar(5000);
        assertEquals(5000, cf.getSaldo(), "El saldo debería ser de $5.000 en la caja fuerte");
        cf.depositar(20500);
        assertEquals(25500, cf.getSaldo(), "El saldo debería ser de $25.500 en la caja fuerte");
    }

    /**
     * Retirar descuenta del saldo el valor total del pedido (R20).
     */
    @Test
    void retirarDescuentaElValorDelPedido() {
        CajaFuerte cf = new CajaFuerte();
        cf.depositar(130000);
        Efectivo pedido = new Efectivo();
        pedido.agregar(Denominacion.BILLETE_20000, 2);
        pedido.agregar(Denominacion.BILLETE_10000, 1);
        cf.retirar(pedido);
        assertEquals(80000, cf.getSaldo(), "Tras retirar $50.000 deben quedar $80.000");
    }

    /**
     * Se puede retirar exactamente todo el saldo, y queda en $0 (R20).
     */
    @Test
    void retirarTodoElSaldoLoDejaEnCero() {
        CajaFuerte cf = new CajaFuerte();
        cf.depositar(5000);
        Efectivo pedido = new Efectivo();
        pedido.agregar(Denominacion.BILLETE_5000, 1);
        cf.retirar(pedido);
        assertEquals(0, cf.getSaldo(), "Tras retirar todo el saldo debe quedar en $0");
    }

    /**
     * No se puede retirar más de lo que hay, y el saldo no cambia si el
     * retiro falla (R20).
     */
    @Test
    void retirarMasDelSaldoFalla() {
        CajaFuerte cf = new CajaFuerte();
        cf.depositar(5000);
        Efectivo pedido = new Efectivo();
        pedido.agregar(Denominacion.BILLETE_10000, 1);
        assertThrows(IllegalArgumentException.class, () -> cf.retirar(pedido));
        assertEquals(5000, cf.getSaldo(), "Un retiro fallido no debe cambiar el saldo");
    }

    /**
     * No se puede retirar un pedido nulo.
     */
    @Test
    void retirarPedidoNuloFalla() {
        CajaFuerte cf = new CajaFuerte();
        assertThrows(NullPointerException.class, () -> cf.retirar(null));
    }

    /**
     * Retirar un efectivo vacío es válido y no cambia el saldo; corresponde
     * al jugador que decide empezar la jornada con una base de $0.
     */
    @Test
    void retirarPedidoVacioNoCambiaElSaldo() {
        CajaFuerte cf = new CajaFuerte();
        cf.depositar(5000);
        cf.retirar(new Efectivo());
        assertEquals(5000, cf.getSaldo(), "Retirar un pedido vacío no debe cambiar el saldo");
    }
}
