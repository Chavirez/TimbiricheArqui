package filtros;

import java.util.function.Consumer;

public class EnvioFilter extends FilterBase<String, Void> {

    private final Consumer<String> estrategiaEnvio;

    public EnvioFilter(Consumer<String> estrategiaEnvio) {
        super();
        if (estrategiaEnvio == null) {
            throw new IllegalArgumentException("EnvioFilter requiere una estrategia de envío válida.");
        }
        this.estrategiaEnvio = estrategiaEnvio;
    }

    @Override
    public void procesar(String entrada) {
        // Ejecuta la acción inyectada (ej. enviar por red)
        estrategiaEnvio.accept(entrada);
    }
}