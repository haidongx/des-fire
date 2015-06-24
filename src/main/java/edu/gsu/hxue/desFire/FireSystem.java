package edu.gsu.hxue.desFire;

import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.exceptions.InvalidLogicException;
import edu.gsu.hxue.desFire.exceptions.NotSupportedFunctionException;
import edu.gsu.hxue.sensors.TemperatureSensorProfile;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class FireSystem extends DESSystem
{
	private static final long serialVersionUID = 5938947600738101048L;
	protected GISData gisMap;
	protected int windModelIndex;
	protected int[][] fireCellIndex;
	
	public FireSystem(FireSystemConfig config)
	{
		super(config);
	}

	@Override
	protected void creatModelsAndCoupling(Object con )
	{
		FireSystemConfig config = (FireSystemConfig)con;
		
		// Set GIS map
		try
		{
			String folder = config.gisFileFolder;
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
		
		// Create cell index container
		fireCellIndex = new int[gisMap.xDim()][gisMap.yDim()];

		// Create the igniter
		new Igniter(this, config.otherFileFolder + config.initialIgnitionFile, config.otherFileFolder + config.laterIgnitionFile, gisMap.xDim(), gisMap.yDim());
	
		System.out.println("Initial Ignition file: " + config.otherFileFolder + config.initialIgnitionFile);
		System.out.println("Later Ignition file: " + config.otherFileFolder + config.laterIgnitionFile);
		
		// Create the suppressor
		Suppressor suppressor = new Suppressor(this, config.otherFileFolder + config.initialContainedCells, gisMap.xDim(), gisMap.yDim());
		System.out.println("Contained cell file: " + config.otherFileFolder + config.initialContainedCells);
		
		// Create the presentation
		FirePresentation presentation = null;
		if(config.showPresentationLayer)
		{
			 presentation = new FirePresentation(this, gisMap, config.displayScale);
			 presentation.setWakeUpInterval(config.presentationLayerWakeUpInterval);
		}
		
		// Create the monitor
		SystemMonitor monitor = null;
		if(config.useSystemMonitor) monitor = new SystemMonitor(this);

		// Create heat writer
		HeatWriter heat = null;
		if(config.useHeatWriter)
		{
			heat = new HeatWriter(this, config.heatUpdateInterval, gisMap.cellSize(), gisMap.xDim(), gisMap.yDim());
			heat.setFolderPath(config.heatOutputFolder);
		}

		// Create the wind model
		// WindModel wind = new WindModel(this, foler + "weather_artificial_DEVSFIRE.txt");
		System.out.println("Initial weather file: " +  config.otherFileFolder + config.initialWeatherFileName);
		ComplexWindModel wind = new ComplexWindModel(this, config.otherFileFolder + config.initialWeatherFileName, gisMap.xDim(), gisMap.yDim());
		this.windModelIndex = wind.getModelIndex();
		// SimpleWindModel wind = new SimpleWindModel(this, foler + "weather_artificial_DEVSFIRE.txt");

		// Create fire cells and couple the monitor
		FireCell[][] cellRefs = new FireCell[this.gisMap.xDim()][this.gisMap.yDim()];
		
		System.out.print("Creating fire cells ... ");
		for (int i = 0; i < this.gisMap.xDim(); i++)
			for (int j = 0; j < this.gisMap.yDim(); j++)
			{
				// Create cell
				cellRefs[i][j] = new FireCell(this, i, j, // coordinates
						8, 90, // initial wind, note: a cell will ask the weather model to update the wind, so this initial values do nothing
						gisMap.slopeAt(i, j), gisMap.aspectAt(i, j), // slope
						StandardCustomizedFuelEnum.modelIndexToFuelType(gisMap.fuelModelIndexAt(i, j)), gisMap.cellSize(), gisMap.cellSize() // cell size
				);
				
				// set heat model
				cellRefs[i][j].setUseNewHeatModelForInitialIgnitonCells(config.useNewHeatModelForInitialIgnitionCells);
				
				// Record index
				fireCellIndex[i][j] = cellRefs[i][j].getModelIndex();
				// couple to a system monitor model
				if(config.useSystemMonitor) cellRefs[i][j].setMonitor(monitor);
				// couple to a wind model
				cellRefs[i][j].setWindModel(wind);
				// couple to a heat writer model
				if(config.useHeatWriter) cellRefs[i][j].setHeatWriter(heat);
				// coupled to a suppressor model
				suppressor.setCellIndex(i, j, cellRefs[i][j].getModelIndex());
				// coupled to a presentation model
				if(config.showPresentationLayer) presentation.setCellIndex(i, j, cellRefs[i][j].getModelIndex());
			}

		System.out.println("Fire cells created");

		// Couple cells
		System.out.print("Coupling fire cells ...");
		double counter = 0;
		//double p = 0;
		for (int i = 0; i < this.gisMap.xDim(); i++)
			for (int j = 0; j < this.gisMap.yDim(); j++)
			{
				FireCell N, NE, E, SE, S, SW, W, NW;

				int x = i;
				int y = j + 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					N = cellRefs[x][y];
				else
					N = null;

				x = i + 1;
				y = j + 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					NE = cellRefs[x][y];
				else
					NE = null;

				x = i + 1;
				y = j;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					E = cellRefs[x][y];
				else
					E = null;

				x = i + 1;
				y = j - 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					SE = cellRefs[x][y];
				else
					SE = null;

				x = i;
				y = j - 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					S = cellRefs[x][y];
				else
					S = null;

				x = i - 1;
				y = j - 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					SW = cellRefs[x][y];
				else
					SW = null;

				x = i - 1;
				y = j;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					W = cellRefs[x][y];
				else
					W = null;

				x = i - 1;
				y = j + 1;
				if (x >= 0 && x < this.gisMap.xDim() && y >= 0 && y < this.gisMap.yDim())
					NW = cellRefs[x][y];
				else
					NW = null;

				cellRefs[i][j].setObserverFireCells(N, NE, E, SE, S, SW, W, NW);

				if (counter++ / (gisMap.xDim() * gisMap.yDim()) >= 0.05)
				{
					System.out.print(".");
					counter = 0;
				}
				
			//	System.out.println(p++/(gisMap.xDim()*gisMap.yDim()));
			}
		System.out.println();
		System.out.println("Fire cells coupled");
		System.out.println("Fire spread system created ");
		
		
	}

	public void serializeTo(String serializedFile)
	{
		try
		{
			FileOutputStream fileOut = new FileOutputStream(serializedFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		}
		catch (IOException i)
		{
			i.printStackTrace();
		}
	}
	
	public static FireSystem deserializeFrom (String file)
	{
		System.out.println("Deserializing "+ file +"... ");
		try
		{
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			FireSystem fire = (FireSystem) in.readObject();
			in.close();
			fileIn.close();
			System.out.println(file + " deserialized!");
			return fire;
		}
		catch (IOException i)
		{
			i.printStackTrace();
			System.exit(1);
		}
		catch (ClassNotFoundException c)
		{
			System.out.println("class not found");
			c.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public void forceToUpdateWeather(String weatherFilePath)
	{
		//Read in weather schedules
		File weatherFile = new File(weatherFilePath);
		System.out.print("Reading weather data from " + weatherFile);
		try
		{
			ComplexWindModel wind = (ComplexWindModel) this.getModel(this.windModelIndex);
			
			// Clear wind schedule
			wind.clearSchedule();
			
			// Add new schedules
			Scanner s = new Scanner(weatherFile);
			// skip the first two lines
			s.nextLine();
			s.nextLine();
			int indicateCount = 0;
			int recordCount = 0;
			while (s.hasNextLine() && s.hasNextInt())
			{
				// update weather
				int x = s.nextInt() - 1;
				int y = s.nextInt() - 1;

				// skip LATITUDE LONGITUDE TEMPA 
				for (int i = 0; i < 3; i++)
					s.next();

				// background fuel temperature
				double backGroundFuelTemperature = s.nextDouble();
				
				FireCell c = this.getFireCell(x, y); 
				if(c!=null) c.setBackGroundFuelTemperature(backGroundFuelTemperature);
				// skip RELH
				s.nextDouble();
				
				// wind speed
				double speed = s.nextDouble();

				// wind direction
				double direction = s.nextDouble();

				// add schedule to wind model
				wind.addSchedule(new ComplexWindModel.WindSchedule(this.getSystemTime(), speed, direction, x, y));

				indicateCount++;
				recordCount++;
				if (indicateCount >= 5000)
				{
					System.out.print(".");
					indicateCount = 0;
				}
			}
			s.close();
			System.out.println("Number of cell weather read in: " + recordCount);
			
			// Invalid the current weather
			wind.invalidateWeather();
			
			// bring wind model to the top of event queue
			this.bringModelToTop(wind);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

	}

	public void forceToUpdateWeather(double speed, double direction)
	{
		ComplexWindModel wind = (ComplexWindModel) this.getModel(this.windModelIndex);
		wind.clearSchedule();

		int indicateCount = 0;
		//int recordCount = 0;
		for (int x = 0; x < this.gisMap.xDim(); x++)
			for (int y = 0; y < this.gisMap.yDim(); y++)
			{
				// add schedule to wind model
				wind.addSchedule(new ComplexWindModel.WindSchedule(this.getSystemTime(), speed, direction, x, y));
				indicateCount++;
				//recordCount++;
				if (indicateCount >= 5000)
				{
					//System.out.print(".");
					indicateCount = 0;
				}
			}
		//System.out.println("Weather data are prepared for " + recordCount + " cells");

		// Invalid the current weather
		wind.invalidateWeather();

		// Bring wind model to the top of event queue
		this.bringModelToTop(wind);
	}

	/**
	 * Calculate sensor readings for the given sensor set
	 * @param sensorProfiles
	 * @return
	 */
	public double[] generateSensorReadings(TemperatureSensorProfile[] sensorProfiles)
	{
		double[] readings = new double[sensorProfiles.length];
		double ambientTemperature = GlobalConstants.AMBIENT_TEMPERATURE; // the ambient temperature 
		
		/*
		 * Note: this temperature estimation method is not good. A heat flux based method should be used to replace this one. 
		 * 
		 * --Haidong 1/14/2014
		 */
		
		
		// Set to 0 Celsius degree
		for(int i=0; i<readings.length; i++) 
			readings[i]=0; //ambientTemperature;
		
		// find fire front
		HashMap<FireCell, Double> frontCells = new HashMap<FireCell, Double>();
		for (int x = 0; x < this.gisMap.xDim(); x++)
			for (int y = 0; y < this.gisMap.yDim(); y++)
			{
				FireCell c = (FireCell) this.getModel(fireCellIndex[x][y]);
				if(c.isBurning() && isOnFireFront(x, y))  // only let the burning and front cell affect temperature
				{
					frontCells.put(c, c.getCelsiusTemperature());
					
					//System.out.println(c.getCelsiusTemperature());
				}
				
				
			}
		
		// Record the highest temperature
		for( FireCell c : frontCells.keySet())
				for(int i=0; i<sensorProfiles.length; i++)
				{
					TemperatureSensorProfile p = sensorProfiles[i];
					
					double dis = p.location.distance(c.getX(), c.getY()) * gisMap.cellSize(); // distance
					if(dis>p.radius) continue; // cells out of the monitoring range do not affect the sensor temperature
					
					double sigma = 50; 
					double sensorTemperature = frontCells.get(c) * Math.exp(-1.0 * dis * dis / (sigma*sigma)) + ambientTemperature; //temperature on the sensor
					//
					if(sensorTemperature > readings[i] ) readings[i] = sensorTemperature; // update sensor temperature when needed
				}
				
		return readings;
	}
	
	public int getXDim() {return this.gisMap.xDim(); }
	public int getYDim() {return this.gisMap.yDim(); }
	
	public GISData getGISMap()
	{
		return gisMap;
	}
	
	public FireCell getFireCell( int x, int y )
	{
		if(x<0 || x>=this.getXDim() || y<0 || y>=this.getYDim()) return null;
		return (FireCell)this.getModel(this.fireCellIndex[x][y]);
	}

	public int getFireCellInex(int x, int y)
	{
		if(x<0 || x>=this.getXDim() || y<0 || y>=this.getYDim()) return -1;
		return fireCellIndex[x][y];
	}

	public int mismatchedCellNumber ( FireSystem anotherSys ) throws NotSupportedFunctionException
	{
		if( this.getXDim() != anotherSys.getXDim() || this.getYDim() != anotherSys.getYDim())
		{
			System.err.println("Two fire system have different dimensions. mismatchedCellNumber() is meaningless. ");
			System.exit(1);
		}
		
		int misMatched = 0;
		for(int x=0; x<this.getXDim(); x++)
			for(int y=0; y<this.getYDim(); y++)
			{
				FireCell c1 = this.getFireCell(x, y);
				FireCell c2 = anotherSys.getFireCell(x, y);
				
				if(c1.isUnburned()&&c2.isUnburned())
					continue;
				
				// Note "burning" cells could be burned out too, "burning" only means "ignited"
				// And here only test if they are both ignited
				if(c1.isBurning() && c2.isBurning() )
					continue;
				
				misMatched++;
			}
		
		return misMatched;
	}

	public double getFireFrontLength()
	{
		int numberOfFrontCells = 0;
		for(int x=0; x<this.getXDim(); x++)
			for(int y=0; y<this.getYDim(); y++)
			{
				if(this.isOnFireFront(x, y)) numberOfFrontCells++;
			}
		
		return numberOfFrontCells * gisMap.cellSize() ;
	}
	
	public double getIgnitedArea() throws NotSupportedFunctionException
	{
		int numberOfIgnitedCells = 0;
		for(int x=0; x<this.getXDim(); x++)
			for(int y=0; y<this.getYDim(); y++)
			{
				FireCell c = this.getFireCell(x, y);
				if(c.isBurning() || c.isBurnedOut()) numberOfIgnitedCells++;
			}
		
		return numberOfIgnitedCells * gisMap.cellSize()* gisMap.cellSize();
	}
	
	public boolean isOnFireFront(int x, int y)
	{
		FireSystem fire = this;
		// If all neighbors are burning, it is not on the front
		boolean allNeighborsBurning=true;
		for( int i=-1; i<=1; i++)
			for( int j=-1; j<=1; j++)
			{
				if(i==0 && j==0) continue;
				
				if(x+i<0 || x+i >= fire.getXDim() || y+j<0 || y+j>=fire.getYDim()) continue;
				
				if(!(fire.getFireCell(x+i, y+j).isBurning()))
				{
					allNeighborsBurning = false;
					break;
				}
			}
		
		if(allNeighborsBurning) return false;
		
		// Given all burning neighbors, if it is burning, it is on the front
		FireCell cell = (FireCell)fire.getFireCell(x, y);
		if(cell.isBurning()) return true;
		return false;
	}

	public void forceToIgnite(int x, int y) throws InvalidLogicException
	{
		if(x<0 || x>=getXDim() || y<0 || y>=getYDim() ) return;
		FireCell c = this.getFireCell(x, y);
		c.forceToIgnite();
	}

	public void forceToBurnOut(int x, int y)
	{
		if(x<0 || x>=getXDim() || y<0 || y>=getYDim() ) return;
		FireCell c = this.getFireCell(x, y);
		c.forceToBurnOut();
	}

	public void forceToUnburn(int x, int y)
	{
		if(x<0 || x>=getXDim() || y<0 || y>=getYDim() ) return;
		FireCell c = this.getFireCell(x, y);
		c.forceToUnburn();
		
	}
}
