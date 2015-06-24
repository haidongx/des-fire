package edu.gsu.hxue.desFire.couplingWithARPS;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desHigherResolutionDESFire.ScalableFireSystem;

import java.io.*;

public class CouplingShell implements Serializable
{
	private static final long serialVersionUID = -4348401485954332948L;
	private CouplingConfig config;
	private FireSystem sys;
	
	public CouplingShell( CouplingConfig config, FireSystem sys)
	{
		this.config = config;
		this.sys = sys;
	}
	
	public CouplingShell(CouplingConfig config)
	{
		this.config = config;
		
		// show visiual
		this.config.showPresentationLayer = true;
		this.config.presentationLayerWakeUpInterval = 100;
		
		if(!config.nested)
			this.sys = new FireSystem(config);
		else
			this.sys = new ScalableFireSystem(config);
	}

	public void coupledSimulate() throws Exception
	{
		for( int step=0; step<config.numberOfSteps; step++)
		{
			// wait until weather is ready
			String fileflag = "weather_" + step + "_ready.txt";
			File f = new File("weatherData/" + fileflag);
			System.out.print("Waiting for the file: " + f.getAbsolutePath() + " at " + sys.getSystemTime());
			
			double beginTime = System.currentTimeMillis();
			while (!f.exists())
			{
				if((System.currentTimeMillis() - beginTime) > 1000*config.waitingTimeout)
				{
					//timeout
					throw new TimeoutException();
				}
				
				System.out.print(".");
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println();
			System.out.println("Found " + fileflag);
			
			String weatherFile = "weatherData/weather_" + step + ".txt"; // for local test only !!!!!!!!!!!!!!!!!!
			//String weatherFile = "weatherData/weather.txt";

			// update weather
		 	sys.forceToUpdateWeather(weatherFile);

			//run simulation for a step
			System.out.println("Simulation started at simulation time " + sys.getSystemTime());
			sys.run(config.weatherUpdateInterval);
			System.out.println("Simulation ended at simulation time " + sys.getSystemTime());
		}
		
		//Serialize
		System.out.print("Save simulation image ... ");
		serializeTo("SimulationImage.ser");
		System.out.print("Simulation image saved");
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
	
	/**
	 * @param args, a image file, or a config file
	 */
	public static void main(String[] args)
	{
		
		try
		{
			String filename = args[0];
			int dot = filename.lastIndexOf('.');
			String extension =  filename.substring(dot + 1);
			
			if(extension.equals("txt"))
			{
				System.out.println("Construct simulation from configuration file");
				CouplingConfig config = new CouplingConfig(filename);
				CouplingShell shell = new CouplingShell( config );
				shell.coupledSimulate();
			}
			else if(extension.equals("ser"))
			{
				System.out.println("Restart from a simulation image");
				CouplingShell shell = deserializeFrom("SimulationImage.ser");
				shell.coupledSimulate();
			}
			else
			{
				System.out.println("Wrong simulation parameter");
				System.out.println("Usage: java -jar DESFire.jar [configuration file | simulation image file]");
			}
		}
		catch (TimeoutException e)
		{
			System.err.println();
			System.err.println("Timeout!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static CouplingShell deserializeFrom (String file)
	{
		System.out.print("Deserializing ... ");
		try
		{
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			CouplingShell shell = (CouplingShell) in.readObject();
			in.close();
			fileIn.close();
			System.out.print("Deserialized");
			return shell;
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

}
