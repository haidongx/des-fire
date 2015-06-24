package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

public class TestClone
{

	public static void main(String[] args)
	{
		try
		{
			//construct
			double time = System.currentTimeMillis();
			FireSystemConfig config = new FireSystemConfig();
			config.showPresentationLayer = false;
			config.useHeatWriter = false;
			config.useSystemMonitor = false;
			FireSystem sys = new FireSystem(config);
			sys.setName("Org");
			
			int N = 10;
			FireSystem[] clones = new FireSystem[N];
			for( int i=0; i<N; i++)	
			{
				clones[i] = (FireSystem) sys.clone();
				clones[i].setName("Clone" + i);
			}
			
			System.out.println("Construction time: " + (System.currentTimeMillis() - time));
			System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
			
			
			//result test
			time = System.currentTimeMillis();
			System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");
			int length = 1200*9;
			
			sys.run(length);
			for( FireSystem f: clones)
				f.run(length);
					
			System.gc();
			System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
			System.out.println("Simulation finished at simulation time " + sys.getSystemTime() + "s");
			System.out.println("Execution time: " + (System.currentTimeMillis()-time)/1000 + "s");
			
		}
		catch( Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

}
