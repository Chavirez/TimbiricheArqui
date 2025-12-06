/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.TableroControlador;
import entidades.Jugador;
import interfaces.IServicioJuego;
import java.awt.BorderLayout;
import java.awt.Color;
import vista.*;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import modelo.TableroModelo;
import observador.Observador;

/**
 *
 * @author santi
 */
public class VentanaJuego extends javax.swing.JFrame implements Observador {
    
    private JLabel lblTurno;
    private JLabel lblCodigoSala;
    private JLabel lblSeparador;
    private PanelLateral panelLateralReal;
    private TableroControlador controladorTablero;

    public VentanaJuego(TableroControlador controladorTablero, IServicioJuego servicioJuego) {
    
        initComponents();
        this.getContentPane().setBackground(new java.awt.Color(45, 40, 90));
        this.controladorTablero = controladorTablero;
        this.panelLateralReal = new PanelLateral(controladorTablero);
        
        controladorTablero.suscribirVentanaJuego(this);

        panelJuego.setLayout(new BorderLayout());
        panelLateral.setLayout(new BorderLayout());
        panelNorte.setLayout(new BorderLayout());
        
        panelJuego.setOpaque(false);
        panelLateral.setOpaque(false);
        panelNorte.setOpaque(false);

        panelLateralReal.setServicioJuego(servicioJuego);
        panelLateral.add(this.panelLateralReal, BorderLayout.CENTER);
        
        TableroVista tableroVista = new TableroVista(controladorTablero);
        panelJuego.add(tableroVista, BorderLayout.CENTER);
 
        configurarPanelNorte(); 
        configurarLabels();
        
        panelFondo.setPreferredSize(new java.awt.Dimension(1000, 1000));
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(panelFondo);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.setContentPane(scrollPane);
        
        this.pack();
        
        actualizar();
    }

    public void configurarPanelNorte(){
        
lblTurno = new JLabel("Esperando...", SwingConstants.LEFT);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 24));
        lblTurno.setForeground(Color.BLACK); // Color blanco para que se vea en fondo oscuro
        lblTurno.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panelNorte.add(lblTurno, BorderLayout.WEST); // BorderLayout.WEST queda mejor aquí

        // El código de sala a la derecha
        lblCodigoSala = new JLabel("Código: ---", SwingConstants.RIGHT);
        lblCodigoSala.setFont(new Font("Arial", Font.BOLD, 24));
        lblCodigoSala.setForeground(Color.BLACK);
        lblCodigoSala.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        panelNorte.add(lblCodigoSala, BorderLayout.EAST);
            }
    
@Override
    public void actualizar() {
        SwingUtilities.invokeLater(() -> {
            
            if (this.panelLateralReal != null) {
                this.panelLateralReal.actualizarUI();
            }

            String codigo = (controladorTablero.getCodigoSala() != null) ? controladorTablero.getCodigoSala() : "---";
            if (lblCodigoSala != null) lblCodigoSala.setText("Código: " + codigo);

            if (controladorTablero.getEstadoActual() == null) {
                if (lblTurno != null) lblTurno.setText("Conectando...");
                return;
            }

            if (lblTurno != null) {
                if (controladorTablero.isJuegoTerminado()) {
                    lblTurno.setText("¡Fin del Juego!");
                    lblTurno.setForeground(Color.YELLOW);
                } else {
                    Jugador actual = controladorTablero.getJugadorActual();
                    if (actual != null) {
                        lblTurno.setText("Turno: " + actual.nombre());
                        lblTurno.setForeground(actual.color());
                    } else {
                        lblTurno.setText("Sala de Espera");
                        lblTurno.setForeground(Color.WHITE);
                    }
                }
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JLayeredPane();
        panelLateral = new javax.swing.JPanel();
        panelJuego = new javax.swing.JPanel();
        panelNorte = new javax.swing.JPanel();
        lblEngranaje = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout panelLateralLayout = new javax.swing.GroupLayout(panelLateral);
        panelLateral.setLayout(panelLateralLayout);
        panelLateralLayout.setHorizontalGroup(
            panelLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        panelLateralLayout.setVerticalGroup(
            panelLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 868, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelJuegoLayout = new javax.swing.GroupLayout(panelJuego);
        panelJuego.setLayout(panelJuegoLayout);
        panelJuegoLayout.setHorizontalGroup(
            panelJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );
        panelJuegoLayout.setVerticalGroup(
            panelJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 503, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelNorteLayout = new javax.swing.GroupLayout(panelNorte);
        panelNorte.setLayout(panelNorteLayout);
        panelNorteLayout.setHorizontalGroup(
            panelNorteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 558, Short.MAX_VALUE)
        );
        panelNorteLayout.setVerticalGroup(
            panelNorteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 191, Short.MAX_VALUE)
        );

        lblEngranaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEngranajeMouseClicked(evt);
            }
        });

        lblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/menus/Partida.png"))); // NOI18N

        panelFondo.setLayer(panelLateral, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelFondo.setLayer(panelJuego, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelFondo.setLayer(panelNorte, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelFondo.setLayer(lblEngranaje, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelFondo.setLayer(lblFondo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelFondoLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblFondo)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addGap(0, 923, Short.MAX_VALUE)
                    .addComponent(lblEngranaje, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(660, Short.MAX_VALUE)
                    .addComponent(panelLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(32, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(80, Short.MAX_VALUE)
                    .addComponent(panelJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(418, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(48, Short.MAX_VALUE)
                    .addComponent(panelNorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(394, Short.MAX_VALUE)))
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelFondoLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblFondo)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelFondoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblEngranaje, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(917, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(103, Short.MAX_VALUE)
                    .addComponent(panelLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(29, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(284, Short.MAX_VALUE)
                    .addComponent(panelJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(213, Short.MAX_VALUE)))
            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                    .addContainerGap(35, Short.MAX_VALUE)
                    .addComponent(panelNorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(774, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblEngranajeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEngranajeMouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_lblEngranajeMouseClicked

    
    private void configurarLabels(){
    
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        lblEngranaje.setCursor(cursor);
        
        
        
        panelFondo.moveToBack(lblFondo);
        panelFondo.moveToFront(lblEngranaje);
        panelFondo.moveToFront(panelLateral);
        panelFondo.moveToFront(panelJuego);


        this.revalidate();
        this.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblEngranaje;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JLayeredPane panelFondo;
    private javax.swing.JPanel panelJuego;
    private javax.swing.JPanel panelLateral;
    private javax.swing.JPanel panelNorte;
    // End of variables declaration//GEN-END:variables
}
