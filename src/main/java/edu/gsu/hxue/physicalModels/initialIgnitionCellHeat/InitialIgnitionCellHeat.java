package edu.gsu.hxue.physicalModels.initialIgnitionCellHeat;

import edu.gsu.hxue.physicalModels.mandel2011Heat.Mandel2011HeatModelImp20120521;

import java.io.Serializable;

public class InitialIgnitionCellHeat implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2843324716915509650L;
	Mandel2011HeatModelImp20120521 m = new Mandel2011HeatModelImp20120521();
	
	public double oneWayfuelFraction(double weightingFactor, double ignitionTime, double currentTime, double spreadRate, double cellSize)
	{
		double deltaT = currentTime - ignitionTime; 
		
		if(deltaT < 0 ) return 1;
		
		double eFoldingTime = weightingFactor / 0.8514;
		double tempA = 2*spreadRate*eFoldingTime/cellSize;
		if(spreadRate*deltaT < cellSize/2 )
		{
			return tempA * (1-(deltaT/eFoldingTime)-(Math.exp(-deltaT/eFoldingTime))) + 1;
		}
		else
		{
			double tempB = -(deltaT-(cellSize/(2*spreadRate)))/eFoldingTime;
			return tempA * (Math.exp(tempB)-Math.exp(-deltaT/eFoldingTime));
		}
	}
	
	public double fuelFraction(double weightingFactor, double ignitionTime, double currentTime, double maxSpreadRate, double minSpreadRate, double cellSize)
	{
		/*System.out.println(weightingFactor);
		System.out.println(ignitionTime);
		System.out.println(currentTime);
		System.out.println(maxSpreadRate);
		System.out.println(minSpreadRate);
		System.out.println(cellSize);*/
		return (oneWayfuelFraction(weightingFactor, ignitionTime, currentTime, maxSpreadRate, cellSize) + oneWayfuelFraction(weightingFactor, ignitionTime, currentTime, minSpreadRate, cellSize))/2;
	}
	
	private double fuelFractionChange(double weightingFactor, double ignitionTime, double startTime, double endTime, double maxSpreadRate, double minSpreadRate, double cellSize)
	{
		return fuelFraction(weightingFactor, ignitionTime, startTime, maxSpreadRate, minSpreadRate, cellSize) - fuelFraction(weightingFactor, ignitionTime, endTime, maxSpreadRate, minSpreadRate, cellSize); 
	}
		
	public double sensibleHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisure, double fuelLoading, double fuelHeatContent, double maxSpreadRate, double minSpreadRate, double cellSize)
	{
		
		double heat =  fuelFractionChange(weightingFactor, ignitionTime, startTime, endTime, maxSpreadRate, minSpreadRate, cellSize) 
				* m.totalSensibleHeat(weightingFactor, fuelMoisure, fuelLoading, fuelHeatContent);
	
		return heat / (endTime - startTime);
	}

	public double latentHeatFluxDesnsity(double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading, double maxSpreadRate, double minSpreadRate, double cellSize )
	{
		double heat =  fuelFractionChange(weightingFactor, ignitionTime, startTime, endTime, maxSpreadRate, minSpreadRate, cellSize) 
				* m.totalLatentHeat(weightingFactor, fuelMoisture, fuelLoading);
		return heat / (endTime - startTime);
	}
	
}
