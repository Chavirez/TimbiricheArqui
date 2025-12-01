package interfaces;

import entidades.Jugador;

/**
 * Define las acciones que un cliente (interfaz de usuario) puede solicitar al
 * sistema.
 *
 * Esta interfaz centraliza el contrato de juego, actuando como fachada para que
 * la vista pueda comunicarse con la lógica de negocio sin conocer los detalles
 * internos de la implementación.
 */
public interface IServicioJuego {

    /**
     * Solicita realizar un movimiento para cerrar una línea en el tablero. La
     * implementación debe validar si el movimiento es legal antes de
     * procesarlo.
     *
     * @param fila Índice de la fila en el tablero.
     * @param col Índice de la columna en el tablero.
     * @param horizontal Indica la orientación de la línea (true para
     * horizontal, false para vertical).
     */
    void reclamarLinea(int fila, int col, boolean horizontal);

    /**
     * Crea una nueva instancia de partida y establece al usuario actual como
     * anfitrión. Generalmente genera un código de acceso para compartir con
     * otros jugadores.
     */
    void crearPartida();

    /**
     * Solicita el ingreso a una partida existente utilizando un código de
     * invitación.
     *
     * @param codigo Cadena de caracteres que identifica la partida a la que se
     * desea entrar.
     */
    void unirseAPartida(String codigo);

    /**
     * Envía y registra la información del jugador local en la sesión actual. Se
     * utiliza para configurar el nombre, avatar o color antes de iniciar.
     *
     * @param jugador Objeto que contiene los datos del usuario local.
     */
    void enviarConfiguracionJugador(Jugador jugador);

    /**
     * Da la orden de comenzar el juego, finalizando la etapa de sala de espera.
     * Normalmente solo puede ser invocado por el anfitrión de la partida.
     */
    void iniciarPartida();
}
