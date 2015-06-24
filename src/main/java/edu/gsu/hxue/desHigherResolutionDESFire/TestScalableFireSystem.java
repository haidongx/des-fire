package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;
import edu.gsu.hxue.desFire.couplingWithARPS.CouplingConfig;

public class TestScalableFireSystem
{
	public static void main(String args[])
	{
		try
		{	
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new CouplingConfig();
			FireSystem sys = new ScalableFireSystem(config);
			System.out.println("Construction time: " + (System.currentTimeMillis() - time));
			System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
			
			
			//result test
			time = System.currentTimeMillis();
			System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");
			
			//sys.run(9600+1);
			sys.run(10800*1 +1);
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
