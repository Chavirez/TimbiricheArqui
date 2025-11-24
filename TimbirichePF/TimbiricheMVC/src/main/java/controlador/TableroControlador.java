package controlador;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.TableroModelo;
import observador.Observador;
import entidades.JuegoConfig;
import vista.ISelectorPuntoUI;

/**
 * El Controlador (de MVC) para el tablero.
 */
public class TableroControlador extends MouseAdapter implements Observador {

    private final TableroModelo modelo;
    private final ISelectorPuntoUI selectorUI;

    private Point primerPunto = null;
    private boolean finJuegoMostrado = false;

    /**
     * Recibe el Modelo y el Selector de UI.
     */
    public TableroControlador(TableroModelo modelo, ISelectorPuntoUI selectorUI) {
        this.modelo = modelo;
        this.selectorUI = selectorUI;
        this.modelo.registrarObservador(this);
        System.out.println("CONTROLADOR: Controlador creado y registrado como observador");
    }

    public Point convertirClickAPunto(int x, int y) {
        int tamaño = modelo.getTamaño();
        if (tamaño == 0) {
            return null;
        }

        int fila = Math.round((float) (y - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);
        int col = Math.round((float) (x - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);

        if (fila < 0 || col < 0 || fila >= tamaño || col >= tamaño) {
            return null;
        }
        return new Point(fila, col);
    }

    public boolean sonAdyacentes(Point p1, Point p2) {
        boolean esHorizontal = (Math.abs(p1.y - p2.y) == 1 && p1.x == p2.x);
        boolean esVertical = (Math.abs(p1.x - p2.x) == 1 && p1.y == p2.y);

        return esHorizontal || esVertical;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("CONTROLADOR: Mouse click en (" + e.getX() + ", " + e.getY() + ")");
        
        if (modelo.isJuegoTerminado() || modelo.getJugadorActual() == null) {
            System.out.println("CONTROLADOR: Juego terminado o jugador actual nulo - ignorando clic");
            return;
        }

        Point puntoClic = convertirClickAPunto(e.getX(), e.getY());
        System.out.println("CONTROLADOR: Punto convertido: " + puntoClic);

        if (puntoClic == null) {
            primerPunto = null;
            selectorUI.setPuntoSeleccionado(null);
            System.out.println("CONTROLADOR: Punto nulo - limpiando selección");
            return;
        }

        if (primerPunto == null) {
            // Primer clic
            primerPunto = puntoClic;
            System.out.println("CONTROLADOR: Primer punto seleccionado: " + primerPunto);
            selectorUI.setPuntoSeleccionado(primerPunto);

        } else {
            // Segundo clic
            System.out.println("CONTROLADOR: Segundo punto seleccionado: " + puntoClic);
            if (!primerPunto.equals(puntoClic) && sonAdyacentes(primerPunto, puntoClic)) {
                System.out.println("CONTROLADOR: Puntos adyacentes - reportando línea");
                reportLineAttempt(primerPunto, puntoClic);
            } else {
                System.out.println("CONTROLADOR: Puntos NO adyacentes o iguales - ignorando");
            }
            // Limpiar la selección después del segundo clic
            primerPunto = null;
            selectorUI.setPuntoSeleccionado(null);
            System.out.println("CONTROLADOR: Selección limpiada");
        }
    }

    private void reportLineAttempt(Point p1, Point p2) {
        boolean horizontal;
        int fila, col;

        if (p1.x == p2.x) {
            horizontal = true;
            fila = p1.x;
            col = Math.min(p1.y, p2.y);
        } else {
            horizontal = false;
            fila = Math.min(p1.x, p2.x);
            col = p1.y;
        }

        System.out.println("CONTROLADOR: Reclamando línea - fila: " + fila + ", col: " + col + ", horizontal: " + horizontal);
        modelo.reclamarLinea(fila, col, horizontal);
    }

    @Override
    public void actualizar() {
        System.out.println("CONTROLADOR: Recibida actualización del modelo");
        
        if (modelo.isJuegoTerminado() && !finJuegoMostrado) {
            finJuegoMostrado = true;
            String mensaje = "¡El juego ha terminado!";

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, mensaje, "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
}