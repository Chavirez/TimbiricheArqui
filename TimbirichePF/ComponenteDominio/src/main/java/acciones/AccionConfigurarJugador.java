package acciones;

import entidades.Jugador;
import java.io.Serializable;

/**
 * DTO que el cliente envía al servidor después de que el usuario ha configurado
 * su perfil (nombre, avatar, color).
 */
public class AccionConfigurarJugador implements Serializable {

    private final Jugador jugador;

    public AccionConfigurarJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Jugador getJugador() {
        return jugador;
    }
}
