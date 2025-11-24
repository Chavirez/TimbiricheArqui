package eventos;

import java.io.Serializable;

/**
 * DTO genérico que el servidor envía al cliente cuando ocurre un error (ej.
 * código de sala incorrecto, no es tu turno).
 */
public class EventoError implements Serializable {

    private final String mensaje;

    public EventoError(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
