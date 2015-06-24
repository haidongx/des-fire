package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.gis.SingleGISLayer;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class TerrainFileRegenerator
{
	public void convertFile(File oldAspectFile, File oldSlopeFile, String newAspectFilePath, String newSlopefilePath, double newCellSize, BorderType borderType, Alignment alignment) throws DataRegenerationException
	{
		try
		{
			//old aspect data
			SingleGISLayer aspect = new SingleGISLayer(oldAspectFile);
			
			//old slope data
			SingleGISLayer slope = new SingleGISLayer(oldSlopeFile);
			
			if(aspect.cellSize()!=slope.cellSize() || aspect.xDim()!=slope.xDim() || aspect.yDim()!= slope.yDim())
				throw new DataRegenerationException("Inconsistant slope and aspect");
			
			
			//construct the coordinate converter
			CoordinateConverter con = new CoordinateConverter(aspect.cellSize(), aspect.xDim(), aspect.yDim(), newCellSize, borderType, alignment);
			
			//construct new data
			double[][] newSlope = new double[con.getNewXDim()][con.getNewYDim()];
			double[][] newAspect = new double[con.getNewXDim()][con.getNewYDim()];
			for(int x=0; x<con.getNewXDim(); x++)
				for(int y=0; y<con.getNewYDim(); y++)
				{
					// calculate new value
					List<CellWithPortion> re = con.calculateCoveredOldCells( x, y);
					
					if(re.isEmpty())
						throw new DataRegenerationException("a new cell covers no old cells");
					// old vectors
					double tempXComponent=0;
					double tempYComponent=0;
					double portionSum=0;
			
					// total portion
					for(CellWithPortion cp : re)
					{
						if(cp.getCell().getX()>=0 && cp.getCell().getX()<con.getOldXDim() && cp.getCell().getY()>=0 && cp.getCell().getY()<con.getOldYDim() )
							portionSum += cp.getPortion();
					}
					
					//System.out.println("Current cell is: " + x + " - " + y);
					
					// weighted averaged vector
					for(CellWithPortion cp : re)
					{
						
						if(cp.getCell().getX()>=0 && cp.getCell().getX()<con.getOldXDim() && cp.getCell().getY()>=0 && cp.getCell().getY()<con.getOldYDim() )
						{
							//System.out.print(cp.getCell().getX() + " - " + cp.getCell().getY() + "\tslope=" + slope.dataAt(cp.getCell().getX(), cp.getCell().getY()) );
							//System.out.println("\taspect=" + aspect.dataAt(cp.getCell().getX(), cp.getCell().getY()));
							
							
							double scalar = slope.dataAt(cp.getCell().getX(), cp.getCell().getY()) * cp.getPortion() / portionSum;
							double radian = Math.toRadians(aspect.dataAt(cp.getCell().getX(), cp.getCell().getY()));
							// just project the vector any base (not the true X and Y axies)
							tempXComponent += Math.cos(radian) * scalar;
							tempYComponent += Math.sin(radian) * scalar;
						}
					}
					
						newSlope[x][y] = Math.sqrt(tempXComponent*tempXComponent+tempYComponent*tempYComponent);
					newAspect[x][y] = Math.toDegrees(Math.atan2(tempYComponent, tempXComponent));
					while(newAspect[x][y] <0) newAspect[x][y] +=360;
					newAspect[x][y] = newAspect[x][y]  % 360.0;
					
					//System.out.println("New Slope = "  + newSlope[x][y]);
					//System.out.println("New Aspect= " + newAspect[x][y]);
					
				}
			
			// save to file
			SingleGISLayer newAspectLayer = new SingleGISLayer( con.getNewXDim(), con.getNewYDim(), newCellSize, newAspect);
			File newAspectFile = new File(newAspectFilePath);
			newAspectLayer.saveToFile(newAspectFile);
			
			SingleGISLayer newSlopeLayer = new SingleGISLayer( con.getNewXDim(), con.getNewYDim(), newCellSize, newSlope);
			File dataSlopeFile = new File(newSlopefilePath);
			newSlopeLayer.saveToFile(dataSlopeFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

}
