package modeloLogico;

import entidades.Jugador;
import modelo.TableroModelo;

/**
 * Contiene la lógica de validación del cliente. Su responsabilidad es evitar
 * enviar solicitudes inválidas al servidor.
 */
public class LogicaCliente {

    /**
     * Valida si es el turno del jugador local.
     * @param modelo
     * @param jugadorLocal
     * @return 
     */
    public boolean esMiTurno(TableroModelo modelo, Jugador jugadorLocal) {
        if (modelo == null || jugadorLocal == null || modelo.getJugadorActual() == null) {
            return false;
        }

        return modelo.getJugadorActual().id() == jugadorLocal.id();
    }
}
