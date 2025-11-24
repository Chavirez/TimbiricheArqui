package utilerias;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Encapsula una cola de mensajes de salida.
 * Es un objeto separado, como se solicitó, para gestionar el orden (FIFO)
 * y la concurrencia (thread-safety) de los mensajes a enviar.
 */
public class ColaDeMensajes {

    /**
     * Se utiliza una cola bloqueante (thread-safe) para manejar la concurrencia
     * entre el hilo que agrega (App/Arquitectura) y el hilo que consume (Red).
     */
    private BlockingQueue<String> cola;

    /**
     * Constructor.
     */
    public ColaDeMensajes() {
        this.cola = new LinkedBlockingQueue<>();
    }

    /**
     * Agrega un mensaje al final de la cola.
     * @param mensaje El string serializado a enviar.
     */
    public void agregar(String mensaje) {
        try {
            this.cola.put(mensaje); // 'put' espera si la cola está llena
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Buena práctica
            System.err.println("Error al agregar mensaje a la cola de envío.");
        }
    }

    /**
     * Toma un mensaje del inicio de la cola.
     * Este método es bloqueante; espera si la cola está vacía.
     * @return El primer mensaje de la cola.
     * @throws InterruptedException Si el hilo es interrumpido mientras espera.
     */
    public String tomar() throws InterruptedException {
        return this.cola.take(); // 'take' espera si la cola está vacía
    }
}