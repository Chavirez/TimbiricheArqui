package utilidades;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;

/**
 * Clase de utilidad para gestionar los recursos del juego (Avatares, Colores).
 */
public class Recursos {

    private static final String[] EXTENSIONES_IMAGEN = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};

    public static List<Color> getColoresDisponibles() {
        return List.of(
                Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE,
                Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK,
                new Color(100, 50, 0), // Marrón
                new Color(150, 150, 150) // Gris
        );
    }

    public static List<String> getAvataresDisponibles() {
        List<String> avatarPaths = new ArrayList<>();
        try {
            URL url = Recursos.class.getResource("/avatars");
            if (url == null) {
                System.err.println("¡ERROR! No se pudo encontrar la carpeta /avatars en los recursos.");
                return Collections.emptyList();
            }
            URI uri = url.toURI();
            if (uri.getScheme().equals("jar")) {
                Map<String, String> env = new HashMap<>();
                try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
                    Path path = fs.getPath("/avatars");
                    try (Stream<Path> walk = Files.walk(path, 1)) {
                        avatarPaths = walk
                                .filter(Files::isRegularFile)
                                .map(Path::toString)
                                .filter(Recursos::esFormatoImagenSoportado)
                                .map(p -> p.startsWith("/") ? p : "/" + p)
                                .collect(Collectors.toList());
                    }
                }
            } else {
                File carpetaAvatares = new File(uri);
                File[] archivos = carpetaAvatares.listFiles();
                if (archivos != null) {
                    for (File archivo : archivos) {
                        if (archivo.isFile() && esFormatoImagenSoportado(archivo.getName())) {
                            avatarPaths.add("/avatars/" + archivo.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los avatares: " + e.getMessage());
        }
        return avatarPaths;
    }

    public static String getNombreAmigableAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return null;
        }
        String nombreArchivo = avatarPath.substring(avatarPath.lastIndexOf('/') + 1);
        int dotIndex = nombreArchivo.lastIndexOf('.');
        if (dotIndex > 0) {
            nombreArchivo = nombreArchivo.substring(0, dotIndex);
        }
        nombreArchivo = nombreArchivo.replace("_", " ").replace("-", " ");
        if (nombreArchivo.length() > 0) {
            return Character.toUpperCase(nombreArchivo.charAt(0)) + nombreArchivo.substring(1);
        }
        return nombreArchivo;
    }

    private static boolean esFormatoImagenSoportado(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        for (String ext : EXTENSIONES_IMAGEN) {
            if (lowerCaseFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static ImageIcon loadScaledAvatar(String resourcePath, int width, int height) {
        try {
            URL imgUrl = Recursos.class.getResource(resourcePath);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar avatar desde " + resourcePath + ": " + e.getMessage());
        }
        return null;
    }
}
