package interfaces;

import entidades.EstadoPartidaDTO;
import eventos.EventoPartidaTerminada; // Asegúrate de importar esto

public interface IObservadorJuego {

    void unionExitosa(EstadoPartidaDTO estadoInicial);

    void estadoActualizado(EstadoPartidaDTO nuevoEstado);

    void partidaIniciada();

    void partidaTerminada(EventoPartidaTerminada evento);

    void error(String mensaje);
}
