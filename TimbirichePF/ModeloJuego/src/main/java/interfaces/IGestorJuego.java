package interfaces;

import entidades.Jugador;
import interfaz.ITuberiaSalida;

/**
 * Define el contrato para el gestor de la lógica del juego.
 *
 * Esta interfaz es responsable de coordinar el ciclo de vida de la partida
 * (creación, unión, inicio), la gestión de los jugadores y la validación de
 * movimientos dentro del tablero. Actúa como intermediario entre la interfaz de
 * usuario y la lógica de red/negocio.
 */
public interface IGestorJuego {

    /**
     * Establece el canal de comunicación para el envío de mensajes. Permite
     * desacoplar la lógica del juego del mecanismo de red.
     *
     * @param envio La instancia de ITuberiaSalida encargada de transmitir los
     * datos.
     */
    void setTuberiaSalida(ITuberiaSalida envio);

    /**
     * Registra un observador para suscribirse a los eventos del juego.
     * Implementa el patrón Observer para notificar cambios de estado a la UI.
     *
     * @param observador El objeto que escuchará las actualizaciones del juego.
     */
    void registrarObservador(IObservadorJuego observador);

    /**
     * Inicializa una nueva sesión de juego como anfitrión. Generalmente crea un
     * código de partida para que otros puedan unirse.
     */
    void crearPartida();

    /**
     * Solicita unirse a una partida existente mediante un código de
     * identificación.
     *
     * @param codigo El código único de la partida a la que se desea ingresar.
     */
    void unirseAPartida(String codigo);

    /**
     * Configura los datos del jugador local para la sesión actual.
     *
     * @param jugador Objeto Jugador que contiene nombre, avatar o color del
     * usuario.
     */
    void configurarJugador(Jugador jugador);

    /**
     * Da la señal para comenzar la partida una vez que los jugadores necesarios
     * están listos. Transiciona el estado del juego de "Sala de espera" a "En
     * juego".
     */
    void iniciarPartida();

    /**
     * Procesa el intento de un jugador de reclamar una línea en el tablero.
     *
     * @param fila Índice de la fila donde se encuentra la línea.
     * @param col Índice de la columna donde se encuentra la línea.
     * @param horizontal Indica la orientación (true si es horizontal, false si
     * es vertical).
     * @param jugadorLocal El jugador que está realizando el movimiento.
     */
    void reclamarLinea(int fila, int col, boolean horizontal, Jugador jugadorLocal);
}
