package neuralnetwork;

import ml.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

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
		
		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix  X = MatrixUtil.concatenate(MatrixUtil.createVector(m, 1), xMatrix, true);
		
		// Forward propagation
		Matrix Y = MathFunctions.sigmoid( theta2.multiply( MatrixUtil.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose() )), false)));
		
		System.out.println(Y.rows() + "x" + Y.columns());
		System.out.println(Y.resize(10, 1000));
		
	}
}
