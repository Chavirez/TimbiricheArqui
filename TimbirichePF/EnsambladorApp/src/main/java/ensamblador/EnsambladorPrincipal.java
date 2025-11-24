package ensamblador;

import componenteArquitectonico.ProcesadorPipesFiltros;
import interfaces.IReceptorExterno;
import interfaces.IDispatcher;
import interfaces.ITuberiaEntrada; 
import interfaces.ITuberiaSalida; 
import javax.swing.SwingUtilities;
import modeloLogico.GestorCliente;
import modeloLogico.IServicioJuego;
import vista.VentanaLobby;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import emisor.EmisorDeRed;
import receptor.ReceptorDeRed;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;

public class EnsambladorPrincipal {

    private static final String IP_GESTOR_PARTIDAS = "";
    private static final int PUERTO_GESTOR_PARTIDAS = 9090;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("[ENSAMBLADOR] Iniciando ensamblaje del cliente...");
                // La infraestructura ahora retorna ITuberiaSalida (el contrato de envío)
                ITuberiaSalida procesadorEnvio = ensamblarInfraestructura(); 
                
                if (procesadorEnvio == null) {
                    throw new IOException("No se pudo conectar al servidor " + IP_GESTOR_PARTIDAS);
                }
                
                // Ensamblamos la aplicación, pasando el Procesador como capacidad de envío
                ensamblarAplicacion(procesadorEnvio);
                
                System.out.println("[ENSAMBLADOR] Ensamblaje completado. Cliente listo.");
            } catch (Exception e) {
                System.err.println("[ENSAMBLADOR] Error fatal durante el ensamblaje: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                        "Error fatal de conexión: " + e.getMessage() + "\nRevise que el servidor esté encendido.",
                        "Error de Ensamblaje",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    // Retorna ITuberiaSalida, implementada por ProcesadorPipesFiltros
    private static ITuberiaSalida ensamblarInfraestructura() {
        System.out.println("[ENSAMBLADOR] Fase 1: Ensamblando infraestructura...");
        try {
            System.out.println("[ENSAMBLADOR] Conectando a " + IP_GESTOR_PARTIDAS + ":" + PUERTO_GESTOR_PARTIDAS + "...");
            Socket socket = new Socket(IP_GESTOR_PARTIDAS, PUERTO_GESTOR_PARTIDAS);
            System.out.println("[ENSAMBLADOR] Conectado. Creando streams...");
            
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Procesador ahora debe implementar ITuberiaSalida y IReceptorExterno
            ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros(); 
            IDispatcherFactory fabrica = new SocketDispatcherFactory();
            
            Runnable alDesconectar = () -> {
                try {
                    socket.close();
                    System.err.println("[ENSAMBLADOR] Conexión cerrada por hilo de red.");
                } catch (IOException e) { /* Ignorar */ }
            };
            
            EmisorDeRed emisor = fabrica.crearEmisor(escritor);
            
            // El procesador se conecta al receptor de red como IReceptorExterno (flujo de Red -> Pipes)
            ReceptorDeRed receptor = fabrica.crearReceptor(lector, (IReceptorExterno) procesador, alDesconectar);
            
            // El procesador se conecta al despachador de red como IDispatcher (flujo de Pipes -> Red)
            procesador.conectarDespachador((IDispatcher) emisor);
            
            new Thread(emisor, "Hilo-Emisor").start();
            new Thread(receptor, "Hilo-Receptor").start();
            
            System.out.println("[ENSAMBLADOR] Infraestructura ensamblada.");
            
            // Retornamos el procesador como su interfaz de envío (ITuberiaSalida)
            return (ITuberiaSalida) procesador; 
        } catch (IOException e) {
            System.err.println("[ENSAMBLADOR] Falla al crear infraestructura de red: " + e.getMessage());
            return null;
        }
    }

    private static void ensamblarAplicacion(ITuberiaSalida procesadorEnvio) {
        System.out.println("[ENSAMBLADOR] Fase 2: Ensamblando aplicación...");
        
        // 1. Crear el GestorCliente, pasándole la capacidad de ENVÍO (ITuberiaSalida)
        GestorCliente gestor = new GestorCliente(procesadorEnvio);
        
        // 2. Crear la Vista (Lobby)
        VentanaLobby lobby = new VentanaLobby((IServicioJuego) gestor);
        
        // 3. Conectar el Procesador al GestorCliente para el flujo de RECEPCIÓN (Pipes -> MVC)
        // El procesador (ProcesadorPipesFiltros) ahora necesita un nuevo método:
        // conectarReceptorDeAplicacion(ITuberiaEntrada receptor)
        ProcesadorPipesFiltros proc = (ProcesadorPipesFiltros) procesadorEnvio;
        proc.conectarReceptorDeAplicacion((ITuberiaEntrada) gestor);
        
        // 4. Conexión de callbacks
        gestor.setVentanaLobby(lobby);
        
        System.out.println("[ENSAMBLADOR] Aplicación ensamblada.");
        lobby.setVisible(true);
    }
}