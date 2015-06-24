package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.gis.SingleGISLayer;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class FuelPresenter
{

	public static void main(String[] args)
	{
		File fuelFile = new File("GISData/MooreBranch_fuel_new16.0.txt");
		showFuel(fuelFile);

	}
	
	public static void showFuel( File fuelFile )
	{
		try
		{
			System.out.println("Loading data ...");
			SingleGISLayer s = new SingleGISLayer(fuelFile);
			System.out.println("Data loaded.");
			CellularAutomataPresentation p = new CellularAutomataPresentation(s.xDim(), s.yDim());

			for (int x = 0; x < s.xDim(); x++)
				for (int y = 0; y < s.yDim(); y++)
				{
					double value = s.dataAt(x, y);
					Color c = StandardCustomizedFuelEnum.modelIndexToFuelType((int)value).getColor();
					p.setCellColor(x, y, c);
					p.setCellText(x, y, "Fuel=" + StandardCustomizedFuelEnum.modelIndexToFuelType((int)value).toString());
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
