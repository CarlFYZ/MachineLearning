package neuralnetwork;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;



public class MainFrame extends JPanel 
{

	double[][] z = null;

	public void init(double[][] z)
	{
		this.z = z;
		setBackground(Color.black);
	}
	/*
	 *Called when repaint
	 */
	public void paint(Graphics g)
	{
		for (int i = 0; i < 20; i++)
		{
			for (int j = 0; j < 20; j++)
			{
				// System.out.print(">" + (int) (z[i][j] * 100) + 100);
				g.setColor(new Color(0, (int) (z[i][j] * 100) + 100, 0));
				g.drawLine(i, j, i, j);

			}
		}
		
	}

	public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }
	


}

