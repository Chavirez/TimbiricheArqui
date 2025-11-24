package entidades;

import java.io.Serializable;
import java.util.List;

public record EstadoPartidaDTO(
    String codigoSala,
    int tamaño,
    int[][] lineasHorizontales,
    int[][] lineasVerticales,
    int[][] cuadrados,
    int jugadorActualIdx,
    List<Jugador> jugadores,
    int[] puntajes,
    int totalCuadrados,
    int cuadradosCompletados,
    boolean juegoTerminado
) implements Serializable {
}