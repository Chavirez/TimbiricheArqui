package interfaces;

import entidades.Jugador;
import interfaz.ITuberiaSalida;

/**
 * Contrato Maestro de la Lógica del Juego. Coordina Red, Reglas, Lobby y
 * Tablero.
 */
public interface IGestorJuego {
    
    void setTuberiaSalida(ITuberiaSalida envio);

    void registrarObservador(IObservadorJuego observador);

    void crearPartida(int tamaino);

    void unirseAPartida(String codigo);

    void configurarJugador(Jugador jugador);

    void iniciarPartida();

    void reclamarLinea(int fila, int col, boolean horizontal, Jugador jugadorLocal);
}
