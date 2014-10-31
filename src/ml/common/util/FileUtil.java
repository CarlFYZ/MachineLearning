package ml.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class FileUtil {
	
	/**
	 * Read a 
	 * @param fileName
	 * @return
	 */
	public static ArrayList <String> readFile(File fileName) {
		ArrayList<String> result  = new ArrayList<String>();
		try {
			
			// if file doesnt exists, then create it
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.add(line);
				//System.out.println(line);
			}
			//System.out.println(line = in.readLine());
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Read comma separated lines into list of list List<List<String>>
	 * @param fileName
	 * @return
	 */
	public static List<List<String>> readTokens(File fileName)
	{
		List<List<String>> result = new LinkedList<List<String>>();

		try
		{

			// if file doesnt exists, then create it
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String line;
			while ((line = in.readLine()) != null)
			{
				String[] tokens = line.split(",");
				result.add( new LinkedList<String>(Arrays.asList(tokens)));
				// System.out.println(line);
			}
			// System.out.println(line = in.readLine());
			in.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Not the columns index must be descending so it 
	 * @param lines
	 * @param columns
	 * @return
	 */
	public static List<List<String>> removeColumn (List<List<String>> lines, int[] columns)
	{
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> line : lines)
		{
			//List<String> newLine = new ArrayList<String>();
			
			Arrays.sort(columns); 

			for (int i = columns.length -1;i >=0 ; i--)
			{
				line.remove(columns[i]);
			}

			result.add(line);
			
		}
		
		return result;
	}
	
	
	public static void writeFile(File fileName, List<List<String>> lines) {
		try {
			
			// if file doesnt exists, then create it
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF8"));

			for (List<String> line : lines)
			{
				for(int i = 0; i< line.size(); i++)
				{
					String delimiter = ",";
					if (i == line.size() -1)
					{
						delimiter = "\n";
					}
					out.write(line.get(i) + delimiter) ;	
				}
								
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void writeFile(File fileName, List<List<String>> lines, Set<String> filters) {
		try {
			
			// if file doesnt exists, then create it
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF8"));

			for (List<String> line : lines)
			{
				if (filters.contains(line.get(0)))
				{
					for (int i = 0; i < line.size(); i++)
					{
						String delimiter = ",";
						if (i == line.size() - 1)
						{
							delimiter = "\n";
						}
						out.write(line.get(i) + delimiter);
					}
				}
				else
				{
					//System.out.println();
				}

			}
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void writeFile(File fileName, Matrix matrix) {
		try {
			
			// if file doesnt exists, then create it
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF8"));

			for (int i = 0; i < matrix.rows(); i++)
			{
				Vector row = matrix.getRow(i); 

					for (int j = 0; j < row.length(); j++)
					{
						String delimiter = ",";
						if (j == row.length() - 1)
						{
							delimiter = "\n";
						}
						out.write(row.get(j) + delimiter);
					}


			}
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
