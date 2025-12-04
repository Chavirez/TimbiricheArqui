/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import PlantillaFROM.*;
import entidades.Jugador;
import interfaces.IServicioJuego;
import java.awt.Color;
import vista.*;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import utilidades.Recursos;

/**
 *
 * @author santi
 */
public class PanelConfiguracionJugador extends javax.swing.JFrame {

    private IServicioJuego servicioJuego;
    private ConfiguracionJugador config;
    
    public PanelConfiguracionJugador(IServicioJuego servicioJuego) {
        
        this.servicioJuego = servicioJuego;
        this.setUndecorated(true);
        
        initComponents();
        inicializarComponentesLogicos();
        configurarLabels();
    }

    private void inicializarComponentesLogicos() {
        // 1. Inicializar configuración por defecto
        List<String> avatares = Recursos.getAvataresDisponibles();
        List<Color> colores = Recursos.getColoresDisponibles();
        
        this.config = new ConfiguracionJugador(
                "Jugador",
                (avatares != null && !avatares.isEmpty()) ? avatares.get(0) : "",
                (colores != null && !colores.isEmpty()) ? colores.get(0) : Color.BLUE
        );



        // 3. Actualizar vistas previas iniciales
        actualizarAvatarUI();
        actualizarColorUI();
    }

    private void seleccionarAvatar() {

        DialogoSelectorAvatar selector = new DialogoSelectorAvatar(this, config.getAvatarPath(), new HashSet<>());
        selector.setVisible(true);
        
        String nuevoPath = selector.getAvatarSeleccionadoPath();
        if (nuevoPath != null) {
            config.setAvatarPath(nuevoPath);
            actualizarAvatarUI();
        }
    }

    private void seleccionarColor() {
        Color nuevoColor = JColorChooser.showDialog(this, "Selecciona tu color", config.getColor());
        if (nuevoColor != null) {
            config.setColor(nuevoColor);
            actualizarColorUI();
        }
    }

    private void registrarJugador() {
        String nombre = fldNombre.getText().trim();
        
        if (nombre.isEmpty() || nombre.equals("Nombre de jugador")) {
            mostrarError("El nombre no puede estar vacío.");
            return;
        }
        if (config.getAvatarPath() == null || config.getAvatarPath().isEmpty()) {
            mostrarError("Debes seleccionar un avatar.");
            return;
        }
        
        config.setNombre(nombre);
        
        Jugador jugadorFinal = new Jugador(
                0, 
                config.getNombre(),
                config.getAvatarPath(),
                config.getColor()
        );

        if (servicioJuego != null) {
            servicioJuego.enviarConfiguracionJugador(jugadorFinal);

        } else {
            System.out.println("Modo prueba: Jugador creado -> " + jugadorFinal);

        }
    }

    private void actualizarAvatarUI() {

    ImageIcon icon = Recursos.loadScaledAvatar(config.getAvatarPath(), 150, 150);

        if (icon != null) {
            lblAvatar.setIcon(icon);
        } else {
            System.err.println("No se pudo cargar la imagen: " + config.getAvatarPath());
        }
    }

    private void actualizarColorUI() {


        panelColor.setBackground(config.getColor());

        

    }
    
    private void mostrarError(String msj) {
        JOptionPane.showMessageDialog(this, msj, "Error de Registro", JOptionPane.WARNING_MESSAGE);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JLayeredPane();
        fldNombre = new javax.swing.JTextField();
        panelColor = new javax.swing.JPanel();
        lblAvatar = new javax.swing.JLabel();
        lblRegistrar = new javax.swing.JLabel();
        lblSeleccionarColor = new javax.swing.JLabel();
        lblSeleccionarAvatar = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fldNombre.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        fldNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fldNombreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelColorLayout = new javax.swing.GroupLayout(panelColor);
        panelColor.setLayout(panelColorLayout);
        panelColorLayout.setHorizontalGroup(
            panelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );
        panelColorLayout.setVerticalGroup(
            panelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        lblRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegistrarMouseClicked(evt);
            }
        });

        lblSeleccionarColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSeleccionarColorMouseClicked(evt);
            }
        });

        lblSeleccionarAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSeleccionarAvatarMouseClicked(evt);
            }
        });

        lblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/menus/Panel Jugador.png"))); // NOI18N

        panelPrincipal.setLayer(fldNombre, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(panelColor, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(lblAvatar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(lblRegistrar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(lblSeleccionarColor, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(lblSeleccionarAvatar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelPrincipal.setLayer(lblFondo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelPrincipalLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblFondo)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(633, Short.MAX_VALUE)
                    .addComponent(lblSeleccionarAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(145, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(636, Short.MAX_VALUE)
                    .addComponent(lblSeleccionarColor, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(141, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(387, Short.MAX_VALUE)
                    .addComponent(lblRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(377, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(412, Short.MAX_VALUE)
                    .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(438, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(380, Short.MAX_VALUE)
                    .addComponent(fldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(164, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(411, Short.MAX_VALUE)
                    .addComponent(panelColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(439, Short.MAX_VALUE)))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelPrincipalLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblFondo)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(379, Short.MAX_VALUE)
                    .addComponent(lblSeleccionarAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(521, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(576, Short.MAX_VALUE)
                    .addComponent(lblSeleccionarColor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(324, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(728, Short.MAX_VALUE)
                    .addComponent(lblRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(169, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(360, Short.MAX_VALUE)
                    .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(490, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(189, Short.MAX_VALUE)
                    .addComponent(fldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(758, Short.MAX_VALUE)))
            .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                    .addContainerGap(556, Short.MAX_VALUE)
                    .addComponent(panelColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(294, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fldNombreActionPerformed

    }//GEN-LAST:event_fldNombreActionPerformed

    private void lblRegistrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistrarMouseClicked

        registrarJugador();
        
    }//GEN-LAST:event_lblRegistrarMouseClicked

    private void lblSeleccionarColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSeleccionarColorMouseClicked
 
        seleccionarColor();
        
    }//GEN-LAST:event_lblSeleccionarColorMouseClicked

    private void lblSeleccionarAvatarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSeleccionarAvatarMouseClicked

        seleccionarAvatar();
        
    }//GEN-LAST:event_lblSeleccionarAvatarMouseClicked

    
    private void configurarLabels(){
    
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        lblRegistrar.setCursor(cursor);
        lblSeleccionarAvatar.setCursor(cursor);
        lblSeleccionarColor.setCursor(cursor);
        
        panelPrincipal.moveToBack(lblFondo);
        panelPrincipal.moveToFront(lblRegistrar);
        panelPrincipal.moveToFront(lblSeleccionarAvatar);
        panelPrincipal.moveToFront(lblSeleccionarColor);

        lblFondo.setFocusable(true);
        
        String placeholder = "Nombre de jugador";
        fldNombre.setText(placeholder);
        fldNombre.setForeground(Color.GRAY);
        
        fldNombre.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                if (fldNombre.getText().equals(placeholder)) {
                    fldNombre.setText("");
                    fldNombre.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (fldNombre.getText().isEmpty()) {
                    fldNombre.setText(placeholder);
                    fldNombre.setForeground(Color.GRAY);
                }
            }
        });
        
        this.revalidate();
        this.repaint();
    }

    public ConfiguracionJugador getConfig() {
        return config;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fldNombre;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JLabel lblRegistrar;
    private javax.swing.JLabel lblSeleccionarAvatar;
    private javax.swing.JLabel lblSeleccionarColor;
    private javax.swing.JPanel panelColor;
    private javax.swing.JLayeredPane panelPrincipal;
    // End of variables declaration//GEN-END:variables
}
