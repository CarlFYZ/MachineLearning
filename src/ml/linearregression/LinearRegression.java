package ml.linearregression;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class LinearRegression
{

	/**
	 * The cost function
	 * @param m
	 * @param y
	 * @param X
	 * @param theta
	 */
	public static void cost(int m, Vector y, Matrix X, Vector theta)
	{
		// Cost function
		Vector temp1 = X.multiply(theta).subtract(y);
		double J = temp1.toRowMatrix().multiply(temp1).sum() * 1 / m;
		System.out.println(J);
	}

	
	/**
	 * Learn the parameter of linear regression by gradient descent
	 * @param steps number of steps to run the training
	 * @param costCalculationInterval  print the cost every # steps 
	 * @param m number of training samples
	 * @param y 
	 * @param X
	 * @param theta
	 * @param a
	 * @return
	 */
	public static Vector gradientDescent(int steps, int costCalculationInterval, int m, Vector y, Matrix X, Vector theta, double a)
	{
		for (int i = 1; i <= steps; i++)
		{
			theta = theta.subtract(X.transpose().multiply((X.multiply(theta).subtract(y))).multiply(a / m));
			if (i % costCalculationInterval == 0)
			{
				cost(m, y, X, theta);
			}
		}
		return theta;
	}

}
