package edu.gsu.hxue.physicalModels.rothermel;

import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;

public class Test
{
	public static void main(String[] args)
	{
		RothermelModel r = new RothermelModelImp20120322();
		
		double winds[] = {0, 5, 10, 15, 20, 25, 30, 35, 40};  // mi/h
		
		System.out.println("OLD1");
		System.out.println("M: " + StandardCustomizedFuelEnum.OLD1.getFuel().rothermelFuelMoisture());
		
		for(double w : winds)
		{
			r.setInputs(StandardCustomizedFuelEnum.OLD1.getFuel(), w*1600/3600, 33, 0, 206);
			double ros  = r.getSpreadRateOnMaxDirection();
			System.out.println(w + " mi/h"+ "\t" + ros*3600/20.1168 + " ch/h"); 
		}
		
		
		System.out.println("GR2");
		System.out.println("M: " + StandardCustomizedFuelEnum.GR2.getFuel().rothermelFuelMoisture());
		for(double w : winds)
		{
			r.setInputs(StandardCustomizedFuelEnum.GR2.getFuel(), w*1600/3600, 33, 0, 206);
			double ros  = r.getSpreadRateOnMaxDirection();
			System.out.println(w + " mi/h"+ "\t" + ros*3600/20.1168 + " ch/h"); 
		}
		
		System.out.println();
	}

}
