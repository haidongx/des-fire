package edu.gsu.hxue.experimentUtilities.dataFileGeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DataFileGenerationCore
{
	public static void generateUniformDataFile ( String filePath, int numberOfColumns, int numberOfRows, double cornerXCoordinate, double cornerYCoordinate, double cellSize, double value )
	{
		
		PrintWriter p;
		try
		{
			p = new PrintWriter(new File(filePath) );
			p.print("ncols: ");
			p.println(numberOfColumns);	
			p.print("nrows: ");
			p.println(numberOfRows);
			p.println("xllcenter: " + cornerXCoordinate);
			p.println("yllcenter: " + cornerYCoordinate);
			p.println("cellsize: " + cellSize);

			for (int row = numberOfRows-1; row >=0; row--)
			{
				for (int col = 0; col<numberOfColumns; col++)
				{
					p.print(String.format("%.2f ", value));
				}
				p.println();
			}
			
			p.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

}
