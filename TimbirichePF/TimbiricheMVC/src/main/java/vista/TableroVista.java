package vista;

import controlador.TableroControlador;
import modelo.TableroModelo;
import observador.Observador;
import entidades.JuegoConfig;
import entidades.Jugador;

import javax.swing.*;
import java.awt.*;

public class TableroVista extends JPanel implements Observador {

    private final TableroControlador controlador;
    
    // Variables para almacenar las métricas dinámicas actuales
    private double espacioActual;
    private int inicioX;
    private int inicioY;

    public TableroVista(TableroControlador controlador) {
        this.controlador = controlador;

        controlador.suscribirTableroVista(this);
        setBackground(Color.WHITE);

        if (controlador != null) {
            this.addMouseListener(controlador);
        }
    }

    @Override
    public void actualizar() {
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // Devolvemos un tamaño predeterminado fijo, 
        // el tablero se estirará o encogerá según el Layout de la ventana.
        return new Dimension(600, 600); 
    }

    // Métodos para que el Controlador sepa cómo se dibujó el tablero
    public double getEspacioActual() {
        return espacioActual;
    }

    public int getInicioX() {
        return inicioX;
    }

    public int getInicioY() {
        return inicioY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (controlador.getTamanio() == 0) {
            g.drawString("Conectando con el servidor...", 50, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // --- CÁLCULO DINÁMICO DE DIMENSIONES ---
        int n = controlador.getTamanio(); // Número de puntos por lado (ej. 10)
        
        // Obtenemos el lado más pequeño del panel para mantener el tablero cuadrado
        int ladoPanel = Math.min(getWidth(), getHeight());
        
        // Calculamos el espacio disponible restando los márgenes
        int espacioDisponible = ladoPanel - (JuegoConfig.MARGEN * 2);
        
        // El espacio entre puntos es el espacio disponible dividido entre (n-1) huecos
        // Usamos double para mayor precisión en el dibujo
        if (n > 1) {
            this.espacioActual = (double) espacioDisponible / (n - 1);
        } else {
            this.espacioActual = 0;
        }

        // Centramos el tablero en el panel
        this.inicioX = (getWidth() - espacioDisponible) / 2;
        this.inicioY = (getHeight() - espacioDisponible) / 2;

        // Escalamos también el ancho de línea y radio de punto proporcionalmente (opcional)
        // Para este ejemplo, mantendremos el ancho de línea fijo pero podrías escalarlo.
        g2.setStroke(new BasicStroke(JuegoConfig.ANCHO_LINEA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // --- DIBUJADO USANDO MÉTRICAS DINÁMICAS ---
        int[][] cuadrados = controlador.getCuadrados();
        int[][] lineasH = controlador.getLineasHorizontales();
        int[][] lineasV = controlador.getLineasVerticales();
        Point puntoSeleccionado = controlador.getPuntoSeleccionado();

        // 1. Dibujar Cuadrados (Rellenos)
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                int jugadorId = cuadrados[i][j];
                if (jugadorId > 0) {
                    g2.setColor(getColorDeJugador(jugadorId).brighter());
                    
                    // Coordenadas dinámicas
                    int x = (int) (inicioX + j * espacioActual);
                    int y = (int) (inicioY + i * espacioActual);
                    int tamañoCelda = (int) Math.ceil(espacioActual); // Asegurar que rellene bien

                    // Ajuste visual
                    g2.fillRect(x + JuegoConfig.ANCHO_LINEA / 2,
                            y + JuegoConfig.ANCHO_LINEA / 2,
                            tamañoCelda - JuegoConfig.ANCHO_LINEA,
                            tamañoCelda - JuegoConfig.ANCHO_LINEA);
                }
            }
        }

        // 2. Dibujar Líneas Horizontales
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                int jugadorId = lineasH[i][j];
                if (jugadorId > 0) {
                    g2.setColor(getColorDeJugador(jugadorId));
                    int x1 = (int) (inicioX + j * espacioActual);
                    int y = (int) (inicioY + i * espacioActual);
                    int x2 = (int) (inicioX + (j + 1) * espacioActual);
                    g2.drawLine(x1, y, x2, y);
                }
            }
        }

        // 3. Dibujar Líneas Verticales
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n; j++) {
                int jugadorId = lineasV[i][j];
                if (jugadorId > 0) {
                    g2.setColor(getColorDeJugador(jugadorId));
                    int x = (int) (inicioX + j * espacioActual);
                    int y1 = (int) (inicioY + i * espacioActual);
                    int y2 = (int) (inicioY + (i + 1) * espacioActual);
                    g2.drawLine(x, y1, x, y2);
                }
            }
        }

        // 4. Dibujar Puntos
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Point puntoActual = new Point(i, j);
                int radio = JuegoConfig.RADIO_PUNTO;

                if (puntoSeleccionado != null && puntoActual.equals(puntoSeleccionado)) {
                    g2.setColor(Color.GREEN);
                    radio = radio + 4;
                } else {
                    g2.setColor(Color.DARK_GRAY);
                }

                int x = (int) (inicioX + j * espacioActual) - radio;
                int y = (int) (inicioY + i * espacioActual) - radio;
                g2.fillOval(x, y, radio * 2, radio * 2);
            }
        }
    }

    private Color getColorDeJugador(int jugadorId) {
        Jugador jugador = controlador.getJugadorPorId(jugadorId);
        return (jugador != null) ? jugador.color() : Color.BLACK;
    }
}