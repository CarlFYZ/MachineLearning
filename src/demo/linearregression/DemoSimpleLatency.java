package demo.linearregression;

import java.io.FileInputStream;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.math.plot.Plot3DPanel;

import ml.core.linearalgebra.MatrixFunctions;
import ml.linearregression.LinearRegression;

public class DemoSimpleLatency
{

	public static void main(String args[]) throws Exception
	{

		Basic2DMatrix matrix = new Basic2DMatrix(Matrices.asSymbolSeparatedSource(new FileInputStream("./data/LatencyFinal.csv")));

		Plot3DPanel plot = new Plot3DPanel();
		// plot.addScatterPlot("my plot", matrix.toArray());

		int steps = 10000;
		int costCalculationInterval = 10;
		
		// /////////////////////////////////
		Vector xcol1 = matrix.getColumn(0);
//		Vector xcol2 = matrix.getColumn(1);
//		Vector xcol3 = matrix.getColumn(2);
//		matrix.resizeColumns(3)
//		Matrix x1Matrix = MatrixFunctions.concatenate(((DenseVector) xcol1).toColumnMatrix(), ((DenseVector) xcol2).toColumnMatrix(), true);
//		Matrix x1Matrix = MatrixFunctions.concatenate(((DenseVector) xcol1).toColumnMatrix(), ((DenseVector) xcol2).toColumnMatrix(), true);
		
		Matrix x1Matrix = matrix.resizeColumns(3);
		// m
		int m = xcol1.length();

		// Y
		Vector y = matrix.getColumn(3);


		// X with 1 as first column
		Matrix X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), x1Matrix, true);

		// Theta
		double[] thetaArray = new double[] { 2, 1, 3, 1 };
		Vector theta = new BasicVector(thetaArray);

		// a
		double a = 0.02;

		// Training
		theta = LinearRegression.gradientDescent(steps, costCalculationInterval, m, y, X, theta, a);

		// add a line plot to the PlotPanel
		System.out.println(theta);



		// double[][] xs = new double[][] { { 1, 1, 1 }, { 1, 15, 25 } };
		//
		//
		//
		// // put the PlotPanel in a JFrame, as a JPanel
		// JFrame frame = new JFrame("a plot panel");
		// frame.setSize(600, 600);
		// frame.setContentPane(plot);
		// frame.setVisible(true);
		//
		// Thread.sleep(5000);
		// PlotUtil.addLine(plot, "line", xs, theta);
		System.out.println(x1Matrix);
		System.out.println(y);
	}

}
