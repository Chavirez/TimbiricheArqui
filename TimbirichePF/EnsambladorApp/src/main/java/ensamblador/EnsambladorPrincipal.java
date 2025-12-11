package ensamblador;

// --- Infraestructura de Red ---
import componenteArquitectonico.ProcesadorPipesFiltros;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import interfaz.IDispatcher;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
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
import receptor.ComponentesRecepcion;

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

        Socket socket = new Socket("localhost", 9090);
        PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros();
        
        IDispatcherFactory fabrica = new SocketDispatcherFactory();
        EmisorDeRed emisor = fabrica.crearEmisor(escritor);
        
        ComponentesRecepcion rx = fabrica.crearSistemaRecepcion(lector, procesador,() -> System.exit(0));


        procesador.conectarDespachador((IDispatcher) emisor);

        GestorJuego gestorJuegoBackend = new GestorJuego();
        
        gestorJuegoBackend.setTuberiaSalida((ITuberiaSalida) procesador);
        procesador.conectarReceptorDeAplicacion((ITuberiaEntrada) gestorJuegoBackend);

        new Thread(emisor).start();
        new Thread(rx.lector).start();
        new Thread(rx.procesador).start();

        AplicacionModelo appModelo = new AplicacionModelo((IGestorJuego) gestorJuegoBackend);

        AplicacionControlador appControlador = new AplicacionControlador(appModelo);

        System.out.println("[ENSAMBLADOR] Sistema ensamblado correctamente. Iniciando UI...");
        appControlador.iniciarAplicacion();
        
    }
}