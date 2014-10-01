package logesticregression;

import org.math.plot.*;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.Comparator;

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
import org.la4j.vector.functor.VectorFunction;

import util.PlotUtil;
import util.la.MatrixUtil;


public class Main {

	public static void main(String args[]) throws Exception {
		

		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/ex2data1.txt")));

		System.out.println(matrix);

		///////////////////////////////////
		Matrix xMatrix = matrix.resizeColumns(2);
		
		// m: number of features
		int m = xMatrix.rows();
		
		// Y
		Vector y = matrix.getColumn(2);
//		double[] yArray = ((DenseVector) y).toArray();

		// X with 1 as first column
		Matrix  X = MatrixUtil.concatenate(MatrixUtil.createVector(m, 1), xMatrix, true);
		X = MatrixUtil.concatenate( X, X.getColumn(1).hadamardProduct(X.getColumn(1)).divide(1000), true);
		X = MatrixUtil.concatenate( X, X.getColumn(2).hadamardProduct(X.getColumn(2)).divide(1000), true);
		System.out.println(X);
		// Theta
		double[] thetaArray = new double[] { -25.2, 0.207, 0.202, 1,1 };
		Vector theta = new BasicVector(thetaArray);
		
		// a
		double a = 0.004;

		// Start Learning
//		///////////////////////////////////
		double J = 100;
		for (int i = 0; i< 500;i ++)
		{
			theta = theta.subtract(X.transpose().multiply(sigmoid(theta,X).subtract(y)).multiply(a/m));
		
			J = cost(m, y, X, theta);
		}
		System.out.println(J);
		System.out.println(theta);
		// System.out.println(sigmoid(new BasicVector(new double[] {0,0}), matrix.resizeColumns(2)));
		
		// add a line plot to the PlotPanel
		Plot2DPanel plot = new Plot2DPanel();
		//plot.addScatterPlot("my plot", ((Basic2DMatrix)matrix.resizeColumns(2)).toArray());
		Comparator<Double> cp = new Comparator<Double>(){
			@Override
			public int compare(Double a, Double b)
			{
				// System.out.println(a + "/" + b);
				return (a>b)?1:-1;
//				if (a > b)
//				{
//					return 1;
//				}
//				else if (a == b)
//				{
//					return 0;
//				}
//				else
//				{
//					return -1;
//				}
			}
		};
		
		
		Matrix ones = MatrixUtil.select(matrix.resizeColumns(2), matrix.getColumn(2), 1, false);
		
		Matrix zeros = MatrixUtil.select(matrix.resizeColumns(2), matrix.getColumn(2), 0, false);
		
		plot.addScatterPlot("my plot", Color.RED, ((Basic2DMatrix)ones).toArray());
		
		plot.addScatterPlot("my plot", Color.blue, ((Basic2DMatrix)zeros).toArray());
		
		//plot.addScatterPlot("my plot", Color.black, MatrixUtil.create2DMatrix(20, 20, 100, 100, 2));
		
		Vector ones2 = MatrixUtil.createVector(400, 1);
		
		//Basic2DMatrix samples = new Basic2DMatrix(MatrixUtil.create2DMatrix(20, 20, 100, 100, 2));
		
		Matrix samples = new Basic2DMatrix(
				MatrixUtil.createMatrix(new double[][]
		{{30,30},{70,70}},2));
		
		samples = MatrixUtil.concatenate( samples, samples.getColumn(1).hadamardProduct(samples.getColumn(1)).divide(1000), true);
		samples = MatrixUtil.concatenate( samples, samples.getColumn(2).hadamardProduct(samples.getColumn(2)).divide(1000), true);
		
		Matrix sampleX = MatrixUtil.concatenate(ones2, samples , true);
		Vector sampleY = sampleX.multiply(theta);
		System.out.println(MatrixUtil.concatenate(samples,sampleY,true));
		System.out.println(sampleX.rows() +"==============" + sampleY.length());
		Matrix sampleOnes = MatrixUtil.select(samples, sampleY, cp, 0, 1, false);
		System.out.println("--------------------");
		//Matrix sampleZeros = MatrixUtil.select(samples, sampleY, cp, 0, -1, false);
		
		plot.addScatterPlot("my plot", Color.BLACK, ((Basic2DMatrix)sampleOnes).toArray());
		
		//plot.addScatterPlot("my plot", Color.GREEN, ((Basic2DMatrix)sampleZeros).toArray());
		
//		double[][] xs = new double[][] { { 1, 1,  1 }, { 1, 15, 30 } };
//		
//		PlotUtil.addLine(plot, "line", xs, theta);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);

	}

	private static double cost(int m, Vector y, Matrix X, Vector theta) {
		// Cost function
		// 
		Vector temp1 =  y.hadamardProduct(log(sigmoid(theta,X))).multiply(-1).
				add(y.subtract(1).hadamardProduct(log(sigmoid(theta,X).subtract(1).multiply(-1))));
		double J = temp1.sum() * 1 /m;
		// System.out.println(J);
		return J;
	}
	
	private static Vector log(Vector v)
	{
		v = v.transform(new VectorFunction()
		{
			@Override
			public double evaluate(int arg0, double value)
			{
				return Math.log(value);
			}
		});
				
		return v;
	}
			
	
	private static Vector sigmoid(Vector theta, Matrix X)
	{
		Vector result = X.multiply(theta);
		result = result.transform(new VectorFunction()
		{
			@Override
			public double evaluate(int arg0, double value)
			{
				return sigmoid(value);
			}
		});
		//System.out.println(result);
		return result;

	}

	private static double sigmoid(double z)
	{
		return 1 / (1 + Math.exp(- z));
	}



}
