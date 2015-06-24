package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;

import java.io.File;

public abstract class DataFileConverter
{
	public abstract void convertFile( File dataFile, String newDataFilePath, double newCellSize, BorderType borderType, Alignment alignment) throws Exception;

}
