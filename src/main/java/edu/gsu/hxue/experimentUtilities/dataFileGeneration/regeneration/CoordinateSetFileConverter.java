package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Cell;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CoordinateSetFileConverter
{

	public static void convertFile(File dataFile, String newDataFilePath, CoordinateConverter con, double threshold) 
	{
		
		try
		{
			//old cell set
			List<Cell> oldCells = new ArrayList<Cell>();
			Scanner s = new Scanner(dataFile);
			while(s.hasNextLine())
			{
				int x, y;
				if(s.hasNextInt()) x=s.nextInt();
				else break;
				
				if(s.hasNextInt()) y=s.nextInt();
				else break;
				
				oldCells.add(new Cell(x, y));
			}
			s.close();
			
			// build a value map
			double[][] valueMap = new double[con.getOldXDim()][con.getOldYDim()];
			for(int i=0; i<con.getOldXDim(); i++)
				for(int j=0; j<con.getOldYDim(); j++)
					valueMap[i][j]=0;
			for( Cell c: oldCells)
				valueMap[c.getX()][c.getY()]=1;
			
			// new cell set
			List<Cell> newCells = new ArrayList<Cell>();
			for( int x=0; x<con.getNewXDim(); x++)
				for(int y=0; y<con.getNewYDim(); y++)
				{
					List<CellWithPortion> re = con.calculateCoveredOldCells(x, y);
					double weightedValue = 0;
					double totalPortion = 0;
					for( CellWithPortion cp : re)
					{
						int oldX = cp.getCell().getX();
						int oldY = cp.getCell().getY();
					
						if(oldX>=0 && oldX<con.getOldXDim() && oldY>=0 && oldY<con.getOldYDim() )
						{
							// calculate the weighted value
							weightedValue += valueMap[oldX][oldY] * cp.getPortion();
							totalPortion += cp.getPortion();
						}
					}
					weightedValue /= totalPortion;
					if(weightedValue>=threshold) newCells.add(new Cell(x, y));
				}
			
			// write the new cells
			PrintWriter pw = new PrintWriter( new File(newDataFilePath));
			for(Cell c : newCells)
				pw.println(c.getX() + "\t" + c.getY());
			pw.close();
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

}
