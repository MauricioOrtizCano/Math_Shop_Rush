package co.edu.poli.mathshoprush.modelo;

/**
 * Resultados de una jornada de trabajo (sección 4.8 de la propuesta).
 * Es inmutable.
 */
public final class ResumenJornada {

    /**
     * Nivel que se jugó.
     */
    private final int nivel;

    /**
     * Clientes atendidos con la venta completada.
     */
    private final int clientesAtendidos;

    /**
     * Errores cometidos (total mal sumado, vuelto incorrecto, clientes
     * que se fueron).
     */
    private final int errores;

    /**
     * Cantidad de bonificaciones por entregar el vuelto con la menor
     * cantidad de piezas.
     */
    private final int bonificaciones;

    /**
     * Ganancias de la jornada en pesos.
     */
    private final int ganancias;

    /**
     * Reputación al terminar la jornada.
     */
    private final int reputacionFinal;

    /**
     * Meta de ganancias del nivel.
     */
    private final int metaGanancias;

    /**
     * Indica si se superó el nivel.
     */
    private final boolean aprobada;

    /**
     * Indica si la jornada terminó porque la reputación llegó a cero.
     */
    private final boolean perdidaPorReputacion;

    /**
     * Crea el resumen de una jornada.
     *
     * @param nivel                nivel jugado
     * @param clientesAtendidos    clientes atendidos
     * @param errores              errores cometidos
     * @param bonificaciones       bonificaciones obtenidas
     * @param ganancias            ganancias en pesos
     * @param reputacionFinal      reputación final
     * @param metaGanancias        meta del nivel
     * @param aprobada             si se superó el nivel
     * @param perdidaPorReputacion si terminó por reputación en cero
     */
    public ResumenJornada(int nivel, int clientesAtendidos, int errores, int bonificaciones,
            int ganancias, int reputacionFinal, int metaGanancias, boolean aprobada,
            boolean perdidaPorReputacion) {
        this.nivel = nivel;
        this.clientesAtendidos = clientesAtendidos;
        this.errores = errores;
        this.bonificaciones = bonificaciones;
        this.ganancias = ganancias;
        this.reputacionFinal = reputacionFinal;
        this.metaGanancias = metaGanancias;
        this.aprobada = aprobada;
        this.perdidaPorReputacion = perdidaPorReputacion;
    }

    /**
     * Devuelve el nivel jugado.
     *
     * @return nivel
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Devuelve los clientes atendidos.
     *
     * @return número de clientes
     */
    public int getClientesAtendidos() {
        return clientesAtendidos;
    }

    /**
     * Devuelve los errores cometidos.
     *
     * @return número de errores
     */
    public int getErrores() {
        return errores;
    }

    /**
     * Devuelve las bonificaciones obtenidas.
     *
     * @return número de bonificaciones
     */
    public int getBonificaciones() {
        return bonificaciones;
    }

    /**
     * Devuelve las ganancias de la jornada.
     *
     * @return ganancias en pesos
     */
    public int getGanancias() {
        return ganancias;
    }

    /**
     * Devuelve la reputación final.
     *
     * @return reputación entre 0 y 100
     */
    public int getReputacionFinal() {
        return reputacionFinal;
    }

    /**
     * Devuelve la meta de ganancias.
     *
     * @return meta en pesos
     */
    public int getMetaGanancias() {
        return metaGanancias;
    }

    /**
     * Indica si se alcanzó la meta de ganancias.
     *
     * @return {@code true} si las ganancias igualan o superan la meta
     */
    public boolean isMetaCumplida() {
        return ganancias >= metaGanancias;
    }

    /**
     * Indica si el nivel fue superado.
     *
     * @return {@code true} si se alcanzó la meta y la reputación sigue
     *         por encima de cero
     */
    public boolean isAprobada() {
        return aprobada;
    }

    /**
     * Indica si la jornada terminó por reputación en cero.
     *
     * @return {@code true} si la tienda perdió la confianza de los clientes
     */
    public boolean isPerdidaPorReputacion() {
        return perdidaPorReputacion;
    }
}
