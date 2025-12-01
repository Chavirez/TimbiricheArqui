package modelo;

import entidades.EstadoPartidaDTO;
import entidades.Jugador;
import java.util.List;
import observador.Observable;

public class TableroModelo extends Observable {

    private EstadoPartidaDTO estadoActual;

    public void actualizarEstado(EstadoPartidaDTO dto) {
        this.estadoActual = dto;
        this.notificarObservadores();
    }

    // --- Getters ---
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

    public entidades.Jugador getJugadorPorId(int id) {
        if (estadoActual == null || estadoActual.jugadores() == null) {
            return null;
        }
        for (entidades.Jugador j : estadoActual.jugadores()) {
            if (j.id() == id) {
                return j;
            }
        }
        return null;
    }

    public Jugador getJugadorActual() {
        if (estadoActual == null || estadoActual.jugadores() == null) {
            return null;
        }
        int idx = estadoActual.jugadorActualIdx();
        return (idx >= 0 && idx < estadoActual.jugadores().size()) ? estadoActual.jugadores().get(idx) : null;
    }

    public int[] getPuntajes() {
        return (estadoActual != null) ? estadoActual.puntajes() : null;
    }

    public int getCuadrado(int f, int c) {
        return (estadoActual != null) ? estadoActual.cuadrados()[f][c] : 0;
    }

    public int getLineaHorizontal(int f, int c) {
        return (estadoActual != null) ? estadoActual.lineasHorizontales()[f][c] : 0;
    }

    public int getLineaVertical(int f, int c) {
        return (estadoActual != null) ? estadoActual.lineasVerticales()[f][c] : 0;
    }

    public boolean isJuegoTerminado() {
        return (estadoActual != null) && estadoActual.juegoTerminado();
    }
}
