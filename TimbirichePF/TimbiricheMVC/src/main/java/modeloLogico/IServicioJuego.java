package modeloLogico;

import entidades.Jugador;

/**
 * Define el contrato para los servicios de lógica del juego. Es la interfaz que
 * el Modelo/Controlador usa para enviar solicitudes de acciones de juego al
 * GestorCliente
 */
public interface IServicioJuego {

    void reclamarLinea(int fila, int col, boolean horizontal);

    void crearPartida();

    void unirseAPartida(String codigo);

    void enviarConfiguracionJugador(Jugador jugador);

    void iniciarPartida();
}
