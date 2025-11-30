package filtros;

import interfaz.IDispatcher;

public class EnvioFilter extends FilterBase<String, Void> {

    private final IDispatcher dispatcher;

    public EnvioFilter(IDispatcher dispatcher) {
        super();
        if (dispatcher == null) {
            throw new IllegalArgumentException("EnvioFilter requiere un IDispatcher no nulo.");
        }
        this.dispatcher = dispatcher;
    }

    @Override
    public void procesar(String entrada) {
        dispatcher.enviar(entrada);
    }
}