package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import ml.common.util.FileUtil;

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
	
	@Test
	public void testReadStringTokens() throws IOException
	{
		List <List<String>> lines =  FileUtil.readTokens(new File("./data/20141020.txt"));
		for (List<String> line : lines)
		{
			for(String token: line)
			{
				System.out.print(token + ",") ;	
			}
			System.out.println();
			
		}
		
		lines = FileUtil.removeColumn(lines,new int[]{2,3});
		for (List<String> line : lines)
		{
			for(String token: line)
			{
				System.out.print(token + ",") ;	
			}
			System.out.println();
			
		}
		lines.toArray();
		
		FileUtil.writeFile(new File("./data/20141020V2.txt"), lines);
		
	}
	
	
	@Test
	public void testFindCommonInstruments() throws IOException
	{
		HashMap <String, Integer> instrumentsMap = new HashMap<String, Integer>();
		List <List<String>> lines1 =  FileUtil.readTokens(new File("./data/20141020.txt"));
		countAll(instrumentsMap, lines1);
		List <List<String>> lines2 =  FileUtil.readTokens(new File("./data/20141021.txt"));
		countAll(instrumentsMap, lines2);
		List <List<String>> lines3 =  FileUtil.readTokens(new File("./data/20141022.txt"));
		countAll(instrumentsMap, lines3);
		List <List<String>> lines4 =  FileUtil.readTokens(new File("./data/20141023.txt"));
		countAll(instrumentsMap, lines4);
		List <List<String>> lines5 =  FileUtil.readTokens(new File("./data/20141024.txt"));
		countAll(instrumentsMap, lines5);
		
		Iterator<String> iterator = instrumentsMap.keySet().iterator();
		HashSet<String> names = new HashSet<String>();
		
		while (iterator.hasNext())
		{
			// 
			String key = iterator.next();
			//System.out.println("    " + key +"/" +  instrumentsMap.get(key));
			
			if ( instrumentsMap.get( key)  == 5 )
			{
				names.add(key);
			}
			
		}
		
		FileUtil.writeFile(new File("./data/A20141020.csv"), lines1, names);
		FileUtil.writeFile(new File("./data/A20141021.csv"), lines2, names);
		FileUtil.writeFile(new File("./data/A20141022.csv"), lines3, names);
		FileUtil.writeFile(new File("./data/A20141023.csv"), lines4, names);
		FileUtil.writeFile(new File("./data/A20141024.csv"), lines5, names);
		

		
		
	}

	protected void countAll(HashMap<String, Integer> instrumentsMap, List<List<String>> lines)
	{
		for (List<String> line : lines)
		{
			int i = 0;
			for(String token: line)
			{
				System.out.print(token + ",") ;
				if  (i == 0)
				{
					Integer count = instrumentsMap.get(token);
					if (count == null)
					{
						instrumentsMap.put(token, 1);	
					}
					else
					{
						instrumentsMap.put(token, count + 1);	
					}
					
				}
				i++;
			}
			System.out.println();
			
		}
	}
}
