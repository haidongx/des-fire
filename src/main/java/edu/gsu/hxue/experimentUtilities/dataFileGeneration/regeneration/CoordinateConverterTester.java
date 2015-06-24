package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;

import java.util.List;

public class CoordinateConverterTester
{
	public static void main(String[] args)
	{
		double oldCellSize = 1; 
		int oldXDim=5;
		int oldYDim=5; 
		
		double newCellSize=0.6;
		int newXCoordinate=0;
		int newYCoordinate=1;
		BorderType borderType = BorderType.LARGER; 
		Alignment alignment = Alignment.LOWER_LEFT;
		
		CoordinateConverter con = new CoordinateConverter(oldCellSize, oldXDim, oldYDim, newCellSize, borderType, alignment);
		List<CellWithPortion> re = con.calculateCoveredOldCells( newXCoordinate, newYCoordinate);
		
		for( CellWithPortion cp : re )
			System.out.println(cp.getCell().getX() + "-" + cp.getCell().getY() + " ==> " + cp.getPortion());

	}

}
