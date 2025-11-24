package pipes;

import filtros.Filter;

public class Pipe<T> {

    private Filter<T, ?> filtroSiguiente;

    public void setFiltroSiguiente(Filter<T, ?> filtro) {
        this.filtroSiguiente = filtro;
    }

    public void pasar(T dato) {
        if (filtroSiguiente != null) {
            filtroSiguiente.procesar(dato);
        }
    }
}
