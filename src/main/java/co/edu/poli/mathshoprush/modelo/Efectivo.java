package co.edu.poli.mathshoprush.modelo;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.OptionalInt;

/**
 * Conjunto de billetes y monedas (cuántas piezas hay de cada denominación).
 * <p>
 * Se usa para representar la caja registradora, la caja fuerte, el pago de
 * un cliente y el vuelto que el jugador arma sobre el mostrador. Es mutable.
 * </p>
 */
public class Efectivo {

    /**
     * Cantidad de piezas por denominación. Solo se guardan cantidades
     * mayores que cero.
     */
    private final Map<Denominacion, Integer> piezas = new EnumMap<>(Denominacion.class);

    /**
     * Crea un efectivo vacío (sin billetes ni monedas).
     */
    public Efectivo() {
    }

    /**
     * Agrega piezas de una denominación.
     *
     * @param denominacion denominación a agregar, no nula
     * @param cantidad número de piezas, no negativo
     * @throws IllegalArgumentException si la cantidad es negativa
     * @throws NullPointerException si la denominación es nula
     */
    public void agregar(Denominacion denominacion, int cantidad) {
        Objects.requireNonNull(denominacion, "denominacion");
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (cantidad > 0) {
            piezas.merge(denominacion, cantidad, Integer::sum);
        }
    }

    /**
     * Agrega una sola pieza de una denominación.
     *
     * @param denominacion denominación a agregar
     */
    public void agregar(Denominacion denominacion) {
        agregar(denominacion, 1);
    }

    /**
     * Agrega todas las piezas de otro efectivo sin modificar el original.
     *
     * @param otro efectivo cuyas piezas se copian a este
     * @throws NullPointerException si el efectivo recibido es nulo
     */
    public void agregarTodo(Efectivo otro) {
        Efectivo origen = otro.copia();
        for (Map.Entry<Denominacion, Integer> e : origen.piezas.entrySet()) {
            agregar(e.getKey(), e.getValue());
        }
    }

    /**
     * Quita piezas de una denominación.
     *
     * @param denominacion denominación a quitar
     * @param cantidad     número de piezas, no negativo
     * @throws IllegalArgumentException si la cantidad es negativa o hay menos
     *                                  piezas de las pedidas
     */
    public void quitar(Denominacion denominacion, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        int actual = cantidad(denominacion);
        if (cantidad > actual) {
            throw new IllegalArgumentException(
                    "No hay suficientes piezas de " + denominacion.getEtiqueta());
        }
        if (cantidad == actual) {
            piezas.remove(denominacion);
        } else if (cantidad > 0) {
            piezas.put(denominacion, actual - cantidad);
        }
    }

    /**
     * Quita una sola pieza de una denominación.
     *
     * @param denominacion denominación a quitar
     * @throws IllegalArgumentException si no hay ninguna pieza
     */
    public void quitar(Denominacion denominacion) {
        quitar(denominacion, 1);
    }

    /**
     * Quita todas las piezas de otro efectivo. Si este efectivo no las
     * contiene todas, no se modifica nada.
     *
     * @param otro efectivo cuyas piezas se deben retirar
     * @throws IllegalArgumentException si este efectivo no contiene las piezas solicitadas
     * @throws NullPointerException si el efectivo recibido es nulo
     */
    public void quitarTodo(Efectivo otro) {
        if (!contiene(otro)) {
            throw new IllegalArgumentException("El efectivo no contiene las piezas pedidas");
        }
        Efectivo origen = otro.copia();
        for (Map.Entry<Denominacion, Integer> e : origen.piezas.entrySet()) {
            quitar(e.getKey(), e.getValue());
        }
    }

    /**
     * Indica si este efectivo tiene al menos las piezas de otro.
     *
     * @param otro efectivo cuyas piezas se desean comprobar
     * @return {@code true} si hay suficientes piezas de cada denominación;
     *         {@code false} en caso contrario
     * @throws NullPointerException si el efectivo recibido es nulo
     */
    public boolean contiene(Efectivo otro) {
        for (Map.Entry<Denominacion, Integer> e : otro.piezas.entrySet()) {
            if (cantidad(e.getKey()) < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Devuelve cuántas piezas hay de una denominación.
     *
     * @param denominacion denominación consultada
     * @return número de piezas (0 si no hay)
     */
    public int cantidad(Denominacion denominacion) {
        return piezas.getOrDefault(denominacion, 0);
    }

    /**
     * Calcula el valor total del efectivo.
     *
     * @return suma de los valores de todas las piezas, en pesos
     */
    public int total() {
        int suma = 0;
        for (Map.Entry<Denominacion, Integer> e : piezas.entrySet()) {
            suma += e.getKey().getValor() * e.getValue();
        }
        return suma;
    }

    /**
     * Cuenta cuántas piezas (billetes y monedas) hay en total.
     *
     * @return número total de piezas
     */
    public int totalPiezas() {
        int suma = 0;
        for (int n : piezas.values()) {
            suma += n;
        }
        return suma;
    }

    /**
     * Indica si no hay ninguna pieza.
     *
     * @return {@code true} si está vacío
     */
    public boolean estaVacio() {
        return piezas.isEmpty();
    }

    /**
     * Elimina todas las piezas.
     */
    public void vaciar() {
        piezas.clear();
    }

    /**
     * Crea una copia independiente de este efectivo.
     *
     * @return nuevo efectivo con las mismas piezas
     */
    public Efectivo copia() {
        Efectivo copia = new Efectivo();
        copia.piezas.putAll(this.piezas);
        return copia;
    }

    /**
     * Indica si con las piezas disponibles se puede formar exactamente un
     * monto.
     * <p>
     * Resuelve el problema por programación dinámica sobre múltiplos de
     * $50, respetando la cantidad disponible de cada denominación.
     * </p>
     *
     * @param monto valor que se quiere formar, en pesos
     * @return {@code true} si existe una combinación exacta
     */
    public boolean puedeFormar(int monto) {
        if (monto < 0 || monto % 50 != 0) {
            return false;
        }
        if (monto == 0) {
            return true;
        }
        int n = monto / 50;
        boolean[] alcanzable = new boolean[n + 1];
        alcanzable[0] = true;
        for (Denominacion d : Denominacion.values()) {
            int disponibles = cantidad(d);
            if (disponibles == 0) {
                continue;
            }
            int unidad = d.getValor() / 50;
            int[] usos = new int[n + 1];
            for (int v = unidad; v <= n; v++) {
                if (!alcanzable[v] && alcanzable[v - unidad] && usos[v - unidad] < disponibles) {
                    alcanzable[v] = true;
                    usos[v] = usos[v - unidad] + 1;
                }
            }
        }
        return alcanzable[n];
    }

    /**
     * Calcula la menor cantidad de piezas con la que se puede formar
     * exactamente un monto, usando solo las piezas disponibles (R11).
     * <p>
     * A diferencia de {@link #conMenorCantidadDePiezas(int)}, respeta la
     * cantidad que hay de cada denominación. Resuelve el problema por
     * programación dinámica sobre múltiplos de $50: cada pieza se procesa
     * una sola vez, recorriendo los montos de mayor a menor para que no se
     * pueda usar dos veces.
     * </p>
     *
     * @param monto valor que se quiere formar, en pesos
     * @return la menor cantidad de piezas, o vacío si el monto es negativo,
     *         no es múltiplo de $50 o no se puede formar con las piezas
     *         disponibles
     */
    public OptionalInt minimoDePiezasPara(int monto) {
        if (monto < 0 || monto % 50 != 0) {
            return OptionalInt.empty();
        }
        int n = monto / 50;
        int infinito = Integer.MAX_VALUE;
        int[] minimo = new int[n + 1];
        Arrays.fill(minimo, infinito);
        minimo[0] = 0;
        for (Denominacion d : Denominacion.values()) {
            int unidad = d.getValor() / 50;
            for (int pieza = 0; pieza < cantidad(d); pieza++) {
                for (int v = n; v >= unidad; v--) {
                    if (minimo[v - unidad] != infinito && minimo[v - unidad] + 1 < minimo[v]) {
                        minimo[v] = minimo[v - unidad] + 1;
                    }
                }
            }
        }
        return minimo[n] == infinito ? OptionalInt.empty() : OptionalInt.of(minimo[n]);
    }

    /**
     * Forma un monto con la menor cantidad posible de piezas.
     * <p>
     * Como las denominaciones siguen la serie 1-2-5, el algoritmo voraz
     * (tomar siempre la pieza más grande posible) es óptimo.
     * </p>
     *
     * @param monto valor a formar, múltiplo de 50 y no negativo
     * @return efectivo con la menor cantidad de piezas que suma el monto
     * @throws IllegalArgumentException si el monto es negativo o no es
     *                                  múltiplo de 50
     */
    public static Efectivo conMenorCantidadDePiezas(int monto) {
        if (monto < 0 || monto % 50 != 0) {
            throw new IllegalArgumentException("El monto debe ser un múltiplo de 50 no negativo");
        }
        Efectivo resultado = new Efectivo();
        int restante = monto;
        for (Denominacion d : Denominacion.values()) {
            resultado.agregar(d, restante / d.getValor());
            restante %= d.getValor();
        }
        return resultado;
    }

    /**
     * Representación en texto del efectivo.
     *
     * @return texto como "Efectivo[1 x $10.000, 3 x $100]"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Efectivo[");
        boolean primero = true;
        for (Denominacion d : Denominacion.values()) {
            int n = cantidad(d);
            if (n > 0) {
                if (!primero) {
                    sb.append(", ");
                }
                sb.append(n).append(" x ").append(d.getEtiqueta());
                primero = false;
            }
        }
        return sb.append("]").toString();
    }
}
