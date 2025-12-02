package eventos;

import entidades.Jugador;
import java.io.Serializable;
import java.util.List;

public class EventoPartidaTerminada implements Serializable {

    private final Jugador ganador; // Puede ser null si es empate
    private final int[] puntajesFinales;
    private final String mensaje;

    public EventoPartidaTerminada(Jugador ganador, int[] puntajesFinales, String mensaje) {
        this.ganador = ganador;
        this.puntajesFinales = puntajesFinales;
        this.mensaje = mensaje;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public int[] getPuntajesFinales() {
        return puntajesFinales;
    }

    public String getMensaje() {
        return mensaje;
    }
}
