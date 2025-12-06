package controlador;

import entidades.EstadoPartidaDTO;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelo.TableroModelo;
import entidades.JuegoConfig;
import entidades.Jugador;
import interfaces.IGestorTablero;
import java.util.List;
import vista.TableroVista;
import vista.VentanaJuego;

public class TableroControlador extends MouseAdapter implements IGestorTablero {

    private final TableroModelo modelo;
    private final IGestorTablero backend;
    private Point primerPuntoCache = null;


    public TableroControlador(TableroModelo modelo, IGestorTablero backend) {
        this.modelo = modelo;
        this.backend = backend;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (modelo.isJuegoTerminado() || modelo.getJugadorActual() == null) return;
        
        Point puntoClic = convertirClickAPunto(e.getX(), e.getY());
        if (puntoClic == null) {
            limpiarSeleccion();
            return;
        }

        if (primerPuntoCache == null) {
            primerPuntoCache = puntoClic;
            modelo.setPuntoSeleccionado(primerPuntoCache);
        } else {
            if (!primerPuntoCache.equals(puntoClic) && sonAdyacentes(primerPuntoCache, puntoClic)) {
                reportarIntentoLinea(primerPuntoCache, puntoClic);
            }
            limpiarSeleccion();
        }
    }

    private void reportarIntentoLinea(Point p1, Point p2) {
        boolean horizontal = (p1.x == p2.x);
        int fila = horizontal ? p1.x : Math.min(p1.x, p2.x);
        int col = horizontal ? Math.min(p1.y, p2.y) : p1.y;
        
        this.reclamarLinea(fila, col, horizontal);
    }

    @Override
    public void reclamarLinea(int fila, int col, boolean horizontal) {
        backend.reclamarLinea(fila, col, horizontal);
    }

    private void limpiarSeleccion() {
        primerPuntoCache = null;
        modelo.setPuntoSeleccionado(null);
    }
    
    private Point convertirClickAPunto(int x, int y) {
        int tamaño = modelo.getTamaño();
        if (tamaño == 0) return null;
        int fila = Math.round((float) (y - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);
        int col = Math.round((float) (x - JuegoConfig.MARGEN) / JuegoConfig.ESPACIO);
        if (fila < 0 || col < 0 || fila >= tamaño || col >= tamaño) return null;
        return new Point(fila, col);
    }

    private boolean sonAdyacentes(Point p1, Point p2) {
        return (Math.abs(p1.y - p2.y) == 1 && p1.x == p2.x) || (Math.abs(p1.x - p2.x) == 1 && p1.y == p2.y);
    }
    
    public void suscribirVentanaJuego(VentanaJuego ventana){
    
        modelo.agregarObservador(ventana);
        
    }
    
    public void suscribirTableroVista(TableroVista tablero){
    
        modelo.agregarObservador(tablero);
        
    }
    
    public String getCodigoSala(){
    
        return modelo.getCodigoSala();
        
    }
    
    public Boolean isJuegoTerminado(){
    
        return modelo.isJuegoTerminado();
    
    }
    
    public Jugador getJugadorActual(){
    
        return modelo.getJugadorActual();
        
    }
    
    public Jugador getJugadorPorId(int id){
    
        return modelo.getJugadorPorId(id);
        
    }
    
    public List<Jugador> getJugadores(){
    
        return modelo.getJugadores();
        
    }
    
    public EstadoPartidaDTO getEstadoActual(){
    
        return modelo.getEstadoActual();
    
    }
    
    public int getTamanio(){
    
        return modelo.getTamaño();
        
    }
    
    public int[][] getCuadrados(){
    
        return modelo.getCuadrados();
        
    }
    
    public int[][] getLineasHorizontales(){
    
        return modelo.getLineasHorizontales();
        
    }
    
    public int[][] getLineasVerticales(){
    
        return modelo.getLineasVerticales();
        
    }
    
    public Point getPuntoSeleccionado(){
    
        return modelo.getPuntoSeleccionado();
        
    }
    
    public int[] getPuntajes(){
    
        return modelo.getPuntajes();
        
    }
}