package pipes;

import filtros.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una cadena de filtros (pipeline). [CORREGIDO para permitir filtros
 * con diferentes tipos de entrada/salida]
 */
public class Pipeline<T> {

    private final List<Filter<?, ?>> filtros = new ArrayList<>();

    public Pipeline<T> agregarFiltro(Filter<?, ?> filtro) {
        if (!filtros.isEmpty()) {
            // Conecta el filtro anterior a este nuevo
            Filter ultimoFiltro = filtros.get(filtros.size() - 1);

            // Usamos un 'helper' para manejar los tipos genéricos
            conectarFiltros(ultimoFiltro, filtro);
        }
        filtros.add(filtro);
        return this;
    }

    /**
     * Helper genérico para conectar dos filtros de forma segura.
     */
    private <A, B> void conectarFiltros(Filter<A, B> filtroAnterior, Filter<B, ?> filtroNuevo) {
        filtroAnterior.conectarAlSiguiente(filtroNuevo);
    }

    public void ejecutar(T datoInicial) {
        if (!filtros.isEmpty()) {
            // Inicia el procesamiento en el primer filtro
            Filter<T, ?> primerFiltro = (Filter<T, ?>) filtros.get(0);
            primerFiltro.procesar(datoInicial);
        }
    }
}
