package filtros;

import com.google.gson.Gson;
import datos.PaqueteDatos;
import filtros.FilterBase;

public class EmpaquetadorFilter extends FilterBase<Object, PaqueteDatos> {

    private final Gson gson;

    public EmpaquetadorFilter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void procesar(Object entrada) {
        if (entrada == null) {
            return;
        }
        String tipo = entrada.getClass().getName();
        String jsonPayload = gson.toJson(entrada);
        PaqueteDatos paquete = new PaqueteDatos(tipo, jsonPayload);
        pasarAlSiguiente(paquete);
    }
}
