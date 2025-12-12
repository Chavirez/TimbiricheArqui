package controlador;

import entidades.Jugador;
import modelo.AplicacionModelo;
import modelo.TableroModelo;
import vista.*;
import javax.swing.*;

// AplicacionControlador ya NO implementa Observador

public class AplicacionControlador {

    private final AplicacionModelo modeloApp;
    private VentanaLobby ventanaLobby;
    private VentanaConfiguracionJugador ventanaConfiguracionGrafica;
    private JFrame frameJuego;
    private boolean configuracionPendiente = false;

    public AplicacionControlador(AplicacionModelo modeloApp) {
        this.modeloApp = modeloApp;
        
        // Registrar un callback (Runnable) para que el Modelo nos notifique
        Runnable callback = () -> {
            // Ejecutar el manejo de estado en el hilo de la interfaz gráfica
            SwingUtilities.invokeLater(this::manejarCambioEstado);
        };
        
        modeloApp.registrarControladorCallback(callback);
    }

    public void iniciarAplicacion() {
        SwingUtilities.invokeLater(this::mostrarLobby);
    }

    public void iniciarPartida() {
        modeloApp.iniciarPartida();
    }

    // --- Métodos que la VISTA llamará ---
    public void crearPartida() {
        configuracionPendiente = true;
        modeloApp.crearPartida();
    }

    public void unirseAPartida(String codigo) {
        configuracionPendiente = true;
        modeloApp.unirseAPartida(codigo);
    }

    // Este es el método que usará VentanaConfiguracionJugador
    public void enviarConfiguracionJugador(Jugador jugador) {
        modeloApp.configurarJugador(jugador);
        configuracionPendiente = false;
        // La notificación se gestiona mediante el callback registrado en el constructor.
    }

    private void manejarCambioEstado() {
        if (modeloApp.getMensajeError() != null) {
            JOptionPane.showMessageDialog(null, modeloApp.getMensajeError(), "Error", JOptionPane.ERROR_MESSAGE);
            modeloApp.limpiarError();
            return;
        }

        switch (modeloApp.getEstadoActual()) {
            case LOBBY:
                mostrarLobby();
                break;

            case PARTIDA:
                Jugador local = modeloApp.getJugadorLocal();
                // Si estamos en partida pero no tenemos configuración, mostrar config
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

    // --- Gestión de Ventanas ---
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
        ventanaLobby = new VentanaLobby(this); // Asumiendo que Lobby recibe el controlador
        ventanaLobby.setVisible(true);
    }

    private void mostrarConfiguracion() {
        cerrarTodasLasVentanas();
        // Pasamos 'this' (el controlador)
        ventanaConfiguracionGrafica = new VentanaConfiguracionJugador(this);
        ventanaConfiguracionGrafica.setVisible(true);
    }

    private void mostrarJuego() {
        if (frameJuego != null && frameJuego.isVisible()) {
            return;
        }

        cerrarTodasLasVentanas();
        TableroModelo modeloTablero = modeloApp.getTableroModelo();
        if (modeloTablero == null) {
            return;
        }

        TableroControlador tableroControlador = new TableroControlador(modeloTablero, modeloApp);

        // Pasamos este mismo controlador a la ventana
        VentanaJuego vJuego = new VentanaJuego(tableroControlador, this);

        this.frameJuego = vJuego;

        // Opcional: poner título
        Jugador local = modeloApp.getJugadorLocal();
        if (local != null) {
            frameJuego.setTitle("Timbiriche - " + local.nombre());
        }

        frameJuego.setVisible(true);
    }

    private void mostrarResultados() {
        if (frameJuego != null && modeloApp.getResultadosFinales() != null) {
            VentanaResultados vr = new VentanaResultados(frameJuego, modeloApp.getResultadosFinales(), this);
            vr.setVisible(true);
        }
    }

    public void volverAlLobby() {
        modeloApp.cambiarEstado(AplicacionModelo.EstadoNavegacion.LOBBY);
        // La llamada a mostrarLobby() la hace el callback del controlador al actualizarse.
    }
}