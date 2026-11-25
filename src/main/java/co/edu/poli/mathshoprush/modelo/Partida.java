package co.edu.poli.mathshoprush.modelo;

import java.util.Objects;

/**
 * Una partida completa de Math Shop Rush: varias jornadas, el nivel actual
 * y la caja fuerte donde se guarda el dinero entre jornadas.
 * <p>
 * Reglas que implementa: R17 (avanzar de nivel), R18 (depositar el valor
 * de la caja en la caja fuerte), R19 (base inicial) y R20 (preparar la
 * caja en las jornadas siguientes retirando de la caja fuerte).
 * </p>
 */
public class Partida {

    /**
     * Generador de clientes que se usa en todas las jornadas.
     */
    private final GeneradorClientes generador;

    /**
     * Caja fuerte donde se guarda el dinero entre jornadas, como un saldo
     * en pesos. Empieza en $0 y se comparte con cada jornada.
     */
    private final CajaFuerte cajaFuerte = new CajaFuerte();

    /**
     * Nivel que se jugará en la próxima jornada.
     */
    private int nivel = ConfiguracionNivel.NIVEL_MINIMO;

    /**
     * Cuántas jornadas se han terminado.
     */
    private int jornadasJugadas;

    /**
     * Jornada actual o la última jugada.
     */
    private Jornada jornadaActual;

    /**
     * Indica si la jornada actual ya se cerró con {@link #finalizarJornada()}.
     */
    private boolean jornadaFinalizada = true;

    /**
     * Indica si el jugador superó los cuatro niveles.
     */
    private boolean completada;

    /**
     * Crea una partida nueva en el nivel 1, con la caja fuerte en $0.
     *
     * @param generador generador de clientes, no nulo
     * @throws NullPointerException si el generador es nulo
     */
    public Partida(GeneradorClientes generador) {
        this.generador = Objects.requireNonNull(generador, "generador");
    }

    /**
     * Crea el efectivo inicial con el que abre la caja en la primera jornada (R19).
     *
     * @return efectivo con una base inicial de $32.500, distribuida
     *         en billetes y monedas
     */
    public static Efectivo baseInicial() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.BILLETE_10000, 1);
        e.agregar(Denominacion.BILLETE_5000, 2);
        e.agregar(Denominacion.BILLETE_2000, 2);
        e.agregar(Denominacion.MONEDA_1000, 4);
        e.agregar(Denominacion.MONEDA_500, 4);
        e.agregar(Denominacion.MONEDA_200, 5);
        e.agregar(Denominacion.MONEDA_100, 10);
        e.agregar(Denominacion.MONEDA_50, 10);
        return e;
    }

    /**
     * Indica si la próxima jornada es la primera de la partida.
     *
     * @return {@code true} si aún no se ha jugado ninguna
     */
    public boolean esPrimeraJornada() {
        return jornadasJugadas == 0;
    }

    /**
     * Abre una jornada nueva en el nivel actual.
     * <p>
     * En la primera jornada la caja abre con {@link #baseInicial()} y el
     * parámetro se ignora (R19). En las siguientes, el jugador retira de la
     * caja fuerte el efectivo que quiera usar como base, eligiendo las
     * denominaciones, sin superar el saldo disponible (R20).
     * </p>
     *
     * @param base efectivo que se retira de la caja fuerte; puede ser
     *             {@code null} o vacío, y entonces la caja abre sin base
     * @return la jornada creada
     * @throws IllegalStateException si la partida ya se completó o la
     *                               jornada anterior no se ha cerrado
     * @throws IllegalArgumentException si el valor de la base supera el
     *                                  saldo disponible
     */
    public Jornada iniciarJornada(Efectivo base) {
        if (completada) {
            throw new IllegalStateException("La partida ya fue completada");
        }
        if (!jornadaFinalizada) {
            throw new IllegalStateException("Hay una jornada sin cerrar");
        }
        Efectivo cajaInicial;
        if (esPrimeraJornada()) {
            cajaInicial = baseInicial();
        } else {
            Efectivo pedido = base == null ? new Efectivo() : base;
            cajaFuerte.retirar(pedido);
            cajaInicial = pedido.copia();
        }
        jornadaActual = new Jornada(ConfiguracionNivel.deNivel(nivel), cajaInicial,
                cajaFuerte, generador);
        jornadaFinalizada = false;
        return jornadaActual;
    }

    /**
     * Cierra la jornada que terminó: deposita en la caja fuerte el valor
     * total del efectivo de la caja (R18) y avanza de nivel si se cumplieron
     * las condiciones (R17); si no, el nivel se repite.
     *
     * @return resumen de la jornada
     * @throws IllegalStateException si no hay una jornada terminada por cerrar
     */
    public ResumenJornada finalizarJornada() {
        if (jornadaActual == null || jornadaFinalizada || !jornadaActual.isTerminada()) {
            throw new IllegalStateException("No hay una jornada terminada por cerrar");
        }
        cajaFuerte.depositar(jornadaActual.getCaja().total());
        ResumenJornada resumen = jornadaActual.getResumen();
        jornadasJugadas++;
        jornadaFinalizada = true;
        if (resumen.isAprobada()) {
            if (nivel < ConfiguracionNivel.NIVEL_MAXIMO) {
                nivel++;
            } else {
                completada = true;
            }
        }
        return resumen;
    }

    /**
     * Devuelve el nivel de la próxima jornada (o el actual si está en curso).
     *
     * @return nivel entre 1 y 4
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Devuelve el dinero disponible en la caja fuerte.
     *
     * @return saldo de la caja fuerte, en pesos
     */
    public int getSaldoCajaFuerte() {
        return cajaFuerte.getSaldo();
    }

    /**
     * Devuelve la jornada actual o la última que se jugó.
     *
     * @return jornada, o {@code null} si aún no se ha iniciado ninguna
     */
    public Jornada getJornadaActual() {
        return jornadaActual;
    }

    /**
     * Devuelve cuántas jornadas se han terminado.
     *
     * @return número de jornadas jugadas
     */
    public int getJornadasJugadas() {
        return jornadasJugadas;
    }

    /**
     * Indica si el jugador superó los cuatro niveles.
     *
     * @return {@code true} si la partida se completó
     */
    public boolean isCompletada() {
        return completada;
    }
}