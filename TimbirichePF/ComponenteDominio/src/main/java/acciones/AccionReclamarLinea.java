package acciones;

import java.io.Serializable;

/**
 * DTO que el cliente envía al servidor cada vez que intenta reclamar una línea.
 */
public class AccionReclamarLinea implements Serializable {

    private final int fila;
    private final int columna;
    private final boolean horizontal;

    public AccionReclamarLinea(int fila, int columna, boolean horizontal) {
        this.fila = fila;
        this.columna = columna;
        this.horizontal = horizontal;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public boolean isHorizontal() {
        return horizontal;
    }
}
