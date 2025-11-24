package vista;

import utilidades.Recursos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel de UI para editar la configuración de un solo jugador.
 */
public class PanelConfiguracionJugador extends JPanel {

    private final ConfiguracionJugador config;
    private final JTextField txtNombre;
    private final JLabel lblAvatarPreview;
    private final JPanel panelColorPreview;
    private final JFrame ownerFrame;

    public PanelConfiguracionJugador(String titulo, ConfiguracionJugador config, JFrame owner) {
        this.config = config;
        this.ownerFrame = owner;
        setBorder(BorderFactory.createTitledBorder(titulo));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(config.getNombre(), 15);
        add(txtNombre, gbc);

        // Avatar
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Avatar:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel panelAvatarSelector = new JPanel(new BorderLayout(5, 0));
        lblAvatarPreview = new JLabel();
        lblAvatarPreview.setPreferredSize(new Dimension(50, 50));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actualizarAvatarPreview(config.getAvatarPath());
        panelAvatarSelector.add(lblAvatarPreview, BorderLayout.WEST);
        JButton btnSeleccionarAvatar = new JButton("Cambiar Avatar");
        btnSeleccionarAvatar.addActionListener(e -> seleccionarAvatar());
        panelAvatarSelector.add(btnSeleccionarAvatar, BorderLayout.CENTER);
        add(panelAvatarSelector, gbc);

        // Color
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Color:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel panelColorSelector = new JPanel(new BorderLayout(5, 0));
        panelColorPreview = new JPanel();
        panelColorPreview.setPreferredSize(new Dimension(50, 50));
        panelColorPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actualizarColorPreview(config.getColor());
        panelColorSelector.add(panelColorPreview, BorderLayout.WEST);
        JButton btnSeleccionarColor = new JButton("Cambiar Color");
        btnSeleccionarColor.addActionListener(e -> seleccionarColor());
        panelColorSelector.add(btnSeleccionarColor, BorderLayout.CENTER);
        add(panelColorSelector, gbc);
    }

    private void seleccionarAvatar() {
        // En una implementación real, el servidor debería enviar la lista
        // de avatares ya en uso. Por ahora, asumimos que puede elegir cualquiera.
        Set<String> avataresEnUso = new HashSet<>();

        DialogoSelectorAvatar selector = new DialogoSelectorAvatar(ownerFrame, this.config.getAvatarPath(), avataresEnUso);
        selector.setVisible(true);
        String nuevoAvatarPath = selector.getAvatarSeleccionadoPath();
        if (nuevoAvatarPath != null) {
            this.config.setAvatarPath(nuevoAvatarPath);
            actualizarAvatarPreview(nuevoAvatarPath);
        }
    }

    private void seleccionarColor() {
        Color nuevoColor = JColorChooser.showDialog(ownerFrame, "Seleccionar Color", this.config.getColor());
        if (nuevoColor != null) {
            this.config.setColor(nuevoColor);
            actualizarColorPreview(nuevoColor);
        }
    }

    private void actualizarAvatarPreview(String path) {
        lblAvatarPreview.setIcon(Recursos.loadScaledAvatar(path, 50, 50));
    }

    private void actualizarColorPreview(Color color) {
        panelColorPreview.setBackground(color);
    }

    public ConfiguracionJugador getConfig() {
        config.setNombre(txtNombre.getText().trim());
        return config;
    }
}
