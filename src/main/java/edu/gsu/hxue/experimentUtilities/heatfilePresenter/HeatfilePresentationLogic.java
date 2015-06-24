package edu.gsu.hxue.experimentUtilities.heatfilePresenter;

import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.experimentUtilities.ExpUtilities;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class HeatfilePresentationLogic
{
	private CellularAutomataPresentation cellularAutomataPresentation;
	private GISData gisData;
	private File initialIgnitionFile;
	private File delayedIgnitionFile;
	private File initialContainedFile;
	private String name;
	private double presentationCellSize; // a grid of this cell size is used to show the input cells, and each cell is mapped to this grid if have different cell size

	
	private double presentation_scalar = 1.5;
	private final Color INITIAL_IGNITION_COLOR = Color.red;
	private final Color DELAYED_IGNITION_COLOR = Color.yellow;
	private final Color CONTAINED_COLOR = Color.gray;
	private final Color BURNED_CELL_IN_HEAT_COLOR = Color.black;
	
	private CoordinateConverter coorConv;
	
	public HeatfilePresentationLogic(String fuelPath, String slopePath, String aspectPath, File initialIgnitionFile, File delayedIgnitionFile, File initialContainedFile, String name, double presentationCellSize, double scalar) throws Exception
	{
		gisData = new GISData(slopePath, // slope
				aspectPath, // aspect
				fuelPath // fuel
		);
		this.name = name;
		this.presentation_scalar = scalar;
		this.presentationCellSize = presentationCellSize;
		coorConv = new  CoordinateConverter(gisData.cellSize(), gisData.xDim(), gisData.yDim(), this.presentationCellSize, BorderType.LARGER , Alignment.LOWER_LEFT);
		
		cellularAutomataPresentation = new CellularAutomataPresentation(coorConv.getNewXDim(), coorConv.getNewYDim(), presentation_scalar);
		cellularAutomataPresentation.setTitle(this.name);
		
		this.initialIgnitionFile = initialIgnitionFile;
		this.delayedIgnitionFile = delayedIgnitionFile;
		this.initialContainedFile = initialContainedFile;
	}
	
	// use the original cell size, if the presentation cell size is not specified
	public HeatfilePresentationLogic(String fuelPath, String slopePath, String aspectPath, File initialIgnitionFile, File delayedIgnitionFile, File initialContainedFile, String name, double scalar) throws Exception
	{
		gisData = new GISData(slopePath, // slope
				aspectPath, // aspect
				fuelPath // fuel
		);
		presentationCellSize = gisData.cellSize();
		this.name = name;
		this.presentation_scalar = scalar;
		presentationCellSize = gisData.cellSize();
		cellularAutomataPresentation = new CellularAutomataPresentation(gisData.xDim(), gisData.yDim(), presentation_scalar);
		cellularAutomataPresentation.setTitle(this.name);
		
		this.initialIgnitionFile = initialIgnitionFile;
		this.delayedIgnitionFile = delayedIgnitionFile;
		this.initialContainedFile = initialContainedFile;
	}
	
	public void drawGIS() throws NotSupportedPresentation
	{
		if(presentationCellSize!=gisData.cellSize()) throw new NotSupportedPresentation();
		ExpUtilities.drawGISMap(cellularAutomataPresentation, gisData);
	}
	
	public void drawIgnition() throws FileNotFoundException, NotSupportedPresentation
	{
		if(presentationCellSize!=gisData.cellSize()) throw new NotSupportedPresentation();
		ExpUtilities.drawIgnition(cellularAutomataPresentation, initialIgnitionFile, INITIAL_IGNITION_COLOR);
	}
	
	public void drawDelayedIgnition() throws FileNotFoundException, NotSupportedPresentation
	{
		if(presentationCellSize!=gisData.cellSize()) throw new NotSupportedPresentation();
		ExpUtilities.drawDelayedIgnition(cellularAutomataPresentation, delayedIgnitionFile, DELAYED_IGNITION_COLOR);
	}
	
	public void drawContainedCells() throws FileNotFoundException, NotSupportedPresentation
	{
		if(presentationCellSize!=gisData.cellSize()) throw new NotSupportedPresentation();
		ExpUtilities.drawContainedCells(cellularAutomataPresentation, initialContainedFile, CONTAINED_COLOR);
	}
	
	public void drawHeatfilesOutline(String folder, int stepLength, int startStep, int endStep) throws FileNotFoundException
	{	
		drawHeatfilesOutline(folder, stepLength, startStep, endStep, this.BURNED_CELL_IN_HEAT_COLOR);
	}
	
	public void drawHeatfilesOutline(String folder, int stepLength, int startStep, int endStep, Color c) throws FileNotFoundException
	{	
		int[][] orgMap = ExpUtilities.getFireMapFromHeatFiles(folder, stepLength, startStep, endStep, gisData.xDim(), gisData.yDim());
		
		int[][] newMap = new int[coorConv.getNewXDim()][coorConv.getNewYDim()];
		
		for( int x=0; x<=coorConv.getNewXDim(); x++)
			for( int y=0; y<=coorConv.getNewXDim(); y++)
			{
				List<CellWithPortion> porList = coorConv.calculateCoveredOldCells(x, y);
				
				for( CellWithPortion cell : porList)
				{
					int oldX = cell.getCell().getX();
					int oldY = cell.getCell().getY();
					
					if( oldX>=0 && oldX<gisData.xDim() && oldY>=0 && oldY<gisData.yDim())
						if(orgMap[oldX][oldY]==1)
						{
							newMap[x][y]=1;
							break;
						}
				}
			}
		
		int[][] outline = ExpUtilities.getFireOutline(newMap, coorConv.getNewXDim() , coorConv.getNewYDim());
		ExpUtilities.drawFireMap(outline, this.cellularAutomataPresentation, coorConv.getNewXDim() , coorConv.getNewYDim(), c);
	}
	
	public void drawHeatfiles(String folder, int stepLength, int startStep, int endStep) throws FileNotFoundException, NotSupportedPresentation
	{
		if(presentationCellSize!=gisData.cellSize()) throw new NotSupportedPresentation();
		ExpUtilities.drawIgnitedCellsFromHeatFiles(folder, stepLength, startStep, endStep, cellularAutomataPresentation, gisData.xDim(), gisData.yDim(), this.BURNED_CELL_IN_HEAT_COLOR);
	}
	
}
