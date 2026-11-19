package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link ConfiguracionNivel} (Tabla 2 de la propuesta).
 */
class ConfiguracionNivelTest {

    /**
     * Parámetros del nivel 1.
     */
    @Test
    void nivelUno() {
        ConfiguracionNivel c = ConfiguracionNivel.deNivel(1);
        assertEquals(1, c.getMinProductos());
        assertEquals(1, c.getMaxProductos());
        assertEquals(1000, c.getPaso());
        assertEquals(0.0, c.getProbabilidadRedondeo(), 1e-9);
    }

    /**
     * Parámetros del nivel 4.
     */
    @Test
    void nivelCuatro() {
        ConfiguracionNivel c = ConfiguracionNivel.deNivel(4);
        assertEquals(3, c.getMinProductos());
        assertEquals(5, c.getMaxProductos());
        assertEquals(50, c.getPaso());
        assertTrue(c.getProbabilidadRedondeo() > 0.5);
    }

    /**
     * La dificultad crece: más productos, precios más finos, menos paciencia
     * y metas más altas.
     */
    @Test
    void ladificultadCrece() {
        for (int n = 1; n < ConfiguracionNivel.NIVEL_MAXIMO; n++) {
            ConfiguracionNivel a = ConfiguracionNivel.deNivel(n);
            ConfiguracionNivel b = ConfiguracionNivel.deNivel(n + 1);
            assertTrue(b.getMaxProductos() >= a.getMaxProductos());
            assertTrue(b.getPaso() < a.getPaso());
            assertTrue(b.getPacienciaSegundos() < a.getPacienciaSegundos());
            assertTrue(b.getMetaGanancias() > a.getMetaGanancias());
            assertTrue(b.getProbabilidadRedondeo() >= a.getProbabilidadRedondeo());
        }
    }

    /**
     * Los niveles fuera de 1 a 4 se rechazan.
     */
    @Test
    void rechazaNivelesInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracionNivel.deNivel(0));
        assertThrows(IllegalArgumentException.class, () -> ConfiguracionNivel.deNivel(5));
    }
}
