package modeloLogico;


import java.awt.Component;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modelo.TableroModelo;
import acciones.*;
import entidades.*;
import eventos.*;
import vista.VentanaConfiguracion;
import vista.VentanaLobby;
import vista.PanelPrincipal;
import vista.TableroVista;       
import vista.ISelectorPuntoUI;  
import controlador.TableroControlador;
import interfaces.ITuberiaEntrada;
import interfaces.ITuberiaSalida;

/**
 * El cerebro del cliente.
 */
public class GestorCliente implements IServicioJuego, ITuberiaEntrada, IClienteJuego {

    //private IProcesadorDeEnvio procesadorEnvio;
    private ITuberiaSalida envio;
    private TableroModelo modelo;
    private LogicaCliente logica;
    private Jugador jugadorLocal;

    private VentanaLobby ventanaLobby;
    private JFrame ventanaJuego;
    private PanelPrincipal panelPrincipal;

    private VentanaConfiguracion ventanaConfiguracionActual;

    public GestorCliente(ITuberiaSalida e) {
        envio=e;
        this.logica = new LogicaCliente();
    }

    public void setVentanaLobby(VentanaLobby lobby) {
        this.ventanaLobby = lobby;
    }

    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal) {
        if (logica.esMiTurno(modelo, jugadorLocal)) {
            envio.enviarDato(new acciones.AccionReclamarLinea(fila, col,horizontal));
        } else {
            System.out.println("[GestorCliente] Clic ignorado, no es mi turno.");
        }
    }

    @Override
    public void crearPartida() {
        envio.enviarDato(new AccionCrearPartida());
    }

    @Override
    public void unirseAPartida(String codigo) {
        envio.enviarDato(new AccionUnirseAPartida(codigo));
    }

    @Override
    public void enviarConfiguracionJugador(Jugador jugador) {
        this.jugadorLocal = jugador;
        envio.enviarDato(new AccionConfigurarJugador(jugador));
    }

    @Override
    public void iniciarPartida() {
        System.out.println("[GestorCliente] Intentando iniciar la partida...");
        envio.enviarDato(new AccionIniciarPartida());
    }

    @Override
    public void alRecibirDato(Object dato) {
        SwingUtilities.invokeLater(() -> {
            switch (dato) {
                case EventoEstadoActualizado e ->
                    onEstadoPartidaActualizado(e.getNuevoEstado());
                case RespuestaUnionExitosa r ->
                    onUnionExitosa(r.getEstadoInicial());
                case EventoPartidaIniciada e ->
                    onPartidaIniciada();
                case EventoError e ->
                    onMostrarError(e.getMensaje());
                default ->
                    System.err.println("DTO desconocido recibido: " + dato.getClass().getName());
            }
        });
    }

    @Override
    public void onUnionExitosa(EstadoPartidaDTO estadoInicial) {
        // 1. Creamos nuestro modelo local
        this.modelo = new TableroModelo();
        this.modelo.setServicioJuego(this);

        // 2. Cerramos el lobby
        if (ventanaLobby != null) {
            ventanaLobby.dispose();
        }

        // --- INICIO ENSAMBLAJE DE VISTA/CONTROLADOR (DI con Interface) ---
        // 3a. Creamos la Vista. Se le pasa null como controlador al inicio.
        TableroVista vista = new TableroVista(modelo, null);

        // 3b. Creamos el Controlador, inyectando el Modelo y la Vista como interfaz.
        // El Controlador SÓLO ve la interfaz ISelectorPuntoUI.
        TableroControlador controlador = new TableroControlador(this.modelo, (ISelectorPuntoUI) vista);

        // 3c. Cerramos el ciclo de dependencia: Conectamos la Vista al Controlador real.
        // Esto registra el controlador como MouseListener.
        vista.addMouseListener(controlador);

        // 4. Creamos PanelPrincipal e inyectamos el MouseListener (Controlador)
        this.panelPrincipal = new PanelPrincipal(this.modelo, (MouseListener) controlador);

        // --- FIN ENSAMBLAJE ---
        // 5. Conectar Observadores (Cableado)
        this.modelo.agregarObservador(panelPrincipal);
        this.modelo.agregarObservador(controlador);

        // 6. Conectar el botón "Iniciar Partida" del PanelLateral
        panelPrincipal.getPanelLateral().setServicioJuego(this);

        // 7. Crear la ventana principal del juego
        ventanaJuego = new JFrame("Timbiriche - Sala de Espera");
        ventanaJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaJuego.add(panelPrincipal);
        ventanaJuego.pack();
        ventanaJuego.setMinimumSize(ventanaJuego.getPreferredSize());
        ventanaJuego.setLocationRelativeTo(null);

        // 8. ACTUALIZAMOS EL MODELO CON EL ESTADO 
        this.modelo.actualizarDesdeDTO(estadoInicial);

        // 9. Mostramos la ventana del juego
        ventanaJuego.setVisible(true);

        // 10. Pedimos la configuración al usuario
        onConfiguracionRequerida();
    }

    @Override
    public void onConfiguracionRequerida() {
        if (this.ventanaConfiguracionActual != null && this.ventanaConfiguracionActual.isVisible()) {
            this.ventanaConfiguracionActual.toFront();
            return;
        }

        VentanaConfiguracion configDialog = new VentanaConfiguracion(ventanaJuego, this);
        this.ventanaConfiguracionActual = configDialog;
        configDialog.setVisible(true);
    }

    @Override
    public void onPartidaIniciada() {
        if (ventanaJuego != null && jugadorLocal != null) {
            ventanaJuego.setTitle("Timbiriche - " + jugadorLocal.nombre());
        }
    }

    @Override
    public void onEstadoPartidaActualizado(EstadoPartidaDTO nuevoEstado) {
        if (this.modelo != null) {

            if (this.jugadorLocal != null && this.jugadorLocal.id() == 0) {
                for (Jugador j : nuevoEstado.jugadores()) {
                    if (j.nombre().equals(this.jugadorLocal.nombre())) {

                        this.jugadorLocal = j;

                        if (this.ventanaConfiguracionActual != null) {
                            this.ventanaConfiguracionActual.setVisible(false);
                            this.ventanaConfiguracionActual.dispose();
                            this.ventanaConfiguracionActual = null;
                        }

                        if (ventanaJuego != null && !nuevoEstado.juegoTerminado()) {
                            ventanaJuego.setTitle("Timbiriche - " + jugadorLocal.nombre());
                        }
                        break;
                    }
                }
            }
            this.modelo.actualizarDesdeDTO(nuevoEstado);
        }
    }

    @Override
    public void onMostrarError(String mensaje) {
        Component ventanaActiva = (this.ventanaConfiguracionActual != null && this.ventanaConfiguracionActual.isVisible())
                ? this.ventanaConfiguracionActual
                : (ventanaJuego != null) ? ventanaJuego : ventanaLobby;

        JOptionPane.showMessageDialog(ventanaActiva, mensaje, "Error del Servidor", JOptionPane.ERROR_MESSAGE);
    }
}
