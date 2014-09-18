package util;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.dense.DenseVector;
import org.math.plot.Plot2DPanel;

public class PlotUtil
{
	/**
	 * The method plot a line Y= X0 * theta0 + X1 * theta1
	 * It's typical use if to plot Y = 1*theta0 + X1 (i.e. X0 = 1)
	 * Input at least three Xs and coeffiencients to plot a line
	 * @param panel the panel to be plotted
	 * @param xs at least three Xs e.g. {{1,X0},{1,X1},{1,X2}}
	 * @param coefficients {theta0, theta1}
	 */
	public static void addLine(Plot2DPanel panel, String name, double[][] xs, double[] coefficients)
	{
		DenseVector theta = new BasicVector(coefficients);
		Basic2DMatrix lineX = new Basic2DMatrix(xs);
		DenseVector lineY = (DenseVector) lineX.transpose().multiply(theta);
		panel.addLinePlot(name, ((DenseVector) lineX.transpose().getColumn(1)).toArray(), lineY.toArray());
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
