package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class TestTemperaturePresentation
{

	public static void main(String[] args)
	{
		try
		{
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new FireSystemConfig();
			config.showPresentationLayer = true;
			
			config.fuelFileName = "GIS_fuel_5.txt";
			config.aspectFileName = "GIS_aspect_5.txt";
			config.slopeFileName = "GIS_slope_5.txt";
			
			config.initialContainedCells = "InitialContainedCells_empty.txt";
			config.initialIgnitionFile = "IgnitionPoints_Feng.txt";
			config.laterIgnitionFile = "IgnitionPoints_later_empty.txt";
			config.initialWeatherFileName = "weather_artificial_unchangedWind_3_215.txt";
			config.heatUpdateInterval = 3600;
			
			
			
			double length = 3600; //6400;
			config.presentationLayerWakeUpInterval = 120;
			FireSystem sys = new FireSystem(config);
			System.out.println("Construction time: " + (System.currentTimeMillis() - time));
	
			//result test
			System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");
			sys.run(length);
			System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
			System.out.println("Simulation finished at simulation time " + sys.getSystemTime() + "s");
			System.out.println("Execution time: " + (System.currentTimeMillis()-time));
			
		}
		catch( Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}
