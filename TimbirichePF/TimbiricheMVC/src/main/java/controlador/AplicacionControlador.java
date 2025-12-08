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
    private PanelConfiguracionJugador ventanaConfiguracionGrafica; 
    private JFrame frameJuego; 
    private IServicioJuego interfazControladorApp;

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
            JOptionPane.showMessageDialog(null, modeloApp.getMensajeError(), "Error", JOptionPane.ERROR_MESSAGE); // Usar null para que salga encima de todo
            modeloApp.limpiarError();
            return;
        }

        switch (modeloApp.getEstadoActual()) {
            case LOBBY:
                mostrarLobby();
                break;

            case PARTIDA:
               
                Jugador local = modeloApp.getJugadorLocal();
                if (configuracionPendiente || local == null || local.id() == 0) {
                    mostrarConfiguracion();
                } else {
                    mostrarJuego();
                }
                break;

            case RESULTADOS:
                mostrarResultados();
                break;
        }
    }


    private void cerrarTodasLasVentanas() {
        if (ventanaLobby != null) {
            ventanaLobby.dispose();
            ventanaLobby = null;
        }
        if (ventanaConfiguracionGrafica != null) {
            ventanaConfiguracionGrafica.dispose();
            ventanaConfiguracionGrafica = null;
        }
        if (frameJuego != null) {
            frameJuego.dispose();
            frameJuego = null;
        }
    }

    private void mostrarLobby() {
        cerrarTodasLasVentanas();
        ventanaLobby = new VentanaLobby(this);
        ventanaLobby.setVisible(true);
    }

    private void mostrarConfiguracion() {
        cerrarTodasLasVentanas();
        ventanaConfiguracionGrafica = new PanelConfiguracionJugador(this);
        ventanaConfiguracionGrafica.setVisible(true);
    }

    private void mostrarJuego() {
        if (frameJuego != null && frameJuego.isVisible()) {
            Jugador local = modeloApp.getJugadorLocal();
            if (local != null) {
                frameJuego.setTitle("Timbiriche - " + local.nombre());
            }
            return;
        }
        cerrarTodasLasVentanas();
        TableroModelo modeloTablero = modeloApp.getTableroModelo();
        if (modeloTablero == null) {
            return;
        }
        TableroControlador tableroControlador = new TableroControlador(modeloTablero, modeloApp);
        VentanaJuego vJuego = new VentanaJuego(tableroControlador , interfazControladorApp);
        this.frameJuego = vJuego;
        Jugador local = modeloApp.getJugadorLocal();
        if (local != null) {
            frameJuego.setTitle("Timbiriche - " + local.nombre());
        }
        frameJuego.setVisible(true);
    }
    private void mostrarResultados() {
        if (frameJuego != null && modeloApp.getResultadosFinales() != null) {
            VentanaResultados vr = new VentanaResultados(frameJuego, modeloApp.getResultadosFinales(), this);
            vr.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    volverAlLobby();
                }
            });
            vr.setVisible(true);
        }
    }
    public void volverAlLobby() {
        modeloApp.cambiarEstado(AplicacionModelo.EstadoNavegacion.LOBBY);
        mostrarLobby(); 
    }
}
