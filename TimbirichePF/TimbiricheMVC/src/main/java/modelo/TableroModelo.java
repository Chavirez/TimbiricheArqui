package modelo;

import entidades.EstadoPartidaDTO;
import entidades.Jugador;
import observador.Observable;
import java.awt.Point;
import java.util.List;
import java.util.Objects;

public class TableroModelo extends Observable {

    private EstadoPartidaDTO estadoActual;
    private Point puntoSeleccionado;

    public void actualizarEstado(EstadoPartidaDTO dto) {
        this.estadoActual = dto;
        this.puntoSeleccionado = null;
        this.notificarObservadores();
    }

    public void setPuntoSeleccionado(Point punto) {
        if (!Objects.equals(this.puntoSeleccionado, punto)) {
            this.puntoSeleccionado = punto;
            this.notificarObservadores();
        }
    }

    public Point getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    // --- Getters Delegados del DTO ---
    public EstadoPartidaDTO getEstadoActual() {
        return estadoActual;
    }

    public String getCodigoSala() {
        return (estadoActual != null) ? estadoActual.codigoSala() : "---";
    }

    public int getTamaño() {
        return (estadoActual != null) ? estadoActual.tamaño() : 0;
    }

    public List<Jugador> getJugadores() {
        return (estadoActual != null) ? estadoActual.jugadores() : null;
    }

    public Jugador getJugadorPorId(int id) {
        if (estadoActual == null || estadoActual.jugadores() == null) return null;
        for (Jugador j : estadoActual.jugadores()) {
            if (j.id() == id) return j;
        }
        return null;
    }

    public Jugador getJugadorActual() {
        if (estadoActual == null || estadoActual.jugadores() == null) return null;
        int idx = estadoActual.jugadorActualIdx();
        return (idx >= 0 && idx < estadoActual.jugadores().size()) ? 
               estadoActual.jugadores().get(idx) : null;
    }

    public int[] getPuntajes() {
        return (estadoActual != null) ? estadoActual.puntajes() : null;
    }

    public int getCuadrado(int f, int c) {
        return (estadoActual != null) ? estadoActual.cuadrados()[f][c] : 0;
    }

    public int[][] getLineasHorizontales() {
        return (estadoActual != null) ? estadoActual.lineasHorizontales() : new int[0][0];
    }

    public int[][] getLineasVerticales() {
        return (estadoActual != null) ? estadoActual.lineasVerticales() : new int[0][0];
    }
    
    public int[][] getCuadrados() {
        return (estadoActual != null) ? estadoActual.cuadrados() : new int[0][0];
    }

    public boolean isJuegoTerminado() {
        return (estadoActual != null) && estadoActual.juegoTerminado();
    }
}