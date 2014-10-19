package ml.neuralnetwork;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class PaintPanel extends JPanel{
    private int pointCount = 0;
    private Point points[] = new Point[400];
    public double input[] = new double[400];
    private Color currentColor;
    private int pointSize;

    Graphics graphics;
    
    
    public PaintPanel(){
        setBackground(Color.WHITE);
        setDefaultColor();
        setDefaultPointSize();
        addMouseMotionListener(
                new MouseMotionAdapter() {

                    public void mouseDragged(MouseEvent event){

                        if(pointCount < points.length){
                            points[pointCount] = event.getPoint();
                            int inputIndex = points[pointCount].x * 20 + points[pointCount].y;
                            //int inputIndex = points[pointCount].y * 20 / 5 + points[pointCount].x/5;
                            input[inputIndex] = 0.95;
                            
                            //System.out.println("-----------" + points[pointCount].x + "/" + points[pointCount].y + "/" + inputIndex);
                            pointCount++;
                            repaint();
                        }
                    }
        }
      );

    }
    
    

    public void setColor(Color newColor){
        currentColor = newColor;
    }

    public void setDefaultColor(){
        currentColor = Color.BLACK;
    }

    public void setPointSize(int size){
        pointSize = size;
    }

    public void setDefaultPointSize(){
        pointSize = 2;
    }
    
    public void clear()
    {
    	graphics.setColor(Color.green);
    	graphics.fillRect(0, 10, 20, 20);
    	
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        if (graphics == null)
        {
        	graphics = g;
        }
        g.setColor(currentColor);
        for(int i = 0; i < pointCount; i++)
        {
            g.fillOval(points[i].x,points[i].y,pointSize,pointSize);
            
            
        }
        
    }
    
	public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}