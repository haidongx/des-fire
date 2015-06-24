package edu.gsu.hxue.physicalModels.mandel2011Heat;

import java.io.Serializable;

public interface Mandel2011HeatModel extends Serializable
{
	/**
	 * 
	 * @param weightingFactor, the weighting factor of a fuel(no unit)
	 * @param ignitionTime, the ignition time (second)
	 * @param currentTime, the current time (second)
	 * @return fuel fraction left (no unit)
	 */
	double fuelFraction(double weightingFactor, double ignitionTime, double currentTime);
	
	/**
	 * 
	 * @param weightingFactor, the weighting factor (no unit)
	 * @param ignitionTime, the ignition time (second)
	 * @param startTime, the start time of a period (second)
	 * @param endTime, the end time of a period (second)
	 * @return the fuel fraction loss of the period from startTime to endTime
	 */
	double fuelFractionChange(double weightingFactor, double ignitionTime, double startTime, double endTime);
	
	/**
	 * 
	 * @param weightingFactor, the weighting factor (no unit)
	 * @param ignitionTime, the ignition time (second)
	 * @param startTime, the start time of a period (second)
	 * @param endTime, the end time of a period (second)
	 * @param fuelMoisture, fuel particle moisture content (no unit) 
	 * @param fuelLoading, total fuel loading (kg/(m*m))
	 * @param fuelHeatContent, fuel heat contents of dry fuel (J/kg)
	 * @return sensible heat flux density (W/(m*m))
	 */
	double sensibleHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading, double fuelHeatContent);
	
	
	/**
	 * 
	 * @param weightingFactor, the weighting factor (no unit)
	 * @param ignitionTime, the ignition time (second)
	 * @param startTime, the start time of a period (second)
	 * @param endTime, the end time of a period (second)
	 * @param fuelMoisture, fuel particle moisture content (no unit) 
	 * @param fuelLoading, total fuel loading (kg/(m*m))
	 * @return latent heat flux density (W/(m*m))
	 */
	double latentHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading);
	
	double totalLatentHeat(double weightingFactor, double fuelMoisture, double fuelLoading);
	
	double totalSensibleHeat(double weightingFactor, double fuelMoisure, double fuelLoading, double fuelHeatContent);
}
