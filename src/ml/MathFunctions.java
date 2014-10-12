package ml;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorFunction;

public class MathFunctions {

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

	/**
	 * Apply sigmoid to every value of X * theta
	 * @param theta
	 * @param X
	 * @return
	 */
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
	
	public static Matrix sigmoid(Matrix X)
	{
		return X.transform(new MatrixFunction() {
			
			@Override
			public double evaluate(int arg0, int arg1, double arg2) 
			{
				return sigmoid(arg2);
			}
		});
	}

}
