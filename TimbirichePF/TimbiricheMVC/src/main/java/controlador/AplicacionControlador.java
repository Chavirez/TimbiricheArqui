package controlador;

import entidades.Jugador;
import eventos.EventoPartidaTerminada;
import modelo.AplicacionModelo;
import modelo.TableroModelo;
import observador.Observador;
import vista.*;
import javax.swing.*;

public class AplicacionControlador implements Observador {

    private final AplicacionModelo modeloApp;
    private VentanaLobby ventanaLobby;
    private VentanaConfiguracionJugador ventanaConfiguracionGrafica;
    private JFrame frameJuego;
    private boolean configuracionPendiente = false;

    public AplicacionControlador(AplicacionModelo modeloApp) {
        this.modeloApp = modeloApp;
        this.modeloApp.agregarObservador(this);
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
        // Forzamos actualización para verificar si entramos al juego
        manejarCambioEstado();
    }

    // --- Manejo de Observador ---
    @Override
    public void actualizar() {
        SwingUtilities.invokeLater(this::manejarCambioEstado);
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
        // AQUI ESTÁ EL CAMBIO: Pasamos 'this' (el controlador)
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

        // --- AQUÍ ESTABA EL ERROR ---
        // Debes usar 'this' para pasar este mismo controlador a la ventana
        VentanaJuego vJuego = new VentanaJuego(tableroControlador, this);
        // ----------------------------

        this.frameJuego = vJuego;

        // Opcional: poner título
        Jugador local = modeloApp.getJugadorLocal();
        if (local != null) {
            frameJuego.setTitle("Timbiriche - " + local.nombre());
        }

        frameJuego.setVisible(true);
    }

    // ... (mostrarResultados y volverAlLobby igual que antes) ...
    private void mostrarResultados() {
        if (frameJuego != null && modeloApp.getResultadosFinales() != null) {
            EventoPartidaTerminada resultados = modeloApp.getResultadosFinales();

            // OBTENEMOS EL GANADOR O MENSAJE FINAL
            String nombreGanador = (resultados.getGanador() != null)
                    ? resultados.getGanador().nombre()
                    : "Empate";

            String mensaje;
            if (resultados.getGanador() != null) {
                // Usamos el mensaje personalizado del servidor (si existe) o uno por defecto
                String mensajeServidor = resultados.getMensaje();

                // Si el servidor mandó un mensaje explícito (como "ganado por abandono")
                if (mensajeServidor != null && !mensajeServidor.isEmpty()) {
                    mensaje = mensajeServidor;
                } else {
                    // Mensaje por defecto si fue fin de juego normal
                    mensaje = "¡Felicidades, " + nombreGanador + "! Has ganado el Timbiriche.";
                }
            } else {
                // Caso de empate
                mensaje = "¡Fin del juego! Resultado: Empate.";
            }

            // Mostrar el mensaje simple de victoria
            JOptionPane.showMessageDialog(null, mensaje, "Fin de Partida", JOptionPane.INFORMATION_MESSAGE);

            // Regresar al lobby después de mostrar el mensaje
            volverAlLobby();
        }
    }

    public void volverAlLobby() {
        modeloApp.cambiarEstado(AplicacionModelo.EstadoNavegacion.LOBBY);
        mostrarLobby();
    }
}
