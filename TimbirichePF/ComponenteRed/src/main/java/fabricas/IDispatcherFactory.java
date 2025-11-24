package fabricas;


import interfaces.IReceptorExterno;
import java.io.BufferedReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
import receptor.ReceptorDeRed;

public interface IDispatcherFactory {
    
    /**
     * Crea únicamente el componente encargado del envío.
     * Encapsula la creación de la ColaDeMensajes interna.
     * @param escritor
     * @return 
     */
    EmisorDeRed crearEmisor(PrintWriter escritor);

    /**
     * Crea únicamente el componente encargado de la recepción.
     * @param lector
     * @param procesador
     * @param alDesconectar
     * @return 
     */
    ReceptorDeRed crearReceptor(BufferedReader lector, IReceptorExterno procesador, Runnable alDesconectar);
}