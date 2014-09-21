package util;

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

}
