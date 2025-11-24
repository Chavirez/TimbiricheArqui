package eventos;

import entidades.EstadoPartidaDTO;
import java.io.Serializable;

/**
 * DTO que el servidor envía a TODOS los clientes en una partida cada vez que el
 * estado del juego cambia.
 */
public class EventoEstadoActualizado implements Serializable {

    private final EstadoPartidaDTO nuevoEstado;

    public EventoEstadoActualizado(EstadoPartidaDTO nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public EstadoPartidaDTO getNuevoEstado() {
        return nuevoEstado;
    }
}
