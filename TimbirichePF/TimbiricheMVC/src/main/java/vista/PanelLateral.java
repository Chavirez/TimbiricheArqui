package vista;

import controlador.AplicacionControlador;
import controlador.TableroControlador;
import utilidades.Recursos;
import entidades.Jugador;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelLateral extends JPanel {

    private final TableroControlador tableroControlador;
    private AplicacionControlador appControlador;

    private JLabel[] labelsPuntajes;
    private JPanel panelJugadores;
    
    // Declaración del botón
    private JButton btnVotarInicio;

    public PanelLateral(TableroControlador tableroControlador) {
        this.tableroControlador = tableroControlador;
        this.labelsPuntajes = new JLabel[0];

        // Configuración básica del panel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 600));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de jugadores
        panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        panelJugadores.setOpaque(false);
        add(panelJugadores);

        // Espacio elástico
        add(Box.createVerticalGlue());

   
        btnVotarInicio = new JButton("Estoy Listo");
       
        
        btnVotarInicio.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVotarInicio.setVisible(false); 
        btnVotarInicio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVotarInicio.setFont(new Font("Arial", Font.BOLD, 14));
        btnVotarInicio.setBackground(new Color(34, 139, 34)); // Verde Bosque
        btnVotarInicio.setForeground(Color.WHITE);

        // Acción del botón (Aquí tronaba antes porque el botón era null)
        btnVotarInicio.addActionListener(e -> {
            
            if (appControlador != null) {
                // Feedback visual
                btnVotarInicio.setEnabled(false);
                btnVotarInicio.setText("Esperando...");
                // Llamada al controlador
                appControlador.votarIniciar();
            } else {
                
            }
        });

        add(btnVotarInicio);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón Salir
        JButton btnSalir = new JButton("Salir");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> {
            System.exit(0);
        });

        add(btnSalir);
    }

    public void setControladorPrincipal(AplicacionControlador appControlador) {
        this.appControlador = appControlador;
    }

    private void inicializarPanelesJugadores() {
        panelJugadores.removeAll();
        List<Jugador> jugadores = tableroControlador.getJugadores();

        if (jugadores == null || jugadores.isEmpty()) {
            JLabel lblEspera = new JLabel("Esperando jugadores...");
            lblEspera.setForeground(Color.WHITE); // Ajustado a blanco para ver mejor
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

        panel.setOpaque(false);
        return panel;
    }

    public void actualizarUI() {
        Jugador jugadorActualTurno = tableroControlador.getJugadorActual();
        List<Jugador> jugadores = tableroControlador.getJugadores();
        int jugadoresSize = (jugadores != null) ? jugadores.size() : 0;

        // 1. Reconstruir lista si cambia la cantidad
        if (this.labelsPuntajes.length != jugadoresSize
                || (jugadorActualTurno == null && jugadoresSize > 0 && this.labelsPuntajes.length == 0)) {
            inicializarPanelesJugadores();
        }

        // 2. Actualizar puntajes
        int[] puntajes = tableroControlador.getPuntajes();
        if (puntajes != null) {
            int limite = Math.min(puntajes.length, labelsPuntajes.length);
            for (int i = 0; i < limite; i++) {
                if (labelsPuntajes[i] != null) {
                    labelsPuntajes[i].setText(String.valueOf(puntajes[i]));
                }
            }
        }

        // 3. Lógica del Botón
        if (jugadorActualTurno == null && !tableroControlador.isJuegoTerminado() && jugadoresSize >= 2) {
            btnVotarInicio.setVisible(true);

            if (!btnVotarInicio.getText().equals("Esperando...")) {
                btnVotarInicio.setText("Estoy Listo");
                btnVotarInicio.setEnabled(true);
            }
        } else {
            btnVotarInicio.setVisible(false);
        }

        repaint();
    }
}