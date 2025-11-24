package emisor;


import interfaces.IDispatcher;
import java.io.PrintWriter;

public class EmisorDeRed implements IDispatcher, Runnable {

    private ColaDeMensajes colaDeEnvio;
    private PrintWriter escritor; // Stream de salida hacia el servidor

    /**
     * @param colaDeEnvio La cola de mensajes compartida.
     * @param escritor El stream de salida del Socket único.
     */
    public EmisorDeRed(ColaDeMensajes colaDeEnvio, PrintWriter escritor) {
        this.colaDeEnvio = colaDeEnvio;
        this.escritor = escritor; // <-- Asignar
    }

    @Override
    public void enviar(String datoSerializado) {
        this.colaDeEnvio.agregar(datoSerializado);
    }

    @Override
    public void run() {
        try {
            System.out.println("[RED-EMISOR] Hilo iniciado. Listo para enviar.");
            
            while (!Thread.currentThread().isInterrupted()) {
                String mensaje = this.colaDeEnvio.tomar(); // Bloqueante
                
                this.escritor.println(mensaje);
                System.out.println("[RED-EMISOR] Enviando a Servidor: " + mensaje);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[RED-EMISOR] Hilo de envío interrumpido.");
        } catch (Exception e) {
            // Si el socket muere (ej. servidor se cae), el hilo morirá.
            System.err.println("[RED-EMISOR] Error de escritura: " + e.getMessage());
        } finally {
             System.out.println("[RED-EMISOR] Hilo terminado.");
             //Notificar al receptor que cierre también.
        }
    }
}