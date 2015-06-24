package edu.gsu.hxue.gis.fuel;

import java.io.Serializable;

/**
 * This class represents a fuel category. It is a collection of objects of BasicFuel.  
 * 
 * @author Haidong Xue
 * @version 9/19/2011
 */

public class FuelCategory implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6225741045733239055L;
	private String categoryName; // category name
	private int numberOfClassSizes; // number of class sizes
	private BasicFuel[] fuelsInCategory; //fuels with different class sizes
	
	private double meanCategoryFuelDepth;  //the mean fuel depth of this category
	private double meanExtinctionMoistureContent; //the mean extinction moisture content of this category
	
	public double meanTotalSurfaceArea()
	{
		double A_i = 0;
		for( BasicFuel b : fuelsInCategory)
		{
			A_i += b.meanTotalSurfaceArea();
		}
		return A_i;
	}
	
	public double rothermelNetLoading()
	{
		double w_n_i = 0;
		
		if (this.meanTotalSurfaceArea() != 0)
		{
			for (BasicFuel b : fuelsInCategory)
			{
				double f_ij = b.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				w_n_i += f_ij * b.rothermelNetLoading();
			}
		}
		
		return w_n_i;
	}
	
	public double rothermelFuelMoisture()
	{
		double M_f_i = 0;
		if (this.meanTotalSurfaceArea() != 0)
			for (BasicFuel b : fuelsInCategory)
			{
				double f_ij = b.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				M_f_i += f_ij * b.fuelMoistureContent();
			}
		
		return M_f_i;
	}
	
	public double rothermelLowHeatContent()
	{
		double h_i = 0;
		if (this.meanTotalSurfaceArea() != 0)
			for (BasicFuel b : fuelsInCategory)
			{
				double f_ij = b.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				h_i += f_ij * b.lowHeatContent();
			}
		
		return h_i;
		
	}
	
	public FuelCategory( String categoryName, BasicFuel[] fuelsInCategory)
	{
		this.categoryName = categoryName; //Set name
		this.numberOfClassSizes = fuelsInCategory.length;// Set class number
		this.fuelsInCategory = fuelsInCategory; //set fuels
		
		//Set mean fuel depth and mean extinction moisture content
		double sumDepth=0;
		double sumExtinctionMoisture=0;
		for(BasicFuel f:fuelsInCategory)
		{
			sumDepth+=f.fuelDepth();
			sumExtinctionMoisture+=f.extinctionMoistureContent();
		}
		this.meanCategoryFuelDepth = sumDepth / fuelsInCategory.length;
		this.meanExtinctionMoistureContent  = sumExtinctionMoisture / fuelsInCategory.length; 
	}
	
	public double meanExtinctionMoistureContent()
	{
		return this.meanExtinctionMoistureContent;
	}
	
	public double meanFuelDepth()
	{
		return this.meanCategoryFuelDepth;
	}
	
	public BasicFuel fuelWithClassSizeNumber( int j )
	{
		return fuelsInCategory[j];
	}

	public String categoryName()
	{
		return categoryName;
	}

	public int numberOfClassSizes()
	{
		return numberOfClassSizes;
	}
	
	public String toString()
	{
		String s = this.categoryName + "\tmean depth=" +this.meanCategoryFuelDepth+"\tmean M_x=" +this.meanExtinctionMoistureContent+ ":\n";
		for(BasicFuel f:fuelsInCategory)
		{
			s += f.toString() + "\n";
		}
		return s;
	}

	public BasicFuel[] getFuelsInCategory()
	{
		return this.fuelsInCategory;
	}
}
