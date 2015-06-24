package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.desFire.*;
import edu.gsu.hxue.desFire.couplingWithARPS.CouplingConfig;

public class ScalableFireSystem extends FireSystem
{
	int scaleFactor;
	
	public ScalableFireSystem(FireSystemConfig config)
	{
		super(config);
	}
	
	@Override
	protected void creatModelsAndCoupling(Object con )
	{
		FireSystemConfig config = (FireSystemConfig)con;
		// Set GIS map
		String folder = config.gisFileFolder;
		try
		{
			System.out.println("Loading GIS map ... ");
			this.gisMap = new GISData(folder + config.slopeFileName, // slope
					folder + config.aspectFileName, // aspect
					folder + config.fuelFileName // fuel
			);
			System.out.println("slope map: " + config.slopeFileName);
			System.out.println("aspect map: " + config.aspectFileName);
			System.out.println("fuel map: " + config.fuelFileName);
			// long mem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("GIS map loaded");
			// System.out.println("GIS map memory usage: " + mem1/1000000.0 + "MB");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		// Create models
		System.out.println("Creating fire spread system ... ");
		
		// scale factor
		int scaleFactor = ((CouplingConfig)config).scaleFactor;
		this.scaleFactor = scaleFactor;
		
		//index
		fireCellIndex = new int[gisMap.xDim()*scaleFactor][gisMap.yDim()*scaleFactor];
		
		// Create the igniter
		ScalableIgniter igniter = new ScalableIgniter(this, config.initialIgnitionFile, gisMap.xDim(), gisMap.yDim(), scaleFactor);  
		
		// Create the suppressor
		Suppressor suppressor = new ScalableSuppressor(this, config.initialContainedCells, gisMap.xDim(), gisMap.yDim(), scaleFactor); 
		System.out.println("Created new suppressor");
		
		// Create the presentation
		ScalableFirePresentation presentation = null;
		if(config.showPresentationLayer)
			 presentation = new ScalableFirePresentation(this, gisMap, scaleFactor);
		
		// Create the monitor
		SystemMonitor monitor = new SystemMonitor(this);  

		// Create heat writer
		double heatUpdateInterval = config.heatUpdateInterval;
		HeatWriter heat = new ScalableHeatWriter(this, heatUpdateInterval, 
				gisMap.cellSize(),  //for output only, need to say the original size 
				gisMap.xDim(), 
				gisMap.yDim(), 
				scaleFactor); 

		// Create the wind model
		// WindModel wind = new WindModel(this, foler + "weather_artificial_DEVSFIRE.txt");
		ComplexWindModel wind = new ScalableComplexWindModel(this, folder + config.initialWeatherFileName, gisMap.xDim(), gisMap.yDim(), scaleFactor); // need to be modified
		this.windModelIndex = wind.getModelIndex();

		// Create fire cells and couple the monitor
		int scaledXDim = this.gisMap.xDim()*scaleFactor;
		int scaledYDim = this.gisMap.yDim()*scaleFactor;
		FireCell[][] cellRefs = new FireCell[scaledXDim][scaledYDim];

		System.out.print("Creating fire cells ... ");
		double counter = 0;
		double counter2 = 0;
		for (int i = 0; i < scaledXDim; i++)
			for (int j = 0; j < scaledYDim; j++)
			{
				int mapX = scaledToReal(i, scaleFactor);
				int mapY = scaledToReal(j, scaleFactor);
				// Create cell
				cellRefs[i][j] = new FireCell(this, i, j, // coordinates
						8, 90, // initial wind
						gisMap.slopeAt(mapX, mapY), gisMap.aspectAt(mapX, mapY), // slope
						StandardCustomizedFuelEnum.modelIndexToFuelType(gisMap.fuelModelIndexAt(mapX, mapY)), gisMap.cellSize()/scaleFactor, gisMap.cellSize()/scaleFactor // cell size
				);
				
				//System.out.println(i+"-" +j+ " " + gisMap.slopeAt(i, j));
				
				// Record index
				fireCellIndex[i][j] = cellRefs[i][j].getModelIndex();

				// couple to system monitor, and wind model
				cellRefs[i][j].setMonitor(monitor);
				cellRefs[i][j].setWindModel(wind);
				cellRefs[i][j].setHeatWriter(heat);
				//igniter.setCellIndex(i, j, cellRefs[i][j].getModelIndex());
				suppressor.setCellIndex(i, j, cellRefs[i][j].getModelIndex());
				if(config.showPresentationLayer) presentation.setCellIndex(i, j, cellRefs[i][j].getModelIndex());
				
				counter2++;
				if (counter++ / (scaledXDim * scaledYDim) >= 0.05)
				{
					System.out.format("%2.0f%%->", (double)counter2/(scaledXDim * scaledYDim)*100);
					counter = 0;
				}
			}

		System.out.println("100%.\n Fire cells created");

		// Couple cells
		System.out.print("Coupling fire cells ...");
		counter = 0;
		counter2 = 0;
		//double p = 0;
		for (int i = 0; i < scaledXDim; i++)
			for (int j = 0; j < scaledYDim; j++)
			{	
				FireCell N, NE, E, SE, S, SW, W, NW;

				int x = i;
				int y = j + 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					N = cellRefs[x][y];
				else
					N = null;

				x = i + 1;
				y = j + 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					NE = cellRefs[x][y];
				else
					NE = null;

				x = i + 1;
				y = j;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					E = cellRefs[x][y];
				else
					E = null;

				x = i + 1;
				y = j - 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					SE = cellRefs[x][y];
				else
					SE = null;

				x = i;
				y = j - 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					S = cellRefs[x][y];
				else
					S = null;

				x = i - 1;
				y = j - 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					SW = cellRefs[x][y];
				else
					SW = null;

				x = i - 1;
				y = j;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					W = cellRefs[x][y];
				else
					W = null;

				x = i - 1;
				y = j + 1;
				if (x >= 0 && x < scaledXDim && y >= 0 && y < scaledYDim)
					NW = cellRefs[x][y];
				else
					NW = null;

				cellRefs[i][j].setObserverFireCells(N, NE, E, SE, S, SW, W, NW);

				counter2++;
				if (counter++ / (scaledXDim * scaledYDim) >= 0.05)
				{
					System.out.format("%2.0f%%->", (double)counter2/(scaledXDim * scaledYDim)*100);
					counter = 0;
				}
				
				
			//	System.out.println(p++/(gisMap.xDim()*gisMap.yDim()));
			}
		System.out.println("100%.\nFire cells coupled");
		System.out.println("Fire spread system created ");
		
		
	}
	
	private int scaledToReal( int v, int scaleFactor )
	{
		return v/scaleFactor;
	}

	
	public int getFireCellInex(int x, int y)
	{
		if(x<0 || x>=this.getXDim()*this.scaleFactor || y<0 || y>=this.getYDim()*this.scaleFactor) return -1;
		return fireCellIndex[x][y];
	}
}
