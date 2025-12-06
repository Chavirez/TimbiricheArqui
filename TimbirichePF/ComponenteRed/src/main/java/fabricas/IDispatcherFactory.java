package fabricas;



import java.io.BufferedReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
import interfaz.IReceptorExterno;
import receptor.ComponentesRecepcion;
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
    public ComponentesRecepcion crearSistemaRecepcion(BufferedReader lector, IReceptorExterno procesadorExterno, Runnable alDesconectar);
}