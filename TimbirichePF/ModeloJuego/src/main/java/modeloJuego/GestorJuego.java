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

public class GestorJuego implements IGestorJuego, ITuberiaEntrada {

    private ITuberiaSalida envio;
    private final List<IObservadorJuego> observadores = new ArrayList<>();
    private EstadoPartidaDTO ultimoEstadoConocido;

    @Override
    public void setTuberiaSalida(ITuberiaSalida envio) {
        this.envio = envio;
    }

    @Override
    public void registrarObservador(IObservadorJuego observador) {
        observadores.add(observador);
    }

    // --- RECEPCIÓN DE DATOS (RED -> LÓGICA) ---
    @Override
    public void alRecibirDato(Object dato) {
        // Mapeo de Eventos de Red a Métodos del Observador (Sin 'on')
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
    }

    private interface AccionNotificacion {
        void ejecutar(IObservadorJuego o);
    }
    
    private void notificar(AccionNotificacion accion) {
        for (IObservadorJuego o : observadores) {
            accion.ejecutar(o);
        }
    }

    // --- ENVÍO DE ACCIONES (LÓGICA -> RED) ---
    // Estos métodos ya estaban en español puro
    @Override
    public void crearPartida() {
        if (envio != null) envio.enviarDato(new AccionCrearPartida());
    }

    @Override
    public void unirseAPartida(String codigo) {
        if (envio != null) envio.enviarDato(new AccionUnirseAPartida(codigo));
    }

    @Override
    public void configurarJugador(Jugador jugador) {
        if (envio != null) envio.enviarDato(new AccionConfigurarJugador(jugador));
    }

    @Override
    public void iniciarPartida() {
        if (envio != null) envio.enviarDato(new AccionIniciarPartida());
    }

    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal, Jugador jugadorLocal) {
        if (esTurnoValido(jugadorLocal)) {
            if (envio != null) envio.enviarDato(new AccionReclamarLinea(fila, col, horizontal));
        } else {
            System.out.println("Acción bloqueada: No es tu turno.");
        }
    }

    private boolean esTurnoValido(Jugador jugadorLocal) {
        if (ultimoEstadoConocido == null || jugadorLocal == null) return false;
        int idx = ultimoEstadoConocido.jugadorActualIdx();
        if (idx < 0 || idx >= ultimoEstadoConocido.jugadores().size()) return false;
        return ultimoEstadoConocido.jugadores().get(idx).id() == jugadorLocal.id();
    }
}