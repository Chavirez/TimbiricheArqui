/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.ensambladorservidor;

import componenteArquitectonico.ProcesadorPipesFiltros;
import gestor.ManejadorCliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import interfaz.ITuberiaEntrada;
import interfaz.ITuberiaSalida;
import componenteArquitectonico.ProcesadorPipesFiltros;
import interfaz.IDispatcher;
import interfaz.IReceptorExterno;
import fabricas.IDispatcherFactory;
import fabricas.SocketDispatcherFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
import gestor.GestorPrincipal;
import java.net.Socket;
import receptor.ReceptorDeRed;

/**
 *
 * @author santi
 */
public class EnsambladorServidor {

    public static final int PUERTO = 9090;
    
    
    public static void main(String[] args) {
        

        try {

            
            GestorPrincipal gestor = new GestorPrincipal();
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            Socket socketCliente = serverSocket.accept();
            
            System.out.println("[GESTOR] Nuevo cliente conectado: " + socketCliente.getInetAddress());
            
            PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            
            // El Procesador ahora debe implementar ITuberiaSalida
            ProcesadorPipesFiltros procesador = new ProcesadorPipesFiltros();
            
            // 1. Crear el Manejador, inyectando el procesador como ITuberiaSalida (para ENVÍO)
            ManejadorCliente manejador = new ManejadorCliente(
                    socketCliente,
                    gestor,
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
            

            
            gestor.iniciarServidor(emisor, receptor);
            
            
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }
}
