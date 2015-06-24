package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class FirefluxTester
{
	public static void main(String[] args)
	{
		try
		{
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new FireSystemConfig();
			config.gisFileFolder = "GISData/";  // gis folder
			
			
			config.fuelFileName = "fuel_fireflux.txt"; // fuel
			config.slopeFileName = "slope_fireflux.txt"; // slope
			config.aspectFileName = "aspect_fireflux.txt"; // aspect
			
			config.initialWeatherFileName = "weather_fireflux.txt"; // initial weather
			config.initialIgnitionFile = "fireflux_initignition.txt"; // initial ignition
			config.laterIgnitionFile = "fireflux_delayedignition.txt"; // later ignition
			config.initialContainedCells = "InitialContainedCells_FF.txt"; // initial contained cells 
			config.showPresentationLayer = false;
			config.useNewHeatModelForInitialIgnitionCells = false;
			
			config.heatUpdateInterval = 100;
			FireSystem sys = new FireSystem(config);
			System.out.println("Construction time: " + (System.currentTimeMillis() - time));
			
			//result test
			time = System.currentTimeMillis();
			System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");
			
			sys.run(301);
			
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
