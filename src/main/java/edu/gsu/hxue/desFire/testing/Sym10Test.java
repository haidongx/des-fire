package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class Sym10Test
{

	
	/*
	 # GIS data
GISFileFolder=GISData/
fuelFileName=fuel_sym_10.txt
slopeFileName=slope_sym_10.txt
aspectFileName=aspect_sym_10.txt

# Initial wind schedule
initialWeatherFileName=weather_artificial_DEVSFIRE.txt

# Ignition
ignitionFile= IgnitionPoints_sym_10m.txt
laterIgnitionFile=IgnitionPoints_later_empty.txt

# Initial contained cells
initialContainedCells=InitialContainedCells_empty.txt

# Coupling related data (numberOfSteps is the number of weather update steps)
weatherUpdateInterval=1800
heatUpdateInterval=60
numberOfSteps=1

# When nested is true, a grid will be modeled by nxn cells, n is the scaleFactor
nested=false
scaleFactor=1


# If use new heat model for initial ignition cells
useNewHeatModelForInitialIgnitionCells = false
waitingTimeout = 600
	 */
	public static void main(String[] args)
	{
		try
		{
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new FireSystemConfig();
			config.showPresentationLayer = true;
			config.fuelFileName = "fuel_sym_10.txt";
			config.aspectFileName = "aspect_sym_10.txt";
			config.slopeFileName = "slope_sym_10.txt";
			config.initialContainedCells = "InitialContainedCells_empty.txt";
			config.initialIgnitionFile = "IgnitionPoints_sym_10m.txt";
			config.laterIgnitionFile = "IgnitionPoints_later_empty.txt";
			config.initialWeatherFileName = "weather_artificial_unchangedWind_12_270.txt";
			config.heatUpdateInterval = 1800;
			
			double length = 1800;
			config.presentationLayerWakeUpInterval = 200;
			
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
