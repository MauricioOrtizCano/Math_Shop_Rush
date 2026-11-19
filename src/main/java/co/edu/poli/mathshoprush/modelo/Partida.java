package co.edu.poli.mathshoprush.modelo;

import java.util.Objects;

/**
 * Una partida completa de Math Shop Rush: varias jornadas, el nivel actual
 * y la caja fuerte donde se guarda el efectivo entre jornadas.
 * <p>
 * Reglas que implementa: R17 (avanzar de nivel), R18 (depositar el
 * efectivo en la caja fuerte), R19 (base inicial) y R20 (preparar la caja
 * en las jornadas siguientes).
 * </p>
 */
public class Partida {

    /**
     * Generador de clientes que se usa en todas las jornadas.
     */
    private final GeneradorClientes generador;

    /**
     * Caja fuerte donde se guarda el efectivo entre jornadas.
     */
    private final Efectivo cajaFuerte = reservaInicialCajaFuerte();

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
     * Crea una partida nueva en el nivel 1.
     *
     * @param generador generador de clientes, no nulo
     * @throws NullPointerException si el generador es nulo
     */
    public Partida(GeneradorClientes generador) {
        this.generador = Objects.requireNonNull(generador, "generador");
    }

    /**
     * Efectivo con el que abre la caja en la primera jornada (R19).
     *
     * @return base inicial de $32.500
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
     * Efectivo que tiene la caja fuerte al empezar la partida. La propuesta
     * no define este valor; se asumió una reserva de $66.000 para que en la
     * primera jornada se pueda pedir efectivo (R9b).
     *
     * @return reserva inicial de la caja fuerte
     */
    public static Efectivo reservaInicialCajaFuerte() {
        Efectivo e = new Efectivo();
        e.agregar(Denominacion.BILLETE_20000, 1);
        e.agregar(Denominacion.BILLETE_10000, 1);
        e.agregar(Denominacion.BILLETE_5000, 2);
        e.agregar(Denominacion.BILLETE_2000, 3);
        e.agregar(Denominacion.MONEDA_1000, 10);
        e.agregar(Denominacion.MONEDA_500, 10);
        e.agregar(Denominacion.MONEDA_200, 10);
        e.agregar(Denominacion.MONEDA_100, 20);
        e.agregar(Denominacion.MONEDA_50, 20);
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
     * caja fuerte el efectivo que quiera usar como base (R20).
     * </p>
     *
     * @param base efectivo que se retira de la caja fuerte; puede ser
     *             {@code null} o vacío
     * @return la jornada creada
     * @throws IllegalStateException    si la partida ya se completó o la
     *                                  jornada anterior no se ha cerrado
     * @throws IllegalArgumentException si la caja fuerte no tiene ese saldo
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
            if (!cajaFuerte.contiene(pedido)) {
                throw new IllegalArgumentException("La caja fuerte no tiene ese saldo disponible");
            }
            cajaFuerte.quitarTodo(pedido);
            cajaInicial = pedido.copia();
        }
        jornadaActual = new Jornada(ConfiguracionNivel.deNivel(nivel), cajaInicial,
                cajaFuerte, generador);
        jornadaFinalizada = false;
        return jornadaActual;
    }

    /**
     * Cierra la jornada que terminó: deposita el efectivo de la caja en la
     * caja fuerte (R18) y avanza de nivel si se cumplieron las condiciones
     * (R17); si no, el nivel se repite.
     *
     * @return resumen de la jornada
     * @throws IllegalStateException si no hay una jornada terminada por cerrar
     */
    public ResumenJornada finalizarJornada() {
        if (jornadaActual == null || jornadaFinalizada || !jornadaActual.isTerminada()) {
            throw new IllegalStateException("No hay una jornada terminada por cerrar");
        }
        cajaFuerte.agregarTodo(jornadaActual.getCaja());
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
     * Devuelve una copia del efectivo de la caja fuerte.
     *
     * @return copia de la caja fuerte
     */
    public Efectivo getCajaFuerte() {
        return cajaFuerte.copia();
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
