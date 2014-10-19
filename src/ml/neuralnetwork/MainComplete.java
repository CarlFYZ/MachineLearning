package ml.neuralnetwork;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import ml.core.linearalgebra.MatrixFunctions;
import ml.core.math.MathFunctions;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;

public class MainComplete
{
	
	
	public static Basic2DMatrix theta1result;
	public static  Basic2DMatrix theta2result;
	
	public static void main(String[] args) throws Exception
	{
		MatFileReader matfilereader = new MatFileReader("./data/ex4weights.mat");
		// model parameters
		double alpha = 0.1;
		double lambda = 1;
		
		// theta1 and theta2 to train
		MLDouble theta1ml = (MLDouble) matfilereader.getMLArray("Theta1");
		MLDouble theta2ml = (MLDouble) matfilereader.getMLArray("Theta2");
		Basic2DMatrix theta1 = new Basic2DMatrix(theta1ml.getArray());
		Basic2DMatrix theta2 = new Basic2DMatrix(theta2ml.getArray());
		System.out.println("theta1:" + theta1ml);
		System.out.println("theta2:" + theta2ml);

		theta1 = MatrixFunctions.createRandomMatrix(25, 401, 0.12);
		theta2 = MatrixFunctions.createRandomMatrix(10, 26, 0.12);
		
		MatFileReader matfilereader2 = new MatFileReader("./data/ex4data1.mat");
		MLDouble Xml = (MLDouble) matfilereader2.getMLArray("X");

		// X
		Basic2DMatrix xMatrix = new Basic2DMatrix(Xml.getArray());
		System.out.println("X:" + Xml);
		System.out.println(xMatrix.getRow(1));

		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), xMatrix, true);

		// y Vector and Y matrix(10 * 5000)
		MLDouble yml = (MLDouble) matfilereader2.getMLArray("y");
		BasicVector y = (BasicVector) (new Basic2DMatrix(yml.getArray())).getColumn(0);
		// System.out.println(y);

		double[][] Ys = new double[10][5000];
		for (int i = 0; i < y.length(); i++)
		{
			Ys[(int) y.get(i) - 1][i] = 1;
		}
		Matrix Y = new Basic2DMatrix(Ys);



		// Cost function
		Matrix zero10_5000 = MatrixFunctions.createMatrix(10, 5000, 0);
		Matrix one10_5000 = MatrixFunctions.createMatrix(10, 5000, 1);

		// Regularized cost

		Matrix trans400 = MatrixFunctions.concatenate(MatrixFunctions.createVector(400, 0).toRowMatrix(), MatrixFunctions.createDiagonalMatrix(400, 1), false);
		Matrix trans25 = MatrixFunctions.concatenate(MatrixFunctions.createVector(25, 0).toRowMatrix(), MatrixFunctions.createDiagonalMatrix(25, 1), false);


		for (int o = 0; o < 3; o++)
		{
			// sigmoid gradient(derivative)
			Matrix cumulativeTheta1Derivative = MatrixFunctions.createMatrix(25, 401, 0);
			Matrix cumulativeTheta2Derivative = MatrixFunctions.createMatrix(10, 26, 0);
			for (int i = 0; i < 5000; i++)
			{
				Matrix[] result = processOneSample(new Matrix[] {theta1, theta2},X.getRow(i), Y.getColumn(i));

				cumulativeTheta1Derivative = cumulativeTheta1Derivative.add(result[0]);
				cumulativeTheta2Derivative = cumulativeTheta2Derivative.add(result[1]);
			}
			Matrix theta1Derivative = cumulativeTheta1Derivative.divide(m);
			Matrix theta2Derivative = cumulativeTheta2Derivative.divide(m);

			MatrixFunction matrixFunctionClearColumn0 = new MatrixFunction()
			{
				
				@Override
				public double evaluate(int arg0, int arg1, double arg2)
				{
					// TODO Auto-generated method stub
					if (arg1 ==0)
					{
						return 0;
					}
					return arg2;
				}
			};
			
			Matrix theta1RegDerivative = theta1.multiply(lambda/m).transform(matrixFunctionClearColumn0);
			Matrix theta2RegDerivative = theta2.multiply(lambda/m).transform(matrixFunctionClearColumn0);
			
			
			// System.out.println(theta1Derivative);
			theta1 = (Basic2DMatrix) theta1.subtract(theta1Derivative.multiply(alpha).add( theta1RegDerivative.multiply(alpha)));
			theta2 = (Basic2DMatrix) theta2.subtract(theta2Derivative.multiply(alpha).add( theta2RegDerivative.multiply(alpha)));

			double J = calculateCost(theta1, theta2, m, X, Y, zero10_5000, one10_5000, trans400, trans25, true);

			// ////////////////////////// verify gradient
//			double epsilon = 0.0001;
//			Basic2DMatrix[][] testThetas = new Basic2DMatrix[2][2];
//
//			Matrix epsilonMatirx = MatrixUtil.initialMatrix(theta1.rows(), theta1.columns(), 0);
//			epsilonMatirx.set(3, 3, epsilon);
//			testThetas[0][0] = (Basic2DMatrix) theta1.subtract(epsilonMatirx);
//			testThetas[0][1] = (Basic2DMatrix) theta1.add(epsilonMatirx);
//
//			// Cost 1
//			Matrix A1 = MathFunctions.sigmoid(theta2.multiply(MatrixUtil.addBias(MathFunctions.sigmoid(testThetas[0][0].multiply(X.transpose())), false)));
//			double J1 = calculateCost(testThetas[0][0], theta2, X.rows(), X, Y, zero10_5000, one10_5000, trans400, trans25, false);
//			// Cost 2
//			Matrix A2 = MathFunctions.sigmoid(theta2.multiply(MatrixUtil.addBias(MathFunctions.sigmoid(testThetas[0][1].multiply(X.transpose())), false)));
//			double J2 = calculateCost(testThetas[0][1], theta2, X.rows(), X, Y, zero10_5000, one10_5000, trans400, trans25, false);
//			System.out.println((J2 - J1) / (2 * epsilon));
//			System.out.println(theta1Derivative.get(3, 3));

			System.out.println(J);
			

		}

		System.out.println("Result -----------------------------");
		Matrix predict = MathFunctions.sigmoid(theta2.multiply(MatrixFunctions.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose())), false)));
		//System.out.println(predict.rows() + " " + predict.columns());
		int correctCount = 0;
		for(int n=0;n<5000;n++)
		{
			Vector onePredict = predict.getColumn(n);
			int highest =0;
			for (int p=1;p<10;p++)
			{
				if(onePredict.get(highest) < onePredict.get(p))
				{
					highest = p;
				}
				
			}
			if (highest==y.get(n)-1)
			{
				correctCount++;
			}
		}
		System.out.println(correctCount);
		
		// System.out.println(MathFunctions.sigmoidDerivative(MatrixUtil.initialDiagonalMatrix(15,
		// -100)));
		theta1result = theta1;
		theta2result = theta2;
		
		showSamples(xMatrix);
	}

	private static double calculateCost(Basic2DMatrix theta1, Basic2DMatrix theta2, int m, Matrix X, Matrix Y, Matrix zero10_5000, Matrix one10_5000, Matrix trans400, Matrix trans25,
			boolean isRegularize)
	{
		Matrix A;
		A = MathFunctions.sigmoid(theta2.multiply(MatrixFunctions.addBias(MathFunctions.sigmoid(theta1.multiply(X.transpose())), false)));

		Matrix theta1a = theta1.multiply(trans400);
		Matrix theta2a = theta2.multiply(trans25);

		Matrix fo1 = (zero10_5000.subtract(Y)).hadamardProduct(MathFunctions.log(A));
		Matrix fo2 = (one10_5000.subtract(Y)).hadamardProduct(MathFunctions.log(one10_5000.subtract(A)));
		// Cost
		double J = fo1.subtract(fo2).sum() / m;
		if (isRegularize)
		{
			J = J + (theta1a.hadamardProduct(theta1a).sum() + theta2a.hadamardProduct(theta2a).sum()) / (2 * m);
		}
		return J;
	}

	/**
	 * Be careful with the index number here
	 * Assume following structure (four layers L = 4)
	 * input layer (layer 1) -> layer 2 -> layer 3 -> output layer (layer 4	)
	 *              z1, a1,   theta1
	 * we use _ to denote a vector (column vector)
	 * @param thetas [t1, t2, t3 ...] thetas[0] corresponds to theta1, with is from layer 1 to layer 2.
	 * @param x_ the input with bias
	 * @param y_ the known result
	 * @return the thetas we learn from one sample in one iteration
	 */
	public static Matrix[] processOneSample(Matrix[] thetas, Vector x_, Vector y_)
	{
		// zs are the waited input of each layer
		// So it starts with z2, which is zs[0]
		// [z2, z3, z4 ...]
		Vector[] zs = new Vector[thetas.length];
		
		// as are the action of each layer
		// So it starts with a1 which is the input x_, so a2 is the sigmoid of z2, then add bias 1
		// [a1, a2 ,a3 ...]
		Vector[] as = new Vector[thetas.length + 1];
		as[0] = x_;
		
		// Forward propagation
		
		for (int i =0;i <thetas.length ;i++)
		{
			zs[i] = thetas[i].multiply(as[i]);
			if (i== thetas.length -1)
			{
				as[i+1] = MathFunctions.sigmoid(zs[i]);
			}
			else
			{
				as[i+1] = MatrixFunctions.addBias(MathFunctions.sigmoid(zs[i]));
			}
		}
		
		// Backward propagation
		// The derivative of each layer
		// [d2,d3,d4...]
		Vector delta[] = new Vector[thetas.length];
		Matrix derivative[] = new Matrix[thetas.length];
		// The first derivative we calculate is for the output layer
		// The formula depends on the cost function, we take it out of the loop
		delta[thetas.length -1] = as[thetas.length].subtract(y_);
		// as starts from a1
		derivative[thetas.length -1] = delta[thetas.length -1].toColumnMatrix().multiply(as[thetas.length -1].toRowMatrix());
		// Then we calculae L -1, L-2
		for (int i = thetas.length-1; i>0; i--)
		{

			// This is the core of backward propagation
		
			// 3 Take the thetas between L+1, L, multiply the derivative from L+1, multiply the z from L
			// Based on our index this is 
			// theta 2 T* dev 3 .* g'z2
			// which is theta[i], dev[i], z[i-1]
			delta[i-1] = thetas[i].transpose().multiply(delta[i])
					.hadamardProduct(MatrixFunctions.addBias(MathFunctions.sigmoidDerivative(zs[i-1])));
			
			// 4 Finally get the derivative, remove bias
			derivative[i-1] = delta[i -1].sliceRight(1).toColumnMatrix().multiply(as[i -1].toRowMatrix());
		}

		
		return derivative;

	}

	protected static void showSamples(Basic2DMatrix xMatrix ) throws InterruptedException
	{
		// define your data
		JFrame f = new JFrame();
		
		f.setLayout(new FlowLayout());
		MainFrame panel = new MainFrame();	
		final PaintPanel pdraw = new PaintPanel();
		
		f.add( panel);
		f.add( pdraw);

		JButton button = new JButton("Calculate");
		f.add(button);
		//
		button.addActionListener(new ActionListener()
		{
			 
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("You clicked the button");
                pdraw.clear();
                double [] a = pdraw.input;
                
                Vector predict = MathFunctions.sigmoid(
                		theta2result.multiply(MatrixFunctions.addBias(
                				MathFunctions.sigmoid(theta1result.multiply(MatrixFunctions.addBias(new BasicVector(a)))))));
                System.out.println(predict);
                int highest=0;
                for (int p=1;p<10;p++)
				{
					if(predict.get(highest) < predict.get(p))
					{
						highest = p;
					}
					
				}
                System.out.println("Your input is " + (highest +1) %10);
                
            }
        });      
		
		f.setSize(120, 240);
		f.show();
		
		for (int k = 0; k < 50; k++)
		{
			double[] z = ((BasicVector) (xMatrix.getRow(k * 100))).toArray();
			double[][] z2d = new double[20][20];

			for (int i = 0; i < 20; i++)
			{
				for (int j = 0; j < 20; j++)
					z2d[j][i] = z[(j * 20) + i];
			}

			panel.init(z2d);
			Thread.sleep(50);
			f.repaint();
		}
		
		
		
		
		
		
		
		

	}
	
	
	
}
