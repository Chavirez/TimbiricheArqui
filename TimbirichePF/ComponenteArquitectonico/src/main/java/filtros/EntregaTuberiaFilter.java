/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filtros;

/**
 *
 * @author NaderCroft
 */
public class EntregaTuberiaFilter extends FilterBase<Object, Void>{
    private final interfaces.ITuberiaEntrada receptor; 

    public EntregaTuberiaFilter(interfaces.ITuberiaEntrada r){
        this.receptor=r;
    } 
    
    @Override
    public void procesar(Object i){ 
        // Llama al método del ProcesadorPipesFiltros
        receptor.alRecibirDato(i); 
    }
}
