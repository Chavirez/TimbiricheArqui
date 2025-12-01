package itson.modelojuego.interfaces;

import entidades.Jugador;

/**
 * Define las acciones que un cliente (UI) puede solicitar al sistema. Movido
 * desde TimbiricheMVC para centralizar el contrato de juego.
 */
public interface IServicioJuego {

    void reclamarLinea(int fila, int col, boolean horizontal);

    void crearPartida();

    void unirseAPartida(String codigo);

    void enviarConfiguracionJugador(Jugador jugador);

    void iniciarPartida();
}
