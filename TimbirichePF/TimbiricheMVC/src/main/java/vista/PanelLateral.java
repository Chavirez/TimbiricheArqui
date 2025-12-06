package vista;

import controlador.TableroControlador;
import utilidades.Recursos;
import entidades.Jugador;
import interfaces.IServicioJuego;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelLateral extends JPanel {

    private final TableroControlador controlador;
    private JLabel[] labelsPuntajes;
    private JPanel panelJugadores;

    private JButton btnIniciarPartida;
    private IServicioJuego servicioJuego; // Referencia al gestor

    public PanelLateral(TableroControlador controlador) {
        this.controlador = controlador;
        this.labelsPuntajes = new JLabel[0];

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 600));
        setOpaque(false); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        panelJugadores.setOpaque(false); // <--- IMPORTANTE
        add(panelJugadores);

        add(Box.createVerticalGlue());

        btnIniciarPartida = new JButton("Iniciar Partida");
        btnIniciarPartida.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciarPartida.setVisible(false);
        btnIniciarPartida.addActionListener(e -> {
            if (servicioJuego != null) {
                servicioJuego.iniciarPartida();
            }
        });

        add(btnIniciarPartida);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnSalir = new JButton("Salir");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        add(btnSalir);
    }
    
    public void setServicioJuego(IServicioJuego servicioJuego) {
        this.servicioJuego = servicioJuego;
    }


    private void inicializarPanelesJugadores() {
        panelJugadores.removeAll();
        List<Jugador> jugadores = controlador.getJugadores();

        if (jugadores == null || jugadores.isEmpty()) {
            panelJugadores.add(new JLabel("Esperando jugadores..."));
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

        // Cargar avatar (asegúrate de que Recursos.loadScaledAvatar maneje rutas nulas si es necesario)
        JLabel lblAvatar = new JLabel(Recursos.loadScaledAvatar(jugador.avatarPath(), 50, 50));
        lblAvatar.setPreferredSize(new Dimension(50, 50));

        JLabel lblNombre = new JLabel(jugador.nombre());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        lblNombre.setForeground(jugador.color()); // Usar el color del jugador

        labelsPuntajes[index] = new JLabel("0");
        labelsPuntajes[index].setFont(new Font("Arial", Font.BOLD, 20));
        labelsPuntajes[index].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        panel.add(lblAvatar, BorderLayout.WEST);
        panel.add(lblNombre, BorderLayout.CENTER);
        panel.add(labelsPuntajes[index], BorderLayout.EAST);
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Método llamado por PanelPrincipal (o el Observador) al actualizarse el Modelo.
     */
    public void actualizarUI() {
        Jugador jugadorActualDelModelo = controlador.getJugadorActual();
        List<Jugador> jugadores = controlador.getJugadores();
        int jugadoresSize = (jugadores != null) ? jugadores.size() : 0;
        
        // Si cambió la cantidad de jugadores o es la primera carga con datos
        if (this.labelsPuntajes.length != jugadoresSize
                || (jugadorActualDelModelo == null && jugadoresSize > 0 && this.labelsPuntajes.length == 0)) {
            inicializarPanelesJugadores();
        }

        // Actualizar los textos de los puntajes
        int[] puntajes = controlador.getPuntajes();
        if (puntajes != null) {
            int limite = Math.min(puntajes.length, labelsPuntajes.length);
            for (int i = 0; i < limite; i++) {
                if (labelsPuntajes[i] != null) {
                    labelsPuntajes[i].setText(String.valueOf(puntajes[i]));
                }
            }
        }
        
        // Lógica para mostrar el botón de inicio:
        // Solo si la partida no ha iniciado (jugadorActual es null), no ha terminado, y hay al menos un jugador.
        // Nota: Ajusta 'jugadoresSize >= 2' si requieres mínimo 2 jugadores.
        if (jugadorActualDelModelo == null && !controlador.isJuegoTerminado() && jugadoresSize >= 1) {
            btnIniciarPartida.setVisible(true);
        } else {
            btnIniciarPartida.setVisible(false);
        }

        repaint();
    }
}