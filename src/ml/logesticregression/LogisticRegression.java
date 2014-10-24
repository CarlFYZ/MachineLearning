package ml.logesticregression;

import ml.core.math.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class LogisticRegression
{

	public static double cost(int m, Vector y, Matrix X, Vector theta)
	{
		// Cost function
		Vector temp1 = y.hadamardProduct(MathFunctions.log(MathFunctions.sigmoid(theta, X))).multiply(-1)
				.add(y.subtract(1).hadamardProduct(MathFunctions.log(MathFunctions.sigmoid(theta, X).subtract(1).multiply(-1))));
		double J = temp1.sum() * 1 / m;
		System.out.println("J=" + J);
		return J;
	}

	/**
	 * 
	 * @param steps
	 * @param costCalculationInterval
	 * @param m
	 * @param y
	 * @param X
	 * @param theta
	 * @param a
	 * @return
	 */
	public static Vector gradientDescent(int steps, int costCalculationInterval, int m, Vector y, Matrix X, Vector theta, double a)
	{
		for (int i = 1; i<= steps ;i ++)
		{
			theta = theta.subtract(X.transpose().multiply(MathFunctions.sigmoid(theta, X).subtract(y)).multiply(a / m));
			if (i % costCalculationInterval == 0)
			{
				cost(m, y, X, theta);
			}

		}
		return theta;
	}

}
