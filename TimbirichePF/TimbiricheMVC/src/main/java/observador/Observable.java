package observador;

import java.util.LinkedList;
import java.util.List;
import observador.Observador;

/**
 * Clase base para objetos que pueden ser "observados". Mantiene
 * una lista de observadores y los notifica cuando ocurre un cambio.
 * [Copiado de AppPrueba/src/main/java/observadores/Observable.java]
 */
public class Observable {

    private List<Observador> observadores;

    public Observable() {
        this.observadores = new LinkedList<>();
    }

    /**
     * Agrega un nuevo observador a la lista.
     * @param obs El observador a agregar.
     */
    public void agregarObservador(Observador obs) {
        if (obs == null || this.observadores.contains(obs)) {
            return;
        }
        this.observadores.add(obs);
    }

    /**
     * Notifica a todos los observadores registrados llamando a su método
     * actualizar().
     */
    protected void notificarObservadores() {
        List<Observador> observadoresACopiar = new LinkedList<>(this.observadores);
        for (Observador obs : observadoresACopiar) {
            obs.actualizar();
        }
    }
}