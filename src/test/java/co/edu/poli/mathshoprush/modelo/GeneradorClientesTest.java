package co.edu.poli.mathshoprush.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase {@link GeneradorClientes}.
 */
class GeneradorClientesTest {

    /**
     * Los clientes cumplen los parámetros de cada nivel y el pago cubre la compra.
     */
    @Test
    void clientesCumplenLasReglasDelNivel() {
        GeneradorClientes gen = new GeneradorClientes(new Random(11));
        for (int nivel = 1; nivel <= 4; nivel++) {
            ConfiguracionNivel cfg = ConfiguracionNivel.deNivel(nivel);
            for (int i = 0; i < 300; i++) {
                Cliente c = gen.generar(cfg);
                int n = c.getCompra().getProductos().size();
                assertTrue(n >= cfg.getMinProductos() && n <= cfg.getMaxProductos());
                for (Producto p : c.getCompra().getProductos()) {
                    assertEquals(0, p.getPrecio() % cfg.getPaso());
                    assertTrue(p.getPrecio() >= cfg.getPaso() && p.getPrecio() <= cfg.getPrecioMaximo());
                }
                Set<String> nombres = c.getCompra().getProductos().stream()
                        .map(Producto::getNombre).collect(Collectors.toSet());
                assertEquals(n, nombres.size());
                assertTrue(c.getPago().total() >= c.getCompra().getTotal());
                assertEquals(cfg.getPacienciaSegundos(), c.getPacienciaMaxima(), 1e-9);
            }
        }
    }

    /**
     * R4: salvo los clientes que redondean, ninguna pieza del pago sobra.
     */
    @Test
    void elPagoNoTienePiezasSobrantes() {
        GeneradorClientes gen = new GeneradorClientes(new Random(21));
        for (int nivel = 1; nivel <= 4; nivel++) {
            ConfiguracionNivel cfg = ConfiguracionNivel.deNivel(nivel);
            for (int i = 0; i < 300; i++) {
                Cliente c = gen.generar(cfg);
                if (c.isRedondea()) {
                    continue;
                }
                int total = c.getCompra().getTotal();
                for (Denominacion d : Denominacion.values()) {
                    if (c.getPago().cantidad(d) > 0) {
                        Efectivo menos = c.getPago();
                        menos.quitar(d);
                        assertTrue(menos.total() < total, "sobra una pieza de " + d);
                    }
                }
            }
        }
    }

    /**
     * R5: los clientes que redondean dejan un vuelto múltiplo de $1.000, y
     * solo aparecen desde el nivel 3.
     */
    @Test
    void clientesQueRedondean() {
        GeneradorClientes gen = new GeneradorClientes(new Random(31));
        for (int nivel = 1; nivel <= 2; nivel++) {
            for (int i = 0; i < 200; i++) {
                assertFalse(gen.generar(ConfiguracionNivel.deNivel(nivel)).isRedondea());
            }
        }
        int redondeos = 0;
        for (int i = 0; i < 500; i++) {
            Cliente c = gen.generar(ConfiguracionNivel.deNivel(4));
            if (c.isRedondea()) {
                redondeos++;
                assertEquals(0, c.getVueltoEsperado() % 1000);
            }
        }
        assertTrue(redondeos > 0);
    }

    /**
     * Con la misma semilla se generan los mismos clientes.
     */
    @Test
    void esRepetibleConLaMismaSemilla() {
        ConfiguracionNivel cfg = ConfiguracionNivel.deNivel(3);
        Cliente a = new GeneradorClientes(new Random(5)).generar(cfg);
        Cliente b = new GeneradorClientes(new Random(5)).generar(cfg);
        assertEquals(a.getCompra().getTotal(), b.getCompra().getTotal());
        assertEquals(a.getPago().total(), b.getPago().total());
    }

    /**
     * Validaciones de entrada.
     */
    @Test
    void validaEntradas() {
        GeneradorClientes gen = new GeneradorClientes(new Random(1));
        assertThrows(IllegalArgumentException.class, () -> gen.generarPago(0));
        assertThrows(IllegalArgumentException.class, () -> gen.generarPago(75));
        assertThrows(NullPointerException.class, () -> new GeneradorClientes(null));
        double intervalo = gen.proximoIntervalo(ConfiguracionNivel.deNivel(1));
        assertTrue(intervalo >= 16 * 0.7 && intervalo <= 16 * 1.3);
    }
}
