package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.gis.SingleGISLayer;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class SlopePresenter
{
	public static void main(String[] args)
	{
		File slopeFile = new File("GISData/MooreBranch_slope_new61.0.txt");
		showSlope(slopeFile);

	}
	
	public static void showSlope( File slopeFile )
	{
		try
		{
			System.out.println("Loading data ...");
			SingleGISLayer s = new SingleGISLayer(slopeFile);
			System.out.println("Data loaded.");
			CellularAutomataPresentation p = new CellularAutomataPresentation(s.xDim(), s.yDim());
			for (int x = 0; x < s.xDim(); x++)
				for (int y = 0; y < s.yDim(); y++)
				{
					double value = s.dataAt(x, y);
					Color c = Color.getHSBColor(0.5f, (float)((value%90)/40), 1f);
					p.setCellColor(x, y, c);
					//System.out.println(value);
					p.setCellText(x, y, String.format("Slope=%.2f", value));
				}
			p.drawDirtyCellsInBuffer();
			p.showBufferOnScreen();
			

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

}
