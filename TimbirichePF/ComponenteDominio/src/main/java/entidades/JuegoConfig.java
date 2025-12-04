package entidades;

import java.io.Serializable;

/**
 * Contiene constantes de configuración globales para el juego Timbiriche.
 * Estando en el componente de dominio, tanto el cliente como el servidor pueden
 * usar estas constantes para consistencia.
 */
public class JuegoConfig implements Serializable {

    private JuegoConfig() {
    }

    public static final int TAMANIO_TABLERO = 10;
    public static final int ANCHO_LINEA = 4;
    public static final int MARGEN = 25;
    public static final int ESPACIO = 50;
    public static final int RADIO_PUNTO = 7;
}
