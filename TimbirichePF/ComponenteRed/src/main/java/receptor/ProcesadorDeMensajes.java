package receptor;

import interfaz.IReceptorExterno;
import utilerias.ColaDeMensajes;

public class ProcesadorDeMensajes implements Runnable {

    private final ColaDeMensajes colaEntrada;
    private final IReceptorExterno receptorExterno;

    public ProcesadorDeMensajes(ColaDeMensajes colaEntrada, IReceptorExterno receptorExterno) {
        this.colaEntrada = colaEntrada;
        this.receptorExterno = receptorExterno;
    }

    @Override
    public void run() {
        System.out.println("[RED-PROCESADOR] Iniciado. Esperando mensajes en cola...");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String mensaje = colaEntrada.tomar(); 
                
                receptorExterno.alRecibirMensaje(mensaje);
            }
        } catch (InterruptedException e) {
            System.out.println("[RED-PROCESADOR] Interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}