package ml.core.linearalgebra;

import java.util.Comparator;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class MatrixFunctions 
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
	
	/**
	 * Subtract scalar by matrix
	 * @param v
	 * @param matrix
	 * @return
	 */
	public static Matrix subtract(double v, Matrix matrix)
	{
		
		return matrix.subtract(v).multiply(-1);
	}
	
	
	
	/**
	 * Create a vector will all same values
	 */
	public static double[][] create2DArray(double startX, double startY, double endX, double endY, double step)
	{
		
		
		int totalXs = (int)((endX - startX) / step); 
		int totalYs = (int)((endY - startY) / step); 
		
		int totalDots = totalXs * totalYs; 
		System.out.println(totalDots);
		double[][] dots = new double[totalDots][2];
		
		int index = 0;
		for (int i =0; i<totalXs; i++)
		{
			for (int j = 0; j <totalYs; j++)
			{
				 
				dots[index][0]  = (startX + i*step);
				dots[index][1]  = (startY + j*step);
				
				index ++;
			}
		}
		return dots;
	}
	
	
	
	public static double[][] create2DArray(double[][] startAndEnd, double step)
	{
		
		int totalColumns = startAndEnd[0].length;
		double totalSizes[] = new double[totalColumns];
		int totalDots = 1;
		for (int i = 0; i<totalSizes.length; i++)
		{
			totalSizes[i] = (startAndEnd[1][i] -  startAndEnd[0][i]) / step;
			totalDots *= totalSizes[i];
		}
 
		System.out.println(totalDots);
		double[][] dots = new double[totalDots][totalColumns];
		for (int i = 0; i< dots.length; i++)
		{
			for (int j=0; j<dots[i].length; j++)
			{
				dots[i][j] = startAndEnd[0][j] + (startAndEnd[1][j] - startAndEnd[0][j]) * Math.random();
			}
			
		}

		return dots;
	}
	
	public static Basic2DMatrix createRandomMatrix(int rows, int cols, double epsilon)
	{
		double[][] matrix2d = new double[rows][cols];

		for (int i = 0; i < rows; i++) 
		{
			for (int j = 0; j < cols; j++) 
			{
				matrix2d[i][j] = Math.random() * 2* epsilon - epsilon;
			}
		}

		return new Basic2DMatrix(matrix2d);
	}
	
	public static Basic2DMatrix createMatrix(int rows, int cols, double value)
	{
		double [][] matrix2d= new double[rows][cols];
		if (value != 0)
		{
			for (int i =0; i<rows; i++)
			{
				for (int j= 0; j<cols; j++)
				{
					matrix2d[i][j] = value;
				}
			}
				
		}
		
		return new Basic2DMatrix(matrix2d);
	}
	
	public static Basic2DMatrix createDiagonalMatrix(int size, double value)
	{
		double [][] matrix2d= new double[size][size];
		if (value != 0)
		{
			for (int i =0; i<size; i++)
			{
					matrix2d[i][i] = value;
			}
		}
		
		return  new Basic2DMatrix(matrix2d);
	}
	
	/**
	 * Add a row or column with all 1 values
	 * @param matrix
	 * @param addColumn
	 * @return
	 */
	public static Matrix addBias(Matrix matrix, boolean addColumn) 
	{
		// Number of samples
		if (addColumn)
		{
			int m = matrix.rows();
			// add 1 to the fist column
			Matrix result = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), matrix, addColumn);

			return result;
		}
		else
		{
			int m = matrix.columns();
			// add 1 to the fist row
			Matrix result = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1).toRowMatrix(), matrix, addColumn);

			return result;
		}
	}
	
	public static Vector addBias(Vector vector) 
	{
			Matrix matrix = vector.toColumnMatrix();
			// add 1 to the fist column
			Matrix result = MatrixFunctions.concatenate(MatrixFunctions.createVector(1, 1).toRowMatrix(), matrix, false);

			return result.toColumnVector();

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
	
	/**
	 * Select row i from the Matrix a, if element i of b matches the value 
	 * @param a
	 * @param b
	 * @param value
	 * @param isHorizontal
	 * @return
	 */
	public static Matrix select(Matrix a, Vector b, double value, boolean isHorizontal)
	{
		Matrix result = null;
		for (int i = 0; i< b.length(); i++)
		{
			if (b.get(i)== value)
			{
				result = MatrixFunctions.concatenate(result, a.getRow(i) , isHorizontal);
			}
		}
		return result;
	}

	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param comp
	 * @param value
	 * @param selector
	 * @param isHorizontal
	 * @return
	 */
	public static Matrix select(Matrix a, Vector b, Comparator<Double> comp, double value, int selector, boolean isHorizontal)
	{
		Matrix result = null;
		for (int i = 0; i < b.length(); i++)
		{
			if (i%1000 == 0)
			{
				System.out.println(i);
			}
			if ( comp.compare( b.get(i), value) == selector )
			{
				result = MatrixFunctions.concatenate(result, a.getRow(i) , isHorizontal);
			}
			else
			{
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
			Matrix transformMatrix = new Basic2DMatrix(squareMatrix);
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
	
	public static MatrixFunction ClearColumn0 = new MatrixFunction()
	{
		
		@Override
		public double evaluate(int arg0, int arg1, double arg2)
		{
			if (arg1 ==0)
			{
				return 0;
			}
			return arg2;
		}
	};
}
