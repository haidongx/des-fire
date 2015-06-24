package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Cell;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CoordinateSetPresenter
{
	public static void main(String[] args)
	{
		File file = new File("OtherData/IgnitionPoints_MB_corrected_new16.0.txt");
		//show(file, 547, 545);
		show(file, 1025, 1021);

	}
	
	public static void show( File dataFile, int xDim, int yDim )
	{
		try
		{
			System.out.println("Loading data ...");
			List<Cell> cells = new ArrayList<Cell>();
			Scanner s = new Scanner(dataFile);
			while(s.hasNextLine())
			{
				int x, y;
				if(s.hasNextInt()) x=s.nextInt();
				else break;
				
				if(s.hasNextInt()) y=s.nextInt();
				else break;
				
				cells.add(new Cell(x, y));
			}
			s.close();
			System.out.println("Data loaded.");
			CellularAutomataPresentation p = new CellularAutomataPresentation(xDim, yDim);

			for( Cell c : cells)
				p.drawACellInBuffer(c.getX(), c.getY(), Color.blue);
			
			p.drawDirtyCellsInBuffer();
			p.showBufferOnScreen();

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}


}
