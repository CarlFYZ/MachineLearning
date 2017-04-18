package ml.core.linearalgebra;

import org.la4j.matrix.dense.Basic2DMatrix;

public class Tensor
{
	
	Basic2DMatrix[] tensor = null;
	
	
	public Tensor(Basic2DMatrix[] tensor)
	{
		this.tensor = tensor;
	}

	public Tensor(int a, int b, int layers)
	{
		this.tensor = new Basic2DMatrix[layers];

		for (int i = 0; i < layers; i++)

		{
			tensor[i] = MatrixFunctions.createRandomMatrix(a, b, Math.sqrt(60.0 / (a + b)));
		}
	}
	
}
