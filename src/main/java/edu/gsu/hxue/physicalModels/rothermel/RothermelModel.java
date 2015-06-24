package edu.gsu.hxue.physicalModels.rothermel;

import edu.gsu.hxue.gis.fuel.ComplexFuel;

import java.io.Serializable;

/**
 * This Rothermel fire behave model interface. 
 * Given fuel data, wind velocity at mid-flame height, wind direction at mid-flame height, slope, slope direction, 
 * it calculates the spread rate and the reaction intensity. 
 * 
 * 
 * (Not finished.)
 *  
 * @author Haidong Xue
 */
public interface RothermelModel extends Serializable
{
	/**
	 * Set inputs for Rothermel model.
	 * @param fuel
	 * @param midFlameWindSpeed
	 * @param midFlameWindDirection
	 * @param slope
	 * @param upSlopeDirection
	 */
	public void setInputs(ComplexFuel fuel, double midFlameWindSpeed, double midFlameWindDirection, double slope, double upSlopeDirection);
	
	/**
	 * 
	 * @return Fire spread rate, [m/s]
	 */
	public double getMaxSpreadDirection();
	
	/**
	 * 
	 * @return max spread rate
	 */
	public double getSpreadRateOnMaxDirection();
	
	/**
	 * 
	 * @return Reaction intensity, [kj/(m2*s)]
	 */
	public double getReactionIntensity();
	
	/**
	 * 
	 * @return wind speed on the max direction
	 */
	public double getEfficientWindSpeedOnMaxDirection();

	public double getFirelineIntensity();
	
	
	public double getEffectiveHeatingNumber();  
	
	public double getMineralDampingCoefficient();
	
	public double getPackingRatio();

	public double getBulkDensity();

	public double getFlameLength();
	

}
