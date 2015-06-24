package edu.gsu.hxue.gis.fuel;

import java.io.Serializable;

/**
 * This class represents a pure fuel type of Rothermel model, describing physical features of a kind of fuel.   
 * 
 * @author Haydon
 * @version 9/18/2011
 *
 */
public class BasicFuel implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7229971076230743266L;
	private double ovendry_fuel_loading; //Ovendry fuel loading, [kg/m2].
	private double sav_ratio; //Fuel particel surface-area-to-volume ration, [m^2/(m^3)]. (In Rothermel model it is also named "sigma".)
	private double fuel_depth; //Fuel depth, [m]. (In Rothermel model it is also named "delta".)
	private double ovendry_particle_density; //Oven particle density, [kg/m3]. (In Rothermel model it is also named "rho_p".)
	private double low_heat_content; //Fuel particle low heat content, [kJ/kg]. (In Rothermel model it is also named "h".)
	private double total_mineral_content; //Fuel particle total mineral content, [kg minerals / kg ovendry wood ]. (In Rothermel model it is also named "S_T".)
	private double effective_mineral_content; //Fuel particle effective mineral content, [kg silica-free minerals/ kg, ovendry wood]. (In Rothermel model it is also named "S_e".)
	private double fuel_moisture_content; //fuel particle moisture content, [kg moisture/ kg ovendry wood][%]. (In Rothermel model it is also named "M_f".)
	private double extinction_moisture_content; //moisture content of extinction, [%]. (In Rothermel model it is also named "M_x".)
	
	public BasicFuel( double ovendry_fuel_loading, double sav_ratio, double fuel_depth, 
			double ovendry_particle_density, double low_heat_content, double total_mineral_content,
			double effective_mineral_content, double fuel_moisture_content, double extinction_moisture_content)
	{
		this.ovendry_fuel_loading = ovendry_fuel_loading;
		this.sav_ratio = sav_ratio;
		this.fuel_depth = fuel_depth;
		this.ovendry_particle_density = ovendry_particle_density;
		this.low_heat_content = low_heat_content;
		this.total_mineral_content = total_mineral_content;
		this.effective_mineral_content = effective_mineral_content;
		this.fuel_moisture_content = fuel_moisture_content;
		this.extinction_moisture_content = extinction_moisture_content;
	}
	
	/**
	 * total surface area according to Rothermel 1972
	 * @return
	 */
	public double meanTotalSurfaceArea()
	{
		return sav_ratio*ovendry_fuel_loading / ovendry_particle_density;
	}
	
	public double rothermelNetLoading()
	{
		double w_n_ij = this.ovendry_fuel_loading / (1 + this.total_mineral_content);
		
		return w_n_ij;
	}
	
	
	/**
	 * 
	 * @return Ovendry fuel loading, [kg/m2].
	 */
	public double ovendryFuelLoading()
	{
		return this.ovendry_fuel_loading;
	}
	
	/**
	 * 
	 * @return Fuel particele surface-area-to-volume ration, [1/m]
	 */
	public double sav()
	{
		return this.sav_ratio;
	}
	
	/**
	 * 
	 * @return Fuel depth, [m]
	 */
	public double fuelDepth()
	{
		return this.fuel_depth;
	}
	
	/**
	 * 
	 * @return Oven particle density, [kg/m3]
	 */
	public double ovendayParticleDensity()
	{
		return this.ovendry_particle_density;
	}
	
	/**
	 * 
	 * @return Fuel particle low heat content, [kJ/kg]
	 */
	public double lowHeatContent()
	{
		return this.low_heat_content;
	}
	
	/**
	 * 
	 * @return Fuel particle total mineral content, [kg minerals / kg ovendry wood ]
	 */
	public double totalMineralContent()
	{
		return this.total_mineral_content;
	}
	
	/**
	 * 
	 * @return Fuel particle effective mineral content, [kg silica-free minerals/ kg, ovendry wood]
	 */
	public double effectiveMineralContent()
	{
		return this.effective_mineral_content;
	}
	
	/**
	 * 
	 * @return fuel particle moisture content, [kg moisture/ kg ovendry wood]
	 */
	public double fuelMoistureContent()
	{
		return this.fuel_moisture_content;
	}
	
	/**
	 * 
	 * @return moisture content of extinction, [%]
	 */
	public double extinctionMoistureContent()
	{
		return this.extinction_moisture_content;
	}
	
	
	
	public String toString()
	{
		return
		"wo="+this.ovendry_fuel_loading +"\t"+
		"sv="+this.sav_ratio  +"\t"+
		"depth="+this.fuel_depth  +"\t"+
		"rho="+this.ovendry_particle_density  +"\t"+
		"h="+this.low_heat_content  +"\t"+
		"S_t="+this.total_mineral_content  +"\t"+
		"S_e="+this.effective_mineral_content  +"\t"+
		"M_f="+this.fuel_moisture_content  +"\t"+
		"M_x="+this.extinction_moisture_content;
	}

}
