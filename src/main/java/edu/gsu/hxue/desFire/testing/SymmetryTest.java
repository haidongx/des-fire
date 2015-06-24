package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class SymmetryTest
{

	public static void main(String[] args)
	{
		try
		{
			int[] winds = {3, 12};
			int[] resolutions = {10, 30, 90};
			
			for( int w: winds)
				for( int res:resolutions)
				{
					//construct
					double time = System.currentTimeMillis();
					FireSystemConfig config = new FireSystemConfig();
					
					config.gisFileFolder = "C:/Users/Haydon/Desktop/temp/DESFire_symtests_20140113/GISData/";
					config.showPresentationLayer = false;
					config.fuelFileName = "fuel_sym_"+ Integer.toString(res) +".txt";
					config.aspectFileName = "aspect_sym_"+ Integer.toString(res) +".txt";
					config.slopeFileName = "slope_sym_"+ Integer.toString(res) +".txt";
					
					config.otherFileFolder = "C:/Users/Haydon/Desktop/temp/DESFire_symtests_20140113/OtherData/";
					config.initialContainedCells = "InitialContainedCells_sym_"+ Integer.toString(res) +"m.txt";
					config.initialIgnitionFile = "IgnitionPoints_sym_"+ Integer.toString(res) +"m.txt";
					config.laterIgnitionFile = "IgnitionPoints_later_empty.txt";
					config.initialWeatherFileName = "weather_artificial_unchangedWind_"+ Integer.toString(w) +"_270.txt";
					config.heatUpdateInterval = 60;
					
					config.heatOutputFolder = "C:/Users/Haydon/Desktop/temp/DESFire_symtests_20140113/heat/res"+ Integer.toString(res) +"_wind"+ Integer.toString(w) +"/";
					
					double length = 1800;
					config.presentationLayerWakeUpInterval = length/20;
					
					FireSystem sys = new FireSystem(config);
					System.out.println("Construction time: " + (System.currentTimeMillis() - time));
			
					//result test
					System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");
					
				
					sys.run(length);
					
					
					System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
					System.out.println("Simulation finished at simulation time " + sys.getSystemTime() + "s");
					System.out.println("Execution time: " + (System.currentTimeMillis()-time));
				}
			
			
			
		}
		catch( Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}
}
