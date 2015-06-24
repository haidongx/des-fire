package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;

import java.io.File;

public class TerrainFileRegeneratorTester
{

	public static void main(String[] args)
	{
		File oldSlopeFile = new File("GISData/MooreBranch_slope.txt");
		File oldAspectFile = new File("GISData/MooreBranch_aspect.txt");
		
		double newCellSize=30;
		BorderType borderType = BorderType.SMALLER; 
		Alignment alignment = Alignment.LOWER_RIGHT;
		
		String newSlopeFilePath = "GISData/MooreBranch_slope_new" + newCellSize + ".txt";
		String newAspectFilePath = "GISData/MooreBranch_aspect_new" + newCellSize + ".txt";
		
		TerrainFileRegenerator tr = new TerrainFileRegenerator();
		try
		{
			tr.convertFile(oldAspectFile, oldSlopeFile, newAspectFilePath, newSlopeFilePath, newCellSize, borderType, alignment);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
