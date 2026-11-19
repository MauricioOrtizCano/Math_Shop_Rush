package co.edu.poli.mathshoprush.modelo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Generador de clientes predefinidos para pruebas deterministas.
 * <p>
 * Entrega los clientes en el orden en que se le dieron. Mientras queden
 * clientes pendientes, llega uno nuevo cada segundo; cuando se acaban, no
 * llega ninguno más.
 * </p>
 */
class GeneradorFijo extends GeneradorClientes {

    /**
     * Clientes que aún no han llegado.
     */
    private final Deque<Cliente> pendientes = new ArrayDeque<>();

    /**
     * Crea el generador con los clientes que van a llegar.
     *
     * @param clientes clientes en orden de llegada
     */
    GeneradorFijo(Cliente... clientes) {
        super(new Random(1));
        for (Cliente c : clientes) {
            pendientes.addLast(c);
        }
    }

    /**
     * Entrega el siguiente cliente predefinido.
     *
     * @param configuracion nivel (se ignora)
     * @return siguiente cliente, o {@code null} si no quedan
     */
    @Override
    public Cliente generar(ConfiguracionNivel configuracion) {
        return pendientes.pollFirst();
    }

    /**
     * Intervalo hasta la próxima llegada.
     *
     * @param configuracion nivel (se ignora)
     * @return 1 segundo si quedan clientes; un valor enorme si no
     */
    @Override
    public double proximoIntervalo(ConfiguracionNivel configuracion) {
        return pendientes.isEmpty() ? 1.0e9 : 1.0;
    }

    /**
     * Arma un efectivo con las piezas indicadas.
     *
     * @param piezas piezas (una por cada aparición)
     * @return efectivo con esas piezas
     */
    static Efectivo pago(Denominacion... piezas) {
        Efectivo e = new Efectivo();
        for (Denominacion d : piezas) {
            e.agregar(d);
        }
        return e;
    }

    /**
     * Crea un cliente con un único producto.
     *
     * @param numero    número del cliente
     * @param total     precio del único producto (total de la compra)
     * @param pago      efectivo con el que paga
     * @param paciencia paciencia en segundos
     * @return cliente de prueba
     */
    static Cliente cliente(int numero, int total, Efectivo pago, double paciencia) {
        return new Cliente(numero, new Compra(List.of(new Producto("Producto", total))),
                pago, false, paciencia);
    }
}
