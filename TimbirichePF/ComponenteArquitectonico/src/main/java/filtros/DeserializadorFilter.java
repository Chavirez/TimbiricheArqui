package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import datos.PaqueteDatos;
// NO IMPORTAMOS NADA DE ACCIONES PARA EVITAR ERRORES

public class DeserializadorFilter extends FilterBase<PaqueteDatos, Object> {

    private final Gson gson;

    public DeserializadorFilter(Gson gson) {
        super();
        this.gson = gson;
    }

    @Override
    public void procesar(PaqueteDatos entrada) {
        try {
        
            
            if (entrada.getTipoClase().contains("AccionIniciarPartida")) {
                // Truco: Cargamos la clase dinámicamente solo si existe
                Class<?> clase = Class.forName("acciones.AccionIniciarPartida");
                Object dto = clase.getDeclaredConstructor().newInstance();
                pasarAlSiguiente(dto);
                return;
            }

            
            Class<?> tipoClase = Class.forName(entrada.getTipoClase());
            Object dto = gson.fromJson(entrada.getJsonDatos(), tipoClase);

            if (dto != null) {
                pasarAlSiguiente(dto);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DeserializadorFilter] CLASE NO ENCONTRADA: " + entrada.getTipoClase());
            System.err.println(">>> ¡HAZ CLEAN AND BUILD EN COMPONENTE DOMINIO! <<<");
        } catch (Exception e) {
            System.err.println("[DeserializadorFilter] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}