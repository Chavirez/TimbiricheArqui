package gestor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class GestorPrincipal {

    private final Map<String, Partida> partidasActivas = Collections.synchronizedMap(new HashMap<>());


    public GestorPrincipal() {
        System.out.println("[GESTOR] Gestor de partidas listo.");
    }

    public synchronized void crearPartida(ManejadorCliente host, int tamanio) {
        String codigoSala = String.format("S%03d", (partidasActivas.size() + 1));
        System.out.println("[GESTOR] Cliente " + host.getIdJugador() + " creando partida: " + codigoSala);
        
        Partida nuevaPartida = new Partida(codigoSala, this, tamanio);
        partidasActivas.put(codigoSala, nuevaPartida);
        
        unirseAPartida(host, codigoSala);
    }

    public synchronized void unirseAPartida(ManejadorCliente jugador, String codigoSala) {
        Partida partida = partidasActivas.get(codigoSala);
        
        if (partida == null) {
            System.out.println("[GESTOR] Error: Sala " + codigoSala + " no existe.");
            jugador.enviarError("La sala '" + codigoSala + "' no existe.");
            return;
        }
        
        System.out.println("[GESTOR] Cliente " + jugador.getIdJugador() + " entrando a " + codigoSala);
        partida.agregarJugador(jugador);
    }

    public synchronized void eliminarPartida(String codigoSala) {
        if (partidasActivas.remove(codigoSala) != null) {
            System.out.println("[GESTOR] Partida " + codigoSala + " finalizada y eliminada.");
        }
    }
}