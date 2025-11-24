package vista;

import utilidades.Recursos;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory;

/**
 * Diálogo modal para seleccionar un avatar.
 */
public class DialogoSelectorAvatar extends JDialog {

    private String avatarSeleccionadoPath = null;

    public DialogoSelectorAvatar(Frame owner, String currentAvatarPath, Set<String> avataresEnUso) {
        super(owner, "Seleccionar Avatar", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelAvatares = new JPanel(new GridLayout(0, 4, 10, 10));
        panelAvatares.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<String> avataresDisponibles = Recursos.getAvataresDisponibles();
        ButtonGroup grupoAvatares = new ButtonGroup();

        for (String pathCompleto : avataresDisponibles) {
            ImageIcon icon = Recursos.loadScaledAvatar(pathCompleto, 80, 80);

            JToggleButton btnAvatar = new JToggleButton();
            btnAvatar.setIcon(icon);
            btnAvatar.setActionCommand(pathCompleto);
            btnAvatar.setToolTipText(Recursos.getNombreAmigableAvatar(pathCompleto));

            boolean estaEnUso = avataresEnUso.contains(pathCompleto) && !pathCompleto.equals(currentAvatarPath);
            btnAvatar.setEnabled(!estaEnUso);

            if (pathCompleto.equals(currentAvatarPath)) {
                btnAvatar.setSelected(true);
                btnAvatar.setEnabled(true);
            }

            grupoAvatares.add(btnAvatar);
            panelAvatares.add(btnAvatar);
        }

        JScrollPane scrollPane = new JScrollPane(panelAvatares);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> {
            ButtonModel seleccion = grupoAvatares.getSelection();
            if (seleccion != null) {
                avatarSeleccionadoPath = seleccion.getActionCommand();
            }
            dispose(); // Cierra el diálogo
        });
        add(btnConfirmar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public String getAvatarSeleccionadoPath() {
        return avatarSeleccionadoPath;
    }
}
