package vista;

import java.awt.Point;

/**
 * * Define la interfaz mínima que el Controlador necesita para actualizar el
 * estado visual temporal de la Vista (ej. el resaltado de un punto).
 */
public interface ISelectorPuntoUI {

    /**
     * Establece el punto que la Vista debe dibujar resaltado.
     *
     * @param p El punto a resaltar, o null para limpiar la selección.
     */
    void setPuntoSeleccionado(Point p);
}
