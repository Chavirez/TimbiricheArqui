package vista;

import entidades.Jugador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import modelo.TableroModelo;
import entidades.JuegoConfig;

/**
 * La Vista (JPanel) que dibuja el tablero y captura los clics.
 */
public class TableroVista extends JPanel implements ISelectorPuntoUI {

    private final TableroModelo modelo;
    private Point puntoSeleccionado;

    public TableroVista(TableroModelo modelo, MouseListener controlador) {
        this.modelo = modelo;
        setBackground(Color.WHITE);
        // Se conecta el controlador en el constructor (DI)
        if (controlador != null) {
            this.addMouseListener(controlador);
            System.out.println("VISTA: Controlador de mouse registrado");
        } else {
            System.err.println("VISTA: ERROR - Controlador nulo");
        }
    }

    /**
     * Implementación del contrato ISelectorPuntoUI.
     */
    @Override
    public void setPuntoSeleccionado(Point p) {
        System.out.println("VISTA: setPuntoSeleccionado llamado con: " + p);
        this.puntoSeleccionado = p;

        SwingUtilities.invokeLater(() -> {
            this.repaint();
            this.revalidate();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("VISTA: paintComponent ejecutándose - puntoSeleccionado: " + puntoSeleccionado);

        if (modelo.getTamaño() == 0) {
            g.drawString("Conectando con el servidor...", 50, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(JuegoConfig.ANCHO_LINEA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int tamaño = modelo.getTamaño();

        // 1. Dibujar Cuadrados
        for (int i = 0; i < tamaño - 1; i++) {
            for (int j = 0; j < tamaño - 1; j++) {
                int jugadorId = modelo.getCuadrado(i, j);
                if (jugadorId != 0) {
                    g2.setColor(getColorDeJugador(jugadorId).brighter());
                    int x = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO;
                    int y = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO;
                    g2.fillRect(x + JuegoConfig.ANCHO_LINEA / 2, y + JuegoConfig.ANCHO_LINEA / 2,
                            JuegoConfig.ESPACIO - JuegoConfig.ANCHO_LINEA,
                            JuegoConfig.ESPACIO - JuegoConfig.ANCHO_LINEA);
                }
            }
        }

        // 2. Dibujar Líneas Horizontales
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño - 1; j++) {
                int jugadorId = modelo.getLineaHorizontal(i, j);
                if (jugadorId != 0) {
                    g2.setColor(getColorDeJugador(jugadorId));
                    int x1 = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO;
                    int y = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO;
                    int x2 = JuegoConfig.MARGEN + (j + 1) * JuegoConfig.ESPACIO;
                    g2.drawLine(x1, y, x2, y);
                }
            }
        }

        // 3. Dibujar Líneas Verticales
        for (int i = 0; i < tamaño - 1; i++) {
            for (int j = 0; j < tamaño; j++) {
                int jugadorId = modelo.getLineaVertical(i, j);
                if (jugadorId != 0) {
                    g2.setColor(getColorDeJugador(jugadorId));
                    int x = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO;
                    int y1 = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO;
                    int y2 = JuegoConfig.MARGEN + (i + 1) * JuegoConfig.ESPACIO;
                    g2.drawLine(x, y1, x, y2);
                }
            }
        }

        // 4. Dibujar Puntos
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño; j++) {
                Point puntoActual = new Point(i, j);
                int radio = JuegoConfig.RADIO_PUNTO;

                if (puntoSeleccionado != null && puntoActual.equals(puntoSeleccionado)) {
                    g2.setColor(Color.GREEN); // 🟢 VERDE
                    radio = radio + 100; // Tamaño aumentado
                    System.out.println("VISTA: Pintando punto VERDE en (" + i + ", " + j + ")");
                } else {
                    g2.setColor(Color.DARK_GRAY);
                }

                int x = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO - radio;
                int y = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO - radio;
                g2.fillOval(x, y, radio * 2, radio * 2);
            }
        }
    }

    private Color getColorDeJugador(int jugadorId) {
        Jugador jugador = modelo.getJugadorPorId(jugadorId);
        return (jugador != null) ? jugador.color() : Color.BLACK;
    }

    @Override
    public Dimension getPreferredSize() {
        int tam = (modelo.getTamaño() > 0 ? modelo.getTamaño() : 10);
        int size = (tam - 1) * JuegoConfig.ESPACIO + JuegoConfig.MARGEN * 2;
        return new Dimension(size, size);
    }
}