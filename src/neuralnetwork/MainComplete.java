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

public class MainComplete 
{
	public static void main(String[] args) throws Exception
	{
		MatFileReader matfilereader = new MatFileReader("./data/ex4weights.mat");
		MLDouble theta1ml = (MLDouble)matfilereader.getMLArray("Theta1");
		MLDouble theta2ml = (MLDouble) matfilereader.getMLArray("Theta2");
		Basic2DMatrix theta1 = new Basic2DMatrix(theta1ml.getArray());
		Basic2DMatrix theta2 = new Basic2DMatrix(theta2ml.getArray());
		System.out.println("theta1:" + theta1ml);
		System.out.println("theta2:" + theta2ml);


		MatFileReader matfilereader2 = new MatFileReader("./data/ex4data1.mat");
		MLDouble Xml = (MLDouble) matfilereader2.getMLArray("X");
		
		// X
		Basic2DMatrix xMatrix = new Basic2DMatrix(Xml.getArray());
		System.out.println("X:" + Xml);
		// showSamples(xMatrix);
		
		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix  X = MatrixUtil.concatenate(MatrixUtil.createVector(m, 1), xMatrix, true);

		// y Vector and Y matrix(10 * 5000)
		MLDouble yml = (MLDouble) matfilereader2.getMLArray("y");
		BasicVector y = (BasicVector)(new Basic2DMatrix(yml.getArray())).getColumn(0);
		System.out.println(y);

		double [][] Ys = new double[10][5000];
		for (int i =0; i<y.length() ; i++)
		{
			Ys[(int)y.get(i)-1][i] = 1;
		}
		Matrix Y = new Basic2DMatrix(Ys);

//		System.out.println(y);
//		System.out.println(Y);

		// Forward propagation
		Matrix A = MathFunctions.sigmoid( theta2.multiply( MatrixUtil.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose() )), false)));

//		System.out.println(A.rows() + "x" + A.columns());
//		System.out.println(A.resize(2, 1000));
		
		// Cost function
		Matrix zero10_5000 = MatrixUtil.initialMatrix(10, 5000, 0);
		Matrix one10_5000 = MatrixUtil.initialMatrix(10, 5000, 1);
		
		
		Matrix fo1 = (zero10_5000.subtract(Y)).hadamardProduct(MathFunctions.log(A));
		Matrix fo2 = (one10_5000.subtract(Y)).hadamardProduct(MathFunctions.log(one10_5000.subtract(A)));
		// Cost
		double J = fo1.subtract(fo2).sum()/m;
	
		// Regularized cost
		
		
		Matrix trans400 = MatrixUtil.concatenate(MatrixUtil.createVector(400, 0).toRowMatrix(),  MatrixUtil.initialDiagonalMatrix(400, 1), false);
		Matrix trans25 = MatrixUtil.concatenate(MatrixUtil.createVector(25, 0).toRowMatrix(),  MatrixUtil.initialDiagonalMatrix(25, 1), false);
		
		Matrix theta1a = theta1.multiply(trans400);
		Matrix theta2a = theta2.multiply(trans25);
		J = J + ( theta1a.hadamardProduct(theta1a).sum() + theta2a.hadamardProduct(theta2a).sum() 
				//- theta1.getColumn(0).hadamardProduct(theta1.getColumn(0)).sum()
				//- theta2.getColumn(0).hadamardProduct(theta2.getColumn(0)).sum()
				)/(2*m);
		
		System.out.println(J);
		//X.resize(arg0, arg1, arg2)
		
		// sigmoid gradient(derivative)
		
		System.out.println(MathFunctions.sigmoidDerivative(MatrixUtil.initialDiagonalMatrix(15, -100)));
	}

	protected static void showSamples(Basic2DMatrix xMatrix) throws InterruptedException
	{
		// define your data
        JFrame f = new JFrame();
    	MainFrame panel = new MainFrame();
		f.add("Center",panel);
		f.setSize(100,100);
		f.show();
		for (int k = 0; k<50; k++)
		{
			double[] z = ((BasicVector) (xMatrix.getRow(k * 100))).toArray();
			double[][] z2d = new double[20][20];

			for (int i = 0; i < 20; i++)
			{
				for (int j = 0; j < 20; j++)
					z2d[j][i] = z[(j * 20) + i];
			}

			panel.init(z2d);
			Thread.sleep(50);
			f.repaint();
		}
	}
}
