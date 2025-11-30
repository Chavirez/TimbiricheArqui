/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filtros;

import interfaz.ITuberiaEntrada;

/**
 *
 * @author NaderCroft
 */
public class EntregaTuberiaFilter extends FilterBase<Object, Void>{
    private final ITuberiaEntrada receptor; 

    public EntregaTuberiaFilter(ITuberiaEntrada r){
        this.receptor=r;
    } 
    
    @Override
    public void procesar(Object i){ 
        // Llama al método del ProcesadorPipesFiltros
        receptor.alRecibirDato(i); 
    }
}
