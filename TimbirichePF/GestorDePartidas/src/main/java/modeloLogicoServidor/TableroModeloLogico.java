package modeloLogicoServidor;

import entidades.Jugador;
import java.util.List;
import java.util.Objects;

public class TableroModeloLogico {

    private final int tamaño;
    private final int[][] lineasHorizontales;
    private final int[][] lineasVerticales;
    private final int[][] cuadrados;
    private int jugadorActualIdx = -1;
    private final List<Jugador> jugadores;
    private final int[] puntajes;
    private final int totalCuadrados;
    private int cuadradosCompletados = 0;

    public TableroModeloLogico(int tamaño, List<Jugador> jugadores) {
        this.tamaño = tamaño;
        this.jugadores = Objects.requireNonNull(jugadores);
        this.puntajes = new int[4];
        this.totalCuadrados = (tamaño - 1) * (tamaño - 1);

        lineasHorizontales = new int[tamaño][tamaño - 1];
        lineasVerticales = new int[tamaño - 1][tamaño];
        cuadrados = new int[tamaño - 1][tamaño - 1];
    }

    // --- Getters ---
    public int getTamaño() {
        return tamaño;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public Jugador getJugadorActual() {
        if (jugadores.isEmpty() || jugadorActualIdx == -1) {
            return null; // CORREGIDO
        }
        return jugadores.get(jugadorActualIdx);
    }

    public int getJugadorActualIdx() {
        return jugadorActualIdx;
    }

    public int[] getPuntajes() {
        return puntajes.clone();
    }

    public int[][] getCuadrados() {
        return cuadrados.clone();
    }

    public int[][] getLineasHorizontales() {
        return lineasHorizontales.clone();
    }

    public int[][] getLineasVerticales() {
        return lineasVerticales.clone();
    }

    public int getTotalCuadrados() {
        return totalCuadrados;
    }

    public int getCuadradosCompletados() {
        return cuadradosCompletados;
    }

    public boolean isJuegoTerminado() {
        return cuadradosCompletados == totalCuadrados && totalCuadrados > 0;
    }

    public void iniciarTurno() {
        if (!jugadores.isEmpty()) {
            this.jugadorActualIdx = 0; // El primer jugador (Host) empieza
        }
    }

    public boolean agregarLinea(int fila, int col, boolean horizontal, int jugadorId) {
        if (horizontal) {
            if (lineasHorizontales[fila][col] != 0) {
                return false;
            }
            lineasHorizontales[fila][col] = jugadorId;
        } else {
            if (lineasVerticales[fila][col] != 0) {
                return false;
            }
            lineasVerticales[fila][col] = jugadorId;
        }
        return true;
    }

    public void siguienteTurno() {
        if (!jugadores.isEmpty()) {
            jugadorActualIdx = (jugadorActualIdx + 1) % jugadores.size();
        }
    }

    public boolean verificarCuadrados(int jugadorId) {
        boolean seCompletoUnCuadrado = false;
        int indicePuntaje = jugadorId - 1;
        if (indicePuntaje < 0 || indicePuntaje >= puntajes.length) {
            System.err.println("Error: ID de jugador inválido " + jugadorId);
            return false;
        }

        for (int i = 0; i < tamaño - 1; i++) {
            for (int j = 0; j < tamaño - 1; j++) {
                if (cuadrados[i][j] == 0
                        && lineasHorizontales[i][j] != 0 && lineasHorizontales[i + 1][j] != 0
                        && lineasVerticales[i][j] != 0 && lineasVerticales[i][j + 1] != 0) {
                    cuadrados[i][j] = jugadorId;
                    puntajes[indicePuntaje]++;
                    cuadradosCompletados++;
                    seCompletoUnCuadrado = true;
                }
            }
        }
        return seCompletoUnCuadrado;
    }

    /**
     * Calcula quién es el ganador basado en los puntajes actuales. Retorna null
     * si hay un empate o si el juego no ha terminado (aunque se recomienda
     * llamar solo al final).
     *
     * @return
     */
    public Jugador obtenerGanador() {
        Jugador ganador = null;
        int maxPuntaje = -1;
        boolean empate = false;
        for (Jugador j : jugadores) {
            int idx = j.id() - 1;
            if (idx < 0 || idx >= puntajes.length) {
                continue;
            }
            int puntajeActual = puntajes[idx];
            if (puntajeActual > maxPuntaje) {
                maxPuntaje = puntajeActual;
                ganador = j;
                empate = false;
            } else if (puntajeActual == maxPuntaje) {
                empate = true;
            }
        }
        if (empate) {
            return null;
        }
        return ganador;
    }
}
