package filtros;

public interface Filter<I, O> {

    void procesar(I entrada);

    void conectarAlSiguiente(Filter<O, ?> filtroSiguiente);
}
