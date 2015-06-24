package edu.gsu.hxue.physicalModels.mandel2011Heat;

import java.io.Serializable;

public class Mandel2011HeatModelImp20120521 implements Mandel2011HeatModel, Serializable
{
	private static final long serialVersionUID = 7123306885116701301L;
	private final double WATER_CONSENDATION_HEAT = 2.5E+6;  // J/kg
	private final double ESTIMATED_MASS_RATIO_OF_WATER_OUTPUT = 0.56;   
	

	@Override
	public double fuelFraction(double weightingFactor, double ignitionTime, double currentTime)
	{
		if(currentTime<=ignitionTime) return 1;
		return Math.exp(-((currentTime-ignitionTime)/(weightingFactor/0.85141)));
	}

	@Override
	public double fuelFractionChange(double weightingFactor, double ignitionTime, double startTime, double endTime)
	{
		return  fuelFraction(weightingFactor, ignitionTime, startTime) - fuelFraction(weightingFactor, ignitionTime, endTime); 
	}

	@Override
	public double sensibleHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading, double fuelHeatContent)
	{
		double heat =  fuelFractionChange(weightingFactor, ignitionTime, startTime, endTime) * fuelHeatContent * fuelLoading/(1+fuelMoisture*0.01);
	/*	System.out.println("fraction: " +fuelFractionChange(weightingFactor, ignitionTime, startTime, endTime)  );
		System.out.println("Heat content: " + fuelHeatContent);
		System.out.println("loading: " +fuelLoading );
		System.out.println("moisure: " + fuelMoisure);
		System.exit(1);*/
		return heat / (endTime - startTime);
	}

	@Override
	public double latentHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading )
	{
		double heat =  fuelFractionChange(weightingFactor, ignitionTime, startTime, endTime) * WATER_CONSENDATION_HEAT * fuelLoading * (ESTIMATED_MASS_RATIO_OF_WATER_OUTPUT+fuelMoisture*0.01)/(1+fuelMoisture*0.01);
		return heat / (endTime - startTime);
	}

	

	@Override
	public double totalSensibleHeat(double weightingFactor, double fuelMoisure, double fuelLoading, double fuelHeatContent)
	{
		return fuelHeatContent * fuelLoading/(1+fuelMoisure*0.01);
	}
	
	@Override
	public double totalLatentHeat(double weightingFactor, double fuelMoisture, double fuelLoading)
	{
		return  WATER_CONSENDATION_HEAT * fuelLoading * (ESTIMATED_MASS_RATIO_OF_WATER_OUTPUT+fuelMoisture*0.01)/(1+fuelMoisture*0.01);
	}

}
