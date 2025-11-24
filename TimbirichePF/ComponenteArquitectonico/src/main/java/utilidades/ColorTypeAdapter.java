package utilidades;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.awt.Color;
import java.io.IOException;

/**
 * Adaptador de Gson para la clase java.awt.Color. Esto es necesario para evitar
 */
public class ColorTypeAdapter extends TypeAdapter<Color> {

    @Override
    public void write(JsonWriter out, Color color) throws IOException {
        if (color == null) {
            out.nullValue();
            return;
        }
        // Escribe el color como un solo valor entero (RGB con alfa)
        out.value(color.getRGB());
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Lee el entero (RGB con alfa) y crea un nuevo Color
        int rgb = in.nextInt();
        return new Color(rgb, true); // true = con canal alfa
    }
}
