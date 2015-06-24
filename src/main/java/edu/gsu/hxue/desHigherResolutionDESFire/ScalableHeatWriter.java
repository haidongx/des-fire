package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.FireCell;
import edu.gsu.hxue.desFire.GlobalConstants;
import edu.gsu.hxue.desFire.HeatWriter;
import edu.gsu.hxue.desFire.exceptions.NotSupportedFunctionException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ScalableHeatWriter extends HeatWriter
{
	private int scaleFactor;
	
	private class CellInfo{
		public double sensibleHeat;
		public double latentHeat;
		public double temperature;
		public double moisture;
		
		public int activeCellNumber=0; 
	}
	
	public ScalableHeatWriter(DESSystem system, double heatUpdateInterval, double cellSize, int xDim, int yDim, int scaleFactor)
	{
		super(system, heatUpdateInterval, cellSize, xDim, yDim);
		this.scaleFactor = scaleFactor;
	}

	@Override
	public void generateOutput() throws NotSupportedFunctionException
	{
		// Set the paths of folder and file
		if (timer <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			String folderPath = "HeatmapFiles";
			int timestamp = (int) Math.round(this.getTime());
			String filePath = folderPath + "/" + timestamp + ".txt";

			// Create folder, if not existing
			File folder = new File(folderPath);
			if (!folder.exists())
				folder.mkdir();

			// Create file, if not existing
			File HeatMapFile = new File(filePath);
			if (!HeatMapFile.exists())
				try
				{
					HeatMapFile.createNewFile();
				}
				catch (IOException e)
				{
					System.out.println("Failed to create file: " + filePath);
					System.exit(1); // force to exit with abnormal reason
				}

			// Create PrintWriter
			PrintWriter writer = null;
			boolean writeToFile = true;
			double burningThreshold = 1E-7;
			try
			{
				if (writeToFile)
				{
					writer = new PrintWriter(HeatMapFile);
					//write the file head
					writer.println("Current simulation time: " + timestamp);
					writer.println("# of burning cells (sensible heat flux>" + burningThreshold + "): " + this.activeCells.size());
					writer.println("Gird dimensions:");
					writer.println("CellSideSize(m)" + "\t" + "SpaceWidth" + "\t" + "SpaceHeight");
					writer.println(cellSize + "\t" + this.xDim + "\t" + this.yDim);
					writer.println();
					writer.println("X_PT" + "\t" + "Y_PT" + "\t" + "SensibleHeat[W/(m*m)]\t" + "LatentHeat[W/(m*m)]\t" + "FlameTemp[K]\t" + "FuelMoisture[%]\t");
				}
				
				Vector<Integer> inactiveCells = new Vector<Integer>();

				HashMap<Point, CellInfo> mergedCells = new HashMap<Point, CellInfo>();
				for (int index : this.activeCells)
				{
					// Merge heat into original grid
					FireCell c = (FireCell) system.getModel(index);
					int x = c.getX();
					int y = c.getY();
					double sensibleHeat = c.getSensibleHeatFluxDensity(this.getTime() - this.heatUpdateInterval, this.getTime());
					double latentHeat = c.getLatenHeatFluxDensity(this.getTime() - this.heatUpdateInterval, this.getTime());
					double temperature = c.getKelvinTemperature();
					double moisture = c.getFuelMoisture();
					
					//merge
					int origX = x/this.scaleFactor;
					int origY = y/this.scaleFactor;
					Point key = new Point(origX, origY);
					
					CellInfo info;
					
					if(mergedCells.containsKey(key))
						info = mergedCells.get(key);
					else
						info = new CellInfo();
					
					info.latentHeat += latentHeat;
					info.sensibleHeat += sensibleHeat;
					info.moisture += moisture;
					info.temperature +=  temperature;
					info.activeCellNumber++;
					
					mergedCells.put(key, info);
					
					//When burned out, record it
					if( c.getFractionAt(this.getTime() )<burningThreshold) inactiveCells.add(index);
				}
				
				// Merging calculation
				for(Map.Entry<Point, CellInfo> orgCell : mergedCells.entrySet())
				{
					CellInfo info = orgCell.getValue();
					double numberOfFineGrids = scaleFactor*scaleFactor;
					info.latentHeat = info.latentHeat / numberOfFineGrids;
					info.sensibleHeat = info.sensibleHeat / numberOfFineGrids;
					info.moisture = info.moisture / info.activeCellNumber;
					info.temperature = info.temperature / info.activeCellNumber;
				}
				
				for ( Map.Entry<Point, CellInfo> c: mergedCells.entrySet())
				{
					int x = c.getKey().x;
					int y = c.getKey().y;
					double sensibleHeat = c.getValue().sensibleHeat;
					double latentHeat = c.getValue().latentHeat;
					double temperature = c.getValue().temperature;
					double moisture = c.getValue().moisture;
					
					// Only output non-zero cells
					if ( !GlobalConstants.REMOVE_ZERO_HEAT_CELL_FROM_HEAT_WRITER || sensibleHeat >= burningThreshold)
						writer.println(x + "\t" + y + "\t" + sensibleHeat + "\t" + latentHeat + "\t" + temperature + "\t" + moisture);
	
				}
				
				//remove burned out cells
				if(GlobalConstants.REMOVE_ZERO_HEAT_CELL_FROM_HEAT_WRITER)
					for(int index : inactiveCells) this.activeCells.remove(index);
				
				//close file
				if (writeToFile) writer.close();

				// Create heat file flag
				File HeatMapFileFlag = new File(filePath + "_ready");
				if (!HeatMapFileFlag.exists())
					try
					{
						HeatMapFileFlag.createNewFile();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

			}
			catch (FileNotFoundException e)
			{
				System.out.println("Failed to open file: " + filePath);
				System.exit(1); // force to exit with abnormal reason
			}
			System.out.println("Heat file " + HeatMapFile.getName() + " is generated!! at " + this.getTime());
			
			
			if(timestamp==0) timer = this.heatUpdateInterval - heatOuputAdvance;
			else timer = this.heatUpdateInterval;
		}
		
	
		
		
	}
	
	

}
