package demo.logisticregression;


import ml.core.linearalgebra.MatrixFunctions;
import ml.logesticregression.LogisticRegression;

import org.math.plot.*;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.Comparator;

import javax.swing.JFrame;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;


public class Main {

	public static void main(String args[]) throws Exception {
		

		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/ex2data1.txt")));

		int steps = 100000;
		int costCalculationInterval = 100;

		///////////////////////////////////
		Matrix xMatrix = matrix.resizeColumns(2);
		xMatrix = xMatrix.transform(new MatrixFunction() {
			
			@Override
			public double evaluate(int arg0, int arg1, double value) {
				// TODO Auto-generated method stub
				return (value -50)/70;
			}
		});
		// m: number of samples
		int m = xMatrix.rows();
		
		// Y
		Vector y = matrix.getColumn(2);
//		double[] yArray = ((DenseVector) y).toArray();

		// X with 1 as first column
		Matrix  X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), xMatrix, true);
		X = MatrixFunctions.concatenate( X, X.getColumn(1).hadamardProduct(X.getColumn(1)).divide(1), true);
		X = MatrixFunctions.concatenate( X, X.getColumn(2).hadamardProduct(X.getColumn(2)).divide(1), true);
		X = MatrixFunctions.concatenate( X, X.getColumn(1).hadamardProduct(X.getColumn(2)).divide(1), true);
		X = MatrixFunctions.concatenate( X, X.getColumn(2).hadamardProduct(X.getColumn(1)).divide(1), true);

		// Theta
		// double[] thetaArray = new double[] {-5.980, 25.178, 24.244, -7.696, -2.272, 18.421, 18.421};
		double[] thetaArray = new double[] {1.980, 2.178, 4.244, 4.696, 2.272, 1.421, 1.421};
		Vector theta = new BasicVector(thetaArray);
		
		// a
		double a = 0.01;

		// Start Learning
		theta = LogisticRegression.gradientDescent(steps, costCalculationInterval, m, y, X, theta, a);
		System.out.println(theta);
		// Done
		
		
		// Start plotting
		// System.out.println(sigmoid(new BasicVector(new double[] {0,0}), matrix.resizeColumns(2)));
		
		// add a line plot to the PlotPanel
		Plot2DPanel plot = new Plot2DPanel();
		//plot.addScatterPlot("my plot", ((Basic2DMatrix)matrix.resizeColumns(2)).toArray());
		Comparator<Double> cp = new Comparator<Double>(){
			@Override
			public int compare(Double a, Double b)
			{
				if (a > b)
				{
					return 1;
				}
				else if (a == b)
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}
		};
		
		
		Matrix ones = MatrixFunctions.select(xMatrix.resizeColumns(2), matrix.getColumn(2), 1, false);
		
		Matrix zeros = MatrixFunctions.select(xMatrix.resizeColumns(2), matrix.getColumn(2), 0, false);
		
		plot.addScatterPlot("my plot", Color.RED, ((Basic2DMatrix)ones).toArray());
		
		plot.addScatterPlot("my plot", Color.blue, ((Basic2DMatrix)zeros).toArray());
		
		
		double[][] sampleSpace = MatrixFunctions.create2DArray(new double[][]{{-.5,-.5},{1,1}},0.05);
		sampleSpace = MatrixFunctions.create2DArray(-.5, -.5,1,1,0.05);
		Matrix samples = new Basic2DMatrix(	sampleSpace);
		
		Vector ones2 = MatrixFunctions.createVector(samples.rows(), 1);
		Matrix sampleX = MatrixFunctions.concatenate(ones2, samples , true);
		sampleX = MatrixFunctions.concatenate( sampleX, sampleX.getColumn(1).hadamardProduct(sampleX.getColumn(1)).divide(1), true);
		sampleX = MatrixFunctions.concatenate( sampleX, sampleX.getColumn(2).hadamardProduct(sampleX.getColumn(2)).divide(1), true);
		sampleX = MatrixFunctions.concatenate( sampleX, sampleX.getColumn(1).hadamardProduct(sampleX.getColumn(2)).divide(1), true);
		sampleX = MatrixFunctions.concatenate( sampleX, sampleX.getColumn(1).hadamardProduct(sampleX.getColumn(2)).divide(1), true);
		
		Vector sampleY = sampleX.multiply(theta);

		Matrix sampleOnes = MatrixFunctions.select(samples, sampleY, cp, 0, 1, false);

		Matrix sampleZeros = MatrixFunctions.select(samples, sampleY, cp, 0, -1, false);
		
		plot.addScatterPlot("my plot", Color.BLACK, ((Basic2DMatrix)sampleOnes).toArray());
		
		plot.addScatterPlot("my plot", Color.GREEN, ((Basic2DMatrix)sampleZeros).toArray());

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);

	}



}
