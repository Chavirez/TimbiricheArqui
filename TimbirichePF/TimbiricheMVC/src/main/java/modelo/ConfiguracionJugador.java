package modelo;

import java.awt.Color;

/**
 * Un POJO mutable (un DTO local) usado por la VentanaConfiguracion para
 * construir un perfil de jugador antes de enviarlo al servidor.
 */
public class ConfiguracionJugador {

    private String nombre;
    private String avatarPath;
    private Color color;

    public ConfiguracionJugador(String nombre, String avatarPath, Color color) {
        this.nombre = nombre;
        this.avatarPath = avatarPath;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
