package vista;

import controlador.TableroControlador;
import modelo.TableroModelo;
import observador.Observador;
import entidades.JuegoConfig;
import entidades.Jugador;

import javax.swing.*;
import java.awt.*;

public class TableroVista extends JPanel implements Observador {

    private final TableroModelo modelo;
    private final TableroControlador controlador;

    public TableroVista(TableroModelo modelo, TableroControlador controlador) {
        this.modelo = modelo;
        this.controlador = controlador;

        this.modelo.agregarObservador(this);
        setBackground(Color.WHITE);

        // Conectar el controlador como MouseListener (Inyección de dependencias)
        if (controlador != null) {
            this.addMouseListener(controlador);
        }
    }

    @Override
    public void actualizar() {
        // Reacciona a los cambios del modelo repintando el tablero
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // Cálculo dinámico del tamaño basado en la configuración
        int tam = (modelo.getTamaño() > 0 ? modelo.getTamaño() : 10);
        int size = (tam - 1) * JuegoConfig.ESPACIO + JuegoConfig.MARGEN * 2;
        return new Dimension(size, size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Validación inicial para evitar errores de dibujado si no hay datos
        if (modelo.getTamaño() == 0) {
            g.drawString("Conectando con el servidor...", 50, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        // Configuración de calidad visual (Antialiasing)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Configuración de trazo redondeado
        g2.setStroke(new BasicStroke(JuegoConfig.ANCHO_LINEA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int tamaño = modelo.getTamaño();

        // Obtenemos las estructuras de datos del modelo actual
        int[][] cuadrados = modelo.getCuadrados();
        int[][] lineasH = modelo.getLineasHorizontales();
        int[][] lineasV = modelo.getLineasVerticales();
        Point puntoSeleccionado = modelo.getPuntoSeleccionado();

        // 1. Dibujar Cuadrados (Rellenos)
        for (int i = 0; i < tamaño - 1; i++) {
            for (int j = 0; j < tamaño - 1; j++) {
                int jugadorId = cuadrados[i][j];
                if (jugadorId > 0) {
                    g2.setColor(getColorDeJugador(jugadorId).brighter());
                    int x = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO;
                    int y = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO;

                    // Ajuste visual para que el cuadrado no tape las líneas
                    g2.fillRect(x + JuegoConfig.ANCHO_LINEA / 2,
                            y + JuegoConfig.ANCHO_LINEA / 2,
                            JuegoConfig.ESPACIO - JuegoConfig.ANCHO_LINEA,
                            JuegoConfig.ESPACIO - JuegoConfig.ANCHO_LINEA);
                }
            }
        }

        // 2. Dibujar Líneas Horizontales
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño - 1; j++) {
                int jugadorId = lineasH[i][j];
                if (jugadorId > 0) {
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
                int jugadorId = lineasV[i][j];
                if (jugadorId > 0) {
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

                // Lógica de resaltado visual del punto seleccionado
                if (puntoSeleccionado != null && puntoActual.equals(puntoSeleccionado)) {
                    g2.setColor(Color.GREEN); // 🟢 VERDE según referencia
                    radio = radio + 6;        // Tamaño aumentado (ajustado para estética)
                } else {
                    g2.setColor(Color.DARK_GRAY);
                }

                // Dibujar el punto centrado
                int x = JuegoConfig.MARGEN + j * JuegoConfig.ESPACIO - radio;
                int y = JuegoConfig.MARGEN + i * JuegoConfig.ESPACIO - radio;
                g2.fillOval(x, y, radio * 2, radio * 2);
            }
        }
    }

    // Método auxiliar para obtener el color de forma segura
    private Color getColorDeJugador(int jugadorId) {
        Jugador jugador = modelo.getJugadorPorId(jugadorId);
        return (jugador != null) ? jugador.color() : Color.BLACK;
    }
}
