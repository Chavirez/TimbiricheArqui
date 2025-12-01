package controlador;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.TableroModelo;
import observador.Observador;
import entidades.JuegoConfig;
import itson.modelojuego.interfaces.IServicioJuego;
import vista.ISelectorPuntoUI;

public class TableroControlador extends MouseAdapter implements Observador {

    private final TableroModelo modelo;
    private final ISelectorPuntoUI selectorUI;
    private final IServicioJuego servicioJuego;
    private Point primerPunto = null;
    private boolean finJuegoMostrado = false;

    public TableroControlador(TableroModelo modelo, ISelectorPuntoUI selectorUI, IServicioJuego servicioJuego) {
        this.modelo = modelo;
        this.selectorUI = selectorUI;
        this.servicioJuego = servicioJuego;
        this.modelo.agregarObservador(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (modelo.isJuegoTerminado() || modelo.getJugadorActual() == null) {
            return;
        }
        Point puntoClic = convertirClickAPunto(e.getX(), e.getY()); // (Asumir método existente)
        if (puntoClic == null) {
            limpiarSeleccion();
            return;
        }

        if (primerPunto == null) {
            primerPunto = puntoClic;
            selectorUI.setPuntoSeleccionado(primerPunto);
        } else {
            if (!primerPunto.equals(puntoClic) && sonAdyacentes(primerPunto, puntoClic)) { // (Asumir método existente)
                reportarIntentoLinea(primerPunto, puntoClic);
            }
            limpiarSeleccion();
        }
    }

    private void reportarIntentoLinea(Point p1, Point p2) {
        boolean horizontal = (p1.x == p2.x);
        int fila = horizontal ? p1.x : Math.min(p1.x, p2.x);
        int col = horizontal ? Math.min(p1.y, p2.y) : p1.y;
        servicioJuego.reclamarLinea(fila, col, horizontal);
    }

    private void limpiarSeleccion() {
        primerPunto = null;
        selectorUI.setPuntoSeleccionado(null);
    }

    @Override
    public void actualizar() {
        if (modelo.isJuegoTerminado() && !finJuegoMostrado) {
            finJuegoMostrado = true;
            SwingUtilities.invokeLater(()
                    -> JOptionPane.showMessageDialog(null, "¡El juego ha terminado!", "Fin", JOptionPane.INFORMATION_MESSAGE)
            );
        }
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

    private boolean sonAdyacentes(Point p1, Point p2) {
        return (Math.abs(p1.y - p2.y) == 1 && p1.x == p2.x) || (Math.abs(p1.x - p2.x) == 1 && p1.y == p2.y);
    }
}
