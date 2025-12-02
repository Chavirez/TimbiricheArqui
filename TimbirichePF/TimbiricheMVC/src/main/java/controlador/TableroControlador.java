package controlador;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelo.TableroModelo;
import entidades.JuegoConfig;
import interfaces.IGestorTablero;

/**
 * Controlador del Tablero.
 * - Implementa IGestorTablero para definir la acción de juego.
 * - Recibe los eventos del mouse y los traduce a lógica de negocio.
 */
public class TableroControlador extends MouseAdapter implements IGestorTablero {

    private final TableroModelo modelo;
    private final IGestorTablero backend; // El modelo de la app o gestor de red
    private Point primerPuntoCache = null;

    /**
     * Constructor Ciego (Sin Vista).
     * @param modelo El estado del tablero (para cálculos y selección).
     * @param backend Quien procesa la jugada final (AplicacionModelo).
     */
    public TableroControlador(TableroModelo modelo, IGestorTablero backend) {
        this.modelo = modelo;
        this.backend = backend;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (modelo.isJuegoTerminado() || modelo.getJugadorActual() == null) return;
        
        Point puntoClic = convertirClickAPunto(e.getX(), e.getY());
        if (puntoClic == null) {
            limpiarSeleccion();
            return;
        }

        if (primerPuntoCache == null) {
            // Fase 1: Selección visual (Actualiza modelo, vista reacciona)
            primerPuntoCache = puntoClic;
            modelo.setPuntoSeleccionado(primerPuntoCache);
        } else {
            // Fase 2: Intentar jugada
            if (!primerPuntoCache.equals(puntoClic) && sonAdyacentes(primerPuntoCache, puntoClic)) {
                reportarIntentoLinea(primerPuntoCache, puntoClic);
            }
            limpiarSeleccion();
        }
    }

    private void reportarIntentoLinea(Point p1, Point p2) {
        boolean horizontal = (p1.x == p2.x);
        int fila = horizontal ? p1.x : Math.min(p1.x, p2.x);
        int col = horizontal ? Math.min(p1.y, p2.y) : p1.y;
        
        // Usamos nuestra propia implementación de la interfaz para enviar la jugada
        this.reclamarLinea(fila, col, horizontal);
    }

    // --- Implementación de IGestorTablero ---
    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal) {
        // Delegamos al backend (AplicacionModelo) que tiene la conexión de red
        backend.reclamarLinea(fila, col, horizontal);
    }

    private void limpiarSeleccion() {
        primerPuntoCache = null;
        modelo.setPuntoSeleccionado(null);
    }
    
    // --- Lógica Auxiliar ---
    private Point convertirClickAPunto(int x, int y) {
        int tamaño = modelo.getTamaño();
        if (tamaño == 0) return null;
        int fila = Math.round((float) (y - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);
        int col = Math.round((float) (x - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);
        if (fila < 0 || col < 0 || fila >= tamaño || col >= tamaño) return null;
        return new Point(fila, col);
    }

    private boolean sonAdyacentes(Point p1, Point p2) {
        return (Math.abs(p1.y - p2.y) == 1 && p1.x == p2.x) || (Math.abs(p1.x - p2.x) == 1 && p1.y == p2.y);
    }
}