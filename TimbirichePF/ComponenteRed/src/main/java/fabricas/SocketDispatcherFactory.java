package fabricas;

import interfaz.IReceptorExterno;
import java.io.BufferedReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
import receptor.ComponentesRecepcion;
import receptor.ProcesadorDeMensajes;
import receptor.ReceptorDeRed;
import utilerias.ColaDeMensajes;

public class SocketDispatcherFactory implements IDispatcherFactory {

    @Override
    public EmisorDeRed crearEmisor(PrintWriter escritor) {
        // La fábrica decide qué implementación de cola usar, ocultando este detalle al exterior.
        ColaDeMensajes cola = new ColaDeMensajes();
        return new EmisorDeRed(cola, escritor);
    }

    @Override
    public ComponentesRecepcion crearSistemaRecepcion(BufferedReader lector, IReceptorExterno procesadorExterno, Runnable alDesconectar) {

        ColaDeMensajes colaDeEntrada = new ColaDeMensajes();

        ReceptorDeRed lectorSocket = new ReceptorDeRed(colaDeEntrada, lector, alDesconectar);

        ProcesadorDeMensajes procesadorLogico = new ProcesadorDeMensajes(colaDeEntrada, procesadorExterno);

        return new ComponentesRecepcion(lectorSocket, procesadorLogico);
    }
    
    
}