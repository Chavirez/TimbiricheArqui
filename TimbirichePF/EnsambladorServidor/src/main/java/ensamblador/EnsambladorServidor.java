package ensamblador; // O el paquete donde decidas ponerlo

import componenteArquitectonico.ProcesadorPipesFiltros;
import gestor.GestorPrincipal;
import gestor.ManejadorCliente;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import emisor.EmisorDeRed;
import receptor.ReceptorDeRed;
import interfaz.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnsambladorServidor {

    public static final int PUERTO = 9090;

    public static void main(String[] args) {

        GestorPrincipal gestor = new GestorPrincipal();
        
        ExecutorService poolDeHilos = Executors.newCachedThreadPool();

        System.out.println("[ENSAMBLADOR] Iniciando servidor en puerto " + PUERTO);

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {

                Socket socketCliente = serverSocket.accept();
                System.out.println("[ENSAMBLADOR] Conexión entrante: " + socketCliente.getInetAddress());

                try {

                    PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);
                    BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

                    ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros();
                    IDispatcherFactory fabrica = new SocketDispatcherFactory();

 
                    ManejadorCliente manejador = new ManejadorCliente(
                            socketCliente, 
                            gestor, 
                            (ITuberiaSalida) procesador
                    );

                    Runnable alDesconectar = () -> manejador.notificarDesconexion();


                    EmisorDeRed emisor = fabrica.crearEmisor(escritor);
                    ReceptorDeRed receptor = fabrica.crearReceptor(lector, (IReceptorExterno) procesador, alDesconectar);

                    procesador.conectarDespachador((IDispatcher) emisor);            
                    procesador.conectarReceptorDeAplicacion((ITuberiaEntrada) manejador); 

                    poolDeHilos.execute(emisor);
                    poolDeHilos.execute(receptor);

                } catch (IOException e) {
                    System.err.println("[ENSAMBLADOR] Error al configurar cliente: " + e.getMessage());
                    socketCliente.close();
                }
            }
        } catch (IOException e) {
            System.err.println("[ENSAMBLADOR] Error crítico del servidor: " + e.getMessage());
        } finally {
            poolDeHilos.shutdown();
        }
    }
}