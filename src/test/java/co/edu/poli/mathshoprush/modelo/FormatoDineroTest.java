package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link FormatoDinero}.
 */
class FormatoDineroTest {

    /**
     * Valores sin separador de miles.
     */
    @Test
    void formateaValoresPequenos() {
        assertEquals("$0", FormatoDinero.formatear(0));
        assertEquals("$500", FormatoDinero.formatear(500));
    }

    /**
     * Valores con separador de miles de punto.
     */
    @Test
    void formateaMiles() {
        assertEquals("$1.000", FormatoDinero.formatear(1000));
        assertEquals("$1.250.000", FormatoDinero.formatear(1250000));
    }
}
