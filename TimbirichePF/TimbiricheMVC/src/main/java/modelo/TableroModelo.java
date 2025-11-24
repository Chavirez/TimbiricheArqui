package modelo;

import entidades.EstadoPartidaDTO;
import entidades.Jugador;
import modeloLogico.IServicioJuego;
import java.util.List;
import observador.Observable;

public class TableroModelo extends Observable {

    private IServicioJuego servicioJuego;
    private EstadoPartidaDTO estadoActual;

    public TableroModelo() {
        // La inicialización de la lista de observadores se hace en la clase base
        super();
    }

    public void setServicioJuego(IServicioJuego servicioJuego) {
        this.servicioJuego = servicioJuego;
    }

    public void reclamarLinea(int fila, int col, boolean horizontal) {
        if (servicioJuego != null) {
            servicioJuego.reclamarLinea(fila, col, horizontal);
        } else {
            System.err.println("[TableroModelo] Error: Servicio de juego no configurado.");
        }
    }

    public void actualizarDesdeDTO(EstadoPartidaDTO dto) {
        this.estadoActual = dto;
        System.out.println("[TableroModelo] Estado actualizado. Notificando observadores...");
        this.notificarObservadores(); // Este método es heredado
    }

    public EstadoPartidaDTO getEstadoActual() {
        return this.estadoActual;
    }

    /**
     * Registra un observador para recibir actualizaciones.
     * Utiliza el método heredado de la clase Observable.
     */
    public void registrarObservador(observador.Observador observador) {
        this.agregarObservador(observador);
        System.out.println("[TableroModelo] Observador registrado: " + observador.getClass().getSimpleName());
    }

    // --- GETTERS ---
    
    public String getCodigoSala() {
        return (estadoActual != null) ? estadoActual.codigoSala() : "---";
    }

    public int getTamaño() {
        return (estadoActual != null) ? estadoActual.tamaño() : 0;
    }

    public List<Jugador> getJugadores() {
        return (estadoActual != null) ? estadoActual.jugadores() : null;
    }

    public Jugador getJugadorActual() {
        if (estadoActual == null || estadoActual.jugadores() == null || estadoActual.jugadores().isEmpty()) {
            return null;
        }
        if (estadoActual.jugadorActualIdx() >= 0 && estadoActual.jugadorActualIdx() < estadoActual.jugadores().size()) {
            return estadoActual.jugadores().get(estadoActual.jugadorActualIdx());
        }
        return null;
    }

    public int[] getPuntajes() {
        return (estadoActual != null) ? estadoActual.puntajes() : null;
    }

    public int getCuadrado(int f, int c) {
        return (estadoActual != null && estadoActual.cuadrados() != null) ? estadoActual.cuadrados()[f][c] : 0;
    }

    public int getLineaHorizontal(int f, int c) {
        return (estadoActual != null && estadoActual.lineasHorizontales() != null) ? estadoActual.lineasHorizontales()[f][c] : 0;
    }

    public int getLineaVertical(int f, int c) {
        return (estadoActual != null && estadoActual.lineasVerticales() != null) ? estadoActual.lineasVerticales()[f][c] : 0;
    }

    public boolean isJuegoTerminado() {
        return (estadoActual != null) && estadoActual.juegoTerminado();
    }

    public Jugador getJugadorPorId(int jugadorId) {
        if (estadoActual == null || estadoActual.jugadores() == null) {
            return null;
        }
        for (Jugador j : estadoActual.jugadores()) {
            if (j.id() == jugadorId) {
                return j;
            }
        }
        return null;
    }
}