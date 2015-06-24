package edu.gsu.hxue.gis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;


/**
 * This class reads and holds GIS data.
 * 
 * @author Haidong Xue
 * @version 9/13/2011
 *
 */

public class GISData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7430162952602735297L;
	private SingleGISLayer slopeMap;
	private SingleGISLayer aspectMap;
	private SingleGISLayer fuelMap;
	
	private int xDim;
	private int yDim;
	private double cellSize;
	
	public GISData( String slopeFilePath, String aspectFilePath, String fuelMapPath ) throws Exception
	{
		slopeMap = new SingleGISLayer( new File(slopeFilePath) );
		aspectMap = new SingleGISLayer( new File(aspectFilePath) );
		fuelMap = new SingleGISLayer( new File(fuelMapPath) );

		if( !((slopeMap.xDim()==aspectMap.xDim())&&(slopeMap.xDim()==fuelMap.xDim())))
		{
			System.out.println("slopeMap xDim: " + slopeMap.xDim());
			System.out.println("aspectMap xDim: " + aspectMap.xDim());
			System.out.println("fuelMap xDim: " + fuelMap.xDim());
			throw new Exception("GIS data files have different dimensions. Data are now dirty!");
		}
		
		this.xDim = slopeMap.xDim();
		
		if( !((slopeMap.yDim()==aspectMap.yDim())&&(slopeMap.yDim()==fuelMap.yDim())))
		{
			System.out.println("slopeMap yDim: " + slopeMap.yDim());
			System.out.println("aspectMap yDim: " + aspectMap.yDim());
			System.out.println("fuelMap yDim: " + fuelMap.yDim());
			throw new Exception("GIS data files have different dimensions. Data are now dirty!");
		}	
		
		this.yDim = slopeMap.yDim();
		
		if( !((slopeMap.cellSize()==aspectMap.cellSize())&&(slopeMap.cellSize()==fuelMap.cellSize())))
		{
			System.out.println("slopeMap cellSize: " + slopeMap.cellSize());
			System.out.println("aspectMap cellSize: " + aspectMap.cellSize());
			System.out.println("fuelMap cellSize: " + fuelMap.cellSize());
			throw new Exception("GIS data files have different cell sizes. Data are now dirty!");
		}
		this.cellSize = slopeMap.cellSize();
	}
	
	public GISData( File slopeFile, File aspectFile, File fuelFile ) throws Exception
	{
		slopeMap = new SingleGISLayer( slopeFile );
		aspectMap = new SingleGISLayer( aspectFile );
		fuelMap = new SingleGISLayer( fuelFile );

		if( !((slopeMap.xDim()==aspectMap.xDim())&&(slopeMap.xDim()==fuelMap.xDim())))
		{
			System.out.println("slopeMap xDim: " + slopeMap.xDim());
			System.out.println("aspectMap xDim: " + aspectMap.xDim());
			System.out.println("fuelMap xDim: " + fuelMap.xDim());
			throw new Exception("GIS data files have different dimensions. Data are now dirty!");
		}
		
		this.xDim = slopeMap.xDim();
		
		if( !((slopeMap.yDim()==aspectMap.yDim())&&(slopeMap.yDim()==fuelMap.yDim())))
		{
			System.out.println("slopeMap yDim: " + slopeMap.yDim());
			System.out.println("aspectMap yDim: " + aspectMap.yDim());
			System.out.println("fuelMap yDim: " + fuelMap.yDim());
			throw new Exception("GIS data files have different dimensions. Data are now dirty!");
		}	
		
		this.yDim = slopeMap.yDim();
		
		if( !((slopeMap.cellSize()==aspectMap.cellSize())&&(slopeMap.cellSize()==fuelMap.cellSize())))
		{
			System.out.println("slopeMap cellSize: " + slopeMap.cellSize());
			System.out.println("aspectMap cellSize: " + aspectMap.cellSize());
			System.out.println("fuelMap cellSize: " + fuelMap.cellSize());
			throw new Exception("GIS data files have different cell sizes. Data are now dirty!");
		}
		this.cellSize = slopeMap.cellSize();
	}
	
	public int xDim()
	{
		return this.xDim;
	}
	
	public int yDim()
	{
		return this.yDim;
	}
	
	public double cellSize()
	{
		return this.cellSize;
	}
	
	public double slopeAt( int x, int y)
	{
		return slopeMap.dataAt(x, y);
	}
	
	public double aspectAt(int x, int y)
	{
		return aspectMap.dataAt(x, y);
	}
	
	public int fuelModelIndexAt(int x, int y)
	{
		return (int)fuelMap.dataAt(x, y);
	}

	public void updateSlopeLayer( String newSlopeFilePath ) throws FileNotFoundException, Exception
	{
		SingleGISLayer newSlopeMap = new SingleGISLayer( new File(newSlopeFilePath));
		this.verifyDataLayer(newSlopeMap);
		this.slopeMap = newSlopeMap;
	}
	
	public void updateAspectLayer(String newAspectFilePath) throws FileNotFoundException, Exception
	{
		SingleGISLayer newAspectMap = new SingleGISLayer(new File(newAspectFilePath));
		this.verifyDataLayer(newAspectMap);
		this.aspectMap = newAspectMap;
	}
	
	public void updateFuelLayer(String newFuelFilePath) throws FileNotFoundException, Exception
	{
		SingleGISLayer newFuelMap = new SingleGISLayer(new File(newFuelFilePath));
		this.verifyDataLayer(newFuelMap);
		this.fuelMap = newFuelMap;
	}
		
	private void verifyDataLayer( SingleGISLayer l ) throws Exception
	{
		if(l.cellSize()!=this.cellSize)
			throw new Exception("Inconsistent cell size is found in " + l.dataFile().getPath() + "!" );
		
	}
}
