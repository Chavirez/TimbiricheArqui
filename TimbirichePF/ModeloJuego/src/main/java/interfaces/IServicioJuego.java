package interfaces;

import entidades.Jugador;

/**
 * Define las acciones relacionadas con el flujo de la aplicación y la sesión (Lobby).
 * SEPARADA de la lógica del tablero.
 */
public interface IServicioJuego {

    /**
     * Crea una nueva instancia de partida y establece al usuario como anfitrión.
     */
    void crearPartida();

    /**
     * Solicita el ingreso a una partida existente.
     * @param codigo Código de la sala.
     */
    void unirseAPartida(String codigo);

    /**
     * Configura los datos del usuario local.
     * @param jugador Datos del jugador.
     */
    void enviarConfiguracionJugador(Jugador jugador);

    /**
     * Finaliza la etapa de espera y comienza el juego.
     */
    void iniciarPartida();
    
    // NOTA: Se eliminó reclamarLinea() porque eso ahora pertenece a IGestorTablero
}