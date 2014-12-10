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


public class Demo2
{
	
	public static Basic2DMatrix theta1result;
	public static  Basic2DMatrix theta2result;
	
	public static void main(String[] args) throws Exception
	{
		
		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/20141025weekV5.csv")));
		
		int steps = 500000;
		int costCalculationInterval = 500;
		
		boolean verifyGradient = false;
		// model parameters
		double alpha = 128;
		// if lambda >= 0, then parameter regularization is used
		double lambda = 5;
		
		
		// theta1 and theta2 to train
		Matrix theta1 = MatrixFunctions.createRandomMatrix(31, 20, Math.sqrt(10.0 / (31 + 20)));
		Matrix theta2 = MatrixFunctions.createRandomMatrix(5, 32, Math.sqrt(10.0 / (5 + 32)));
		Matrix theta3 = MatrixFunctions.createRandomMatrix(1, 6, Math.sqrt(10.0 / (1 + 6)));
		
//		theta1 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta1")));
//		theta2 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta2")));
//		theta3 = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/Demo2Theta3")));
//		
//		Matrix theta4 = MatrixFunctions.createRandomMatrix(2, 4, Math.sqrt(6.0 / (2 + 4)));
//		Matrix theta5 = MatrixFunctions.createRandomMatrix(1, 3, Math.sqrt(6.0 / (1 + 3)));

		int totalSamples = matrix.rows();
		int endTraining = totalSamples * 9/10;
		int endCrossValidation =  endTraining + totalSamples * 1 /10;
		int nbTest = totalSamples;
		
		// Prepare a tempMatrix for training, cross validation, test
		// Remove y_
		Matrix tempMatrix = matrix.slice(0, 0, matrix.rows(), matrix.columns()-1);
		// Add bias to all
		tempMatrix = MatrixFunctions.concatenate(MatrixFunctions.createVector(tempMatrix.rows(), 1), tempMatrix, true);
		
		// X
		Matrix X = tempMatrix.slice(0, 0, endTraining, tempMatrix.columns());
		
		// CV
		Matrix cvMatrix = tempMatrix.slice(endTraining, 0, endCrossValidation, tempMatrix.columns());
		
		// Test
		Matrix testMatrix = tempMatrix.slice(endCrossValidation, 0, totalSamples, tempMatrix.columns());
		
		System.out.println(X.rows());
		System.out.println(X.columns());

		// number of samples
		int m = X.rows();




		// y Vector and Y matrix(10 * 5000)
		Vector y_ = matrix.getColumn(19).slice(0, endTraining);
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
		
		
		NeuralNetwork.predict(learningThetas, X, y_, false);
		for (int i = 0; i<learningThetas.length; i++ )
		{
			//System.out.println("theta" + (i+1) + " = \n" +  learningThetas[i]);
			FileUtil.writeFile(new File("./data/Demo2theta" + (i+1)), learningThetas[i]);
		}
		
		///////////////////////////////////////////////////////////////
		//                   Start Cross Validation                  // 
		///////////////////////////////////////////////////////////////
		System.out.println("===========================");
		NeuralNetwork.predict(learningThetas, cvMatrix, matrix.getColumn(19).slice(endTraining, endCrossValidation), false);
		
		
	}
	
	
}
