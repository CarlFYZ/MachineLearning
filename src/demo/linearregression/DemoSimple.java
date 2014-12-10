package demo.linearregression;

import ml.common.util.PlotUtil;
import ml.core.linearalgebra.MatrixFunctions;
import ml.linearregression.LinearRegression;

import org.math.plot.*;

import java.io.FileInputStream;

import javax.swing.JFrame;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.dense.DenseVector;

public class Main
{

	public static void main(String args[]) throws Exception
	{

		Basic2DMatrix matrix = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/ex1data1.txt")));

		int steps = 1000;
		int costCalculationInterval = 10;
		
		// /////////////////////////////////
		Vector xcol = matrix.getColumn(0);
		Matrix x1Matrix = ((DenseVector) xcol).toColumnMatrix();
		// m
		int m = xcol.length();

		// Y
		Vector y = matrix.getColumn(1);

		// X with 1 as first column
		Matrix X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), x1Matrix, true);

		// Theta
		double[] thetaArray = new double[] { 0, 2 };
		Vector theta = new BasicVector(thetaArray);

		// a
		double a = 0.02;

		// Training
		theta = LinearRegression.gradientDescent(steps, costCalculationInterval, m, y, X, theta, a);

		// add a line plot to the PlotPanel
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot("my plot", matrix.toArray());

		double[][] xs = new double[][] { { 1, 1, 1 }, { 1, 15, 30 } };

		PlotUtil.addLine(plot, "line", xs, theta);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);

	}

}
