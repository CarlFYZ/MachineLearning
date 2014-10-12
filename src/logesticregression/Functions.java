package logesticregression;

import ml.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class Functions {

	public static double cost(int m, Vector y, Matrix X, Vector theta) {
		// Cost function
		Vector temp1 =  y.hadamardProduct(MathFunctions.log(MathFunctions.sigmoid(theta,X))).multiply(-1).
				add(y.subtract(1).hadamardProduct(MathFunctions.log(MathFunctions.sigmoid(theta,X).subtract(1).multiply(-1))));
		double J = temp1.sum() * 1 /m;
		System.out.println(J);
		return J;
	}

	public static Vector gradientDescent(int m, Vector y, Matrix X,
			Vector theta, double a) {
		return theta.subtract(X.transpose().multiply(MathFunctions.sigmoid(theta,X).subtract(y)).multiply(a/m));
	}


}
