package acciones;

import java.io.Serializable;

/**
 * DTO que el cliente envía al servidor para unirse a una partida existente
 * usando un código.
 */
public class AccionUnirseAPartida implements Serializable {

    private final String codigoSala;

    public AccionUnirseAPartida(String codigoSala) {
        this.codigoSala = codigoSala;
    }

    public String getCodigoSala() {
        return codigoSala;
    }
}
