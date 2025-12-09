package vista;

import controlador.AplicacionControlador;
import eventos.EventoPartidaTerminada;
import javax.swing.*;
import java.awt.*;

public class VentanaResultados extends JDialog {

    private  AplicacionControlador controladorApp;

    public VentanaResultados(JFrame parent, EventoPartidaTerminada evento, AplicacionControlador controladorApp) {
        super(parent, "Fin del Juego", true); // 'true' modal bloquea la ventana de atrás
        this.controladorApp = controladorApp;
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Panel de Mensaje Central ---
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelCentral.setBackground(Color.WHITE);

        // Mensaje Principal
        JLabel lblMensaje = new JLabel(evento.getMensaje());
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 24));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Colorear según resultado
        if (evento.getGanador() != null) {
            lblMensaje.setForeground(new Color(0, 102, 204)); // Azul victoria
        } else {
            lblMensaje.setForeground(Color.DARK_GRAY); // Empate
        }

        panelCentral.add(lblMensaje);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador

        // Mostrar Puntajes (Opcional)
        if (evento.getPuntajesFinales() != null) {
            JPanel panelPuntajes = new JPanel(new GridLayout(0, 1, 5, 5));
            panelPuntajes.setBackground(Color.WHITE);
            panelPuntajes.setBorder(BorderFactory.createTitledBorder("Puntajes Finales"));
            
            int[] puntajes = evento.getPuntajesFinales();
            for (int i = 0; i < puntajes.length; i++) {
                if (puntajes[i] > -1) { // Mostrar solo válidos
                    JLabel lblPunto = new JLabel("Jugador " + (i + 1) + ": " + puntajes[i] + " puntos");
                    lblPunto.setHorizontalAlignment(SwingConstants.CENTER);
                    panelPuntajes.add(lblPunto);
                }
            }
            panelCentral.add(panelPuntajes);
        }

        add(panelCentral, BorderLayout.CENTER);

        // --- 2. Panel de Botones Inferior ---
        JPanel panelSur = new JPanel();
        panelSur.setBackground(new Color(240, 240, 240));
        
        JButton btnCerrar = new JButton("Volver al Lobby");
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnCerrar.addActionListener(e -> {
            dispose();
            // El EnsambladorPrincipal detectará el cierre o cambio de estado,
            // pero si necesitas una acción explícita para resetear, puedes usar el controlador:
            // controladorApp.salirAlLobby(); (si existiera el método)
            
            // En tu caso actual, al cerrar el diálogo, el flujo continúa en el ensamblador
            if (parent != null) {
                parent.dispose(); // Cierra el tablero
            }
        });
        
        panelSur.add(btnCerrar);
        add(panelSur, BorderLayout.SOUTH);
    }
}