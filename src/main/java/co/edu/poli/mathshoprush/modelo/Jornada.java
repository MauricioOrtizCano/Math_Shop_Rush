package co.edu.poli.mathshoprush.modelo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Una jornada de trabajo en la tienda (8:00 a. m. a 6:00 p. m. simuladas).
 * <p>
 * Contiene toda la lógica de las reglas R1 a R16: llegada de clientes y
 * fila de espera, paciencia, atención en dos pasos (calcular el total y
 * entregar el vuelto), caja registradora, caja fuerte, ganancias y
 * reputación. No depende de la interfaz gráfica.
 * </p>
 * <p>
 * La interfaz llama a {@link #avanzar(double)} en cada fotograma y a los
 * demás métodos según las acciones del jugador.
 * </p>
 */
public class Jornada {

    /**
     * Paso en el que está la atención del cliente que está en el mostrador.
     */
    public enum PasoAtencion {
        /** El jugador debe sumar los productos y escribir el total. */
        CALCULAR_TOTAL,
        /** El jugador debe armar el vuelto con el efectivo de la caja. */
        ENTREGAR_VUELTO
    }

    /**
     * Resultado de responder el total de la compra.
     */
    public enum ResultadoTotal {
        /** El total era correcto. */
        CORRECTO,
        /** El total era incorrecto (la tienda pierde reputación). */
        INCORRECTO
    }

    /**
     * Resultado de entregar el vuelto.
     */
    public enum ResultadoVuelto {
        /** Vuelto exacto. */
        CORRECTO,
        /** Vuelto exacto con la menor cantidad posible de piezas. */
        CORRECTO_CON_BONO,
        /** Vuelto equivocado: el cliente se va sin completar la compra. */
        INCORRECTO
    }

    /**
     * Estado general de la jornada.
     */
    public enum EstadoJornada {
        /** La tienda está abierta. */
        EN_CURSO,
        /** La jornada terminó (por tiempo, por reputación o a petición). */
        TERMINADA
    }

    /**
     * Reputación con la que inicia cada jornada (R12).
     */
    public static final int REPUTACION_INICIAL = 100;

    /**
     * Puntos de reputación que se pierden por un total mal sumado (R13).
     */
    public static final int PENALIZACION_TOTAL = 10;

    /**
     * Puntos de reputación que se pierden por un vuelto incorrecto (R13).
     */
    public static final int PENALIZACION_VUELTO = 20;

    /**
     * Puntos de reputación que se pierden cuando un cliente se va por
     * esperar demasiado (R13).
     */
    public static final int PENALIZACION_PACIENCIA = 15;

    /**
     * Bonificación en pesos por entregar el vuelto con la menor cantidad de
     * piezas (R11).
     */
    public static final int BONO_VUELTO_MINIMO = 500;

    /**
     * Máximo de clientes en la tienda (el del mostrador más la fila).
     */
    public static final int MAXIMO_CLIENTES = 4;

    /**
     * Tiempo real que consume ir a la caja fuerte, en segundos (R9b).
     */
    public static final double SEGUNDOS_CAJA_FUERTE = 6.0;

    /**
     * Fracción de la paciencia máxima que consume pedir sencillo (R9a).
     */
    public static final double FRACCION_PACIENCIA_SENCILLO = 0.3;

    /**
     * Minuto del día en que abre la tienda (8:00 a. m.).
     */
    public static final int MINUTO_APERTURA = 8 * 60;

    /**
     * Minutos simulados que dura la jornada (10 horas).
     */
    public static final int MINUTOS_JORNADA = 10 * 60;

    /**
     * Parámetros del nivel que se está jugando.
     */
    private final ConfiguracionNivel configuracion;

    /**
     * Generador de clientes.
     */
    private final GeneradorClientes generador;

    /**
     * Caja registradora de esta jornada.
     */
    private final Efectivo caja;

    /**
     * Caja fuerte compartida con la partida (se modifica al retirar dinero).
     */
    private final Efectivo cajaFuerte;

    /**
     * Efectivo que el jugador ha puesto en el mostrador como vuelto.
     */
    private final Efectivo mostrador = new Efectivo();

    /**
     * Clientes presentes: el primero está en el mostrador y los demás
     * esperan en la fila.
     */
    private final Deque<Cliente> cola = new ArrayDeque<>();

    /**
     * Paso de atención del cliente en el mostrador.
     */
    private PasoAtencion paso = PasoAtencion.CALCULAR_TOTAL;

    /**
     * Indica si el pago del cliente en el mostrador ya entró a la caja.
     */
    private boolean pagoRecibido;

    /**
     * Tiempo real transcurrido en la jornada, en segundos.
     */
    private double transcurridos;

    /**
     * Segundos que faltan para que llegue el siguiente cliente.
     */
    private double segundosParaLlegada = 1.0;

    /**
     * Reputación actual de la tienda.
     */
    private int reputacion = REPUTACION_INICIAL;

    /**
     * Ganancias acumuladas en pesos.
     */
    private int ganancias;

    /**
     * Clientes con la venta completada.
     */
    private int clientesAtendidos;

    /**
     * Errores cometidos.
     */
    private int errores;

    /**
     * Bonificaciones obtenidas.
     */
    private int bonificaciones;

    /**
     * Clientes que se fueron por falta de paciencia.
     */
    private int clientesPerdidos;

    /**
     * Estado de la jornada.
     */
    private EstadoJornada estado = EstadoJornada.EN_CURSO;

    /**
     * Indica si la jornada terminó por reputación en cero.
     */
    private boolean perdidaPorReputacion;

    /**
     * Crea una jornada.
     *
     * @param configuracion parámetros del nivel, no nulos
     * @param cajaInicial   efectivo con el que abre la caja registradora
     *                      (se copia)
     * @param cajaFuerte    caja fuerte de la partida; esta instancia se
     *                      modifica cuando el jugador retira dinero
     * @param generador     generador de clientes, no nulo
     * @throws NullPointerException si algún parámetro es nulo
     */
    public Jornada(ConfiguracionNivel configuracion, Efectivo cajaInicial, Efectivo cajaFuerte,
            GeneradorClientes generador) {
        this.configuracion = Objects.requireNonNull(configuracion, "configuracion");
        this.caja = Objects.requireNonNull(cajaInicial, "cajaInicial").copia();
        this.cajaFuerte = Objects.requireNonNull(cajaFuerte, "cajaFuerte");
        this.generador = Objects.requireNonNull(generador, "generador");
    }

    // ------------------------------------------------------------ tiempo

    /**
     * Hace avanzar el reloj de la jornada.
     * <p>
     * Reduce la paciencia de todos los clientes, retira a los que se
     * quedaron sin paciencia (R13, R15), hace llegar nuevos clientes y
     * termina la jornada si se acabó el tiempo (R16) o la reputación llegó
     * a cero (R14).
     * </p>
     *
     * @param segundos tiempo real transcurrido, no negativo
     * @return cuántos clientes se fueron por falta de paciencia en este avance
     * @throws IllegalArgumentException si los segundos son negativos
     */
    public int avanzar(double segundos) {
        if (segundos < 0) {
            throw new IllegalArgumentException("El tiempo no puede ser negativo");
        }
        if (estado != EstadoJornada.EN_CURSO) {
            return 0;
        }
        transcurridos += segundos;
        int perdidos = retirarClientesSinPaciencia(segundos);
        registrarLlegadas(segundos);
        if (reputacion <= 0) {
            terminar(true);
        } else if (transcurridos >= ConfiguracionNivel.DURACION_JORNADA_SEGUNDOS) {
            terminar(false);
        }
        return perdidos;
    }

    /**
     * Reduce la paciencia de todos los clientes y retira a los agotados.
     *
     * @param segundos tiempo transcurrido
     * @return número de clientes que se fueron
     */
    private int retirarClientesSinPaciencia(double segundos) {
        List<Cliente> agotados = new ArrayList<>();
        for (Cliente c : cola) {
            c.consumirPaciencia(segundos);
            if (c.sinPaciencia()) {
                agotados.add(c);
            }
        }
        for (Cliente c : agotados) {
            if (c == cola.peekFirst()) {
                cancelarAtencionActual();
            } else {
                cola.remove(c);
            }
            penalizar(PENALIZACION_PACIENCIA);
            clientesPerdidos++;
        }
        return agotados.size();
    }

    /**
     * Hace llegar un cliente nuevo cuando corresponde, si hay espacio.
     *
     * @param segundos tiempo transcurrido
     */
    private void registrarLlegadas(double segundos) {
        segundosParaLlegada -= segundos;
        if (segundosParaLlegada > 0) {
            return;
        }
        if (cola.size() < MAXIMO_CLIENTES) {
            Cliente nuevo = generador.generar(configuracion);
            if (nuevo != null) {
                cola.addLast(nuevo);
                if (cola.size() == 1) {
                    prepararClienteActual();
                }
            }
            segundosParaLlegada = generador.proximoIntervalo(configuracion);
        } else {
            segundosParaLlegada = 1.0;
        }
    }

    /**
     * Avanza el reloj en pasos pequeños para que la paciencia y las
     * llegadas se actualicen de forma natural.
     *
     * @param total segundos a avanzar
     */
    private void avanzarEnPasos(double total) {
        double restante = total;
        while (restante > 0 && estado == EstadoJornada.EN_CURSO) {
            double dt = Math.min(0.25, restante);
            avanzar(dt);
            restante -= dt;
        }
    }

    /**
     * Devuelve la hora simulada de la jornada.
     *
     * @return texto con formato "HH:mm", entre "08:00" y "18:00"
     */
    public String getHoraTexto() {
        double fraccion = Math.min(transcurridos, ConfiguracionNivel.DURACION_JORNADA_SEGUNDOS)
                / ConfiguracionNivel.DURACION_JORNADA_SEGUNDOS;
        int minutos = MINUTO_APERTURA + (int) (fraccion * MINUTOS_JORNADA);
        return String.format(Locale.US, "%02d:%02d", minutos / 60, minutos % 60);
    }

    /**
     * Devuelve qué fracción de la jornada ha transcurrido.
     *
     * @return valor entre 0 y 1
     */
    public double getProgreso() {
        return Math.min(1.0, transcurridos / ConfiguracionNivel.DURACION_JORNADA_SEGUNDOS);
    }

    // ------------------------------------------------- atención al cliente

    /**
     * Revisa el total que el jugador calculó para el cliente del mostrador.
     * <p>
     * Si es incorrecto la tienda pierde reputación (R3). En ambos casos el
     * cliente paga y su dinero entra a la caja (R6). Si el pago es exacto
     * la venta se completa; si no, se pasa a entregar el vuelto.
     * </p>
     *
     * @param valor total escrito por el jugador
     * @return si el total era correcto o no
     * @throws IllegalStateException si la jornada terminó, no hay cliente o
     *                               el total ya fue respondido
     */
    public ResultadoTotal responderTotal(int valor) {
        verificarEnCurso();
        Cliente cliente = clienteActualObligatorio();
        if (paso != PasoAtencion.CALCULAR_TOTAL) {
            throw new IllegalStateException("El total de este cliente ya fue respondido");
        }
        ResultadoTotal resultado = ResultadoTotal.CORRECTO;
        if (valor != cliente.getCompra().getTotal()) {
            resultado = ResultadoTotal.INCORRECTO;
            penalizar(PENALIZACION_TOTAL);
        }
        caja.agregarTodo(cliente.getPago());
        pagoRecibido = true;
        if (cliente.getVueltoEsperado() == 0) {
            completarVenta(cliente, 0);
        } else {
            paso = PasoAtencion.ENTREGAR_VUELTO;
        }
        if (reputacion <= 0) {
            terminar(true);
        }
        return resultado;
    }

    /**
     * Pasa una pieza de la caja al mostrador (el jugador la arrastra).
     *
     * @param denominacion pieza que se arrastra
     * @return {@code true} si se pudo mover; {@code false} si no hay piezas
     *         de esa denominación o no es momento de entregar el vuelto
     */
    public boolean ponerEnMostrador(Denominacion denominacion) {
        if (!puedeManipularVuelto() || caja.cantidad(denominacion) == 0) {
            return false;
        }
        caja.quitar(denominacion);
        mostrador.agregar(denominacion);
        return true;
    }

    /**
     * Devuelve una pieza del mostrador a la caja.
     *
     * @param denominacion pieza que se devuelve
     * @return {@code true} si se pudo mover; {@code false} si no había
     *         piezas de esa denominación en el mostrador
     */
    public boolean quitarDelMostrador(Denominacion denominacion) {
        if (!puedeManipularVuelto() || mostrador.cantidad(denominacion) == 0) {
            return false;
        }
        mostrador.quitar(denominacion);
        caja.agregar(denominacion);
        return true;
    }

    /**
     * Indica si en este momento el jugador puede mover piezas entre la caja
     * y el mostrador.
     *
     * @return {@code true} si hay un cliente esperando el vuelto
     */
    private boolean puedeManipularVuelto() {
        return estado == EstadoJornada.EN_CURSO && !cola.isEmpty()
                && paso == PasoAtencion.ENTREGAR_VUELTO;
    }

    /**
     * Entrega el vuelto que hay en el mostrador al cliente.
     * <p>
     * Es correcto si su valor es exactamente pago menos total (R8). Si se
     * usó la menor cantidad de piezas posible hay bonificación (R11). Si es
     * incorrecto el cliente reclama, se lleva su dinero y se va sin
     * completar la compra (R13).
     * </p>
     *
     * @return resultado de la entrega
     * @throws IllegalStateException si la jornada terminó o no hay un
     *                               cliente esperando el vuelto
     */
    public ResultadoVuelto confirmarVuelto() {
        verificarEnCurso();
        Cliente cliente = clienteActualObligatorio();
        if (paso != PasoAtencion.ENTREGAR_VUELTO) {
            throw new IllegalStateException("Primero se debe responder el total");
        }
        int esperado = cliente.getVueltoEsperado();
        if (mostrador.total() == esperado) {
            boolean minimo = mostrador.totalPiezas()
                    == Efectivo.conMenorCantidadDePiezas(esperado).totalPiezas();
            mostrador.vaciar();
            if (minimo) {
                bonificaciones++;
                completarVenta(cliente, BONO_VUELTO_MINIMO);
                return ResultadoVuelto.CORRECTO_CON_BONO;
            }
            completarVenta(cliente, 0);
            return ResultadoVuelto.CORRECTO;
        }
        penalizar(PENALIZACION_VUELTO);
        cancelarAtencionActual();
        if (reputacion <= 0) {
            terminar(true);
        }
        return ResultadoVuelto.INCORRECTO;
    }

    /**
     * Indica si con el efectivo disponible (caja más lo que ya está en el
     * mostrador) se puede formar el vuelto exacto del cliente actual.
     *
     * @return {@code true} si es posible dar el vuelto; {@code false} si no
     *         hay cliente esperando el vuelto o falta sencillo
     */
    public boolean puedeDarVuelto() {
        Cliente cliente = cola.peekFirst();
        if (cliente == null || paso != PasoAtencion.ENTREGAR_VUELTO) {
            return false;
        }
        Efectivo disponible = caja.copia();
        disponible.agregarTodo(mostrador);
        return disponible.puedeFormar(cliente.getVueltoEsperado());
    }

    /**
     * Le pide sencillo al cliente cuando en la caja no hay efectivo para dar
     * el vuelto (R9a).
     * <p>
     * El cliente cambia sus billetes por piezas que sí permiten dar el
     * vuelto (el valor que paga no cambia) y gasta parte de su paciencia.
     * </p>
     *
     * @throws IllegalStateException si no hay un cliente esperando el vuelto
     *                               o si la caja sí tiene efectivo suficiente
     */
    public void pedirSencillo() {
        verificarEnCurso();
        Cliente cliente = clienteActualObligatorio();
        if (paso != PasoAtencion.ENTREGAR_VUELTO) {
            throw new IllegalStateException("Solo se puede pedir sencillo al dar el vuelto");
        }
        if (puedeDarVuelto()) {
            throw new IllegalStateException("La caja tiene efectivo suficiente para el vuelto");
        }
        caja.agregarTodo(mostrador);
        mostrador.vaciar();
        caja.quitarTodo(cliente.getPago());
        Efectivo nuevoPago = Efectivo.conMenorCantidadDePiezas(cliente.getCompra().getTotal());
        nuevoPago.agregarTodo(Efectivo.conMenorCantidadDePiezas(cliente.getVueltoEsperado()));
        cliente.cambiarPago(nuevoPago);
        caja.agregarTodo(nuevoPago);
        cliente.consumirPaciencia(FRACCION_PACIENCIA_SENCILLO * cliente.getPacienciaMaxima());
    }

    /**
     * Retira efectivo de la caja fuerte y lo pasa a la caja registradora
     * (R9b). Consume {@link #SEGUNDOS_CAJA_FUERTE} segundos del reloj.
     *
     * @param pedido piezas que se quieren retirar
     * @throws IllegalArgumentException si la caja fuerte no tiene ese saldo
     * @throws IllegalStateException    si la jornada terminó
     */
    public void sacarDeCajaFuerte(Efectivo pedido) {
        verificarEnCurso();
        Objects.requireNonNull(pedido, "pedido");
        if (!cajaFuerte.contiene(pedido)) {
            throw new IllegalArgumentException("La caja fuerte no tiene ese saldo disponible");
        }
        if (pedido.estaVacio()) {
            return;
        }
        cajaFuerte.quitarTodo(pedido);
        caja.agregarTodo(pedido);
        avanzarEnPasos(SEGUNDOS_CAJA_FUERTE);
    }

    /**
     * Termina la jornada antes de que se acabe el tiempo (botón "Terminar
     * Jornada").
     */
    public void terminarAnticipadamente() {
        terminar(false);
    }

    // ------------------------------------------------------------ privados

    /**
     * Comprueba que la jornada siga en curso.
     *
     * @throws IllegalStateException si ya terminó
     */
    private void verificarEnCurso() {
        if (estado != EstadoJornada.EN_CURSO) {
            throw new IllegalStateException("La jornada ya terminó");
        }
    }

    /**
     * Devuelve el cliente del mostrador.
     *
     * @return cliente actual
     * @throws IllegalStateException si no hay clientes
     */
    private Cliente clienteActualObligatorio() {
        Cliente cliente = cola.peekFirst();
        if (cliente == null) {
            throw new IllegalStateException("No hay ningún cliente en el mostrador");
        }
        return cliente;
    }

    /**
     * Reinicia el estado de atención para el cliente que quedó en el
     * mostrador.
     */
    private void prepararClienteActual() {
        paso = PasoAtencion.CALCULAR_TOTAL;
        pagoRecibido = false;
    }

    /**
     * Registra una venta completada y pasa al siguiente cliente (R10).
     *
     * @param cliente cliente atendido
     * @param bono    bonificación adicional en pesos
     */
    private void completarVenta(Cliente cliente, int bono) {
        ganancias += cliente.getCompra().getTotal() + bono;
        clientesAtendidos++;
        cola.removeFirst();
        prepararClienteActual();
    }

    /**
     * Cancela la atención del cliente del mostrador sin completar la venta:
     * el efectivo del mostrador vuelve a la caja y el cliente recupera su
     * pago.
     */
    private void cancelarAtencionActual() {
        Cliente cliente = cola.peekFirst();
        if (cliente == null) {
            return;
        }
        caja.agregarTodo(mostrador);
        mostrador.vaciar();
        if (pagoRecibido) {
            caja.quitarTodo(cliente.getPago());
        }
        cola.removeFirst();
        prepararClienteActual();
    }

    /**
     * Resta reputación y cuenta un error.
     *
     * @param puntos puntos de reputación que se pierden
     */
    private void penalizar(int puntos) {
        reputacion = Math.max(0, reputacion - puntos);
        errores++;
    }

    /**
     * Cierra la jornada: devuelve el efectivo del mostrador a la caja,
     * despide al cliente en atención y vacía la fila.
     *
     * @param porReputacion {@code true} si termina porque la reputación llegó
     *                      a cero
     */
    private void terminar(boolean porReputacion) {
        if (estado != EstadoJornada.EN_CURSO) {
            return;
        }
        cancelarAtencionActual();
        cola.clear();
        estado = EstadoJornada.TERMINADA;
        perdidaPorReputacion = porReputacion;
    }

    // ------------------------------------------------------------ consultas

    /**
     * Construye el resumen de la jornada.
     *
     * @return resultados de la jornada hasta el momento
     */
    public ResumenJornada getResumen() {
        boolean metaCumplida = ganancias >= configuracion.getMetaGanancias();
        boolean aprobada = estado == EstadoJornada.TERMINADA && !perdidaPorReputacion
                && reputacion > 0 && metaCumplida;
        return new ResumenJornada(configuracion.getNivel(), clientesAtendidos, errores,
                bonificaciones, ganancias, reputacion, configuracion.getMetaGanancias(),
                aprobada, perdidaPorReputacion);
    }

    /**
     * Indica si la jornada terminó.
     *
     * @return {@code true} si ya no está en curso
     */
    public boolean isTerminada() {
        return estado == EstadoJornada.TERMINADA;
    }

    /**
     * Indica si la jornada terminó porque la reputación llegó a cero.
     *
     * @return {@code true} si se perdió por reputación
     */
    public boolean isPerdidaPorReputacion() {
        return perdidaPorReputacion;
    }

    /**
     * Devuelve el cliente que está en el mostrador.
     *
     * @return cliente actual, o {@code null} si no hay ninguno
     */
    public Cliente getClienteActual() {
        return cola.peekFirst();
    }

    /**
     * Devuelve los clientes presentes; el primero está en el mostrador.
     *
     * @return copia de la lista de clientes
     */
    public List<Cliente> getClientesEnCola() {
        return new ArrayList<>(cola);
    }

    /**
     * Devuelve el paso de atención del cliente del mostrador.
     *
     * @return paso actual
     */
    public PasoAtencion getPaso() {
        return paso;
    }

    /**
     * Indica si ya se puede mostrar el total correcto de la compra (después
     * de que el jugador respondió).
     *
     * @return {@code true} si el cliente espera el vuelto
     */
    public boolean isTotalVisible() {
        return !cola.isEmpty() && paso == PasoAtencion.ENTREGAR_VUELTO;
    }

    /**
     * Devuelve una copia del efectivo de la caja registradora.
     *
     * @return copia de la caja
     */
    public Efectivo getCaja() {
        return caja.copia();
    }

    /**
     * Devuelve una copia del efectivo que hay en el mostrador.
     *
     * @return copia del mostrador
     */
    public Efectivo getMostrador() {
        return mostrador.copia();
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
     * Devuelve los parámetros del nivel.
     *
     * @return configuración del nivel
     */
    public ConfiguracionNivel getConfiguracion() {
        return configuracion;
    }

    /**
     * Devuelve la reputación de la tienda.
     *
     * @return reputación entre 0 y 100
     */
    public int getReputacion() {
        return reputacion;
    }

    /**
     * Devuelve las ganancias acumuladas.
     *
     * @return ganancias en pesos
     */
    public int getGanancias() {
        return ganancias;
    }

    /**
     * Devuelve los clientes atendidos.
     *
     * @return número de ventas completadas
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
     * Devuelve cuántos clientes se fueron por falta de paciencia.
     *
     * @return número de clientes perdidos
     */
    public int getClientesPerdidos() {
        return clientesPerdidos;
    }

    /**
     * Devuelve el tiempo real transcurrido.
     *
     * @return segundos transcurridos
     */
    public double getSegundosTranscurridos() {
        return transcurridos;
    }
}
