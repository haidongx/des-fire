package edu.gsu.hxue.gis.fuel;

public class Test
{
	public static void main(String[] args)
	{
		StandardCustomizedFuel s = new StandardCustomizedFuel("C:/Users/Haydon/Documents/ResearchCloud/OnUsingDataSet/FuelData/" + "nffl7.prp");
		System.out.println(s);

		// Set ovendry fuel loadings
		System.out.println("w0_d1 " + s.getFuel(0, 0).ovendryFuelLoading());
		System.out.println("w0_d2 " + s.getFuel(0, 1).ovendryFuelLoading());
		System.out.println("w0_d3 " + s.getFuel(0, 2).ovendryFuelLoading());
		System.out.println("w0_lh " + s.getFuel(1, 0).ovendryFuelLoading());
		System.out.println("w0_lw " + s.getFuel(1, 1).ovendryFuelLoading());

		// Set surface-area-volume ratios
		System.out.println("sv_d1 " + s.getFuel(0, 0).sav());
		System.out.println("sv_d2 " + s.getFuel(0, 1).sav());
		System.out.println("sv_d3 " + s.getFuel(0, 2).sav());
		System.out.println("sv_lh " + s.getFuel(1, 0).sav());
		System.out.println("sv_lw " + s.getFuel(1, 1).sav());

		// Set moistures
		System.out.println("m_d1 " + s.getFuel(0, 0).fuelMoistureContent());
		System.out.println("m_d2 " + s.getFuel(0, 1).fuelMoistureContent());
		System.out.println("m_d3 " + s.getFuel(0, 2).fuelMoistureContent());
		System.out.println("m_lh " + s.getFuel(1, 0).fuelMoistureContent());
		System.out.println("m_lw " + s.getFuel(1, 1).fuelMoistureContent());

		// Set fuel mean depth
		System.out.println("depth " + s.meanFuelDepth());

		// Set particle density, note: in Andi's Rothermel implementation, all
		// fuel share the same particle density
		System.out.println("rho_p " + s.getFuel(0, 0).ovendayParticleDensity());

		// Set low heat content, note: in Andi's Rothermel implementation, all
		// fuel share the same heat content
		System.out.println("heat " + s.getFuel(0, 0).lowHeatContent());

		// Set total mineral content, note: in Andi's Rothermel implementation,
		// all fuel share the same total mineral content
		System.out.println("s_t " + s.getFuel(0, 0).totalMineralContent());

		// Set effective mineral content, note: in Andi's Rothermel
		// implementation, all fuel share the same effective mineral content
		System.out.println("s_e " + s.getFuel(0, 0).effectiveMineralContent());

		// Set dead fuel moisture of extinction, note: in Andi's Rothermel
		// implementation, all fuel share the same dead fuel moisture of
		// extinction
		System.out.println("mx " + s.getFuelCategory(0).meanExtinctionMoistureContent());
	  		
	}


}
