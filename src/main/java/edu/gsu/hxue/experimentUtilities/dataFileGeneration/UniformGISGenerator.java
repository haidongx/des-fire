package edu.gsu.hxue.experimentUtilities.dataFileGeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class UniformGISGenerator
{

	public static void main(String[] args)
	{
		int xDim = 200;
		int yDim = 200;
		double cellSize = 60;
		
		double value = 7;
		String fileName = "fuel" + xDim + "_" + cellSize + ".txt";
		Generate( xDim, yDim, cellSize, value, fileName);
		
		value = 0;
		fileName = "slope" + xDim + "_" + cellSize + ".txt";
		Generate( xDim, yDim, cellSize, value, fileName);
		
		value = 0;
		fileName = "aspect" + xDim + "_" + cellSize + ".txt";
		Generate( xDim, yDim, cellSize, value, fileName);

	}
	
	private static void Generate( int xDim, int yDim, double cellSize, double value, String fileName )
	{
		PrintWriter p;
		try
		{
			p = new PrintWriter(new File("GISData/"+fileName) );
			p.print("ncols: ");
			p.println(xDim);	
			p.print("nrows: ");
			p.println(yDim);
			p.println("xllcenter: 0.0");
			p.println("yllcenter: 0.0");
			p.println("cellsize: " + cellSize);

			for (int row = yDim-1; row >=0; row--)
			{
				for (int col = 0; col<xDim; col++)
				{
					p.print(value+ " ");
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
