package datos;

public class PaqueteDatos {

    private final String tipoClase;
    private final String jsonDatos;

    public PaqueteDatos(String tipoClase, String jsonDatos) {
        this.tipoClase = tipoClase;
        this.jsonDatos = jsonDatos;
    }

    public String getTipoClase() {
        return tipoClase;
    }

    public String getJsonDatos() {
        return jsonDatos;
    }
}