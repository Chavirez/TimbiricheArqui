package entidades;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa a un jugador en el juego (Entidad de Dominio / DTO). Es una clase
 * inmutable y serializable para ser enviada por la red.
 */
public record Jugador(int id, String nombre, String avatarPath, Color color) implements Serializable {

    /**
     * Constructor compacto para validar que los datos no sean nulos.
     */
    public Jugador    {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        Objects.requireNonNull(avatarPath, "La ruta del avatar no puede ser nula");
        Objects.requireNonNull(color, "El color no puede ser nulo");
    }
}
