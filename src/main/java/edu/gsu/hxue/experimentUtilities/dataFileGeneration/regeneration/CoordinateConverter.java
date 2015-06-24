package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import java.util.ArrayList;
import java.util.List;

public class CoordinateConverter
{
	private final static double THRESHOLD = 1e-8;
	
	private double oldCellSize;
	private int oldXDim;
	private int oldYDim;


	private double newCellSize;
	
	private BorderType borderType;
	private Alignment alignment;
	
	private int newXDim;
	private int newYDim;
	private double xOffset;
	private double yOffset;
	
	public int getOldXDim()
	{
		return oldXDim;
	}

	public int getOldYDim()
	{
		return oldYDim;
	} 
	
	public double getOldCellSize()
	{
		return oldCellSize;
	}


	public double getNewCellSize()
	{
		return newCellSize;
	}

	public BorderType getBorderType()
	{
		return borderType;
	}

	public Alignment getAlignment()
	{
		return alignment;
	}

	public int getNewXDim()
	{
		return newXDim;
	}

	public int getNewYDim()
	{
		return newYDim;
	}

	public double getxOffset()
	{
		return xOffset;
	}

	public double getyOffset()
	{
		return yOffset;
	}
	
	public CoordinateConverter(double oldCellSize, int oldXDim, int oldYDim, double newCellSize, BorderType borderType, Alignment alignment)
	{
		this.oldCellSize = oldCellSize;
		this.oldXDim = oldXDim;
		this.oldYDim = oldYDim;
		this.newCellSize = newCellSize;
		this.borderType = borderType;
		this.alignment = alignment;

		// old absolute dimensions
		double xOldAbs = oldCellSize * oldXDim;
		double yOldAbs = oldCellSize * oldYDim;

		// new cell dimensions
		newXDim = (int) (xOldAbs / newCellSize);
		if (xOldAbs % newCellSize > THRESHOLD)
			if (borderType == BorderType.LARGER)
				newXDim++;

		newYDim = (int) (yOldAbs / newCellSize);
		if (yOldAbs % newCellSize > THRESHOLD)
			if (borderType == BorderType.LARGER)
				newYDim++;

		// the offset of new cells
		if (alignment == Alignment.UPPER_LEFT || alignment == Alignment.LOWER_LEFT)
			xOffset = 0;
		else
			xOffset = xOldAbs - newXDim * newCellSize;

		if (alignment == Alignment.LOWER_RIGHT || alignment == Alignment.LOWER_LEFT)
			yOffset = 0;
		else
			yOffset = yOldAbs - newYDim * newCellSize;
	}

	public List<CellWithPortion> calculateCoveredOldCells(int newXCoordinate, int newYCoordinate)
	{
		List<CellWithPortion> coveredOldCells = new ArrayList<CellWithPortion>();

		// new cell position
		double xNew_start = newXCoordinate * newCellSize + xOffset;
		double yNew_start = newYCoordinate * newCellSize + yOffset;
		double xNew_end = xNew_start + newCellSize;
		double yNew_end = yNew_start + newCellSize;

		// involved old cells
		int xInvolvedMin;
		if(xNew_start>=0)
			xInvolvedMin = (int) (xNew_start / oldCellSize);
		else
			xInvolvedMin = (int) (xNew_start / oldCellSize)-1;
		
		int xInvolvedMax = (int) (xNew_end / oldCellSize);
		if (xNew_end % oldCellSize <= THRESHOLD)
			xInvolvedMax--;
		
		int yInvolvedMin;
		if(yNew_start>=0)
			yInvolvedMin = (int) (yNew_start / oldCellSize);
		else
			yInvolvedMin = (int) (yNew_start / oldCellSize)-1;
		
		int yInvolvedMax = (int) (yNew_end / oldCellSize);
		if (yNew_end % oldCellSize <= THRESHOLD)
			yInvolvedMax--;
		
		// calculate the portion of each involved cells
		for( int x=xInvolvedMin; x<=xInvolvedMax; x++)
			for( int y=yInvolvedMin; y<=yInvolvedMax; y++)
			{
				double involvedXMin=Math.max(x*oldCellSize, xNew_start);
				double involvedXMax=Math.min(x*oldCellSize+oldCellSize, xNew_end);
				double involvedX = involvedXMax - involvedXMin;
				if(involvedX<0) involvedX=0;
				
				double involvedYMin=Math.max(y*oldCellSize, yNew_start);
				double involvedYMax=Math.min(y*oldCellSize+oldCellSize, yNew_end);
				double involvedY = involvedYMax - involvedYMin;
				if(involvedY<0) involvedY=0;
				
				coveredOldCells.add(new CellWithPortion( new Cell(x, y), involvedX*involvedY/(oldCellSize*oldCellSize)));
			}

		return coveredOldCells;
	}



	public static enum BorderType
	{
		LARGER, SMALLER;
	}

	public static enum Alignment
	{
		UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT;
	}

	public static class CellWithPortion
	{
		private Cell cell;
		private double portion;

		public CellWithPortion(Cell cell, double portion)
		{
			super();
			this.cell = cell;
			this.portion = portion;
		}

		public Cell getCell()
		{
			return cell;
		}

		public void setCell(Cell cell)
		{
			this.cell = cell;
		}

		public double getPortion()
		{
			return portion;
		}

		public void setPortion(double portion)
		{
			this.portion = portion;
		}
	}

	public static class Cell
	{
		private int x;
		private int y;

		public Cell(int x, int y)
		{
			super();
			this.x = x;
			this.y = y;
		}

		public int getX()
		{
			return x;
		}

		public void setX(int x)
		{
			this.x = x;
		}

		public int getY()
		{
			return y;
		}

		public void setY(int y)
		{
			this.y = y;
		}

	}
}
