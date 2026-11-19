package co.edu.poli.mathshoprush.modelo;

/**
 * Parámetros de dificultad de cada uno de los cuatro niveles del juego
 * (Tabla 2 de la propuesta). Es inmutable y se obtiene con
 * {@link #deNivel(int)}.
 * <p>
 * Las metas de ganancias y la paciencia son valores iniciales que el grupo
 * puede ajustar después de las pruebas.
 * </p>
 */
public final class ConfiguracionNivel {

    /**
     * Nivel más bajo.
     */
    public static final int NIVEL_MINIMO = 1;

    /**
     * Nivel más alto.
     */
    public static final int NIVEL_MAXIMO = 4;

    /**
     * Duración real de una jornada completa (8:00 a. m. a 6:00 p. m.).
     */
    public static final double DURACION_JORNADA_SEGUNDOS = 240.0;

    /**
     * Número del nivel.
     */
    private final int nivel;

    /**
     * Mínimo de productos por cliente.
     */
    private final int minProductos;

    /**
     * Máximo de productos por cliente.
     */
    private final int maxProductos;

    /**
     * Múltiplo en el que se expresan los precios.
     */
    private final int paso;

    /**
     * Precio máximo de un producto.
     */
    private final int precioMaximo;

    /**
     * Paciencia de cada cliente en segundos.
     */
    private final double pacienciaSegundos;

    /**
     * Probabilidad (0 a 1) de que un cliente redondee el vuelto.
     */
    private final double probabilidadRedondeo;

    /**
     * Tiempo promedio entre la llegada de dos clientes, en segundos.
     */
    private final double intervaloLlegadaSegundos;

    /**
     * Ganancias necesarias para superar el nivel, en pesos.
     */
    private final int metaGanancias;

    /**
     * Construye la configuración de un nivel.
     *
     * @param nivel                    número del nivel
     * @param minProductos             mínimo de productos por cliente
     * @param maxProductos             máximo de productos por cliente
     * @param paso                     múltiplo de los precios
     * @param precioMaximo             precio máximo de un producto
     * @param pacienciaSegundos        paciencia de cada cliente
     * @param probabilidadRedondeo     probabilidad de redondear el vuelto
     * @param intervaloLlegadaSegundos tiempo entre llegadas de clientes
     * @param metaGanancias            meta de ganancias del nivel
     */
    private ConfiguracionNivel(int nivel, int minProductos, int maxProductos, int paso,
            int precioMaximo, double pacienciaSegundos, double probabilidadRedondeo,
            double intervaloLlegadaSegundos, int metaGanancias) {
        this.nivel = nivel;
        this.minProductos = minProductos;
        this.maxProductos = maxProductos;
        this.paso = paso;
        this.precioMaximo = precioMaximo;
        this.pacienciaSegundos = pacienciaSegundos;
        this.probabilidadRedondeo = probabilidadRedondeo;
        this.intervaloLlegadaSegundos = intervaloLlegadaSegundos;
        this.metaGanancias = metaGanancias;
    }

    /**
     * Obtiene la configuración de un nivel.
     *
     * @param nivel nivel deseado, entre 1 y 4
     * @return configuración del nivel
     * @throws IllegalArgumentException si el nivel está fuera de rango
     */
    public static ConfiguracionNivel deNivel(int nivel) {
        switch (nivel) {
            case 1:
                return new ConfiguracionNivel(1, 1, 1, 1000, 5000, 90, 0.0, 16, 20000);
            case 2:
                return new ConfiguracionNivel(2, 1, 2, 500, 6000, 75, 0.0, 14, 50000);
            case 3:
                return new ConfiguracionNivel(3, 2, 3, 100, 8000, 65, 0.25, 12, 90000);
            case 4:
                return new ConfiguracionNivel(4, 3, 5, 50, 10000, 50, 0.6, 10, 150000);
            default:
                throw new IllegalArgumentException(
                        "El nivel debe estar entre " + NIVEL_MINIMO + " y " + NIVEL_MAXIMO);
        }
    }

    /**
     * Devuelve el número del nivel.
     *
     * @return nivel entre 1 y 4
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Devuelve el mínimo de productos por cliente.
     *
     * @return mínimo de productos
     */
    public int getMinProductos() {
        return minProductos;
    }

    /**
     * Devuelve el máximo de productos por cliente.
     *
     * @return máximo de productos
     */
    public int getMaxProductos() {
        return maxProductos;
    }

    /**
     * Devuelve el múltiplo en que se expresan los precios.
     *
     * @return paso de precio en pesos
     */
    public int getPaso() {
        return paso;
    }

    /**
     * Devuelve el precio máximo de un producto.
     *
     * @return precio máximo en pesos
     */
    public int getPrecioMaximo() {
        return precioMaximo;
    }

    /**
     * Devuelve la paciencia de los clientes.
     *
     * @return paciencia en segundos
     */
    public double getPacienciaSegundos() {
        return pacienciaSegundos;
    }

    /**
     * Devuelve la probabilidad de que un cliente redondee.
     *
     * @return probabilidad entre 0 y 1
     */
    public double getProbabilidadRedondeo() {
        return probabilidadRedondeo;
    }

    /**
     * Devuelve el tiempo promedio entre llegadas de clientes.
     *
     * @return intervalo en segundos
     */
    public double getIntervaloLlegadaSegundos() {
        return intervaloLlegadaSegundos;
    }

    /**
     * Devuelve la meta de ganancias del nivel.
     *
     * @return meta en pesos
     */
    public int getMetaGanancias() {
        return metaGanancias;
    }
}
