package edu.gsu.hxue.gis.fuel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * A implementation of the model in paper (Joe H Scott, 2005).
 * @author Haydon
 *
 */
public class StandardCustomizedFuel extends ComplexFuel
{
	private static final long serialVersionUID = 9188641285566513679L;
	private static final int NUMBER_OF_CATEGORY = 2;
	private static final int DEAD = 0; //dead fuels
	private static final int LIVE = 1; //live fuels
	
	private static final int NUMBER_OF_CLASS_SIZES_IN_CATEGORY_DEAD = 3;
	private static final int DEAD_1 = 0;  //dead for 1 hour
	private static final int DEAD_10 = 1;  //dead for 10 hours
	private static final int DEAD_100 = 2;  //dead for 100 hours
	
	private static final int NUMBER_OF_CLASS_SIZES_IN_CATEGORY_LIVE = 2;
	private static final int LIVE_HERB = 0;  //live herbaceous
	private static final int LIVE_WOODY = 1;  //live woody
	
	
	/**
	 * This methods load data from a .prp file, and put them into 5 basic fuels to construct a StandardCustomizedFuel instance.
	 * 
	 * @param prpFilePath
	 * @return a instance of StandardCustomizedFuel
	 */
	public StandardCustomizedFuel( String prpFilePath )
	{
		Properties prop = new Properties();
		FileInputStream f;
		try
		{
			f = new FileInputStream(prpFilePath);
			prop.load(f);
			
			//Dead fuels
			BasicFuel[] deadFuels = new BasicFuel[NUMBER_OF_CLASS_SIZES_IN_CATEGORY_DEAD];
			deadFuels[DEAD_1] = new BasicFuel (
					meanValue("w0_d1", prop), meanValue("sv_d1", prop), meanValue("depth", prop), 
					meanValue("rho", prop), meanValue("heat", prop), meanValue("s_t", prop), 
					meanValue("s_e", prop), meanValue("m_d1", prop), meanValue("mx", prop));
			deadFuels[DEAD_10] = new BasicFuel (
					meanValue("w0_d2", prop), meanValue("sv_d2", prop), meanValue("depth", prop), 
					meanValue("rho", prop), meanValue("heat", prop), meanValue("s_t", prop), 
					meanValue("s_e", prop), meanValue("m_d2", prop), meanValue("mx", prop));
			deadFuels[DEAD_100] = new BasicFuel (
					meanValue("w0_d3", prop), meanValue("sv_d3", prop), meanValue("depth", prop), 
					meanValue("rho", prop), meanValue("heat", prop), meanValue("s_t", prop), 
					meanValue("s_e", prop), meanValue("m_d3", prop), meanValue("mx", prop));
			FuelCategory deadFuelCategory = new FuelCategory ( "Dead Fuels", deadFuels);
			
			
			//Live fuels
			BasicFuel[] liveFuels = new BasicFuel[NUMBER_OF_CLASS_SIZES_IN_CATEGORY_LIVE];
			liveFuels[LIVE_HERB] = new BasicFuel (
					meanValue("w0_lh", prop), meanValue("sv_lh", prop), meanValue("depth", prop), 
					meanValue("rho", prop), meanValue("heat", prop), meanValue("s_t", prop), 
					meanValue("s_e", prop), meanValue("m_lh", prop), meanValue("mx", prop));
			liveFuels[LIVE_WOODY] = new BasicFuel (
					meanValue("w0_lw", prop), meanValue("sv_lw", prop), meanValue("depth", prop), 
					meanValue("rho", prop), meanValue("heat", prop), meanValue("s_t", prop), 
					meanValue("s_e", prop), meanValue("m_lw", prop), meanValue("mx", prop));
			FuelCategory liveFuelCategory = new FuelCategory ( "Live Fuels", liveFuels);
			
			//Construct a standard complex fuel, which has multiple fuel categories
			FuelCategory[] fuelCategories = new FuelCategory[NUMBER_OF_CATEGORY];
			fuelCategories[DEAD] = deadFuelCategory;
			fuelCategories[LIVE] = liveFuelCategory;
			
			this.setFuelCategories(fuelCategories);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException("When reading fuel prp file, file " + prpFilePath + " not found!", e);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot read file: " + prpFilePath, e);
		}
	}
	
	private static double meanValue( String key, Properties p )
	{
		String meanValueString = (new StringTokenizer(p.getProperty(key))).nextToken();
		return Double.parseDouble(meanValueString);
	}
	

}
