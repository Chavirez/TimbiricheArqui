package modeloLogico;

import entidades.*;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.TableroModelo;
import vista.*;
import controlador.TableroControlador;
import interfaces.IGestorJuego;
import interfaces.IObservadorJuego;
import interfaces.IServicioJuego;

/**
 * Orquestador de la UI. Implementa IServicioJuego (contrato) y IObservadorJuego
 * (notificaciones). Se mantiene en MVC porque depende de javax.swing.
 */
public class GestorCliente implements IServicioJuego, IObservadorJuego {

    private final IGestorJuego gestorJuego;
    private TableroModelo modeloMVC;
    private Jugador jugadorLocal;

    // Ventanas
    private VentanaLobby ventanaLobby;
    private JFrame ventanaJuego;
    private PanelPrincipal panelPrincipal;
    private VentanaConfiguracion ventanaConfiguracionActual;

    public GestorCliente(IGestorJuego gestorJuego) {
        this.gestorJuego = gestorJuego;
        this.gestorJuego.registrarObservador(this);
    }

    public void setVentanaLobby(VentanaLobby lobby) {
        this.ventanaLobby = lobby;
    }

    // --- IMPLEMENTACIÓN DE IServicioJuego (Acciones UI -> Lógica) ---
    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal) {
        // Delegamos a la lógica pura, inyectando el jugador local que la UI conoce
        gestorJuego.reclamarLinea(fila, col, horizontal, jugadorLocal);
    }

    @Override
    public void crearPartida() {
        gestorJuego.crearPartida();
    }

    @Override
    public void unirseAPartida(String codigo) {
        gestorJuego.unirseAPartida(codigo);
    }

    @Override
    public void enviarConfiguracionJugador(Jugador jugador) {
        this.jugadorLocal = jugador;
        gestorJuego.configurarJugador(jugador);
    }

    @Override
    public void iniciarPartida() {
        gestorJuego.iniciarPartida();
    }

    // --- IMPLEMENTACIÓN DE IObservadorJuego (Lógica -> UI) ---
    @Override
    public void unionExitosa(EstadoPartidaDTO estadoInicial) {
        SwingUtilities.invokeLater(() -> {
            inicializarJuegoMVC(estadoInicial);
            mostrarConfiguracion();
        });
    }

    @Override
    public void estadoActualizado(EstadoPartidaDTO nuevoEstado) {
        SwingUtilities.invokeLater(() -> {
            actualizarIdentidadLocal(nuevoEstado);
            if (modeloMVC != null) {
                modeloMVC.actualizarEstado(nuevoEstado);
            }
        });
    }

    @Override
    public void partidaIniciada() {
        SwingUtilities.invokeLater(this::actualizarTitulo);
    }

    @Override
    public void error(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            Component parent = (ventanaConfiguracionActual != null && ventanaConfiguracionActual.isVisible())
                    ? ventanaConfiguracionActual : (ventanaJuego != null ? ventanaJuego : ventanaLobby);
            JOptionPane.showMessageDialog(parent, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    // --- MÉTODOS PRIVADOS DE UI ---
    public void mostrarConfiguracion() {
        if (ventanaConfiguracionActual == null || !ventanaConfiguracionActual.isVisible()) {
            ventanaConfiguracionActual = new VentanaConfiguracion(ventanaJuego, this);
            ventanaConfiguracionActual.setVisible(true);
        } else {
            ventanaConfiguracionActual.toFront();
        }
    }

    private void inicializarJuegoMVC(EstadoPartidaDTO estadoInicial) {
        this.modeloMVC = new TableroModelo();
        this.modeloMVC.actualizarEstado(estadoInicial);

        if (ventanaLobby != null) {
            ventanaLobby.dispose();
        }

        TableroVista vista = new TableroVista(modeloMVC, null);
        TableroControlador controlador = new TableroControlador(modeloMVC, vista, this);
        vista.addMouseListener(controlador);

        this.panelPrincipal = new PanelPrincipal(modeloMVC, controlador);
        modeloMVC.agregarObservador(panelPrincipal);
        modeloMVC.agregarObservador(controlador);

        panelPrincipal.getPanelLateral().setServicioJuego(this);

        ventanaJuego = new JFrame("Timbiriche - Sala de Espera");
        ventanaJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaJuego.add(panelPrincipal);
        ventanaJuego.pack();
        ventanaJuego.setLocationRelativeTo(null);
        ventanaJuego.setVisible(true);
    }

    private void actualizarIdentidadLocal(EstadoPartidaDTO estado) {
        if (this.jugadorLocal != null && this.jugadorLocal.id() == 0) {
            for (Jugador j : estado.jugadores()) {
                if (j.nombre().equals(this.jugadorLocal.nombre())) {
                    this.jugadorLocal = j;
                    if (ventanaConfiguracionActual != null) {
                        ventanaConfiguracionActual.dispose();
                        ventanaConfiguracionActual = null;
                    }
                    actualizarTitulo();
                    break;
                }
            }
        }
    }

    private void actualizarTitulo() {
        if (ventanaJuego != null && jugadorLocal != null) {
            ventanaJuego.setTitle("Timbiriche - " + jugadorLocal.nombre());
        }
    }
}
