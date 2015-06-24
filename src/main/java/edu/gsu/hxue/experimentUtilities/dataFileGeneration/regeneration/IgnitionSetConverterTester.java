package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;

import java.io.File;

public class IgnitionSetConverterTester
{
	public static void main(String[] args)
	{
		File dataFile = new File("OtherData/IgnitionPoints_MB_corrected.txt");
		
		
		double newCellSize=75;
		BorderType borderType = BorderType.SMALLER; 
		Alignment alignment = Alignment.LOWER_RIGHT;
		
		double oldCellSize = 30; 
		int oldXDim=547;
		int oldYDim=545;
		
		String newDataFilePath = "OtherData/IgnitionPoints_MB_corrected_new" + newCellSize+".txt";
		CoordinateConverter con = new CoordinateConverter(oldCellSize, oldXDim, oldYDim, newCellSize, borderType, alignment);

		IgnitionSetConverter.convertFile(dataFile, newDataFilePath, con);
		System.out.println("New Data File generated");
		System.out.println("new xDim: " + con.getNewXDim());
		System.out.println("new yDim: " + con.getNewYDim());
	}

}
