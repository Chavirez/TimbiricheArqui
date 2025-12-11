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
        
        if (!(e.getSource() instanceof TableroVista)) return;
        TableroVista vista = (TableroVista) e.getSource();

        Point puntoClic = convertirClickAPunto(e.getX(), e.getY(), vista);
        
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

    private Point convertirClickAPunto(int mouseX, int mouseY, TableroVista vista) {
        int tamaño = modelo.getTamaño();
        if (tamaño == 0) return null;

        double espacio = vista.getEspacioActual();
        int inicioX = vista.getInicioX();
        int inicioY = vista.getInicioY();
        int radioTolerancia = JuegoConfig.RADIO_PUNTO * 2; // Margen de error para el clic

        // Cálculo inverso: (Coordenada Mouse - Margen Inicial) / Espacio Dinámico
        // Usamos Math.round para encontrar el punto más cercano
        int col = (int) Math.round((mouseX - inicioX) / espacio);
        int fila = (int) Math.round((mouseY - inicioY) / espacio);

        // Validar que esté dentro de la matriz
        if (fila < 0 || col < 0 || fila >= tamaño || col >= tamaño) return null;

        // Validación extra opcional: Asegurar que el clic no esté demasiado lejos del punto
        // Calcula la posición exacta en píxeles de ese punto candidato
        int pixelX = (int) (inicioX + col * espacio);
        int pixelY = (int) (inicioY + fila * espacio);
        
        // Si el clic está muy lejos del centro del punto, lo ignoramos (mejora la precisión)
        if (Math.abs(mouseX - pixelX) > radioTolerancia || Math.abs(mouseY - pixelY) > radioTolerancia) {
            return null;
        }

        return new Point(fila, col);
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