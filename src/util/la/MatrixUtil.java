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
	

	public static Matrix concatenate(Vector a, Matrix b, boolean isHorizontal )
	{
		if (isHorizontal)
		{
			return concatenate(a.toColumnMatrix(), b, isHorizontal); 
		}
		else
		{
			return concatenate(a.toRowMatrix(), b, isHorizontal); 
		}
		
	}
	
	public static Matrix concatenate(Vector a, Vector b, boolean isHorizontal  )
	{
		if (isHorizontal)
		{
			return concatenate(a.toColumnMatrix(), b.toColumnMatrix(), isHorizontal); 
		}
		else
		{
			return concatenate(a.toRowMatrix(), b.toRowMatrix(), isHorizontal); 
		}
	}
	
	public static Matrix concatenate(Matrix a, Vector b, boolean isHorizontal )
	{
		if (isHorizontal)
		{
			return concatenate(a, b.toColumnMatrix(), isHorizontal);
		}
		else
		{
			return concatenate(a, b.toRowMatrix(), isHorizontal); 
		}
	}
	
	public static Matrix select(Matrix a, Vector b, double value, boolean isHorizontal)
	{
		Matrix result = null;
		for (int i = 0; i< b.length(); i++)
		{
			if (b.get(i)== value)
			{
				result = MatrixUtil.concatenate(result, a.getRow(i) , isHorizontal);
			}
		}
		return result;
	}

	/**
	 * Concatenate two matrix uses matrix operation
	 * @param a first matrix
	 * @param b second matrix
	 * @param isHorizontal concatenate from horizontally or vertically
	 */
	public static Matrix concatenate(Matrix a, Matrix b, boolean isHorizontal)
	{
		if (a == null) 
		{
			return b;
		} 
		else if (b == null) 
		{
			return a;
		}
			
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
