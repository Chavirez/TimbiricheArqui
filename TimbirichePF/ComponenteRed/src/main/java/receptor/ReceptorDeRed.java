package receptor;

import interfaces.IReceptorExterno;
import java.io.BufferedReader;


public class ReceptorDeRed implements Runnable {

    private IReceptorExterno receptorExterno;
    private BufferedReader lector; 
    private final Runnable alDesconectar; // CAMPO AGREGADO

    /**
     * @param receptorExterno La capa de arquitectura a la que notificar.
     * @param lector El stream de entrada del Socket único.
     * @param alDesconectar Accion a ejecutar cuando el hilo finaliza (ej. desconexion).
     */
    public ReceptorDeRed(IReceptorExterno receptorExterno, BufferedReader lector, Runnable alDesconectar) {
        this.receptorExterno = receptorExterno;
        this.lector = lector;
        this.alDesconectar = alDesconectar;
    }

    @Override
    public void run() {
        try {
            System.out.println("[RED-RECEPTOR] Hilo iniciado. Escuchando mensajes.");
            
            String mensajeEntrante;
            while ((mensajeEntrante = lector.readLine()) != null) {
                System.out.println("[RED-RECEPTOR] Recibido de Servidor: " + mensajeEntrante);
                this.receptorExterno.alRecibirMensaje(mensajeEntrante);
            }
        } catch (Exception e) {
            System.err.println("[RED-RECEPTOR] Desconectado o error de conexión: " + e.getMessage());
        } finally {
            System.out.println("[RED-RECEPTOR] Hilo terminado.");
            if (alDesconectar != null) {
                alDesconectar.run();
            }
        }
    }
}