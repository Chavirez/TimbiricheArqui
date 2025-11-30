package gestor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import componenteArquitectonico.ProcesadorPipesFiltros;
import interfaz.IDispatcher;
import interfaz.IReceptorExterno;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
import receptor.ReceptorDeRed;

public class GestorPrincipal {

    public static final int PUERTO = 9090;

    private final Map<String, Partida> partidasActivas = Collections.synchronizedMap(new HashMap<>());

    private final ExecutorService poolDeHilos = Executors.newCachedThreadPool();

    public void iniciarServidor() {
        System.out.println("[GESTOR] Iniciando servidor en el puerto " + PUERTO);
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("[GESTOR] Nuevo cliente conectado: " + socketCliente.getInetAddress());
                try {
                    PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);
                    BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                    
                    // El Procesador ahora debe implementar ITuberiaSalida
                    ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros(); 
                    
                    // 1. Crear el Manejador, inyectando el procesador como ITuberiaSalida (para ENVÍO)
                    ManejadorCliente manejador = new ManejadorCliente(
                            socketCliente,
                            this,
                            (ITuberiaSalida) procesador // Inyección del contrato de envío
                    );
                    
                    IDispatcherFactory fabrica = new SocketDispatcherFactory();
                    Runnable alDesconectar = () -> manejador.notificarDesconexion();
                    
                    EmisorDeRed emisor = fabrica.crearEmisor(escritor);
                    
                    // 2. Conexión Red -> Pipes: El procesador recibe mensajes de la red
                    ReceptorDeRed receptor = fabrica.crearReceptor(lector, (IReceptorExterno) procesador, alDesconectar);
                    
                    // 3. Conexión Pipes -> Red: El procesador envía mensajes a la red
                    procesador.conectarDespachador((IDispatcher) emisor);
                    
                    // 4. Conexión Pipes -> Aplicación: El procesador entrega mensajes al manejador
                    // Se asume que ProcesadorPipesFiltros tiene este nuevo método:
                    procesador.conectarReceptorDeAplicacion((ITuberiaEntrada) manejador); 
                    
                    poolDeHilos.execute(emisor);
                    poolDeHilos.execute(receptor);
                } catch (IOException e) {
                    System.err.println("[GESTOR] Falla al ensamblar conexión para nuevo cliente: " + e.getMessage());
                    socketCliente.close();
                }
            }
        } catch (IOException e) {
            System.err.println("[GESTOR] Error fatal del servidor: " + e.getMessage());
            poolDeHilos.shutdown();
        }
    }

    public synchronized void crearPartida(ManejadorCliente host) {
        String codigoSala = String.format("S%03d", (partidasActivas.size() + 1));
        System.out.println("[GESTOR] Cliente " + host.getIdJugador() + " creando partida con código: " + codigoSala);
        Partida nuevaPartida = new Partida(codigoSala, this);
        partidasActivas.put(codigoSala, nuevaPartida);
        unirseAPartida(host, codigoSala);
    }

    public synchronized void unirseAPartida(ManejadorCliente jugador, String codigoSala) {
        Partida partida = partidasActivas.get(codigoSala);
        if (partida == null) {
            System.out.println("[GESTOR] Cliente " + jugador.getIdJugador() + " intentó unirse a sala inexistente: " + codigoSala);
            jugador.enviarError("La sala '" + codigoSala + "' no existe.");
            return;
        }
        System.out.println("[GESTOR] Cliente " + jugador.getIdJugador() + " uniéndose a " + codigoSala);
        partida.agregarJugador(jugador);
    }

    public synchronized void eliminarPartida(String codigoSala) {
        partidasActivas.remove(codigoSala);
        System.out.println("[GESTOR] Partida " + codigoSala + " eliminada.");
    }

    public static void main(String[] args) {
        GestorPrincipal gestor = new GestorPrincipal();
        gestor.iniciarServidor();
    }
}