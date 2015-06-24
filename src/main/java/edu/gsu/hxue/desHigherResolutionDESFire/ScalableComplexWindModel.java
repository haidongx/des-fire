package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.ComplexWindModel;
import edu.gsu.hxue.desFire.FireCell;

/**
 * This wind model provide dynamic wind speed and direction, cells can have different wind at the same time
 * @author Haydon
 *
 */
public class ScalableComplexWindModel extends ComplexWindModel
{	
	int scaleFactor;	

	public ScalableComplexWindModel(DESSystem system, String filePath, int spaceWidth, int spaceHeight, int scaleFactor)
	{
		super(system, filePath, spaceWidth, spaceHeight);
		
		this.scaleFactor = scaleFactor;
	}

	@Override
	public void generateOutput()
	{
		// For ignited cells
		if(newWind)
		{
			for (int cellIndex : this.observorCellIndex)
			{
				FireCell f = (FireCell) system.getModel(cellIndex);
				int cellX = f.getX();
				int cellY = f.getY();
				CellWindInfo windInfo = this.windSpace[cellX/this.scaleFactor][cellY/this.scaleFactor];  //mapping back to the original coordinates
				if (true/*windInfo.lastUpdateTimeStamp > windInfo.observorTimeStamp*/)
				{
					// System.out.println("Weather sent to " + cellX + "-" + cellY + "at" + this.getTime() + " " + windInfo.currentWindSpeed + " " + windInfo.currentWindDirection);
					this.sendMessage(cellIndex, new MessageFromWindModelToCell(windInfo.currentWindSpeed, windInfo.currentWindDirection));
					//windInfo.observorTimeStamp = windInfo.lastUpdateTimeStamp;
				}
			}
			newWind = false;
		}
		
		// For a single cell
		if(this.isNeededToUpdateACell)
		{
			FireCell f = (FireCell) system.getModel(targetCellModelIndex);
			int cellX = f.getX();
			int cellY = f.getY();
			CellWindInfo windInfo = this.windSpace[cellX/this.scaleFactor][cellY/this.scaleFactor];
			this.sendMessage(targetCellModelIndex, new MessageFromWindModelToCell(windInfo.currentWindSpeed, windInfo.currentWindDirection));
			//System.out.println("Reply " + cellX + "-" + cellY + ": " + windInfo.currentWindSpeed + " " + windInfo.currentWindDirection);
			//windInfo.observorTimeStamp = windInfo.lastUpdateTimeStamp;
			this.isNeededToUpdateACell = false;
		}
	}


}
