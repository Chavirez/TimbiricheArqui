package observador;

/**
 * Define la interfaz para un objeto que "observa" a otro. Será notificado
 * cuando el objeto observado cambie. [Copiado de
 * AppPrueba/src/main/java/observadores/Observador.java]
 */
public interface Observador {

    /**
     * Método llamado por el Observable para notificar al observador de un
     * cambio de estado.
     */
    void actualizar();
}
