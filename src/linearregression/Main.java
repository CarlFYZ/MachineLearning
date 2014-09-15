package linearregression;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class Main {
	
	public static void main(String args[])
	{
		Matrix a = new Basic2DMatrix(new double[][]{
				  { 1.0, 2.0, 3.0 },
				  { 4.0, 5.0, 6.0 },
				  { 3.0, 8.0, 9.0 }
				});

				// This one uses 1D array as internal representation
				Matrix b = new Basic1DMatrix(new double[][]{
				  { 1.0, 2.0, 3.0 },
				  { 4.0, 5.0, 6.0 },
				  { 7.0, 8.0, 9.0 }
	});
				//System.out.println(a.multiply(b));
				//a.transpose();
				


						// We will use Gauss-Jordan method for inverting
						MatrixInverter inverter = a.withInverter(LinearAlgebra.GAUSS_JORDAN);
						// The 'b' matrix will be dense
						b = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
						System.out.println(b);
						System.out.println(a.multiply(b));
				
	}

}
