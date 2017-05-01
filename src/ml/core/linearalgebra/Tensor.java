package ml.core.linearalgebra;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class Tensor
{
	
	Matrix[] tensor = null;
	
	
	public Tensor(Matrix... tensor)
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
	
	public Tensor(int a, int b, int layers, int value)
	{
		this.tensor = new Basic2DMatrix[layers];

		for (int i = 0; i < layers; i++)
		{
			tensor[i] = MatrixFunctions.createMatrix(a, b, value);
		}
	}

	
	
	public int filters()
	{
		return tensor.length;
	}
	
	public int rows()
	{
		return tensor[0].rows();
	}
	
	public int columns()
	{
		return tensor[0].columns();
	}
	
	/**
	 * Get 2D filter by index.
	 * e.g. 5*5
	 */
	public Matrix getFilter(int filterIndex)
	{
		return tensor[filterIndex];
	}
}
