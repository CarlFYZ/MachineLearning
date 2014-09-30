package linearregression;

import org.math.plot.*;

import java.io.FileInputStream;

import javax.swing.JFrame;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.dense.DenseVector;

import util.PlotUtil;
import util.la.MatrixUtil;


public class Main {

	public static void main(String args[]) throws Exception {
		

		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/ex1data1.txt")));


		///////////////////////////////////
		Vector xcol = matrix.getColumn(0);
		Matrix x1Matrix = ((DenseVector) xcol).toColumnMatrix();
		// m
		int m = xcol.length();
		
		// Y
		Vector y = matrix.getColumn(1);
		double[] yArray = ((DenseVector) y).toArray();

		// X with 1 as first column
		Matrix  X = MatrixUtil.concatenate(MatrixUtil.createVector(m, 1), x1Matrix, true);
		
		// Theta
		double[] thetaArray = new double[] { 0, 2 };
		Vector theta = new BasicVector(thetaArray);
		
		// a
		double a = 0.02;
		
		///////////////////////////////////
		
		for (int i = 0; i< 1000;i ++)
		{
		theta = theta.subtract( X.transpose().multiply( (X.multiply(theta).subtract(y))).multiply(a/m));
		
		cost(m, y, X, theta);
		}
		
		
		
		// add a line plot to the PlotPanel
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot("my plot", matrix.toArray());
		
		double[][] xs = new double[][] { { 1, 1,  1 }, { 1, 15, 30 } };
		
		PlotUtil.addLine(plot, "line", xs, theta);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);

	}

	private static void cost(int m, Vector y, Matrix X, Vector theta) {
		// Cost function 
		Vector temp1 = X.multiply(theta).subtract(y);
		double J = temp1.toRowMatrix().multiply(temp1).sum() * 1 /m;
		System.out.println(J);
	}




}
