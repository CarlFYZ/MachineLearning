package demo.neuralnetwork;

import java.io.File;
import java.io.FileInputStream;

import ml.common.util.FileUtil;
import ml.core.linearalgebra.MatrixFunctions;
import ml.neuralnetwork.NeuralNetwork;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;


public class Demo2
{
	
	public static Basic2DMatrix theta1result;
	public static  Basic2DMatrix theta2result;
	
	public static void main(String[] args) throws Exception
	{
		
		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/20141025weekV5.csv")));
		
		int steps = 100000;
		int costCalculationInterval = 100;
		
		boolean verifyGradient = false;
		// model parameters
		double alpha = 0.05;
		// if lambda >= 0, then parameter regularization is used
		double lambda = 1;
		
		
		// theta1 and theta2 to train
		Matrix theta1 = MatrixFunctions.createRandomMatrix(40, 20, Math.sqrt(6.0 / (20 + 19)));
		Matrix theta2 = MatrixFunctions.createRandomMatrix(10, 41, Math.sqrt(6.0 / (10 + 41)));
		Matrix theta3 = MatrixFunctions.createRandomMatrix(1, 11, Math.sqrt(6.0 / (1 + 11)));
		
//		theta1 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta1")));
//		theta2 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta2")));
//		theta3 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta3")));
//		
//		Matrix theta4 = MatrixFunctions.createRandomMatrix(2, 4, Math.sqrt(6.0 / (2 + 4)));
//		Matrix theta5 = MatrixFunctions.createRandomMatrix(1, 3, Math.sqrt(6.0 / (1 + 3)));

		
		// X
		Matrix xMatrix = matrix.slice(0, 0, matrix.rows(), matrix.columns()-1);
		// should be m * 20
		
		System.out.println(xMatrix.rows());
		System.out.println(xMatrix.columns());


		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), xMatrix, true);

		// y Vector and Y matrix(10 * 5000)
		Vector y_ = matrix.getColumn(19);
		// System.out.println(y);

//		double[][] Ys = new double[1][m];
//		for (int i = 0; i < y_.length(); i++)
//		{
//			Ys[(int) y_.get(i) - 1][i] = 1;
//		}
		Matrix Y = new Basic2DMatrix(y_.toRowMatrix());
		
		// The thetas to learn, it starts from the original input and changes every iteration
		Matrix[] learningThetas = new Matrix[] {theta1, theta2, theta3};// theta4, theta5};

		
		
		

		///////////////////////////////////////////////////////////////
		//                   Start training                          // 
		///////////////////////////////////////////////////////////////
		learningThetas = NeuralNetwork.gradientDescent(steps, costCalculationInterval, verifyGradient, alpha, lambda, m, X, y_, Y, learningThetas);
		
		
		NeuralNetwork.predict(learningThetas, X, y_, true);
		for (int i = 0; i<learningThetas.length; i++ )
		{
			System.out.println("theta" + (i+1) + " = \n" +  learningThetas[i]);
			FileUtil.writeFile(new File("./data/Demo2theta" + (i+1)), learningThetas[i]);
		}
		
		
	}
	
	
}
