package modeloLogico;

import entidades.EstadoPartidaDTO;

/**
 * Define el contrato para el cliente de juego. Es la interfaz que el
 * GestorCliente usa para notificar a la UI (o a sí mismo en el hilo de Swing)
 * sobre eventos recibidos del servidor. [Equivalente a la interfaz interna
 */
public interface IClienteJuego {

    void onUnionExitosa(EstadoPartidaDTO estadoInicial);

    void onEstadoPartidaActualizado(EstadoPartidaDTO nuevoEstado);

    void onConfiguracionRequerida();

    void onPartidaIniciada();

    void onMostrarError(String mensaje);
}
