package test;

import ml.common.util.PlotUtil;
import ml.core.linearalgebra.MatrixFunctions;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;

import junit.framework.TestCase;

public class BasicMatrixOperationTest extends TestCase{

	
	public void testMatrixOp()
	{
		Matrix a = new Basic2DMatrix(new double[][] { { 1.0, 2.0, 3.0 },
				{ 4.0, 5.0, 6.0 }, { 3.0, 8.0, 9.0 } });

		// This one uses 1D array as internal representation
		Matrix b = new Basic1DMatrix(new double[][] { { 1.0, 2.0, 3.0 },
				{ 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } });
		
		Matrix ab = MatrixFunctions.concatenate(a, b, true);
		System.out.println(a);
		System.out.println(b);
		System.out.println(ab);
		
		Matrix ainv = MatrixFunctions.inverse(a);
		
		System.out.println(a.multiply(ainv));
		
		assertTrue(a.multiply(ainv).is(Matrices.IDENTITY_MATRIX));
	}

}
