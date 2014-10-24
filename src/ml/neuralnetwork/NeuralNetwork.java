package ml.neuralnetwork;

import ml.core.linearalgebra.MatrixFunctions;
import ml.core.math.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class NeuralNetwork
{

	/**
	 * Cost function of NN
	 * @param thetas the weight
	 * @param m number of samples
	 * @param X all samples
	 * @param Y the expected result
	 * @param lambda regularize parameters, enabled if lambda > 0
	 * @return
	 */
	public static double cost(Matrix thetas[], int m, Matrix X, Matrix Y,	double lambda)
	{
		
		
		Matrix A = X.transpose();
		for (int i = 0; i < thetas.length; i++)
		{
			A = MathFunctions.sigmoid(thetas[i].multiply(A));
			if (i < thetas.length - 1)
			{
				A = MatrixFunctions.addBias(A, false);
			}
		}
		
		// The logarithm cost function
		// y * log (h(x)) - (1-y)log(1-h(x))
		Matrix fo1 = (Y.multiply(-1)).hadamardProduct(MathFunctions.log(A));
		Matrix fo2 = (MatrixFunctions.subtract(1,Y)).hadamardProduct(MathFunctions.log(MatrixFunctions.subtract(1,A)));
		// Cost
		double J = fo1.subtract(fo2).sum() / m;
		
		
		// Cost function include Regularize parameter
		if (lambda >0)
		{
			Matrix[] thetasReg = new Matrix[thetas.length];
			
			for (int i = 0;i<thetas.length;i++)
			{
				thetasReg[i] = thetas[i].sliceBottomRight(0, 1);
			}
	
			double sumRegularized = 0;
			for (int i = 0;i<thetas.length;i++)
			{
				sumRegularized = sumRegularized + thetasReg[i].hadamardProduct(thetasReg[i]).sum();
			}
			J = J + sumRegularized / (2 * m) * lambda;
		}
		return J;
	}

	/**
	 * Forward propagation of NN
	 * @param thetas
	 * @param zs
	 * @param as
	 */
	public static void forwardPropagation(Matrix[] thetas, Vector[] zs, Vector[] as)
	{
		for (int i =0;i <thetas.length ;i++)
		{
			zs[i] = thetas[i].multiply(as[i]);
			if (i== thetas.length -1)
			{
				as[i+1] = MathFunctions.sigmoid(zs[i]);
			}
			else
			{
				as[i+1] = MatrixFunctions.addBias(MathFunctions.sigmoid(zs[i]));
			}
		}
	}

	/**
	 * Learn the NN by gradient descent
	 * @param steps
	 * @param costCalculationInterval
	 * @param verifyGradient
	 * @param alpha
	 * @param lambda
	 * @param m
	 * @param X
	 * @param y_
	 * @param Y
	 * @param learningThetas
	 * @return
	 */
	public static Matrix[] gradientDescent(int steps, int costCalculationInterval, boolean verifyGradient, double alpha, double lambda, int m, Matrix X, BasicVector y_, Matrix Y,
			Matrix[] learningThetas)
	{
		for (int o = 1; o <= steps ; o++)
		{
			// We need to cumulative the derivatives over all the samples and then average
			// Here we initial all them to zeros
			Matrix averageDerivatives[] = new Matrix[learningThetas.length];
			for (int i = 0; i< learningThetas.length ; i ++)
			{
				averageDerivatives[i] =  MatrixFunctions.createMatrix(learningThetas[i].rows(), learningThetas[i].columns(), 0);
			}
	
			// Here we calculate the derivatives from all samples
			for (int i = 0; i < m; i++)
			{
				// This is the core part of NN, processing one sample 
				Matrix[] result = processOneSample(learningThetas,X.getRow(i), Y.getColumn(i));
	
				// average derivative
				for (int j = 0; j< learningThetas.length ; j ++)
				{
					averageDerivatives[j] = averageDerivatives[j].add(result[j].divide(m));
				}
	
			}
	
	
			
			// Calculate regularized derivative (if configured) and apply it to the learningThetas
			Matrix derivativeOfRregularizationTerm[] = new Matrix [learningThetas.length];
			if (lambda >0)
			{
				for (int j = 0; j< learningThetas.length ; j ++)
				{
					derivativeOfRregularizationTerm[j] = learningThetas[j].multiply(lambda/(m)).transform(MatrixFunctions.ClearColumn0);
					averageDerivatives[j] = averageDerivatives[j].add( derivativeOfRregularizationTerm[j]);
				}
			}
	
			//verify gradient, run this before update learningThetas, disable it before real training
			if (verifyGradient)
			{
				verifyGradient(lambda, m, X, Y, learningThetas, averageDerivatives);
			}
	
			for (int j = 0; j< learningThetas.length ; j ++)
			{
				learningThetas[j] = learningThetas[j].subtract(averageDerivatives[j].multiply(alpha));
			}
	
			
			// Calculate cost and print, not necessary for final product.
			if ( o %costCalculationInterval == 0)
			{
				double J = cost(learningThetas, m, X, Y, lambda);
				System.out.println("Step = " + o + " J= " + J);
				predict( learningThetas, X, y_);
			}
	
		}
		return learningThetas;
	}

	/**
	 * Now we can used the learnt thetas to test the result 
	 * @param X
	 * @param y
	 * @param learningThetas
	 */
	public static void predict(Matrix[] learningThetas, Matrix X, BasicVector y )
	{
		Matrix predict = MathFunctions.sigmoid(learningThetas[0].multiply(X.transpose())); //output layer
		for (int i = 1; i< learningThetas.length; i++)
		{
			// System.out.println("step " + i);
			//predict = MathFunctions.sigmoid(theta2.multiply(MatrixFunctions.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose())), false)));
			predict = MathFunctions.sigmoid(learningThetas[i].multiply(MatrixFunctions.addBias(predict, false)));
		}
		
		//System.out.println(predict);
		
		//System.out.println(predict.rows() + " " + predict.columns());
		int correctCount = 0;
		for(int n=0;n<5000;n++)
		{
			Vector onePredict = predict.getColumn(n);
			int highest =0;
			for (int p=1;p<10;p++)
			{
				if(onePredict.get(highest) < onePredict.get(p))
				{
					highest = p;
				}
				
			}
			if (highest==y.get(n)-1)
			{
				correctCount++;
			}
		}
		System.out.println(correctCount);
	}

	/**
	 * Training with one sample
	 * Be careful with the index number here
	 * Assume following structure (four layers L = 4)
	 * input layer (layer 1) -> layer 2 -> layer 3 -> output layer (layer 4	)
	 *              z1, a1,   theta1
	 * we use _ to denote a vector (column vector)
	 * @param thetas [t1, t2, t3 ...] thetas[0] corresponds to theta1, with is from layer 1 to layer 2.
	 * @param x_ the input with bias
	 * @param y_ the known result
	 * @return the thetas we learn from one sample in one iteration
	 */
	public static Matrix[] processOneSample(Matrix[] thetas, Vector x_, Vector y_)
	{
		// zs are the waited input of each layer
		// So it starts with z2, which is zs[0]
		// [z2, z3, z4 ...]
		Vector[] zs = new Vector[thetas.length];
		
		// as are the action of each layer
		// So it starts with a1 which is the input x_, so a2 is the sigmoid of z2, then add bias 1
		// [a1, a2 ,a3 ...]
		Vector[] as = new Vector[thetas.length + 1];
		as[0] = x_;
		
		// Forward propagation
		
		forwardPropagation(thetas, zs, as);
		
		// Backward propagation
		// The derivative of each layer
		// [d2,d3,d4...]
		Vector delta[] = new Vector[thetas.length];
		Matrix derivative[] = new Matrix[thetas.length];
		// The first derivative we calculate is for the output layer
		// The formula depends on the cost function, we take it out of the loop
		delta[thetas.length -1] = as[thetas.length].subtract(y_);
		// as starts from a1
		derivative[thetas.length -1] = delta[thetas.length -1].toColumnMatrix().multiply(as[thetas.length -1].toRowMatrix());
		// Then we calculae L -1, L-2
		for (int i = thetas.length-1; i>0; i--)
		{
	
			// This is the core of backward propagation
		
			// 3 Take the thetas between L+1, L, multiply the derivative from L+1, multiply the z from L
			// Based on our index this is 
			// theta 2 T* dev 3 .* g'z2
			// which is theta[i], dev[i], z[i-1]
			Vector tempDelta;
			if (i == thetas.length-1)
			{
				tempDelta = delta[i];
			}
			else
			{
				tempDelta = delta[i].sliceRight(1);
				
			}
			delta[i-1] = thetas[i].transpose().multiply(tempDelta)
					.hadamardProduct(MatrixFunctions.addBias(MathFunctions.sigmoidDerivative(zs[i-1])));
	
			// 4 Finally get the derivative, remove bias
			derivative[i-1] = delta[i -1].sliceRight(1).toColumnMatrix().multiply(as[i -1].toRowMatrix());
		}
	
		
		return derivative;
	
	}

	/**
	 * Verify the gradient with numeric approximated gradient
	 */
	public static void verifyGradient(double lambda, int m, Matrix X, Matrix Y, Matrix[] learningThetas, Matrix[] averageDerivatives)
	{
		double epsilon = 0.0001;
		// We randomly pick one theta to test the gradient
	
		int randomTheta = (int)(Math.random() * learningThetas.length);
		int randomRow = (int)(Math.random() * learningThetas[randomTheta].rows());
		int randomCol = (int)(Math.random() * learningThetas[randomTheta].columns());
		System.out.println("Testing " + randomTheta + "/" + randomRow + "/" + randomCol );
		 
		
		Matrix epsilonMatirx = MatrixFunctions.createMatrix(learningThetas[randomTheta].rows(), learningThetas[randomTheta].columns(), 0);
		
		epsilonMatirx.set(randomRow, randomCol, epsilon);
		
		Matrix randomThetaLeft = learningThetas[randomTheta].subtract(epsilonMatirx);
		Matrix randomThetaRight = learningThetas[randomTheta].add(epsilonMatirx);
		
		Matrix[] toTest = new Matrix[learningThetas.length];
		for (int i = 0; i < learningThetas.length; i++)
		{
			if (i == randomTheta)
			{
				toTest[i] = randomThetaLeft;
			} else
			{
				toTest[i] = learningThetas[i];
			}
	
		}
	
		// Cost 1
	
		double J1 = cost(toTest, m, X, Y, lambda);
	
		
		// Cost 2
		Matrix[] toTest2 = new Matrix[learningThetas.length];
		for (int i = 0; i < learningThetas.length; i++)
		{
			if (i== randomTheta)
			{
				toTest2[i] = randomThetaRight;
			}
			else
			{
				toTest2[i] = learningThetas[i];	
			}
			
		}
		double J2 = cost(toTest2, m, X, Y, lambda);
		
		System.out.println((J2 - J1) / (2 * epsilon) + " - " + averageDerivatives[randomTheta].get(randomRow, randomCol) + " = " + ((J2 - J1) / (2 * epsilon) - averageDerivatives[randomTheta].get(randomRow, randomCol))  );
	}

}
