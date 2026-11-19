package co.edu.poli.mathshoprush.modelo;

import java.util.Locale;

/**
 * Utilidad para mostrar valores en pesos con separador de miles de punto.
 */
public final class FormatoDinero {

    /**
     * Evita que se creen instancias de la utilidad.
     */
    private FormatoDinero() {
    }

    /**
     * Da formato a un valor en pesos.
     *
     * @param valor cantidad en pesos
     * @return texto como "$1.500"
     */
    public static String formatear(int valor) {
        return "$" + String.format(Locale.US, "%,d", valor).replace(',', '.');
    }
}
