package eventos;

import entidades.EstadoPartidaDTO;
import java.io.Serializable;

/**
 * DTO que el servidor envía al cliente cuando se ha unido exitosamente a una
 * partida. Incluye el estado inicial.
 */
public class RespuestaUnionExitosa implements Serializable {

    private final EstadoPartidaDTO estadoInicial;

    public RespuestaUnionExitosa(EstadoPartidaDTO estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoPartidaDTO getEstadoInicial() {
        return estadoInicial;
    }
}
