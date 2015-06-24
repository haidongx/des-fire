package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class TestNewHeat
{
	public static void main(String[] args)
	{
		try
		{
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new FireSystemConfig();
			config.showPresentationLayer = true;
			config.fuelFileName = "fuel_qd.txt";
			config.aspectFileName = "aspect_qd.txt";
			config.slopeFileName = "slope_qd.txt";
			
			config.initialContainedCells = "InitialContainedCells_empty.txt";
			config.initialIgnitionFile = "IgnitionPoints_qdstraight.txt";
			config.laterIgnitionFile = "IgnitionPoints_later_empty.txt";
			config.initialWeatherFileName = "weather_artificial_qd.txt";
			config.heatUpdateInterval = 10;
			
			double length = 90;
			config.presentationLayerWakeUpInterval = length/40;
			
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
