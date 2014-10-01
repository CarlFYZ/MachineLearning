package logesticregression;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorFunction;

public class Functions {

	public static double cost(int m, Vector y, Matrix X, Vector theta) {
		// Cost function
		Vector temp1 =  y.hadamardProduct(log(sigmoid(theta,X))).multiply(-1).
				add(y.subtract(1).hadamardProduct(log(sigmoid(theta,X).subtract(1).multiply(-1))));
		double J = temp1.sum() * 1 /m;
		System.out.println(J);
		return J;
	}

	public static Vector gradientDescent(int m, Vector y, Matrix X,
			Vector theta, double a) {
		return theta.subtract(X.transpose().multiply(sigmoid(theta,X).subtract(y)).multiply(a/m));
	}

	public static Vector log(Vector v)
	{
		v = v.transform(new VectorFunction()
		{
			@Override
			public double evaluate(int arg0, double value)
			{
				return Math.log(value);
			}
		});
				
		return v;
	}

	public static double sigmoid(double z)
	{
		return 1 / (1 + Math.exp(- z));
	}

	public static Vector sigmoid(Vector theta, Matrix X)
	{
		Vector result = X.multiply(theta);
		result = result.transform(new VectorFunction()
		{
			@Override
			public double evaluate(int arg0, double value)
			{
				return sigmoid(value);
			}
		});
		//System.out.println(result);
		return result;
	
	}

}
