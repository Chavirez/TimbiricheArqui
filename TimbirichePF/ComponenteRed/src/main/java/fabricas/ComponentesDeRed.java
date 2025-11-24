package fabricas;

import emisor.EmisorDeRed;
import receptor.ReceptorDeRed;

/**
 * Contenedor para devolver los componentes de I/O ensamblados.
 */
public class ComponentesDeRed {
    public final EmisorDeRed emisor;
    public final ReceptorDeRed receptor;

    public ComponentesDeRed(EmisorDeRed emisor, ReceptorDeRed receptor) {
        this.emisor = emisor;
        this.receptor = receptor;
    }
}