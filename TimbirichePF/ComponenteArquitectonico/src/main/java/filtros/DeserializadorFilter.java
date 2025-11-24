package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import datos.PaqueteDatos;

/**
 * Segundo filtro en el pipeline de recepción. Entrada: PaqueteDatos Salida:
 * Object (el DTO original)
 */
public class DeserializadorFilter extends FilterBase<PaqueteDatos, Object> {

    private final Gson gson;

    public DeserializadorFilter(Gson gson) {
        super();
        this.gson = gson;
    }

    @Override
    public void procesar(PaqueteDatos entrada) {
        try {
            Class<?> tipoClase = Class.forName(entrada.getTipoClase());
            Object dto = gson.fromJson(entrada.getJsonDatos(), tipoClase);

            if (dto != null) {
                pasarAlSiguiente(dto);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DeserializadorFilter] Clase no encontrada: " + entrada.getTipoClase());
        } catch (JsonSyntaxException e) {
            System.err.println("[DeserializadorFilter] Error de sintaxis JSON al deserializar DTO: " + e.getMessage());
        }
    }
}
