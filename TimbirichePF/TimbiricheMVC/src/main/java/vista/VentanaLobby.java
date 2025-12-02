package vista;

import interfaces.IServicioJuego;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana inicial (JFrame) que permite al usuario crear o unirse a una partida.
 * Esta es la primera pantalla que ve el usuario.
 */
public class VentanaLobby extends JFrame {

    private final IServicioJuego servicioJuego;
    private final JTextField txtCodigoSala;

    public VentanaLobby(IServicioJuego servicioJuego) {
        this.servicioJuego = servicioJuego;

        setTitle("Timbiriche - Lobby");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Margen interno para que no quede todo pegado a los bordes
        if (getRootPane() != null) {
            getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        // --- Panel para unirse a una sala ---
        JPanel panelUnirse = new JPanel(new FlowLayout());
        panelUnirse.setBorder(BorderFactory.createTitledBorder("Unirse a Partida"));
        
        txtCodigoSala = new JTextField(10);
        JButton btnUnirse = new JButton("Unirse");
        
        panelUnirse.add(new JLabel("Código:"));
        panelUnirse.add(txtCodigoSala);
        panelUnirse.add(btnUnirse);

        // --- Panel para crear una sala ---
        JPanel panelCrear = new JPanel(new FlowLayout());
        panelCrear.setBorder(BorderFactory.createTitledBorder("Crear Partida"));
        
        JButton btnCrear = new JButton("Crear Nueva Partida");
        panelCrear.add(btnCrear);

        // --- Agregar paneles al Frame ---
        add(panelUnirse, BorderLayout.CENTER);
        add(panelCrear, BorderLayout.SOUTH);

        // --- Controladores (Listeners) ---
        btnCrear.addActionListener(e -> {
            if (servicioJuego != null) {
                servicioJuego.crearPartida();
            }
        });

        btnUnirse.addActionListener(e -> {
            String codigo = txtCodigoSala.getText().trim().toUpperCase();
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Debe ingresar un código de sala.", 
                        "Aviso", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (servicioJuego != null) {
                servicioJuego.unirseAPartida(codigo);
            }
        });
    }
}