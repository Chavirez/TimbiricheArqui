package interfaces;

import entidades.Jugador;
import interfaz.ITuberiaSalida;

public interface IGestorJuego {
    // Configuración de infraestructura
    void setTuberiaSalida(ITuberiaSalida envio);
    void registrarObservador(IObservadorJuego observador);
    
    // Acciones de Juego (UI -> Lógica)
    void crearPartida();
    void unirseAPartida(String codigo);
    void configurarJugador(Jugador jugador);
    void iniciarPartida();
    void reclamarLinea(int fila, int col, boolean horizontal, Jugador jugadorLocal);
}