package co.edu.poli.mathshoprush.modelo;

import java.util.Objects;

/**
 * Caja fuerte de la tienda: guarda el dinero acumulado entre jornadas
 * como un saldo en pesos.
 * <p>
 * Al cerrar cada jornada se deposita el valor total del efectivo de la
 * caja (R18). Para armar la base del día o durante una emergencia, el
 * jugador retira efectivo eligiendo las denominaciones, sin superar el
 * saldo disponible (R9 y R20).
 * </p>
 */
public class CajaFuerte {
    /**
     * Dinero disponible en la caja fuerte, en pesos. Nunca es negativo y
     * siempre es múltiplo de $50.
     */
    private int saldo;

    /**
     * Crea una caja fuerte vacía, con saldo de $0.
     */
    public CajaFuerte() {
        this.saldo = 0;
    }

    /**
     * Devuelve el dinero disponible en la caja fuerte.
     *
     * @return saldo actual en pesos
     */
    public int getSaldo() {
        return this.saldo;
    }

    /**
     * Deposita dinero en la caja fuerte, sumándolo al saldo actual (R18).
     * Se permite depositar $0, por ejemplo cuando la caja termina la
     * jornada vacía.
     *
     * @param valor monto a depositar, en pesos
     * @throws IllegalArgumentException si el monto es negativo o no es
     *                                  múltiplo de $50
     */
    public void depositar(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("El monto a depositar no puede ser negativo");
        }
        if (valor % 50 != 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser múltiplo de $50");
        }
        this.saldo += valor;
    }

    /**
     * Retira efectivo de la caja fuerte, descontando su valor total del
     * saldo. El jugador elige las denominaciones que quiere, sin superar
     * el saldo disponible (R9 y R20). Retirar un efectivo vacío no cambia
     * el saldo.
     *
     * @param pedido billetes y monedas que el jugador quiere retirar
     * @throws NullPointerException     si el pedido es nulo
     * @throws IllegalArgumentException si el valor del pedido supera el
     *                                  saldo disponible
     */
    public void retirar(Efectivo pedido) {
        Objects.requireNonNull(pedido, "El pedido no puede ser nulo");
        int valor = pedido.total();
        if (valor > this.saldo) {
            throw new IllegalArgumentException("La caja fuerte no tiene saldo suficiente para ese retiro");
        }
        this.saldo -= valor;
    }
}
