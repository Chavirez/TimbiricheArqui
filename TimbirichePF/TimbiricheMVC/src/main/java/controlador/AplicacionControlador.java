package controlador;

import interfaces.IServicioJuego;
import entidades.Jugador;
import modelo.AplicacionModelo;
import modelo.TableroModelo;
import observador.Observador;
import vista.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AplicacionControlador implements IServicioJuego, Observador {

    private final AplicacionModelo modeloApp;
    private VentanaLobby ventanaLobby;
    private VentanaConfiguracion ventanaConfig;
    private PanelConfiguracionJugador ventanaConfiguracionGrafica;
    private JFrame frameJuego;

    private boolean configuracionPendiente = false;

    public AplicacionControlador(AplicacionModelo modeloApp) {
        this.modeloApp = modeloApp;
        this.modeloApp.agregarObservador(this);
    }

    public void iniciarAplicacion() {
        SwingUtilities.invokeLater(this::mostrarLobby);
    }

    @Override
    public void crearPartida() {
        configuracionPendiente = true; 
        modeloApp.crearPartida();
    }

    @Override
    public void unirseAPartida(String codigo) {
        configuracionPendiente = true; 
        modeloApp.unirseAPartida(codigo);
    }

    @Override
    public void enviarConfiguracionJugador(Jugador jugador) {
        modeloApp.configurarJugador(jugador);
        configuracionPendiente = false; 
    }

    @Override
    public void iniciarPartida() {
        modeloApp.iniciarPartida();
    }

    @Override
    public void actualizar() {
        SwingUtilities.invokeLater(this::manejarCambioEstado);
    }

    private void manejarCambioEstado() {
        if (modeloApp.getMensajeError() != null) {
            JOptionPane.showMessageDialog(getVentanaActiva(), modeloApp.getMensajeError(), "Error", JOptionPane.ERROR_MESSAGE);
            modeloApp.limpiarError();
            return;
        }

        switch (modeloApp.getEstadoActual()) {
            case LOBBY:
                mostrarLobby();
                break;
                
            case PARTIDA:
                mostrarJuego();
                verificarConfiguracionRequerida();
                break;
                
            case RESULTADOS:
                mostrarResultados();
                break;
        }
    }
    private void verificarConfiguracionRequerida() {
            Jugador local = modeloApp.getJugadorLocal();

            if (configuracionPendiente || local == null || local.id() == 0) {
                mostrarConfiguracion();
            } 

            else {
                if (ventanaConfiguracionGrafica != null && ventanaConfiguracionGrafica.isVisible()) {
                    ventanaConfiguracionGrafica.dispose();
                    ventanaConfiguracionGrafica = null;
                }

                if (ventanaConfig != null && ventanaConfig.isVisible()) {
                    ventanaConfig.dispose();
                    ventanaConfig = null;
                }

                if(frameJuego != null) frameJuego.setTitle("Timbiriche - " + local.nombre());
            }
        }

    private void mostrarLobby() {
        cerrarVentanasDeJuego();
        if (ventanaLobby == null) {
            ventanaLobby = new VentanaLobby(this);
        }
        ventanaLobby.setVisible(true);
    }

    private void mostrarJuego() {
        if (ventanaLobby != null) {
            ventanaLobby.dispose();
            ventanaLobby = null;
        }

        if (frameJuego != null && frameJuego.isVisible()) return;

        TableroModelo modeloTablero = modeloApp.getTableroModelo();
        if (modeloTablero == null) return;

        TableroControlador tableroControlador = new TableroControlador(modeloTablero, modeloApp);


        VentanaJuego ventanaJuego = new VentanaJuego(tableroControlador, this);
        
        this.frameJuego = ventanaJuego;
        
        frameJuego.setVisible(true);
    }

    private void mostrarConfiguracion() {
        if (ventanaConfiguracionGrafica != null && ventanaConfiguracionGrafica.isVisible()) {
            ventanaConfiguracionGrafica.toFront();
            return;
        }
        ventanaConfiguracionGrafica = new PanelConfiguracionJugador(this);

        ventanaConfiguracionGrafica.setLocationRelativeTo(null);
        ventanaConfiguracionGrafica.setVisible(true);
    }
    
    private void mostrarResultados() {
        if (frameJuego != null && modeloApp.getResultadosFinales() != null) {
            VentanaResultados vr = new VentanaResultados(frameJuego, modeloApp.getResultadosFinales(), this);
            vr.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) {
                    volverAlLobby();
                }
            });
            vr.setVisible(true);
        }
    }

    public void volverAlLobby() {
        cerrarVentanasDeJuego();
        modeloApp.cambiarEstado(AplicacionModelo.EstadoNavegacion.LOBBY);
        mostrarLobby();
    }

    private void cerrarVentanasDeJuego() {
        if (frameJuego != null) { frameJuego.dispose(); frameJuego = null; }

        if (ventanaConfiguracionGrafica != null) { 
            ventanaConfiguracionGrafica.dispose(); 
            ventanaConfiguracionGrafica = null; 
        }
    }

    private java.awt.Window getVentanaActiva() {
        if (ventanaConfig != null && ventanaConfig.isVisible()) return ventanaConfig;
        if (frameJuego != null && frameJuego.isVisible()) return frameJuego;
        return ventanaLobby;
    }
}