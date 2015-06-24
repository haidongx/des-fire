package edu.gsu.hxue.physicalModels.initialIgnitionCellHeat;

public class InitialIgnitionCellHeatTester
{
	public static void main(String[] args)
	{
		InitialIgnitionCellHeat i = new InitialIgnitionCellHeat();
		
		double weightingFactor = 100;
		double ignitionTime = 0;
		
		double minSpreadRate = 1; 
		double maxSpreadRate = 4;
		double cellSize= 30;
		
		for( double t=-1; t<2000; t+=20)
			System.out.println(t + ": " + i.fuelFraction(weightingFactor, ignitionTime, t, maxSpreadRate, minSpreadRate, cellSize));

	}

}
