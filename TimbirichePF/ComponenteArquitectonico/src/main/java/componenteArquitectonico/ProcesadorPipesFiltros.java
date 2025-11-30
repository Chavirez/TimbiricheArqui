package componenteArquitectonico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import filtros.*;
import interfaz.ITuberiaSalida;
import interfaz.IDispatcher;
import interfaz.IReceptorExterno;
import interfaz.ITuberiaEntrada;
import pipes.Pipeline;
import utilidades.ColorTypeAdapter;

public class ProcesadorPipesFiltros implements ITuberiaSalida, ITuberiaEntrada, IReceptorExterno {

    private Pipeline<Object> tuberiaSalida;
    private Pipeline<String> tuberiaEntrada;
    private ITuberiaEntrada receptorAplicacion;

    private final Gson gson;

    public ProcesadorPipesFiltros() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .create();
    }

    public void conectarDespachador(IDispatcher dispatcher) {
        if (dispatcher == null) {
            return;
        }
        System.out.println("[ARQUITECTURA] Ensamblando tubería de salida...");
        this.tuberiaSalida = ensamblarTuberiaSalida(dispatcher);
    }

    private Pipeline<Object> ensamblarTuberiaSalida(IDispatcher dispatcher) {
        Pipeline<Object> tuberia = new Pipeline<>();
        tuberia.agregarFiltro(new EmpaquetadorFilter(gson));
        tuberia.agregarFiltro(new SerializadorFilter(gson));
        tuberia.agregarFiltro(new EnvioFilter(dispatcher));

        return tuberia;
    }

    
    public void conectarReceptorDeAplicacion(ITuberiaEntrada receptor) { 
        this.receptorAplicacion = receptor;
        // Creamos la tubería de entrada que termina en el nuevo filtro EntregaTuberia
        tuberiaEntrada = new pipes.Pipeline<>();
        tuberiaEntrada.agregarFiltro(new filtros.DesempaquetadorFilter(gson))
            .agregarFiltro(new filtros.DeserializadorFilter(gson))
            // El filtro final ahora llama a nuestro propio método alRecibirDato
            .agregarFiltro(new filtros.EntregaTuberiaFilter(this));
    }

    // 1. Método implementado de ITuberiaSalida: Inicia el flujo de envío de Acciones.
    @Override
    public void enviarDato(Object d) { 
        if(tuberiaSalida!=null) tuberiaSalida.ejecutar(d); 
    }
    
    // 2. Método implementado de IReceptorExterno: Inicia el flujo de recepción de Red.
    @Override
    public void alRecibirMensaje(String m) { 
        if(tuberiaEntrada!=null) tuberiaEntrada.ejecutar(m); 
    }

    // 3. Método implementado de ITuberiaEntrada: Fin del flujo de recepción.
    // Pasa el objeto deserializado a la aplicación (GestorCliente).
    @Override
    public void alRecibirDato(Object dato) {
        if (receptorAplicacion != null) {
            receptorAplicacion.alRecibirDato(dato);
        }

    } 
}
