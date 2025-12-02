package gestor;

import emisor.EmisorDeRed;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import receptor.ReceptorDeRed;

public class GestorPrincipal {

    public static final int PUERTO = 9090;

    private final Map<String, Partida> partidasActivas = Collections.synchronizedMap(new HashMap<>());

    private final ExecutorService poolDeHilos = Executors.newCachedThreadPool();

    public void iniciarServidor(EmisorDeRed emisor, ReceptorDeRed receptor) {
        System.out.println("[GESTOR] Iniciando servidor en el puerto " + PUERTO);
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("[GESTOR] Nuevo cliente conectado: " + socketCliente.getInetAddress());
                poolDeHilos.execute(emisor);
                poolDeHilos.execute(receptor);
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


}