package co.edu.poli.mathshoprush.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Genera clientes aleatorios según el nivel: productos, precios, forma de
 * pago y si redondean el vuelto.
 * <p>
 * Recibe el generador de números aleatorios por constructor para que las
 * pruebas puedan usar una semilla fija. Las pruebas también pueden crear
 * una subclase que devuelva clientes predefinidos.
 * </p>
 */
public class GeneradorClientes {

    /**
     * Catálogo de productos de la tienda.
     */
    public static final List<String> NOMBRES_PRODUCTOS = List.of(
            "Leche", "Pan", "Huevos", "Arroz", "Café", "Aceite", "Panela", "Galletas",
            "Queso", "Jabón", "Papas", "Gaseosa", "Chocolate", "Atún", "Azúcar");

    /**
     * Fuente de números aleatorios.
     */
    private final Random azar;

    /**
     * Cantidad de clientes generados hasta ahora.
     */
    private int contador;

    /**
     * Crea un generador.
     *
     * @param azar fuente de aleatoriedad, no nula
     * @throws NullPointerException si {@code azar} es nulo
     */
    public GeneradorClientes(Random azar) {
        this.azar = Objects.requireNonNull(azar, "azar");
    }

    /**
     * Genera un cliente nuevo para el nivel indicado.
     * <p>
     * Crea una compra con productos y precios aleatorios, genera el pago
     * correspondiente y determina si el cliente redondea el vuelto.
     * Los precios son múltiplos del paso del nivel (reglas R4 y R5).
     * </p>
     *
     * @param configuracion parámetros del nivel que determinan las características
     *                      del cliente
     * @return nuevo cliente generado
     * @throws NullPointerException si la configuración es nula
     */
    public Cliente generar(ConfiguracionNivel configuracion) {
        int cantidad = configuracion.getMinProductos()
                + azar.nextInt(configuracion.getMaxProductos() - configuracion.getMinProductos() + 1);
        List<String> nombres = new ArrayList<>(NOMBRES_PRODUCTOS);
        Collections.shuffle(nombres, azar);
        int opciones = configuracion.getPrecioMaximo() / configuracion.getPaso();

        List<Producto> productos = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            int precio = (azar.nextInt(opciones) + 1) * configuracion.getPaso();
            productos.add(new Producto(nombres.get(i), precio));
        }
        Compra compra = new Compra(productos);

        Efectivo pago = generarPago(compra.getTotal());
        boolean redondea = false;
        if (azar.nextDouble() < configuracion.getProbabilidadRedondeo()) {
            redondea = redondear(pago, compra.getTotal());
        }
        contador++;
        return new Cliente(contador, compra, pago, redondea, configuracion.getPacienciaSegundos());
    }

    /**
     * Calcula cuánto tiempo pasará hasta que llegue el siguiente cliente.
     *
     * @param configuracion parámetros del nivel
     * @return segundos de espera, entre el 70 % y el 130 % del intervalo
     *         configurado
     * @throws NullPointerException si la configuración es nula
     */
    public double proximoIntervalo(ConfiguracionNivel configuracion) {
        return configuracion.getIntervaloLlegadaSegundos() * (0.7 + 0.6 * azar.nextDouble());
    }

    /**
     * Genera un pago que cubre el total sin piezas sobrantes: si se retira
     * cualquier billete o moneda, el valor restante ya no alcanza el total
     * (regla R4).
     *
     * @param total total de la compra, múltiplo de 50 y mayor que cero
     * @return efectivo con el que paga el cliente
     * @throws IllegalArgumentException si el total no es válido
     */
    public Efectivo generarPago(int total) {
        if (total <= 0 || total % 50 != 0) {
            throw new IllegalArgumentException("El total debe ser un múltiplo de 50 mayor que cero");
        }
        List<Denominacion> piezas = new ArrayList<>();
        int restante = total;
        while (restante > 0) {
            List<Denominacion> menoresOIguales = new ArrayList<>();
            Denominacion siguienteMayor = null;
            for (Denominacion d : Denominacion.values()) {
                if (d.getValor() <= restante) {
                    menoresOIguales.add(d);
                } else {
                    siguienteMayor = d;
                }
            }
            if (siguienteMayor != null
                    && (menoresOIguales.isEmpty() || azar.nextDouble() < 0.6)) {
                piezas.add(siguienteMayor);
                break;
            }
            Denominacion elegida;
            if (menoresOIguales.size() == 1 || azar.nextDouble() < 0.7) {
                elegida = menoresOIguales.get(0);
            } else {
                elegida = menoresOIguales.get(azar.nextInt(Math.min(3, menoresOIguales.size())));
            }
            piezas.add(elegida);
            restante -= elegida.getValor();
        }
        quitarSobrantes(piezas, total);
        Efectivo pago = new Efectivo();
        for (Denominacion d : piezas) {
            pago.agregar(d);
        }
        return pago;
    }

    /**
     * Elimina las piezas que sobran para que ninguna se pueda retirar
     * sin dejar de cubrir el total de la compra.
     *
     * @param piezas piezas del pago que se modifican durante el proceso
     * @param total total de la compra, en pesos
     * @throws NullPointerException si la lista de piezas es nula
     */
    private void quitarSobrantes(List<Denominacion> piezas, int total) {
        piezas.sort((a, b) -> Integer.compare(a.getValor(), b.getValor()));
        int suma = 0;
        for (Denominacion d : piezas) {
            suma += d.getValor();
        }
        boolean huboCambio = true;
        while (huboCambio) {
            huboCambio = false;
            for (int i = 0; i < piezas.size(); i++) {
                if (suma - piezas.get(i).getValor() >= total) {
                    suma -= piezas.get(i).getValor();
                    piezas.remove(i);
                    huboCambio = true;
                    break;
                }
            }
        }
    }

    /**
     * Agrega al pago las monedas necesarias para que el vuelto sea un
     * múltiplo de $1.000 (regla R5).
     *
     * @param pago  pago del cliente (se modifica)
     * @param total total de la compra
     * @return {@code true} si se agregaron piezas; {@code false} si el vuelto
     *         ya era múltiplo de $1.000
     */
    private boolean redondear(Efectivo pago, int total) {
        int resto = (pago.total() - total) % 1000;
        if (resto == 0) {
            return false;
        }
        pago.agregarTodo(Efectivo.conMenorCantidadDePiezas(1000 - resto));
        return true;
    }
}
