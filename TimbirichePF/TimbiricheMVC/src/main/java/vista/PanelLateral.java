package vista;

import controlador.AplicacionControlador; // CAMBIO: Usamos el controlador principal
import controlador.TableroControlador;
import utilidades.Recursos;
import entidades.Jugador;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelLateral extends JPanel {

    // Controlador para datos del tablero (Puntajes, Turnos, Lista Jugadores)
    private final TableroControlador tableroControlador;

    // Controlador para acciones globales (Iniciar partida, Salir, Cambiar vistas)
    private AplicacionControlador appControlador;

    private JLabel[] labelsPuntajes;
    private JPanel panelJugadores;
    private JButton btnIniciarPartida;

    public PanelLateral(TableroControlador tableroControlador) {
        this.tableroControlador = tableroControlador;
        this.labelsPuntajes = new JLabel[0];

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 600));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        panelJugadores.setOpaque(false);
        add(panelJugadores);

        add(Box.createVerticalGlue());

        // --- Botón Iniciar Partida ---
        btnIniciarPartida = new JButton("Iniciar Partida");
        btnIniciarPartida.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciarPartida.setVisible(false);
        btnIniciarPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnIniciarPartida.addActionListener(e -> {
            System.out.println("Botón Iniciar presionado..."); // <--- DEBUG

            if (appControlador != null) {
                System.out.println("Enviando orden al controlador..."); // <--- DEBUG
                appControlador.iniciarPartida();
            } else {
                System.err.println("ERROR: appControlador es NULL en PanelLateral"); // <--- IMPORTANTE
            }
        });

        add(btnIniciarPartida);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // --- Botón Salir ---
        JButton btnSalir = new JButton("Salir");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> {
            if (appControlador != null) {
                // Podrías tener un método appControlador.salirDelJuego()
                System.exit(0);
            } else {
                System.exit(0);
            }
        });

        add(btnSalir);
    }

    /**
     * Inyecta el controlador principal de la aplicación.Se llama desde
     * VentanaJuego o AplicacionControlador al crear la vista.
     *
     * @param appControlador
     */
    public void setControladorPrincipal(AplicacionControlador appControlador) {
        this.appControlador = appControlador;
    }

    private void inicializarPanelesJugadores() {
        panelJugadores.removeAll();
        List<Jugador> jugadores = tableroControlador.getJugadores();

        if (jugadores == null || jugadores.isEmpty()) {
            JLabel lblEspera = new JLabel("Esperando jugadores...");
            lblEspera.setForeground(Color.DARK_GRAY);
            lblEspera.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelJugadores.add(lblEspera);
            this.labelsPuntajes = new JLabel[0];
            panelJugadores.revalidate();
            panelJugadores.repaint();
            return;
        }

        this.labelsPuntajes = new JLabel[jugadores.size()];
        for (int i = 0; i < jugadores.size(); i++) {
            panelJugadores.add(crearPanelJugador(jugadores.get(i), i));
            panelJugadores.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        panelJugadores.revalidate();
        panelJugadores.repaint();
    }

    private JPanel crearPanelJugador(Jugador jugador, int index) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(240, 60));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Cargar avatar
        ImageIcon icono = Recursos.loadScaledAvatar(jugador.avatarPath(), 50, 50);
        JLabel lblAvatar = new JLabel(icono);
        lblAvatar.setPreferredSize(new Dimension(50, 50));

        JLabel lblNombre = new JLabel(jugador.nombre());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        lblNombre.setForeground(jugador.color());

        labelsPuntajes[index] = new JLabel("0");
        labelsPuntajes[index].setFont(new Font("Arial", Font.BOLD, 20));
        labelsPuntajes[index].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        panel.add(lblAvatar, BorderLayout.WEST);
        panel.add(lblNombre, BorderLayout.CENTER);
        panel.add(labelsPuntajes[index], BorderLayout.EAST);

        // Hacemos el panel semitransparente o transparente según diseño
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Método llamado por VentanaJuego cuando el Modelo notifica cambios.
     */
    public void actualizarUI() {
        Jugador jugadorActualTurno = tableroControlador.getJugadorActual();
        List<Jugador> jugadores = tableroControlador.getJugadores();
        int jugadoresSize = (jugadores != null) ? jugadores.size() : 0;

        // 1. Reconstruir lista si cambia la cantidad de jugadores
        if (this.labelsPuntajes.length != jugadoresSize
                || (jugadorActualTurno == null && jugadoresSize > 0 && this.labelsPuntajes.length == 0)) {
            inicializarPanelesJugadores();
        }

        // 2. Actualizar puntajes numéricos
        int[] puntajes = tableroControlador.getPuntajes();
        if (puntajes != null) {
            int limite = Math.min(puntajes.length, labelsPuntajes.length);
            for (int i = 0; i < limite; i++) {
                if (labelsPuntajes[i] != null) {
                    labelsPuntajes[i].setText(String.valueOf(puntajes[i]));
                }
            }
        }

        if (jugadorActualTurno == null && !tableroControlador.isJuegoTerminado() && jugadoresSize >= 1) {
            btnIniciarPartida.setVisible(true);
        } else {
            btnIniciarPartida.setVisible(false);
        }

        repaint();
    }
}
