package edu.gsu.hxue.gis.fuel;

import java.io.Serializable;

/**
 * This class represents a complex fuel. Each complex fuel is a combination of fuel categories.
 * 
 * @author Haidong Xue
 * @version 9/19/2011
 * 
 */

public class ComplexFuel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1108941217606449761L;
	private FuelCategory[] fuelCategories;
	private double meanFuelDepth; // the mean fuel depth of all the fuels

	public void setFuelCategories(FuelCategory[] fuelCategories)
	{
		this.fuelCategories = fuelCategories;

		// Set meanFuelDepth
		double sumDepth = 0;
		int numberOfFuels = 0;
		for (FuelCategory c : fuelCategories)
		{
			numberOfFuels += c.numberOfClassSizes();
			for (int i = 0; i < c.numberOfClassSizes(); i++)
			{
				sumDepth += c.fuelWithClassSizeNumber(i).fuelDepth();
			}
		}
		this.meanFuelDepth = sumDepth / numberOfFuels;
	}

	public double meanFuelDepth()
	{
		return this.meanFuelDepth;
	}

	public BasicFuel getFuel(int categoryNumber, int classNumber)
	{
		return this.fuelCategories[categoryNumber].fuelWithClassSizeNumber(classNumber);
	}

	public FuelCategory getFuelCategory(int categoryNumber)
	{
		return this.fuelCategories[categoryNumber];
	}

	public String toString()
	{
		String s = "mean_depth=" + this.meanFuelDepth + "\n";
		for (FuelCategory c : fuelCategories)
			s += c.toString();
		return s;
	}

	public double meanTotalSurfaceArea()
	{
		double A_T = 0;
		for (FuelCategory c : fuelCategories)
		{
			A_T += c.meanTotalSurfaceArea();
		}
		return A_T;
	}

	/**
	 * The net fuel loading calculated using Rothermel weighting method
	 * 
	 * @return
	 */
	public double rothermelNetFuelLoading()
	{
		double w_n_T = 0;

		if (this.meanTotalSurfaceArea() != 0)
			for (FuelCategory c : fuelCategories)
			{
				double f_i = c.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				w_n_T += f_i * c.rothermelNetLoading();
			}
		return w_n_T;
	}

	/**
	 * The fuel moisture calculated using Rothermel weighting method
	 * 
	 * @return
	 */
	public double rothermelFuelMoisture()
	{
		double M_f_T = 0;
		if (this.meanTotalSurfaceArea() != 0)
			for (FuelCategory c : fuelCategories)
			{
				double f_i = c.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				M_f_T += f_i * c.rothermelFuelMoisture();
			}
		return M_f_T;
	}

	/**
	 * The low heat content calculated using Rothermel weighting method
	 * 
	 * @return (kj/kg)
	 */
	public double rothermelLowHeatContent()
	{
		double h_T = 0;
		if (this.meanTotalSurfaceArea() != 0)
			for (FuelCategory c : fuelCategories)
			{
				double f_i = c.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
				h_T += f_i * c.rothermelLowHeatContent();
			}
		return h_T;
	}
	
	public double averageNetFuelLoading()
	{
		double re=0;
		double counter=0;
		for (FuelCategory c : fuelCategories)
		{
			for(BasicFuel f:c.getFuelsInCategory())
			{
				re+=f.ovendryFuelLoading();
				counter++;
			}
		}
		if(counter!=0) return re/counter;
		else return 0;
	}
	
	public double totalNetFuelLoading()
	{
		double re=0;
		for (FuelCategory c : fuelCategories)
		{
			for(BasicFuel f:c.getFuelsInCategory())
			{
				re+=f.ovendryFuelLoading();
			}
		}
		return re;
	}

	public void printRothermelWeightingMethodInfo()
	{
		System.out.println("complex fuel total surface: " + this.meanTotalSurfaceArea());
		int i = 1;
		for (FuelCategory c : fuelCategories)
		{
			System.out.println("==============category-" + i++);
			System.out.println(c.toString());
			System.out.println("category total surface: " + c.meanTotalSurfaceArea());

			for (BasicFuel b : c.getFuelsInCategory())
			{
				double f_ij = b.meanTotalSurfaceArea() / c.meanTotalSurfaceArea();
				System.out.println("f_ij: " + f_ij);
			}

			double f_i = c.meanTotalSurfaceArea() / this.meanTotalSurfaceArea();
			System.out.println("category fi: " + f_i);
		}
	}

}
