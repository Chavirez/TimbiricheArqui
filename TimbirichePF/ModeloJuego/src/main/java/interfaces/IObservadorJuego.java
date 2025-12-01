package interfaces;

import entidades.EstadoPartidaDTO;

/**
 * Interfaz para que la UI escuche eventos del Modelo de Juego.
 * Nombres de métodos en español y sin prefijos técnicos.
 */
public interface IObservadorJuego {
    
    /**
     * Notifica que el cliente se ha unido correctamente a una sala.
     */
    void unionExitosa(EstadoPartidaDTO estadoInicial);

    /**
     * Notifica que el estado del tablero o jugadores ha cambiado.
     */
    void estadoActualizado(EstadoPartidaDTO nuevoEstado);

    /**
     * Notifica que el juego ha comenzado oficialmente.
     */
    void partidaIniciada();

    /**
     * Notifica que ocurrió un error (reglas, conexión, etc).
     */
    void error(String mensaje);
}