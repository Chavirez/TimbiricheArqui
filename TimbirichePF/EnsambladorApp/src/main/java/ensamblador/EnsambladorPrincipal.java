package ensamblador;

import componenteArquitectonico.ProcesadorPipesFiltros;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import interfaz.IDispatcher;
import interfaz.IReceptorExterno;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;
import modeloLogico.GestorCliente;
import receptor.ReceptorDeRed;
import emisor.EmisorDeRed;
import interfaces.IGestorJuego;
import itson.modelojuego.interfaces.IServicioJuego;
import modeloJuego.GestorJuego;
import vista.VentanaLobby;

public class EnsambladorPrincipal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Infraestructura de Red
                Socket socket = new Socket("localhost", 9090);
                PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros();
                IDispatcherFactory fabrica = new SocketDispatcherFactory();
                
                // 2. Capa de Lógica de Negocio (ModeloJuego)
                // Esta capa recibe los mensajes de la red (ITuberiaEntrada)
                GestorJuego gestorJuego = new GestorJuego();

                // 3. Conexiones Físicas
                EmisorDeRed emisor = fabrica.crearEmisor(escritor);
                // Conectamos el receptor físico al procesador
                ReceptorDeRed receptor = fabrica.crearReceptor(lector, (IReceptorExterno) procesador, () -> {});
                
                // 4. Cableado de Tuberías
                // Salida: Lógica -> Procesador -> Red
                procesador.conectarDespachador((IDispatcher) emisor);
                gestorJuego.setTuberiaSalida((ITuberiaSalida) procesador);

                // Entrada: Red -> Procesador -> Lógica
                procesador.conectarReceptorDeAplicacion((ITuberiaEntrada) gestorJuego);

                // 5. Iniciar Hilos
                new Thread(emisor).start();
                new Thread(receptor).start();

                // 6. Capa de Vista (MVC)
                // Inyectamos la Lógica en el GestorCliente
                GestorCliente gestorCliente = new GestorCliente((IGestorJuego) gestorJuego);
                
                VentanaLobby lobby = new VentanaLobby((IServicioJuego) gestorCliente);
                gestorCliente.setVentanaLobby(lobby);
                
                lobby.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}