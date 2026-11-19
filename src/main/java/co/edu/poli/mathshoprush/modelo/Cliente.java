package co.edu.poli.mathshoprush.modelo;

import java.util.Objects;

/**
 * Cliente de la tienda: tiene una compra, un pago (billetes y monedas) y
 * una barra de paciencia que disminuye mientras espera.
 */
public class Cliente {

    /**
     * Número consecutivo del cliente dentro de la partida.
     */
    private final int numero;

    /**
     * Compra que el cliente quiere pagar.
     */
    private final Compra compra;

    /**
     * Billetes y monedas con los que paga.
     */
    private Efectivo pago;

    /**
     * Indica si el cliente agregó piezas para redondear el vuelto.
     */
    private boolean redondea;

    /**
     * Paciencia con la que llega, en segundos.
     */
    private final double pacienciaMaxima;

    /**
     * Paciencia que le queda, en segundos.
     */
    private double pacienciaActual;

    /**
     * Crea un cliente.
     *
     * @param numero          número consecutivo del cliente
     * @param compra          compra que va a pagar, no nula
     * @param pago            efectivo con el que paga; su valor debe ser
     *                        igual o mayor al total de la compra
     * @param redondea        {@code true} si el pago incluye piezas extra
     *                        para que el vuelto sea múltiplo de $1.000
     * @param pacienciaMaxima paciencia inicial en segundos, mayor que cero
     * @throws IllegalArgumentException si el pago no cubre la compra o la
     *                                  paciencia no es positiva
     */
    public Cliente(int numero, Compra compra, Efectivo pago, boolean redondea,
            double pacienciaMaxima) {
        Objects.requireNonNull(compra, "compra");
        Objects.requireNonNull(pago, "pago");
        if (pago.total() < compra.getTotal()) {
            throw new IllegalArgumentException("El pago no cubre el total de la compra");
        }
        if (pacienciaMaxima <= 0) {
            throw new IllegalArgumentException("La paciencia debe ser positiva");
        }
        this.numero = numero;
        this.compra = compra;
        this.pago = pago.copia();
        this.redondea = redondea;
        this.pacienciaMaxima = pacienciaMaxima;
        this.pacienciaActual = pacienciaMaxima;
    }

    /**
     * Devuelve el número del cliente.
     *
     * @return número consecutivo
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la compra del cliente.
     *
     * @return compra
     */
    public Compra getCompra() {
        return compra;
    }

    /**
     * Devuelve una copia del efectivo con el que paga el cliente.
     *
     * @return copia del pago
     */
    public Efectivo getPago() {
        return pago.copia();
    }

    /**
     * Calcula el vuelto que se le debe entregar.
     *
     * @return pago menos total de la compra, en pesos
     */
    public int getVueltoEsperado() {
        return pago.total() - compra.getTotal();
    }

    /**
     * Indica si el cliente agregó piezas para redondear el vuelto.
     *
     * @return {@code true} si es un cliente que redondea
     */
    public boolean isRedondea() {
        return redondea;
    }

    /**
     * Devuelve la paciencia inicial del cliente.
     *
     * @return paciencia máxima en segundos
     */
    public double getPacienciaMaxima() {
        return pacienciaMaxima;
    }

    /**
     * Devuelve la paciencia que le queda al cliente.
     *
     * @return paciencia actual en segundos (nunca negativa)
     */
    public double getPacienciaActual() {
        return pacienciaActual;
    }

    /**
     * Devuelve la paciencia restante como fracción de la inicial.
     *
     * @return valor entre 0 y 1
     */
    public double getFraccionPaciencia() {
        return pacienciaActual / pacienciaMaxima;
    }

    /**
     * Reduce la paciencia del cliente.
     *
     * @param segundos tiempo consumido, no negativo
     */
    public void consumirPaciencia(double segundos) {
        pacienciaActual = Math.max(0, pacienciaActual - Math.max(0, segundos));
    }

    /**
     * Indica si la paciencia se agotó.
     *
     * @return {@code true} si ya no le queda paciencia
     */
    public boolean sinPaciencia() {
        return pacienciaActual <= 0;
    }

    /**
     * Cambia las piezas con las que paga, manteniendo su valor total
     * (cuando el jugador le pide sencillo).
     *
     * @param nuevoPago nuevo efectivo, con el mismo valor que el anterior
     * @throws IllegalArgumentException si el valor total cambia
     */
    public void cambiarPago(Efectivo nuevoPago) {
        if (nuevoPago.total() != pago.total()) {
            throw new IllegalArgumentException("El nuevo pago debe tener el mismo valor");
        }
        this.pago = nuevoPago.copia();
        this.redondea = false;
    }
}
