package receptor;

import java.io.BufferedReader;
import utilerias.ColaDeMensajes;

public class ReceptorDeRed implements Runnable {

    private final ColaDeMensajes colaEntrada;
    private final BufferedReader lector; 
    private final Runnable alDesconectar;

    public ReceptorDeRed(ColaDeMensajes colaEntrada, BufferedReader lector, Runnable alDesconectar) {
        this.colaEntrada = colaEntrada;
        this.lector = lector;
        this.alDesconectar = alDesconectar;
    }

    @Override
    public void run() {
        try {
            String mensajeEntrante;
            while ((mensajeEntrante = lector.readLine()) != null) {
                System.out.println("[RED-RECEPTOR] Encolando mensaje...");
                this.colaEntrada.agregar(mensajeEntrante);
            }
        } catch (Exception e) {
            System.err.println("[RED-RECEPTOR] Error: " + e.getMessage());
        } finally {
            if (alDesconectar != null) alDesconectar.run();
        }
    }
}