package edu.gsu.hxue.desFire.couplingWithARPS;

import edu.gsu.hxue.desFire.FireSystemConfig;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Additional configuration for DESFire/ARPS coupled simulation
 * @author Haydon
 *
 */
public class CouplingConfig extends FireSystemConfig implements Serializable 
{
	private static final long serialVersionUID = -6188784075703186524L;
	
	//public FireSystemConfig fireConfig = new FireSystemConfig();	
	public double weatherUpdateInterval = 100;
	public int numberOfSteps = 36;
	
	public boolean nested = true; // using nested fire grid
	public int scaleFactor = 2; // a original fire grid will be divided into nxn cells
	
	public int waitingTimeout = 15; // seconds, the longest time it waits for ARPS
	
	public void store()
	{
		Properties p = new Properties();
		p.setProperty("GISFileFolder", gisFileFolder);
		p.setProperty("slopeFileName", slopeFileName);
		p.setProperty("aspectFileName", aspectFileName);
		p.setProperty("fuelFileName", fuelFileName);
		p.setProperty("initialWeatherFileName", initialWeatherFileName);
		p.setProperty("initialContainedCells", initialContainedCells);
		
		p.setProperty("weatherUpdateInterval", Double.toString(weatherUpdateInterval));
		p.setProperty("numberOfSteps", Integer.toString(numberOfSteps));
		
		p.setProperty("heatUpdateInterval", Double.toString(heatUpdateInterval));
		
		p.setProperty("ignitionFile", initialIgnitionFile);
		p.setProperty("laterIgnitionFile", laterIgnitionFile);
		
		p.setProperty("showPresentationLayer", Boolean.toString(showPresentationLayer));
		
		p.setProperty("nested", Boolean.toString(nested));
		p.setProperty("scaleFactor", Integer.toString(scaleFactor));
		
		p.setProperty("useNewHeatModelForInitialIgnitionCells", Boolean.toString(useNewHeatModelForInitialIgnitionCells));
		
		p.setProperty("waitingTimeout", Integer.toString(waitingTimeout));
		
		
		try
		{
			p.store(new FileOutputStream("CouplingConfig.txt"), "Configuration of DES-FIRE/ARPS coupled simulation");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public CouplingConfig() 
	{
		showPresentationLayer = false;
	}
	
	public CouplingConfig( String configFile )
	{
		Properties p = new Properties();
		try
		{
			p.load(new FileInputStream(configFile));
			
			gisFileFolder = p.getProperty("GISFileFolder");
			slopeFileName = p.getProperty("slopeFileName");
			aspectFileName = p.getProperty("aspectFileName");
			fuelFileName = p.getProperty("fuelFileName");
			initialContainedCells = p.getProperty("initialContainedCells");
			initialWeatherFileName = p.getProperty("initialWeatherFileName");
			heatUpdateInterval = Double.parseDouble(p.getProperty("heatUpdateInterval"));
			initialIgnitionFile = p.getProperty("ignitionFile");
			laterIgnitionFile = p.getProperty("laterIgnitionFile");
			
			showPresentationLayer = Boolean.parseBoolean(p.getProperty("showPresentationLayer"));
			
			weatherUpdateInterval = Double.parseDouble(p.getProperty("weatherUpdateInterval"));
			numberOfSteps = Integer.parseInt(p.getProperty("numberOfSteps"));
			nested = Boolean.parseBoolean(p.getProperty("nested"));
			scaleFactor = Integer.parseInt(p.getProperty("scaleFactor"));
			
			waitingTimeout = Integer.parseInt(p.getProperty("waitingTimeout"));
			
			this.useNewHeatModelForInitialIgnitionCells = Boolean.parseBoolean(p.getProperty("useNewHeatModelForInitialIgnitionCells"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
