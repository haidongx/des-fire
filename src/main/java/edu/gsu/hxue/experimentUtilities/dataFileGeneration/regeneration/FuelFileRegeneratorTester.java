package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;

import java.io.File;

public class FuelFileRegeneratorTester
{

	public static void main(String[] args)
	{
		File oldDataFile = new File("GISData/MooreBranch_fuel.txt");
		
		double newCellSize=16;
		BorderType borderType = BorderType.SMALLER; 
		Alignment alignment = Alignment.LOWER_RIGHT;
		
		String newFilePath = "GISData/MooreBranch_fuel_new" + newCellSize + ".txt";
		
		FuelFileRegenerator tr = new FuelFileRegenerator();
		try
		{
			tr.convertFile(oldDataFile, newFilePath, newCellSize, borderType, alignment);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
