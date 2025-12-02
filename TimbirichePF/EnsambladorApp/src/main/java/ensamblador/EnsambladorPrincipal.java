package ensamblador;

// --- Infraestructura de Red ---
import componenteArquitectonico.ProcesadorPipesFiltros;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import interfaz.IDispatcher;
import interfaz.IReceptorExterno;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
import receptor.ReceptorDeRed;
import emisor.EmisorDeRed;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// --- Lógica Compartida ---
import interfaces.IGestorJuego;
import modeloJuego.GestorJuego;

// --- MVC de Aplicación ---
import modelo.AplicacionModelo;
import controlador.AplicacionControlador;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EnsambladorPrincipal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EnsambladorPrincipal().iniciar();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error fatal al iniciar: " + e.getMessage());
            }
        });
    }

    public void iniciar() throws Exception {
        // -----------------------------------------------------------
        // 1. INFRAESTRUCTURA DE RED (Pipes & Filters)
        // -----------------------------------------------------------
        // Configuración de Sockets
        Socket socket = new Socket("localhost", 9090);
        PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Creación del Procesador Arquitectónico
        ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros();
        
        // Fábrica para crear componentes de red
        IDispatcherFactory fabrica = new SocketDispatcherFactory();
        EmisorDeRed emisor = fabrica.crearEmisor(escritor);
        ReceptorDeRed receptor = fabrica.crearReceptor(lector, (IReceptorExterno) procesador, () -> System.exit(0));

        // Conectar el despachador (Emisor) a la tubería de salida
        procesador.conectarDespachador((IDispatcher) emisor);

        // -----------------------------------------------------------
        // 2. LÓGICA DE NEGOCIO (Backend Local)
        // -----------------------------------------------------------
        GestorJuego gestorJuegoBackend = new GestorJuego();
        
        // Conexiones bidireccionales entre el Gestor de Juego y el Procesador
        gestorJuegoBackend.setTuberiaSalida((ITuberiaSalida) procesador);
        procesador.conectarReceptorDeAplicacion((ITuberiaEntrada) gestorJuegoBackend);

        // Iniciar hilos de red
        new Thread(emisor).start();
        new Thread(receptor).start();

        // -----------------------------------------------------------
        // 3. MVC DE LA APLICACIÓN (UI y Estado)
        // -----------------------------------------------------------
        // El Modelo se conecta con el Backend local
        AplicacionModelo appModelo = new AplicacionModelo((IGestorJuego) gestorJuegoBackend);
        
        // El Controlador recibe el Modelo. Ahora él gestionará las vistas.
        AplicacionControlador appControlador = new AplicacionControlador(appModelo);

        // -----------------------------------------------------------
        // 4. ARRANQUE
        // -----------------------------------------------------------
        System.out.println("[ENSAMBLADOR] Sistema ensamblado correctamente. Iniciando UI...");
        appControlador.iniciarAplicacion();
    }
}