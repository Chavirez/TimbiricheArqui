package interfaces;

/**
 * Define ÚNICAMENTE las acciones que ocurren dentro del área de juego.
 */
public interface IGestorTablero {

    void reclamarLinea(int fila, int col, boolean horizontal);
}
