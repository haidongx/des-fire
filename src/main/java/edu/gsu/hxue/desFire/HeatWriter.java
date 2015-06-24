package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.exceptions.NotSupportedFunctionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Vector;

public class HeatWriter extends Model implements Cloneable
{
	private static final long serialVersionUID = 6778478491467950512L;
	protected double heatUpdateInterval;
	protected HashSet<Integer> activeCells = new HashSet<Integer>();
	protected int xDim;
	protected int yDim;
	protected double cellSize;	
	protected double timer;
	
	protected double heatOuputAdvance = 0.1;
	
	protected boolean moreDetail = false;
	
	private String folderPath = "HeatmapFiles/";

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public Object clone()
	{
		HeatWriter c = (HeatWriter) super.clone();
		return c;
	}
	
	public HeatWriter(DESSystem system, double heatUpdateInterval, double cellSize, int xDim, int yDim)
	{
		super(system);
		this.heatUpdateInterval = heatUpdateInterval;
		timer = 0;
		
		this.xDim = xDim;
		this.yDim = yDim;
		this.cellSize = cellSize;
	}

	@Override
	public void internalTransit(double delta_time)
	{
		timer -= delta_time;
	}

	@Override
	public void externalTransit(Message message)
	{
		if (message instanceof FireCell.MessageToHeatWriter)
		{
			FireCell.MessageToHeatWriter m = (FireCell.MessageToHeatWriter) message;
			if (m.type == FireCell.MessageToHeatWriter.MessageToHeatWriterEnum.SUBSCRIBE)
			{
				this.activeCells.add(m.fireCellIndex);
			}
			else if (m.type == FireCell.MessageToHeatWriter.MessageToHeatWriterEnum.UNSUBSCRIBE)
			{
				this.activeCells.remove(m.fireCellIndex);
			}
		}
	}

	@Override
	public void generateOutput() throws NotSupportedFunctionException
	{
		// Set the paths of folder and file
		if (timer <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			int timestamp = (int) Math.round(this.getTime());
			String sysName = this.getSystem().getName();
			String filePath =  folderPath + sysName + timestamp + ".txt";

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

				for (int index : this.activeCells)
				{
					FireCell c = (FireCell) system.getModel(index);
					int x = c.getX();
					int y = c.getY();
					double sensibleHeat = c.getSensibleHeatFluxDensity(this.getTime() - this.heatUpdateInterval, this.getTime());
					double latentHeat = c.getLatenHeatFluxDensity(this.getTime() - this.heatUpdateInterval, this.getTime());
					
					//double temperature = c.getKelvinTemperature();
					double temperature = estimateReactionTemperature(this.getTime() - this.heatUpdateInterval, this.getTime(), c);
					
					
					double moisture = c.getFuelMoisture();
					
					// Only output non-zero cells
					if ( !GlobalConstants.REMOVE_ZERO_HEAT_CELL_FROM_HEAT_WRITER || sensibleHeat >= burningThreshold)
						writer.println(x + "\t" + y + "\t" + sensibleHeat + "\t" + latentHeat + "\t" + temperature + "\t" + moisture);
					
					// More detailed information
					/*
					 * with the values (with units) for fuel fraction at the beginning and end of the time step, 
					 * initial (unburned) fuel mass per square meter, 
					 * specific heat released by combustion for the given fuel type (joules per kilogram), 
					 * and moisture content (%)
					 */
					if(moreDetail)
					{
						writer.println("========More detail about " + x + "-" + y);
						writer.println("========Fule type: " + c.getFuleName());
						writer.println("========Fuel fraction: " + c.getFractionAt(this.getTime() - this.heatUpdateInterval) + " ==> " +  c.getFractionAt(this.getTime()));
						writer.println("========Initial fuel loading [kg/m2]: " + c.getTotalFuelLoadingDensity());
						writer.println("========Heat content[KJ/kg]: " + c.getHeatContent());
						writer.println("========Fuel mosture: " + c.getFuelMoisture());
						
						if(x==289 && y==237)
						{
							System.out.println( sensibleHeat + " at " + this.getTime());
						}
					}
					
					//When burned out, record it
					if( c.getFractionAt(this.getTime() )<burningThreshold) inactiveCells.add(index);
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
			
			
			// advance the heat release by heatOuputAdvance seconds
			if(timestamp==0) timer = this.heatUpdateInterval - heatOuputAdvance;
			else timer = this.heatUpdateInterval;
		}
	}

	/**
	 * Note: it is only affected by fuel loss rate but not heat flux
	 * @param startTime
	 * @param endTime
	 * @param c
	 * @return
	 * @throws NotSupportedFunctionException 
	 */
	private static double  estimateReactionTemperature(double startTime, double endTime, FireCell c) throws NotSupportedFunctionException
	{
		double backgroundTemp = c.getBackGroundFuelTemperature();
		double weightFactor = c.getWeighingFactor();
		double tf = weightFactor/0.85141;
		double ignitionTemp = c.getIgnitionTemperature();
		double tempA = Math.pow(backgroundTemp, 4);
		double tempB = (c.getFractionAt(endTime) - c.getFractionAt(startTime))*tf*(Math.pow(ignitionTemp, 4) - tempA)/(endTime-startTime);
		
		return Math.pow((tempA-tempB), 0.25);
	}

	@Override
	public double getNextInternalTransitionTime()
	{
		return this.timer;
	}

}
