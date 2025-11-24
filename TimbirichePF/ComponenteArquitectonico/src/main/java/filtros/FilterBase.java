package filtros;

import pipes.Pipe;

public abstract class FilterBase<I, O> implements Filter<I, O> {

    protected Pipe<O> pipeDeSalida;

    public FilterBase() {
        this.pipeDeSalida = new Pipe<>();
    }

    @Override
    public void conectarAlSiguiente(Filter<O, ?> filtroSiguiente) {
        this.pipeDeSalida.setFiltroSiguiente(filtroSiguiente);
    }

    protected void pasarAlSiguiente(O datoDeSalida) {
        this.pipeDeSalida.pasar(datoDeSalida);
    }
}
