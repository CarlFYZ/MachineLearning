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


public class Main {

	public static void main(String args[]) throws Exception {
		Matrix a = new Basic2DMatrix(new double[][] { { 1.0, 2.0, 3.0 },
				{ 4.0, 5.0, 6.0 }, { 3.0, 8.0, 9.0 } });

		// This one uses 1D array as internal representation
		Matrix b = new Basic1DMatrix(new double[][] { { 1.0, 2.0, 3.0 },
				{ 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } });
		// System.out.println(a.multiply(b));
		// a.transpose();

		// We will use Gauss-Jordan method for inverting
		MatrixInverter inverter = a.withInverter(LinearAlgebra.GAUSS_JORDAN);
		// The 'b' matrix will be dense
		b = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
		
		Matrix bb = PlotUtil.concatenate(a, b, true);
		System.out.println(a);
		System.out.println(b);
		System.out.println(bb);
		
		
		System.out.println(a.multiply(b));

		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/ex1data1.txt")));
		//System.out.println(matrix);
		
		//Matrix X = matrix.select(arg0, arg1)
		
		

		Plot2DPanel plot = new Plot2DPanel();

		Vector xcol = matrix.getColumn(0);
		Vector ycol = matrix.getColumn(1);
		Matrix x1Matrix = ((DenseVector) xcol).toColumnMatrix();
		
		double[] y = ((DenseVector) ycol).toArray();

		// create your PlotPanel (you can use it as a JPanel)

		// add a line plot to the PlotPanel
		plot.addScatterPlot("my plot", matrix.toArray());
		double[] thetaV = new double[] { 0, 2 };
		double[][] xs = new double[][] { { 1, 1,  1 }, { 1, 15, 30 } };
		
		PlotUtil.addLine(plot, "line", xs, thetaV);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);

	}




}
