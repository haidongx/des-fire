package edu.gsu.hxue.experimentUtilities;

public class CoupledDifferenceCalculator
{
	private String name;
	private int xDim;
	private int yDim;
	private String oneWayHeatFileFolder;
	private String twoWayHeatFileFolder;
	private int heatStepLength;
	private int heatStepNumber;
	private double oneWayIgnitedCellNumber;
	private double twoWayIgnitedCellNumber;
	private double absoluteDifference;
	private double relativeDifference;
	
	public double getAbsoluteDifference() { return this.absoluteDifference;}
	public double getRelativeDifference() { return this.relativeDifference;}
	
	public CoupledDifferenceCalculator( String name, String oneWayFolder, String twoWayFolder, int xDim, int yDim, int heatStepLength, int heatStepNumber)
	{
		this.name = name;
		this.xDim = xDim;
		this.yDim = yDim;
		this.oneWayHeatFileFolder = oneWayFolder;
		this.twoWayHeatFileFolder = twoWayFolder;
		this.heatStepLength = heatStepLength;
		this.heatStepNumber = heatStepNumber;
		calculate();
	}
	
	private void calculate()
	{
		boolean[][] oneWay =  FireErrorCalculator.simFireReader(this.oneWayHeatFileFolder, this.heatStepLength, this.heatStepNumber, this.xDim, this.yDim);
		boolean[][] twoWay =  FireErrorCalculator.simFireReader(this.twoWayHeatFileFolder, this.heatStepLength, this.heatStepNumber, this.xDim, this.yDim);
		double oneWayIgnitedCellNumber = 0;
		double twoWayIgnitedCellNumber = 0;
		for(int x=0; x<xDim; x++)
			for(int y=0; y<yDim; y++)
			{
				if(oneWay[x][y]) oneWayIgnitedCellNumber++;
				if(twoWay[x][y]) twoWayIgnitedCellNumber++;
			}
		this.oneWayIgnitedCellNumber = oneWayIgnitedCellNumber;
		this.twoWayIgnitedCellNumber = twoWayIgnitedCellNumber;
		this.absoluteDifference = twoWayIgnitedCellNumber - oneWayIgnitedCellNumber;
		this.relativeDifference = this.absoluteDifference / oneWayIgnitedCellNumber;
	}
	
	public String toString()
	{
		return name + "- Ignited1Way: " + this.oneWayIgnitedCellNumber + " Ignited2Way: " + this.twoWayIgnitedCellNumber + " Difference: " + this.absoluteDifference + "/" + this.relativeDifference; 
	}
	
	public static void main(String[] args)
	{	
		int xDim = 200;
		int yDim = 200;
		int heatStep = 60;
		int heatStepNumber = 60;
		CoupledDifferenceCalculator c3 = new CoupledDifferenceCalculator( 
				"3m/s",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_3ms_1way_20121215/",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_3ms_coupled_20121215/",
				xDim, 
				yDim,
				heatStep, 
				heatStepNumber
				);
		
		CoupledDifferenceCalculator c10 = new CoupledDifferenceCalculator(
				"10m/s",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_10ms_1way_20121215/",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_10ms_coupled_20121215/",
				xDim, 
				yDim,
				heatStep, 
				heatStepNumber
				);
		
		CoupledDifferenceCalculator c20 = new CoupledDifferenceCalculator(
				"20m/s",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_20ms_1way_20121215/",
				"C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_20ms_coupled_20121215/",
				xDim, 
				yDim,
				heatStep, 
				heatStepNumber
				);
		
		System.out.println(c3);
		System.out.println(c10);
		System.out.println(c20);
		

	}

}
