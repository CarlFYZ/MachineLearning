package demo.neuralnetwork;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;

import ml.core.linearalgebra.MatrixFunctions;
import ml.core.math.MathFunctions;
import ml.neuralnetwork.NeuralNetwork;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;


public class Demo2
{
	
	public static Basic2DMatrix theta1result;
	public static  Basic2DMatrix theta2result;
	
	public static void main(String[] args) throws Exception
	{
		
		Basic2DMatrix matrix = new Basic2DMatrix(
				Matrices.asSymbolSeparatedSource(new FileInputStream(
						"./data/20141025weekV5.csv")));
		
		int steps = 100000;
		int costCalculationInterval = 100;
		
		boolean verifyGradient = false;
		// model parameters
		double alpha = 0.1;
		// if lambda >= 0, then parameter regularization is used
		double lambda = 0;
		
		
		// theta1 and theta2 to train
		Matrix theta1 = MatrixFunctions.createRandomMatrix(40, 20, Math.sqrt(6.0 / (20 + 19)));
		Matrix theta2 = MatrixFunctions.createRandomMatrix(10, 41, Math.sqrt(6.0 / (10 + 41)));
		Matrix theta3 = MatrixFunctions.createRandomMatrix(1, 11, Math.sqrt(6.0 / (1 + 11)));
//		Matrix theta4 = MatrixFunctions.createRandomMatrix(2, 4, Math.sqrt(6.0 / (2 + 4)));
//		Matrix theta5 = MatrixFunctions.createRandomMatrix(1, 3, Math.sqrt(6.0 / (1 + 3)));
		// X
		//Matrix xMatrix = matrix.slice(0, 0, matrix.rows(), 5 );
		
//		Matrix xMatrix = MatrixFunctions.concatenate( matrix.getColumn(0).subtract(2.5).divide(2.5), 
//				MatrixFunctions.concatenate( matrix.getColumn(1).subtract(2.5).divide(2.5), 
//						MatrixFunctions.concatenate( matrix.getColumn(2).subtract(2.5).divide(2.5), 
//								MatrixFunctions.concatenate(matrix.getColumn(3).subtract(2.5).divide(2.5), 
//										matrix.getColumn(4).subtract(500000).divide(500000),  true),true),true),true);
		
		Matrix xMatrix = matrix.slice(0, 0, matrix.rows(), matrix.columns()-1);
		// should be m * 20
		
		System.out.println(xMatrix.rows());
		System.out.println(xMatrix.columns());


		// number of samples
		int m = xMatrix.rows();

		// X with 1 as first column
		Matrix X = MatrixFunctions.concatenate(MatrixFunctions.createVector(m, 1), xMatrix, true);

		// y Vector and Y matrix(10 * 5000)
		Vector y_ = matrix.getColumn(19);
		// System.out.println(y);

//		double[][] Ys = new double[1][m];
//		for (int i = 0; i < y_.length(); i++)
//		{
//			Ys[(int) y_.get(i) - 1][i] = 1;
//		}
		Matrix Y = new Basic2DMatrix(y_.toRowMatrix());
		
		// The thetas to learn, it starts from the original input and changes every iteration
		Matrix[] learningThetas = new Matrix[] {theta1, theta2, theta3};// theta4, theta5};


		///////////////////////////////////////////////////////////////
		//                   Start training                          // 
		///////////////////////////////////////////////////////////////
		learningThetas = NeuralNetwork.gradientDescent(steps, costCalculationInterval, verifyGradient, alpha, lambda, m, X, y_, Y, learningThetas);

		//showSamples(xMatrix);
	}


	/**
	 * Some demo code
	 * @param xMatrix
	 * @throws InterruptedException
	 */
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
