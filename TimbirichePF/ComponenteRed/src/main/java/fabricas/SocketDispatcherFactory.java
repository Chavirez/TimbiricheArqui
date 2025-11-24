package fabricas;

import interfaces.IReceptorExterno;
import java.io.BufferedReader;
import java.io.PrintWriter;
import emisor.EmisorDeRed;
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
    public ReceptorDeRed crearReceptor(BufferedReader lector, IReceptorExterno procesador, Runnable alDesconectar) {
        return new ReceptorDeRed(procesador, lector, alDesconectar);
    }
    
}