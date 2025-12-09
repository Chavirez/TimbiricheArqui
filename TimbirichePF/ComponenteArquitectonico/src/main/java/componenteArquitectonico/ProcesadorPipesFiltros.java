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
        tuberia.agregarFiltro(new EnvioFilter(dispatcher::enviar));
        return tuberia;
    }

    public void conectarReceptorDeAplicacion(ITuberiaEntrada receptor) {
        this.receptorAplicacion = receptor;
        tuberiaEntrada = new pipes.Pipeline<>();
        tuberiaEntrada.agregarFiltro(new filtros.DesempaquetadorFilter(gson))
                .agregarFiltro(new filtros.DeserializadorFilter(gson))
                .agregarFiltro(new filtros.EntregaTuberiaFilter(this::alRecibirDato));
    }
    
    @Override
    public void enviarDato(Object d) {
        if (tuberiaSalida != null) {
            tuberiaSalida.ejecutar(d);
        }
    }
    
    @Override
    public void alRecibirMensaje(String m) {
        if (tuberiaEntrada != null) {
            tuberiaEntrada.ejecutar(m);
        }
    }

    @Override
    public void alRecibirDato(Object dato) {
        if (receptorAplicacion != null) {
            receptorAplicacion.alRecibirDato(dato);
        }

    }
}
