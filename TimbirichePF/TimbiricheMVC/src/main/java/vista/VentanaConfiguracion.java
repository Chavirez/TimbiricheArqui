package vista;

import utilidades.Recursos;
import entidades.Jugador;
import interfaces.IServicioJuego;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Diálogo modal para configurar los detalles de un jugador.
 */
public class VentanaConfiguracion extends JDialog {

    private final PanelConfiguracionJugador panelConfigUI;
    private final ConfiguracionJugador configMutable; // POJO local
    private IServicioJuego servicioJuego;
    private boolean configurado = false; // Este campo ahora es manejado por GestorCliente

    public VentanaConfiguracion(Frame owner, IServicioJuego servicioJuego) {
        super(owner, "Configurar Jugador", true); // Modal
        this.servicioJuego = servicioJuego;

        // Carga valores por defecto
        List<String> avatares = Recursos.getAvataresDisponibles();
        List<Color> colores = Recursos.getColoresDisponibles();
        this.configMutable = new ConfiguracionJugador(
                "Jugador",
                avatares.isEmpty() ? "" : avatares.get(0),
                colores.isEmpty() ? Color.BLACK : colores.get(0)
        );

        setLayout(new BorderLayout(10, 10));
        panelConfigUI = new PanelConfiguracionJugador("Mi Perfil", configMutable, (JFrame) owner);

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> confirmarConfiguracion());

        add(new JScrollPane(panelConfigUI), BorderLayout.CENTER);
        add(btnConfirmar, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private void confirmarConfiguracion() {
        ConfiguracionJugador configFinal = panelConfigUI.getConfig();
        try {
            // 1. Validación LOCAL (solo campos vacíos)
            validarConfiguracion(configFinal);

            // 2. Crea el Jugador DTO inmutable (record) y lo envía
            Jugador jugador = new Jugador(
                    0, // ID 0, el servidor asignará el real
                    configFinal.getNombre(),
                    configFinal.getAvatarPath(),
                    configFinal.getColor()
            );

            // 3. Envía la solicitud al servidor para validación de duplicados
            servicioJuego.enviarConfiguracionJugador(jugador);


        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Configuración", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarConfiguracion(ConfiguracionJugador c) {
        if (c.getNombre() == null || c.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (c.getAvatarPath() == null || c.getAvatarPath().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un avatar.");
        }
        if (c.getColor() == null) {
            throw new IllegalArgumentException("Debe seleccionar un color.");
        }
    }

    /**
     * Este método ya no es usado por GestorCliente, pero
     * se mantiene por si acaso.
     */
    public boolean fueConfigurado() {
        return this.configurado;
    }
}