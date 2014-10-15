package neuralnetwork;

import static org.math.array.DoubleArray.increment;

import javax.swing.JFrame;

import ml.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;
import org.math.plot.Plot3DPanel;

import util.la.MatrixUtil;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;

public class Main 
{
	public static void main(String[] args) throws Exception
	{
		MatFileReader matfilereader = new MatFileReader("./data/ex3weights.mat");
		MLDouble theta1ml = (MLDouble)matfilereader.getMLArray("Theta1");
		MLDouble theta2ml = (MLDouble) matfilereader.getMLArray("Theta2");
		Basic2DMatrix theta1 = new Basic2DMatrix(theta1ml.getArray());
		Basic2DMatrix theta2 = new Basic2DMatrix(theta2ml.getArray());
		System.out.println("theta1:" + theta1ml);
		System.out.println("theta2:" + theta2ml);


		MatFileReader matfilereader2 = new MatFileReader("./data/ex3data1.mat");
		MLDouble Xml = (MLDouble) matfilereader2.getMLArray("X");
		
		Basic2DMatrix xMatrix = new Basic2DMatrix(Xml.getArray());
		System.out.println("X:" + Xml);
		System.out.println(xMatrix.rows() + "x" + xMatrix.columns());
		
		MLDouble Yml = (MLDouble) matfilereader2.getMLArray("Y");
		
		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix  X = MatrixUtil.concatenate(MatrixUtil.createVector(m, 1), xMatrix, true);
		
		
		// Forward propagation
		Matrix A = MathFunctions.sigmoid( theta2.multiply( MatrixUtil.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose() )), false)));
		
		System.out.println(A.rows() + "x" + A.columns());
		System.out.println(A.resize(2, 1000));
		
		
		
		  // define your data
        JFrame f = new JFrame();
    	MainFrame panel = new MainFrame();
		f.add("Center",panel);
		f.setSize(100,100);
		f.show();
		for (int k = 0; k<500; k++)
		{
        double[] z = ((BasicVector)(xMatrix.getRow(k*10))).toArray();
        double[][] z2d = new double [20][20];

        for(int i=0; i<20;i++)
        {
           for(int j=0;j<20;j++)
        	   z2d[j][i] = z[(j*20) + i];
        }
		
        panel.init(z2d);
        Thread.sleep(50);

		
		
		f.repaint();
		}
		
	}
}
