package filtros;

import java.util.function.Consumer;

public class EntregaTuberiaFilter extends FilterBase<Object, Void> {
    
    private final Consumer<Object> consumidor; 

    public EntregaTuberiaFilter(Consumer<Object> consumidor){
        this.consumidor = consumidor;
    } 
    
    @Override
    public void procesar(Object entrada){ 
        // Entrega el dato al consumidor (tu método alRecibirDato)
        consumidor.accept(entrada); 
    }
}