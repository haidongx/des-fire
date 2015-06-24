package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;

public class HeatFilePresenter
{
	private static void drawHeatFiles ( String heatFileFolder, String settingFileFolder, String aspect, String fuel, String slope)
	{
		try
		{
		/*	// get dimension info
			System.out.println("Loading GIS map ... ");
			File gisFolder = new File (settingFileFolder);
			
			if(!gisFolder.isDirectory() || !gisFolder.exists())
			{
				System.err.println("GIS folder is not valid");
			}
			
			GISData gisMap = new GISData(
					new File(gisFolder, slope), // slope
					new File(gisFolder, aspect), // aspect
					new File(gisFolder, fuel) // fuel
			);
			
			int xDim = gisMap.xDim();
			int yDim = gisMap.yDim();*/
			
			int xDim = 3282;
			int yDim = 3270;
			
			// create presentation layer
			CellularAutomataPresentation p = new CellularAutomataPresentation(xDim, yDim, 0.3);
			p.setTitle("MB Fire - " + aspect + " " + fuel + " " + slope);
			p.setDrawFrameOfReference(true);
			
			int startStep = 0;
			int endStep = 720*2;
			int stepInterval = 60;
					
			ExpUtilities.drawIgnitedCellsFromHeatFiles(heatFileFolder, stepInterval, startStep, endStep, p, xDim, yDim, Color.black);
	}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		String heatFileFolderString = "C:/Users/haydon/Desktop/Temp/MooreBranch/arps_150m5m_heatfiles/";
		String gisFolder = "C:/Users/haydon/Google Drive/DEVSFIRE_ARPS Coupling/ExpOnMooreBranch/sensitivityMB_GIS";
		String aspect = "MooreBranch_aspect5m.txt";
		String fuel = "MooreBranch_fuel5m.txt";
		String slope = "MooreBranch_slope5m.txt";
		
		drawHeatFiles(heatFileFolderString, gisFolder, aspect, fuel, slope);
	}

}
