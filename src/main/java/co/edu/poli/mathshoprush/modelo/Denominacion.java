package co.edu.poli.mathshoprush.modelo;

/**
 * Denominaciones de billetes y monedas colombianas que maneja la tienda.
 * <p>
 * Los valores se declaran de mayor a menor, por lo que {@link #values()}
 * devuelve siempre las denominaciones en ese orden.
 * </p>
 */
public enum Denominacion {

    /**
     * Billete de $50.000.
     */
    BILLETE_50000(50000, true),

    /**
     * Billete de $20.000.
     */
    BILLETE_20000(20000, true),

    /**
     * Billete de $10.000.
     */
    BILLETE_10000(10000, true),

    /**
     * Billete de $5.000.
     */
    BILLETE_5000(5000, true),

    /**
     * Billete de $2.000.
     */
    BILLETE_2000(2000, true),

    /**
     * Moneda de $1.000.
     */
    MONEDA_1000(1000, false),

    /**
     * Moneda de $500.
     */
    MONEDA_500(500, false),

    /**
     * Moneda de $200.
     */
    MONEDA_200(200, false),

    /**
     * Moneda de $100.
     */
    MONEDA_100(100, false),

    /**
     * Moneda de $50.
     */
    MONEDA_50(50, false);

    /**
     * Valor de la pieza en pesos.
     */
    private final int valor;

    /**
     * Indica si la pieza es un billete ({@code true}) o una moneda.
     */
    private final boolean billete;

    /**
     * Crea una denominación.
     *
     * @param valor   valor en pesos
     * @param billete {@code true} si es billete, {@code false} si es moneda
     */
    Denominacion(int valor, boolean billete) {
        this.valor = valor;
        this.billete = billete;
    }

    /**
     * Devuelve el valor de la pieza.
     *
     * @return valor en pesos
     */
    public int getValor() {
        return valor;
    }

    /**
     * Indica si la pieza es un billete.
     *
     * @return {@code true} si es billete, {@code false} si es moneda
     */
    public boolean esBillete() {
        return billete;
    }

    /**
     * Devuelve el valor formateado para mostrarlo al usuario.
     *
     * @return texto como "$50.000"
     */
    public String getEtiqueta() {
        return FormatoDinero.formatear(valor);
    }
}
