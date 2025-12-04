package utilidades;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MoverVentana extends MouseAdapter {
    private final JFrame frame;
    private int xMouse, yMouse;

    public MoverVentana(JFrame frame, JComponent componenteDeAgarre) {
        this.frame = frame;
        componenteDeAgarre.addMouseListener(this);
        componenteDeAgarre.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent evt) {

        xMouse = evt.getX();
        yMouse = evt.getY();
    }

    @Override
    public void mouseDragged(MouseEvent evt) {

        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        
        this.frame.setLocation(x - xMouse, y - yMouse);
    }
}