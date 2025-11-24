package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import datos.PaqueteDatos;
import filtros.FilterBase;

/**
 * Primer filtro en el pipeline de recepción. Entrada: String (JSON crudo)
 * Salida: PaqueteDatos (Objeto)
 */
public class DesempaquetadorFilter extends FilterBase<String, PaqueteDatos> {

    private final Gson gson;

    public DesempaquetadorFilter(Gson gson) {
        super();
        this.gson = gson;
    }

    @Override
    public void procesar(String entrada) {
        try {
            PaqueteDatos paquete = gson.fromJson(entrada, PaqueteDatos.class);
            if (paquete != null && paquete.getTipoClase() != null) {
                pasarAlSiguiente(paquete);
            } else {
                System.err.println("[DesempaquetadorFilter] PaqueteDatos nulo o tipoClase nulo.");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("[DesempaquetadorFilter] Error de sintaxis JSON: " + e.getMessage());
        }
    }
}
