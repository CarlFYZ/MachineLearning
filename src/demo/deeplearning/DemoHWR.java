package demo.deeplearning;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;

import demo.neuralnetwork.MainFrame;
import demo.neuralnetwork.PaintPanel;
import ml.core.linearalgebra.MatrixFunctions;
import ml.core.linearalgebra.Tensor;
import ml.core.math.MathFunctions;
import ml.deeplearning.ConvolutionalNeuralNetwork;


public class DemoHWR
{
	
	public static Basic2DMatrix theta1result;
	public static  Basic2DMatrix theta2result;
	
	
	
	/**
	 * 
	 * Color * Width * Height 3 * 20 * 20
	 * 
	 * 
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception
	{
		MatFileReader matfilereader = new MatFileReader("./data/ex4weights.mat");
		
		int steps = 1000;
		int costCalculationInterval = 1;
		
		boolean verifyGradient = false;
		// model parameters
		double alpha = 0.05;
		// if lambda >= 0, then parameter regularization is used
		double lambda = 1;
		
		
		// theta1 and theta2 to train
		MLDouble theta1ml = (MLDouble) matfilereader.getMLArray("Theta1");
		MLDouble theta2ml = (MLDouble) matfilereader.getMLArray("Theta2");
		Basic2DMatrix theta1 = new Basic2DMatrix(theta1ml.getArray());
		Basic2DMatrix theta2 = new Basic2DMatrix(theta2ml.getArray());
		System.out.println("theta1:" + theta1ml);
		System.out.println("theta2:" + theta2ml);

		theta1 = MatrixFunctions.createRandomMatrix(25, 91, Math.sqrt(60.0 / (25 + 91)));
		theta2 = MatrixFunctions.createRandomMatrix(10, 26, Math.sqrt(60.0 / (10 + 26)));

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
		BasicVector y_ = (BasicVector) (new Basic2DMatrix(yml.getArray())).getColumn(0);
		// System.out.println(y);

		double[][] Ys = new double[10][5000];
		for (int i = 0; i < y_.length(); i++)
		{
			Ys[(int) y_.get(i) - 1][i] = 1;
		}
		Matrix Y = new Basic2DMatrix(Ys);
		
		// Deeplearning: Layer 1: 20 Filters
		Tensor[][] learningFilters = new Tensor[2][];

		learningFilters[0] = new Tensor[20];
		for (int i = 0; i < learningFilters[0].length; i++)
		{

			learningFilters[0][i] = new Tensor(5, 5, 3);
		}
		
		
		learningFilters[1] = new Tensor[10];
		for (int i = 0; i < learningFilters[1].length; i++)
		{
			learningFilters[1][i] = new Tensor(3, 3, 20);
		}

		// The thetas to learn, it starts from the original input and changes every iteration
		Matrix[] learningThetas = new Matrix[] {theta1, theta2};


		///////////////////////////////////////////////////////////////
		//                   Start training                          // 
		///////////////////////////////////////////////////////////////
		learningThetas = ConvolutionalNeuralNetwork.gradientDescent(steps, costCalculationInterval, verifyGradient, alpha, lambda, m, X, y_, Y, learningThetas, learningFilters);

		showSamples(xMatrix);
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
