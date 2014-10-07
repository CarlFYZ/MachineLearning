package test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.la4j.matrix.dense.Basic2DMatrix;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

/**
 * The test suite for JMatIO
 * 
 * @author fengyuanz
 */
public class MatFileToMatrixTest
{
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter( MatFileToMatrixTest.class );
    }
    
	@Test
	public void testReadDoubleArrayAndConvert() throws IOException
	{
		MatFileReader matfilereader = new MatFileReader("./data/ex3weights.mat");
		MLArray theta1 = matfilereader.getMLArray("Theta1");
		MLDouble theta2 = (MLDouble) matfilereader.getMLArray("Theta2");
		System.out.println(theta1);
		System.out.println(theta2);
		System.out.println(theta2.getArray().length);
		System.out.println(theta1.contentToString());

		MatFileReader matfilereader2 = new MatFileReader("./data/ex3data1.mat");
		MLDouble X = (MLDouble) matfilereader2.getMLArray("X");
		Basic2DMatrix matrix = new Basic2DMatrix(X.getArray());
		System.out.println(X);
		System.out.println(matrix.rows());
		assertEquals(matrix.rows(), 5000);
		assertEquals(matrix.columns(), 400);

	}
  
}
