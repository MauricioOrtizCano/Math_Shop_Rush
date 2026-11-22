package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link CajaFuerte}: reglas R18 a R20.
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
}
