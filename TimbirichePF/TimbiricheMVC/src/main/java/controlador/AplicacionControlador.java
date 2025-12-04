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

    // BANDERA NUEVA: Fuerza la apertura de configuración tras conectar
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
        configuracionPendiente = true; // Marcamos que falta configurar
        modeloApp.crearPartida();
    }

    @Override
    public void unirseAPartida(String codigo) {
        configuracionPendiente = true; // Marcamos que falta configurar
        modeloApp.unirseAPartida(codigo);
    }

    @Override
    public void enviarConfiguracionJugador(Jugador jugador) {
        modeloApp.configurarJugador(jugador);
        configuracionPendiente = false; // ¡Listo! Ya enviamos la configuración
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

    // --- GESTIÓN DE VENTANAS ---
    private void mostrarLobby() {
        cerrarVentanasDeJuego();
        if (ventanaLobby == null) {
            ventanaLobby = new VentanaLobby(this);
        }
        ventanaLobby.setVisible(true);
    }

private void mostrarJuego() {
        // 1. Cerrar el Lobby si está abierto
        if (ventanaLobby != null) {
            ventanaLobby.dispose();
            ventanaLobby = null;
        }

        // Si ya existe la ventana de juego, no la volvemos a crear
        if (frameJuego != null && frameJuego.isVisible()) return;

        TableroModelo modeloTablero = modeloApp.getTableroModelo();
        if (modeloTablero == null) return;

        // 2. Crear el controlador del tablero (lógica de clics)
        TableroControlador tableroControlador = new TableroControlador(modeloTablero, modeloApp);

        // 3. CREAR LA NUEVA VENTANA DEDICADA
        // Pasamos 'this' porque el Controlador implementa IServicioJuego
        VentanaJuego ventanaJuego = new VentanaJuego(modeloTablero, tableroControlador, this);
        
        // Asignamos a la variable global frameJuego (que es de tipo JFrame)
        this.frameJuego = ventanaJuego;
        
        // 4. Mostrar
        frameJuego.setVisible(true);
    }

    private void mostrarConfiguracion() {
        if (ventanaConfiguracionGrafica != null && ventanaConfiguracionGrafica.isVisible()) {
            ventanaConfiguracionGrafica.toFront();
            return;
        }
        // Pasamos 'this' (el controlador) porque implementa IServicioJuego
        ventanaConfiguracionGrafica = new PanelConfiguracionJugador(this);

        // Centrar en pantalla
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
        // Cerrar la nueva ventana gráfica
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