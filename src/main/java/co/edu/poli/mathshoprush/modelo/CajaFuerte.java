package co.edu.poli.mathshoprush.modelo;

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
}
