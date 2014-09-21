package util.la;

import java.lang.reflect.Array;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class MatrixUtil 
{
	
	/**
	 * Inverse a vector using Gauss Jordan method
	 * 
	 */
	public static Matrix inverse(Matrix a) 
	{
		// We will use Gauss-Jordan method for inverting
		MatrixInverter inverter = a.withInverter(LinearAlgebra.GAUSS_JORDAN);
		// The 'ainv' matrix will be dense
		Matrix ainv = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
		return ainv;
	}
	
	/**
	 * Create a vector will all same values
	 */
	public static Vector createVector(int size, double value)
	{
		double [] firstColumn = new double[size];
		for (int i =0; i<size; i++)
		{
			firstColumn[i] = value;
		}
		return new BasicVector(firstColumn);
	}
	

	public static Matrix concatenate(Vector a, Matrix b )
	{
		return concatenate(a.toColumnMatrix(), b, true); 
	}
	
	public static Matrix concatenate(Vector a, Vector b )
	{
		return concatenate(a.toColumnMatrix(), b.toColumnMatrix(), true); 
	}
	
	public static Matrix concatenate(Matrix a, Vector b )
	{
		return concatenate(a, b.toColumnMatrix(), true); 
	}

	/**
	 * Concatenate two matrix uses matrix operation
	 * @param a first matrix
	 * @param b second matrix
	 * @param isHorizontal concatenate from horizontally or vertically
	 */
	public static Matrix concatenate(Matrix a, Matrix b, boolean isHorizontal)
	{
		
		if (isHorizontal)
		{
			int columnsA = a.columns();
			int columnsB = b.columns();
	
			double [][] squareMatrix =  new double[columnsA+columnsB][columnsA+columnsB] ;
			for (int i = 0; i < columnsB; i++)
			{
				squareMatrix[i][columnsA + i] = 1; 
			}
			b = b.resizeColumns(columnsA + columnsB);
			a = a.resizeColumns(columnsA + columnsB);
			Basic2DMatrix transformMatrix = new Basic2DMatrix(squareMatrix);
			return b.multiply( transformMatrix).add(a);
	
		} 
		else
		{
			int rowsA = a.rows();
			int rowsB = b.rows();
			
	
			double [][] squareMatrix =  new double[rowsA+rowsB][rowsA+rowsB] ;
			for (int i = 0; i < rowsB; i++)
			{
				squareMatrix[rowsA + i][i] = 1;
			}
			b = b.resizeRows(rowsA + rowsB);
			a = a.resizeRows(rowsA + rowsB);
			Basic2DMatrix transformMatrix = new Basic2DMatrix(squareMatrix);
			return transformMatrix.multiply(b).add(a);
		}
		
	}

	/**
	 * Concatenate two matrix uses loop operation
	 * @param a 
	 * @param b
	 * @param isHorizontal concatenate from horizontally or vertically
	 */
	protected static Matrix concatenateSlow(Matrix a, Matrix b, boolean isHorizontal)
	{
		
		if (isHorizontal)
		{
	
			int columnsA = a.columns();
			int columnsB = b.columns();
			a = a.resizeColumns(columnsA + columnsB);
			b = b.resizeColumns(columnsA + columnsB);
			for (int i = 0; i < columnsB; i++)
			{
				b.swapColumns(columnsB - 1 - i, columnsA + columnsB - 1 - i);
			}
			return a.add( b );
		} 
		else
		{
			int rowsA = a.rows();
			int rowsB = b.rows();
			a = a.resizeRows(rowsA + rowsB);
			b = b.resizeRows(rowsA + rowsB);
			for (int i = 0; i < rowsB; i++)
			{
				b.swapRows(rowsB - 1 - i, i + rowsA + rowsB - 1 - i);
			}
			return a.add( b );
		}
		
	}
}
