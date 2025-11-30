package gestor;

import interfaz.ITuberiaEntrada;
import entidades.Jugador;
import eventos.EventoError;
import java.io.IOException;
import java.net.Socket;
import acciones.*;
import interfaz.ITuberiaSalida;


public class ManejadorCliente implements ITuberiaEntrada {

    private final Socket socket; 
    private final GestorPrincipal gestor;
    // Cambiar el tipo inyectado a ITuberiaSalida
    private final ITuberiaSalida procesadorEnvio; 
    
    private Partida partidaActual = null;
    private Jugador jugador = null;

    /**
     * Constructor con Inyección de Dependencias.
     * Recibe la interfaz de envío ya configurada por GestorPrincipal.
     */
    public ManejadorCliente(Socket socket, GestorPrincipal gestor, ITuberiaSalida procesadorEnvio) {
        this.socket = socket;
        this.gestor = gestor;
        this.procesadorEnvio = procesadorEnvio;
    }
    
    public int getIdJugador() {
        return (jugador != null) ? jugador.id() : socket.getPort();
    }
    public Jugador getJugador() {
        return jugador;
    }
    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
    public void setPartidaActual(Partida partida) {
        this.partidaActual = partida;
    }

    @Override
    public void alRecibirDato(Object dato) {
        System.out.println("[MANEJADOR " + getIdJugador() + "] DTO Recibido: " + dato.getClass().getSimpleName());
        
        switch (dato) {
            case AccionCrearPartida a -> gestor.crearPartida(this);
            case AccionUnirseAPartida a -> gestor.unirseAPartida(this, a.getCodigoSala());
            case AccionConfigurarJugador a -> {
                if (partidaActual != null) {
                    // Nota: Si quieres mantener el ID del cliente o asignarle uno nuevo, 
                    // deberías crear el Jugador en el servidor antes de este punto
                    this.jugador = a.getJugador(); 
                    partidaActual.configurarJugador(this, this.jugador);
                }
            }
            case AccionReclamarLinea a -> {
                if (partidaActual != null && jugador != null) {
                    partidaActual.reclamarLinea(this, a);
                } else {
                    enviarError("Debe estar en una partida y configurado para reclamar líneas.");
                }
            }
            case AccionIniciarPartida a -> {
                if (partidaActual != null && jugador != null) {
                    partidaActual.iniciarPartida(this);
                } else {
                    enviarError("Debe estar en una partida y configurado para iniciar.");
                }
            }
            default -> System.err.println("[MANEJADOR " + getIdJugador() + "] DTO no reconocido.");
        }
    }
    


    public void enviarDTO(Object dto) {
        System.out.println("[MANEJADOR " + getIdJugador() + "] DTO Enviando: " + dto.getClass().getSimpleName());
        // Delega al componente inyectado (el bus de arquitectura)
        procesadorEnvio.enviarDato(dto); 
    }

    public void enviarError(String mensaje) {
        enviarDTO(new EventoError(mensaje));
    }
    
    /**
     * Lógica de limpieza al desconectarse. 
     * Debe ser llamada por el hilo de I/O (ReceptorDeRed) al finalizar.
     */
    public void notificarDesconexion() {
        if (partidaActual != null) {
            partidaActual.eliminarJugador(this);
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) { /* Ignorar */ }
        System.out.println("[MANEJADOR " + getIdJugador() + "] Tarea de limpieza completada.");
    }
}