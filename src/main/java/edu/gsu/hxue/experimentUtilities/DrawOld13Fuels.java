package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.CellularAutomataPresentation;

public class DrawOld13Fuels
{
	
	static void showOldFuels(){
		
		int xDim = 200;
		int yDim = 200;
		int height = yDim/15;
		CellularAutomataPresentation p = new CellularAutomataPresentation(xDim, yDim);
		p.setDrawFrameOfReference(false);

		for( int i=1; i<=13; i++ ){
			StandardCustomizedFuelEnum fuel = StandardCustomizedFuelEnum.modelIndexToFuelType(i);
			for(int x=0; x<100; x++){
				for( int y=(i-1)*height; y<=i*height-5; y++)
					p.setCellColor(x, y, fuel.getColor());
			}
		}
		
		p.drawDirtyCellsInBuffer();
		p.showBufferOnScreen();
	}
	
	public static void main( String[] args ){
		showOldFuels();
	}
}
