package edu.gsu.hxue.experimentUtilities.NaturalHazardsPaper;

import edu.gsu.hxue.experimentUtilities.ExpUtilities;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;

public class ShowMBSlope
{

	public static void main(String[] args)
	{
		// draw slope
		CellularAutomataPresentation p = ExpUtilities.showSlope( new File("GISData/MooreBranch_slope.txt"), false, 0, 15);
		
		// draw MB day 5
		int xDim = p.getXDim();
		int yDim = p.getYDim();
		int[][] burnedArea = ExpUtilities.getFireMapFromPointSetFile("OtherData/Day5_B.txt", xDim, yDim);
		int[][] outLine = ExpUtilities.getFireOutline(burnedArea, xDim, yDim);
		ExpUtilities.drawFireMapThick(outLine, p, burnedArea.length, burnedArea[0].length, Color.black);
	}

}
