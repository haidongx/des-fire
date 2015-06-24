package edu.gsu.hxue.physicalModels.newHeatModel;

import edu.gsu.hxue.desFire.FireCell.IgnitionDirectionTypeEnum;
import edu.gsu.hxue.desFire.FireCell.IgnitionTypeEnum;
import edu.gsu.hxue.physicalModels.mandel2011Heat.Mandel2011HeatModelImp20120521;

import java.io.Serializable;

public class NewHeatModel implements Serializable 
{
	private static final long serialVersionUID = -9195935170679499142L;
	
	Mandel2011HeatModelImp20120521 m = new Mandel2011HeatModelImp20120521();

	
	public double straightOneWayFuelFraction(double weightingFactor, double ignitionTime, double currentTime, double spreadRate, double cellSize)
	{
		double deltaT = currentTime - ignitionTime; 
		
		if(deltaT <= 0 ) return 1;
		
		
		double eFoldingTime = weightingFactor / 0.8514;
		double tempA = spreadRate*eFoldingTime/cellSize;
		if(spreadRate*deltaT <= cellSize )
		{
			return tempA * (1-(deltaT/eFoldingTime)-(Math.exp(-deltaT/eFoldingTime))) + 1;
		}
		else
		{
			double tempB = -(deltaT-(cellSize/spreadRate))/eFoldingTime;
			return tempA * (Math.exp(tempB)-Math.exp(-deltaT/eFoldingTime));
		}
	}
	
	public double diagonalOneWayFuelFraction(double weightingFactor, double ignitionTime, double currentTime, double spreadRate, double cellSize)
	{
		double deltaT = currentTime - ignitionTime; 
		
		if(deltaT <= 0 ) return 1;
		
		final double C = Math.sqrt(2);
		double eFoldingTime = weightingFactor / 0.8514;
		if(spreadRate*deltaT <= C/2.0*cellSize )
		{
			double tempA = (spreadRate * deltaT /cellSize) * (spreadRate * deltaT /cellSize);
			double tempB = (spreadRate*eFoldingTime/cellSize)*(spreadRate*eFoldingTime/cellSize);
			double tempC = ((deltaT/eFoldingTime)+Math.exp(-(deltaT/eFoldingTime))-1);
			return 1 - tempA + 2*tempB*tempC;
		}
		else
		{
			if(spreadRate*deltaT <= C*cellSize )
			{
				double tempA = (spreadRate * deltaT /cellSize - C)*(spreadRate * deltaT /cellSize - C);
				double tempB = (spreadRate*eFoldingTime/cellSize)*(spreadRate*eFoldingTime/cellSize);
				double tempC1 = (-deltaT + C*cellSize/spreadRate)/eFoldingTime;
				double tempC2 = Math.exp(-deltaT/eFoldingTime);
				double tempC3 = 1 - 2.0*Math.exp(C/2.0*cellSize/(spreadRate*eFoldingTime));
				double tempC = 1 + tempC1 + tempC2*tempC3;
				return tempA + 2*tempB*tempC;
			}
			else
			{
				double tempB = (spreadRate*eFoldingTime/cellSize)*(spreadRate*eFoldingTime/cellSize);
				return 2*tempB * Math.exp(-deltaT/eFoldingTime) * (1 + Math.exp(C*cellSize/(spreadRate*eFoldingTime)) -2*Math.exp(C/2.0*(cellSize) / (spreadRate*eFoldingTime)));
			}
		}
	}

	public double fuelFraction(IgnitionTypeEnum ignitionType, IgnitionDirectionTypeEnum directionType, double weightingFactor, double ignitionTime, double currentTime, double spreadRate1, double spreadRate2, double cellSize) throws NotSupportedHeatException
	{
		
		
		double fraction = -1;
		switch(ignitionType)
		{
		case BY_CELL:
			switch(directionType)
			{
			case N:
			case S:
			case W:
			case E:
				fraction = this.straightOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate1, cellSize);
				break;
			case NW:
			case SW:
			case SE:
			case NE:
				fraction = this.diagonalOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate1, cellSize);
				break;
			default:
				throw new NotSupportedHeatException();
			}
			break;
		case BY_IGNITER:
			switch(directionType)
			{
			case N:
			case S:
			case W:
			case E:
				fraction = this.straightOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate1, cellSize) + straightOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate2, cellSize);
				fraction = fraction/2;
				break;
			case NW:
			case SW:
			case SE:
			case NE:
				fraction = this.diagonalOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate1, cellSize) + diagonalOneWayFuelFraction(weightingFactor, ignitionTime, currentTime, spreadRate2, cellSize);
				fraction = fraction/2;
				break;
			default:
				throw new NotSupportedHeatException();
			}
			break;
		}
		
		return fraction;
		//return (oneWayfuelFraction(weightingFactor, ignitionTime, currentTime, maxSpreadRate, cellSize) + oneWayfuelFraction(weightingFactor, ignitionTime, currentTime, minSpreadRate, cellSize))/2;
	}
	
	private double fuelFractionChange(IgnitionTypeEnum ignitionType, IgnitionDirectionTypeEnum directionType, double weightingFactor, double ignitionTime, double startTime, double endTime, double spreadRate1, double spreadRate2, double cellSize) throws NotSupportedHeatException
	{
		return fuelFraction(ignitionType, directionType, weightingFactor, ignitionTime, startTime, spreadRate1, spreadRate2, cellSize) - fuelFraction(ignitionType, directionType, weightingFactor, ignitionTime, endTime, spreadRate1, spreadRate2, cellSize); 
	}
		
	public double sensibleHeatFluxDesnsity(IgnitionTypeEnum ignitionType, IgnitionDirectionTypeEnum directionType, double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisure, double fuelLoading, double fuelHeatContent, double spreadRate1, double spreadRate2, double cellSize) throws NotSupportedHeatException
	{
		
		double heat =  fuelFractionChange(ignitionType, directionType, weightingFactor, ignitionTime, startTime, endTime, spreadRate1, spreadRate2, cellSize) 
				* m.totalSensibleHeat(weightingFactor, fuelMoisure, fuelLoading, fuelHeatContent);
	
		return heat / (endTime - startTime);
	}

	public double latentHeatFluxDesnsity(IgnitionTypeEnum ignitionType, IgnitionDirectionTypeEnum directionType, double weightingFactor, double ignitionTime, double startTime, double endTime, double fuelMoisture, double fuelLoading, double spreadRate1, double spreadRate2, double cellSize ) throws NotSupportedHeatException
	{
		double heat =  fuelFractionChange(ignitionType, directionType, weightingFactor, ignitionTime, startTime, endTime, spreadRate1, spreadRate2, cellSize) 
				* m.totalLatentHeat(weightingFactor, fuelMoisture, fuelLoading);
		return heat / (endTime - startTime);
	}
	
}