package vista;

import controlador.TableroControlador;
import entidades.Jugador;
import java.awt.*;
import javax.swing.*;
import modelo.TableroModelo;
import observador.Observador;

public class PanelPrincipal extends JPanel implements Observador {

    private final TableroModelo modelo;
    private final JLabel lblTurno;
    private final PanelLateral panelLateral;
    private final TableroVista tableroVista;
    private final JLabel lblCodigoSala;

    /**
     * @param modelo El modelo de datos.
     * @param controlador El TableroControlador para gestionar los clics en la vista.
     */
    public PanelPrincipal(TableroModelo modelo, TableroControlador controlador) {
        this.modelo = modelo;
        
        // Es buena práctica suscribirse al modelo aquí si este panel debe reaccionar por sí mismo
        // aunque a veces se hace desde fuera. Lo dejamos según tu snippet (sin suscripción explícita aquí)
        // asumiendo que el ensamblador o el modelo agregan este observador.
        this.modelo.agregarObservador(this); 

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(200, 200, 200));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. Panel Superior (NORTE): Turno y Código ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        // Hacemos el panel norte transparente para que se vea el fondo gris
        panelNorte.setOpaque(false); 

        lblTurno = new JLabel(" ", SwingConstants.LEFT);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 24));
        lblTurno.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panelNorte.add(lblTurno, BorderLayout.CENTER);

        lblCodigoSala = new JLabel("Código: ---");
        lblCodigoSala.setFont(new Font("Arial", Font.PLAIN, 16));
        lblCodigoSala.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        panelNorte.add(lblCodigoSala, BorderLayout.EAST);

        // --- 2. Tablero Central (CENTRO) ---
        // Se usa TableroControlador explícitamente para coincidir con el constructor de TableroVista
        this.tableroVista = new TableroVista(modelo, controlador);

        // --- 3. Panel Lateral (ESTE) ---
        this.panelLateral = new PanelLateral(modelo);

        // --- 4. Ensamblaje ---
        add(panelNorte, BorderLayout.NORTH);
        add(tableroVista, BorderLayout.CENTER);
        add(panelLateral, BorderLayout.EAST);

        // Se llama a actualizar una vez para inicializar las etiquetas
        SwingUtilities.invokeLater(this::actualizar);
    }

    public TableroVista getTableroVista() {
        return tableroVista;
    }

    public PanelLateral getPanelLateral() {
        return this.panelLateral;
    }

    @Override
    public void actualizar() {
        // Ejecutar en el hilo de eventos para la seguridad de Swing
        SwingUtilities.invokeLater(() -> {

            // 1. Actualizar Código de Sala
            // Verificamos si el modelo tiene el método getCodigoSala, si no, manejamos el caso
            String codigo = (modelo.getCodigoSala() != null) ? modelo.getCodigoSala() : "---";
            lblCodigoSala.setText("Código: " + codigo);

            if (modelo.getEstadoActual() == null) {
                lblTurno.setText("Esperando estado del servidor...");
                lblCodigoSala.setText("Código: ---");
                return;
            }

            // 2. Lógica de Turno/Estado
            if (modelo.isJuegoTerminado()) {
                lblTurno.setText("¡Fin del Juego!");
                lblCodigoSala.setText("");
                lblTurno.setForeground(Color.BLACK);
            } else {
                Jugador actual = modelo.getJugadorActual();
                if (actual != null) {
                    // Turno en curso
                    lblTurno.setText("Turno: " + actual.nombre());
                    lblTurno.setForeground(actual.color());
                } else {
                    // Sala de Espera (Partida no iniciada)
                    lblTurno.setText("Sala de Espera");
                    lblTurno.setForeground(Color.BLACK);
                }
            }

            // 3. Actualizar Subcomponentes (Vista y Lateral)
            if (panelLateral != null) panelLateral.actualizarUI();
            if (tableroVista != null) tableroVista.repaint();
        });
    }
}