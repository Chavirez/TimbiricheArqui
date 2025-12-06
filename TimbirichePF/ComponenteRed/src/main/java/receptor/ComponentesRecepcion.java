package receptor;

public class ComponentesRecepcion {
    public final Runnable lector; 
    public final Runnable procesador; 
    
    public ComponentesRecepcion(Runnable l, Runnable p) { this.lector = l; this.procesador = p; }
}