package edu.gsu.hxue.gis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;

/**
 * This class represents a data layer that is a two dimension area. There is a double number on each of the positions.
 * 
 * @author Haidong Xue
 * @version 9/14/2011
 *
 */

public class SingleGISLayer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8348004689736034487L;
	private int xDim;  //unit: number of cells
	private int yDim;  //unit: number of cells
	private double cellSize; // unit: meters
	private File dataFile; // data file path
	private double data[][]; //data
	
	public SingleGISLayer(File dataFile) throws FileNotFoundException
	{
		this.dataFile = dataFile;
		
		Scanner s = new Scanner(this.dataFile);
		s.next(); // skip "ncols"
		this.xDim = s.nextInt(); // load the value of "ncols"
		s.next(); // skip "nrows"
		this.yDim = s.nextInt(); // load the value of "nrows"
		s.next(); // skip "xllcenter"
		s.next(); // skip the value of "xllcenter"
		s.next(); // skip "yllcenter"
		s.next(); // skip the value of "yllcenter"
		s.next(); // skip "cellsize"
		this.cellSize = s.nextDouble(); // load the value of "cellsize"

		this.data = new double[xDim][yDim];

		for (int row = yDim-1; row >=0; row--)
		{
			for (int col = 0; col<xDim; col++)
			{
				if (s.hasNextDouble())
				{
					this.data[col][row] = s.nextDouble();
				}
				else
				{
					System.out.println("Error has been found when reading from: " + this.dataFile.getPath());
				}
			}
		}
		
		s.close();
	}
	
	public void saveToFile( File dataFile )
	{
		try
		{
			PrintWriter pw;
			pw = new PrintWriter(dataFile);
			pw.println("ncols\t" + this.xDim);
			pw.println("nrows\t" + this.yDim);
			pw.println("xllcenter\t0");
			pw.println("yllcenter\t0");
			pw.println("cellsize\t" + this.cellSize);
			
			for (int row = yDim-1; row >=0; row--)
			{
				for (int col = 0; col<xDim; col++)
				{
					pw.print(this.data[col][row] + "\t");
				}
				pw.println();
			}
			
			pw.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public SingleGISLayer( int xDim, int yDim, double cellSize, double[][] data)
	{
		this.xDim = xDim;
		this.yDim = yDim;
		this.cellSize = cellSize;
		this.data = new double[data.length][];
		for( int i=0; i<data.length; i++ )		
		{
			this.data[i] = new double[data[i].length];
			for( int j=0; j<data[i].length; j++)
				this.data[i][j] = data[i][j];
		}
			
	}
	
	
	
	public int xDim()
	{
		return this.xDim;
	}
	
	public int yDim()
	{
		return this.yDim;
	}
	
	public double cellSize()
	{
		return this.cellSize;
	}
	
	public File dataFile()
	{
		return this.dataFile;
	}
	
	public double dataAt( int x, int y)
	{
		return this.data[x][y];
	}
}
