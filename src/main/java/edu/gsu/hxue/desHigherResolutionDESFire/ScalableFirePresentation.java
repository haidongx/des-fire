package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.desFire.GlobalConstants;
import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.FireCell;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;

public class ScalableFirePresentation extends Model
{
	CellularAutomataPresentation presentation;
	double wakeUpInterval = 600;
	double timer = 0;
	int xDim;
	int yDim;
	
	int scaleFactor;

	private int cellIndex[][];

	public ScalableFirePresentation(DESSystem system, GISData gisMap, int scaleFactor)
	{
		super(system);
		
		this.scaleFactor = scaleFactor;
		
		this.xDim = gisMap.xDim()*this.scaleFactor;
		this.yDim = gisMap.yDim()*this.scaleFactor;
		presentation = new CellularAutomataPresentation(xDim, yDim);
		
		cellIndex = new int[xDim][yDim];

		for (int i = 0; i < xDim; i++)
			for (int j = 0; j < yDim; j++)
				cellIndex[i][j] = -1;
		
		drawGISMap(gisMap);
	}
	
	private void drawGISMap(GISData gisMap)
	{
		for (int i = 0; i < xDim; i++)
			for (int j = 0; j < yDim; j++)
				presentation.setCellColor(i, j, StandardCustomizedFuelEnum.modelIndexToFuelType(gisMap.fuelModelIndexAt(i/this.scaleFactor, j/this.scaleFactor)).getColor());
		presentation.drawDirtyCellsInBuffer();
		presentation.showBufferOnScreen();
	}

	/**
	 * Set a receiver cell's model index
	 * @param x
	 * @param y
	 * @param index
	 */
	public void setCellIndex ( int x, int y, int index )
	{
		this.cellIndex[x][y]=index;
	}

	@Override
	public void internalTransit(double delta_time)
	{
		timer += delta_time;

	}

	@Override
	public void externalTransit(Message message)
	{

	}

	@Override
	public void generateOutput()
	{
		if(this.wakeUpInterval - timer <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			for(int x=0; x<xDim; x++)
				for(int y=0; y<yDim; y++)
				{
					FireCell cell = (FireCell)system.getModel(this.cellIndex[x][y]);
					if(cell.isBurning())
					{
						/*double value = cell.getFractionAt(getTime());
						float h = (float) 0.6;
						float s = (float) value;
						float b = (float) 1;
						Color c = Color.getHSBColor(h, 1-s, b);
						presentation.setCellColor(x, y, c);*/
						
						if(cell.isBurning())
							presentation.setCellColor(x, y, Color.red);
						
						if(cell.isAllSent())	
							presentation.setCellColor(x, y, Color.black);
							
							
						
					}
				}
			presentation.drawDirtyCellsInBuffer();
			presentation.showBufferOnScreen();
			presentation.setTitle("Simulation Time: " + (int)this.getTime() + "s");
			timer=0;
		}
	}

	@Override
	public double getNextInternalTransitionTime()
	{
		return wakeUpInterval - timer;
	}


}
