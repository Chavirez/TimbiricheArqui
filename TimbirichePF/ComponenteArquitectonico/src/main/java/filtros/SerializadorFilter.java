package filtros;

import com.google.gson.Gson;
import datos.PaqueteDatos;
import filtros.FilterBase;

public class SerializadorFilter extends FilterBase<PaqueteDatos, String> {

    private final Gson gson;

    public SerializadorFilter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void procesar(PaqueteDatos entrada) {
        String jsonFinal = gson.toJson(entrada);   
        pasarAlSiguiente(jsonFinal);
    }
}