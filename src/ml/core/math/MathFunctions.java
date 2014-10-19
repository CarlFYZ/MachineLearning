package ml.core.math;

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
	
	
	public static Matrix log(Matrix v)
	{
		v = v.transform(new MatrixFunction()
		{
			
			@Override
			public double evaluate(int arg0, int arg1, double arg2)
			{
				// TODO Auto-generated method stub
				return Math.log(arg2);
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
	
	public static Vector sigmoid(Vector x)
	{
		return x.transform(new VectorFunction() 
		{
			
			@Override
			public double evaluate(int arg0, double value)
			{
				return sigmoid(value);
			}
		});
	}
	
	
	public static Vector sigmoidDerivative(Vector X)
	{
		return X.transform(new VectorFunction() {
			
			@Override
			public double evaluate(int arg0, double value)
			{
				double g = sigmoid(value);
				return g*(1-g);
			}

		});
	}
	public static Matrix sigmoidDerivative(Matrix X)
	{
		return X.transform(new MatrixFunction() {
			
			@Override
			public double evaluate(int arg0, int arg1, double arg2) 
			{
				double g = sigmoid(arg2);
				return g*(1-g);
			}
		});
	}

}
