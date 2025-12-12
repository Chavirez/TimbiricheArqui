package modelo;

import entidades.*;
import eventos.EventoPartidaTerminada;
import interfaces.*;
import observador.Observable;
import java.awt.Color;

public class AplicacionModelo extends Observable implements IObservadorJuego, IGestorTablero {

    public enum EstadoNavegacion {
        LOBBY,
        PARTIDA,
        RESULTADOS
    }
    private final IGestorJuego gestorRed;
    private EstadoNavegacion estadoActual = EstadoNavegacion.LOBBY;
    private Jugador jugadorLocal;
    private TableroModelo tableroModelo;
    private String mensajeError;
    private EventoPartidaTerminada resultadosFinales;

    // Usamos Runnable como un callback simple para el Controlador
    private Runnable controladorCallback;

    public AplicacionModelo(IGestorJuego gestorRed) {
        this.gestorRed = gestorRed;
        this.gestorRed.registrarObservador(this);
    }

    public void registrarControladorCallback(Runnable callback) {
        this.controladorCallback = callback;
    }

    // --- ACCIONES ---
    public void crearPartida() {
        gestorRed.crearPartida();
    }

    public void unirseAPartida(String codigo) {
        gestorRed.unirseAPartida(codigo);
    }

    public void configurarJugador(Jugador jugador) {
        this.jugadorLocal = jugador;
        gestorRed.configurarJugador(jugador);
        // Notificación al Controlador
        if (controladorCallback != null) {
            controladorCallback.run();
        }
    }

    public void iniciarPartida() {
        gestorRed.iniciarPartida();
    }

    @Override
    public void reclamarLinea(int f, int c, boolean h) {
        if (jugadorLocal != null) {
            gestorRed.reclamarLinea(f, c, h, jugadorLocal);
        }
    }

    public void cambiarEstado(EstadoNavegacion nuevo) {
        if (this.estadoActual != nuevo) {
            this.estadoActual = nuevo;
            notificarObservadores(); // Notifica a las Vistas suscritas (TableroVista, VentanaJuego)
            // Notificación específica al Controlador
            if (controladorCallback != null) {
                controladorCallback.run();
            }
        }
    }

    // --- RESPUESTAS DE RED ---
    @Override
    public void unionExitosa(EstadoPartidaDTO estadoInicial) {
        this.tableroModelo = new TableroModelo();
        this.tableroModelo.actualizarEstado(estadoInicial);
        if (this.jugadorLocal == null) {
            this.jugadorLocal = new Jugador(0, "Jugador", "avatar_default", Color.GRAY);
        }
        cambiarEstado(EstadoNavegacion.PARTIDA);
        sincronizarJugadorLocal(estadoInicial);
    }

    @Override
    public void estadoActualizado(EstadoPartidaDTO nuevoEstado) {
        if (this.tableroModelo != null) {
            this.tableroModelo.actualizarEstado(nuevoEstado);
        }
        sincronizarJugadorLocal(nuevoEstado);
        // Notificamos al Controlador para que maneje la posible transición de vista (ej. fin del juego)
        if (controladorCallback != null) {
            controladorCallback.run();
        }
    }

    private void sincronizarJugadorLocal(EstadoPartidaDTO estado) {
        if (this.jugadorLocal != null && this.jugadorLocal.id() == 0) {
            for (Jugador j : estado.jugadores()) {
                if (j.nombre().equals(this.jugadorLocal.nombre())) {
                    this.jugadorLocal = j; // Actualizamos con el ID real y color asignado
                    break;
                }
            }
        }
    }

    @Override
    public void partidaIniciada() {
        // La gestión de la vista se maneja en el callback del controlador
        if (controladorCallback != null) {
            controladorCallback.run();
        }
    }

    @Override
    public void partidaTerminada(EventoPartidaTerminada evento) {
        this.resultadosFinales = evento;
        cambiarEstado(EstadoNavegacion.RESULTADOS);
    }

    @Override
    public void error(String mensaje) {
        this.mensajeError = mensaje;
        // Notificamos al Controlador para que muestre el error y maneje la vista
        if (controladorCallback != null) {
            controladorCallback.run();
        }
    }

    public void limpiarError() {
        this.mensajeError = null;
    }

    // Getters
    public TableroModelo getTableroModelo() {
        return tableroModelo;
    }

    public EstadoNavegacion getEstadoActual() {
        return estadoActual;
    }

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    public EventoPartidaTerminada getResultadosFinales() {
        return resultadosFinales;
    }

    public String getMensajeError() {
        return mensajeError;
    }
}
