package modeloJuego;

import acciones.*;
import entidades.*;
import eventos.*;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
import interfaces.IGestorJuego;
import interfaces.IObservadorJuego;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación principal de la lógica del juego.
 *
 * Esta clase actúa como el coordinador central entre la capa de red y la
 * interfaz de usuario. Se encarga de procesar mensajes entrantes, mantener el
 * estado local para validaciones rápidas y despachar las acciones del usuario
 * hacia el servidor.
 */
public class GestorJuego implements IGestorJuego, ITuberiaEntrada {

    /**
     * Canal de comunicación utilizado para enviar solicitudes al servidor o a
     * otros clientes.
     */
    private ITuberiaSalida envio;

    /**
     * Lista de componentes de la interfaz de usuario suscritos a los cambios
     * del juego.
     */
    private final List<IObservadorJuego> observadores = new ArrayList<>();

    /**
     * Almacena el último estado de la partida recibido desde la red. Sirve como
     * caché para validar acciones locales (ej. turnos) antes de enviarlas.
     */
    private EstadoPartidaDTO ultimoEstadoConocido;

    /**
     * Establece el mecanismo de salida para la comunicación por red.
     *
     * @param envio Instancia encargada de la transmisión de datos.
     */
    @Override
    public void setTuberiaSalida(ITuberiaSalida envio) {
        this.envio = envio;
    }

    /**
     * Registra un nuevo observador que será notificado sobre los eventos del
     * juego.
     *
     * @param observador El componente que desea recibir actualizaciones.
     */
    @Override
    public void registrarObservador(IObservadorJuego observador) {
        observadores.add(observador);
    }

    /**
     * Procesa los datos entrantes desde la red. Identifica el tipo de mensaje
     * (Evento o Respuesta) y delega la actualización a los observadores
     * registrados.
     *
     * @param dato El objeto recibido a través de la tubería de entrada.
     */
    @Override
    public void alRecibirDato(Object dato) {
        if (dato instanceof EventoEstadoActualizado e) {
            this.ultimoEstadoConocido = e.getNuevoEstado();
            notificar(o -> o.estadoActualizado(e.getNuevoEstado()));

        } else if (dato instanceof RespuestaUnionExitosa r) {
            this.ultimoEstadoConocido = r.getEstadoInicial();
            notificar(o -> o.unionExitosa(r.getEstadoInicial()));

        } else if (dato instanceof EventoPartidaIniciada) {
            notificar(IObservadorJuego::partidaIniciada);

        } else if (dato instanceof EventoError e) {
            notificar(o -> o.error(e.getMensaje()));
        }

        System.out.println("[GestorJuego] Dato recibido y procesado: " + dato.getClass().getSimpleName());
    }

    /**
     * Interfaz funcional interna para encapsular una acción a realizar sobre un
     * observador. Facilita la iteración sobre la lista de suscriptores.
     */
    private interface AccionNotificacion {

        /**
         * Ejecuta la operación definida sobre un observador específico.
         *
         * @param o El observador destino.
         */
        void ejecutar(IObservadorJuego o);
    }

    /**
     * Método auxiliar para difundir un evento a todos los observadores
     * registrados.
     *
     * @param accion La lógica de notificación a ejecutar para cada observador.
     */
    private void notificar(AccionNotificacion accion) {
        for (IObservadorJuego o : observadores) {
            accion.ejecutar(o);
        }
    }

    /**
     * Envía una solicitud a la red para crear una nueva partida.
     */
    @Override
    public void crearPartida() {
        if (envio != null) {
            envio.enviarDato(new AccionCrearPartida());
        }
    }

    /**
     * Envía una solicitud a la red para unirse a una partida existente.
     *
     * @param codigo El código de identificación de la partida.
     */
    @Override
    public void unirseAPartida(String codigo) {
        if (envio != null) {
            envio.enviarDato(new AccionUnirseAPartida(codigo));
        }
    }

    /**
     * Envía la configuración del perfil del jugador (nombre, avatar, etc.) a la
     * red.
     *
     * @param jugador El objeto con los datos del jugador local.
     */
    @Override
    public void configurarJugador(Jugador jugador) {
        if (envio != null) {
            envio.enviarDato(new AccionConfigurarJugador(jugador));
        }
    }

    /**
     * Envía la señal para comenzar la partida a la red. Este es el caso de uso
     *
     */
    @Override
    public void solicitarInicioPartida() {
        try {

            acciones.AccionIniciarPartida accion = new acciones.AccionIniciarPartida();

            if (this.envio != null) {
                this.envio.enviarDato(accion);

            } else {

            }

        } catch (Throwable t) {

            t.printStackTrace();

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error interno al iniciar: " + t.getClass().getSimpleName(),
                    "Error Cliente",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Intenta realizar un movimiento de reclamación de línea. Valida localmente
     * si es el turno del jugador antes de enviar la acción a la red, evitando
     * tráfico innecesario si el movimiento no es válido por turno.
     *
     * @param fila Coordenada de la fila.
     * @param col Coordenada de la columna.
     * @param horizontal Orientación de la línea.
     * @param jugadorLocal Jugador que intenta realizar la acción.
     */
    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal, Jugador jugadorLocal) {
        if (esTurnoValido(jugadorLocal)) {
            if (envio != null) {
                envio.enviarDato(new AccionReclamarLinea(fila, col, horizontal));
            }
        } else {
            System.out.println("Acción bloqueada: No es tu turno.");
        }
    }

    /**
     * Verifica si el jugador local tiene el turno activo basándose en el último
     * estado conocido.
     *
     * @param jugadorLocal El jugador a validar.
     * @return true si el ID del jugador coincide con el turno actual, false en
     * caso contrario.
     */
    private boolean esTurnoValido(Jugador jugadorLocal) {
        if (ultimoEstadoConocido == null || jugadorLocal == null) {
            return false;
        }
        int idx = ultimoEstadoConocido.jugadorActualIdx();
        if (idx < 0 || idx >= ultimoEstadoConocido.jugadores().size()) {
            return false;
        }
        return ultimoEstadoConocido.jugadores().get(idx).id() == jugadorLocal.id();
    }
}
