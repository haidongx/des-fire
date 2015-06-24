package edu.gsu.hxue.physicalModels.rothermel;

/**
 * This class calculates the maximum fire spread direction, rate and related parameters. 
 * It currently uses the code from Andreas Bachmann as the core, since the code from Andreas Bachmann has already been verified. 
 * 
 * Note: this implementation only accepts standard fuel.
 * 
 * @author Haidong Xue
 * @version 9/19/2011
 *
 */

import edu.gsu.hxue.gis.fuel.ComplexFuel;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuel;
import edu.gsu.hxue.physicalModels.rothermel.andreasBachman.Behave;

public class RothermelModelImp20120322 implements RothermelModel
{
	private static final long serialVersionUID = 3451539939908805852L;
	//inputs
	private StandardCustomizedFuel fuel;  //this implementation only accepts standard fuel
	private double windSpeed; // at mid-flame height
	private double windDirection;
	private double slope;
	private double slopeDirection;
	
	//outputs
	private double maxSpreadRate;
	private double maxSpreadDirection;
	private double windSpeedOnMaxSpreadDirection;
	private double reactionIntensity;
	private double firelineIntensity;
	
	// intermediate
	public double packingRatio;
	public double effectiveEffectiveHeatingNumber;
	public double mineralDampingCoefficient;
	public double liveMoistureContent;
	public double deadMoistureContent;
	private double bulkDensity;
	private double flameLength;
	
	
	
	
	
	/**
	 * Get the effective wind speed on the max spread direction. It is a output of Rothermel model.
	 * @return the effective wind speed on the max spread direction, [m/s]
	 */
	@Override
	public double getEfficientWindSpeedOnMaxDirection()
	{
		return windSpeedOnMaxSpreadDirection;
	}

	/**
	 * Get the reaction intensity. It is a output of Rothermel model.
	 * @return the reaction intensity, [kJ/(m2*s)]
	 */
	@Override
	public double getReactionIntensity()
	{
		return reactionIntensity;
	}
	
	/**
	 * Get the max spread direction. It is a output of Rothermel model.
	 * @return the wind direction on which fire spread the fastest, [degree]
	 */
	@Override
	public double getMaxSpreadDirection()
	{
		return maxSpreadDirection;
	}
	
	/**
	 * Get the spread rate on the max spread direction. It is a output of Rothermel model. 
	 * @return the spread rate on the max spread direction, [m/s]
	 */
	@Override
	public double getSpreadRateOnMaxDirection()
	{
		return maxSpreadRate;
	}
	
	public void updateOutputs()
	{
		Behave andiRothermel = new Behave();
		
		//Set ovendry fuel loadings
		andiRothermel.setParameterMean("w0_d1", this.fuel.getFuel(0, 0).ovendryFuelLoading());
		andiRothermel.setParameterMean("w0_d2", this.fuel.getFuel(0, 1).ovendryFuelLoading());
		andiRothermel.setParameterMean("w0_d3", this.fuel.getFuel(0, 2).ovendryFuelLoading());
		andiRothermel.setParameterMean("w0_lh", this.fuel.getFuel(1, 0).ovendryFuelLoading());
		andiRothermel.setParameterMean("w0_lw", this.fuel.getFuel(1, 1).ovendryFuelLoading());
		
		//Set surface-area-volume ratios
		andiRothermel.setParameterMean("sv_d1", this.fuel.getFuel(0, 0).sav());
		andiRothermel.setParameterMean("sv_d2", this.fuel.getFuel(0, 1).sav());
		andiRothermel.setParameterMean("sv_d3", this.fuel.getFuel(0, 2).sav());
		andiRothermel.setParameterMean("sv_lh", this.fuel.getFuel(1, 0).sav());
		andiRothermel.setParameterMean("sv_lw", this.fuel.getFuel(1, 1).sav());
		
		//Set moistures
		andiRothermel.setParameterMean("m_d1", this.fuel.getFuel(0, 0).fuelMoistureContent());
		andiRothermel.setParameterMean("m_d2", this.fuel.getFuel(0, 1).fuelMoistureContent());
		andiRothermel.setParameterMean("m_d3", this.fuel.getFuel(0, 2).fuelMoistureContent());
		andiRothermel.setParameterMean("m_lh", this.fuel.getFuel(1, 0).fuelMoistureContent());
		andiRothermel.setParameterMean("m_lw", this.fuel.getFuel(1, 1).fuelMoistureContent());
		
		//Set fuel mean depth 
		andiRothermel.setParameterMean("depth",this.fuel.meanFuelDepth() );
		
		//Set particle density, note: in Andi's Rothermel implementation, all fuel share the same particle density
		andiRothermel.setParameterMean("rho_p",this.fuel.getFuel(0, 0).ovendayParticleDensity() );
		
		//Set low heat content, note: in Andi's Rothermel implementation, all fuel share the same heat content
		andiRothermel.setParameterMean("heat",this.fuel.getFuel(0, 0).lowHeatContent());
		
		//Set total mineral content, note: in Andi's Rothermel implementation, all fuel share the same total mineral content
		andiRothermel.setParameterMean("s_t",this.fuel.getFuel(0, 0).totalMineralContent() );
		
		//Set effective mineral content, note: in Andi's Rothermel implementation, all fuel share the same effective mineral content
		andiRothermel.setParameterMean("s_e",this.fuel.getFuel(0, 0).effectiveMineralContent() );

		//Set dead fuel moisture of extinction, note: in Andi's Rothermel implementation, all fuel share the same dead fuel moisture of extinction
		andiRothermel.setParameterMean("mx",this.fuel.getFuelCategory(0).meanExtinctionMoistureContent() );
		
		//Set wind and slope
		andiRothermel.setParameterMean("wsp", this.windSpeed );
		andiRothermel.setParameterMean("wdr", this.windDirection );
		andiRothermel.setParameterMean("slp", this.slope );
		andiRothermel.setParameterMean("asp", this.slopeDirection );
		
		//Update outputs using Andi's Rothermel core
		andiRothermel.calc();
		this.maxSpreadRate = andiRothermel.ros;
		this.maxSpreadDirection = andiRothermel.sdr;
		this.windSpeedOnMaxSpreadDirection = andiRothermel.efw;
		this.reactionIntensity = andiRothermel.I_r;
		this.firelineIntensity = andiRothermel.fli;
		
		this.packingRatio = andiRothermel.beta;

		// using equation(14)
		/*
		 * Effective heating number: epsilon = exp(-138 / sigma_ft) (14) = exp(-138 / (sigma_m * 0.3048)) conversion! = exp( -452.76 / sigma)
		 */
		this.effectiveEffectiveHeatingNumber = Math.exp(-452.76 / andiRothermel.sigma);
				
		this.mineralDampingCoefficient = andiRothermel.eta_s;
		
		this.bulkDensity = andiRothermel.rho_b;
		
		this.flameLength = andiRothermel.fln;
	}

	@Override
	public void setInputs(ComplexFuel fuel, double midFlameWindSpeed, double midFlameWindDirection, double slope, double upSlopeDirection)
	{
		this.fuel = (StandardCustomizedFuel)fuel;
		this.windSpeed = midFlameWindSpeed;
		this.windDirection = midFlameWindDirection;
		this.slope = slope;
		this.slopeDirection = upSlopeDirection;
		
		this.updateOutputs();
	}

	@Override
	public double getFirelineIntensity()
	{
		return this.firelineIntensity;
	}

	@Override
	public double getEffectiveHeatingNumber()
	{
		return this.effectiveEffectiveHeatingNumber;
	}

	@Override
	public double getMineralDampingCoefficient()
	{
		return this.mineralDampingCoefficient;
	}
	
	@Override
	public double getPackingRatio()
	{
		return this.packingRatio;
	}

	@Override
	public double getBulkDensity()
	{
		return this.bulkDensity;
	}

	@Override
	public double getFlameLength()
	{
		return this.flameLength;
	}
	
}
