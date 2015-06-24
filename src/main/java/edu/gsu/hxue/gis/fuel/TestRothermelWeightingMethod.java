package edu.gsu.hxue.gis.fuel;

public class TestRothermelWeightingMethod
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//OLD1
		/*StandardCustomizedFuel f = StandardCustomizedFuelEnum.OLD1.getFuel();
		f.printRothermelWeightingMethodInfo();*/
		
		/*Mandel 2011:  fuel loading for each fuel =  0.166, 0.897, 1.076, 2.468, 0.785, 1.345, 1.09231, 1.121, 0.780, 2.694, 2.582, 7.749, 13.024*/
		double mandelFuelLoading[] = { 0.166, 0.897, 1.076, 2.468, 0.785, 1.345, 1.09231, 1.121, 0.780, 2.694, 2.582, 7.749, 13.024};
		
		System.out.println("rothermel weighted fuel loading");
		System.out.println(StandardCustomizedFuelEnum.OLD1.getFuel().rothermelNetFuelLoading() - mandelFuelLoading[0]);
		System.out.println(StandardCustomizedFuelEnum.OLD2.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[1]);
		System.out.println(StandardCustomizedFuelEnum.OLD3.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[2]);
		System.out.println(StandardCustomizedFuelEnum.OLD4.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[3]);
		System.out.println(StandardCustomizedFuelEnum.OLD5.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[4]);
		System.out.println(StandardCustomizedFuelEnum.OLD6.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[5]);
		System.out.println(StandardCustomizedFuelEnum.OLD7.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[6]);
		System.out.println(StandardCustomizedFuelEnum.OLD8.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[7]);
		System.out.println(StandardCustomizedFuelEnum.OLD9.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[8]);
		System.out.println(StandardCustomizedFuelEnum.OLD10.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[9]);
		System.out.println(StandardCustomizedFuelEnum.OLD11.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[10]);
		System.out.println(StandardCustomizedFuelEnum.OLD12.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[11]);
		System.out.println(StandardCustomizedFuelEnum.OLD13.getFuel().rothermelNetFuelLoading()- mandelFuelLoading[12]);
		
		System.out.println("average fuel loading");
		System.out.println(StandardCustomizedFuelEnum.OLD1.getFuel().averageNetFuelLoading()- mandelFuelLoading[0]);
		System.out.println(StandardCustomizedFuelEnum.OLD2.getFuel().averageNetFuelLoading()- mandelFuelLoading[1]);
		System.out.println(StandardCustomizedFuelEnum.OLD3.getFuel().averageNetFuelLoading()- mandelFuelLoading[2]);
		System.out.println(StandardCustomizedFuelEnum.OLD4.getFuel().averageNetFuelLoading()- mandelFuelLoading[3]);
		System.out.println(StandardCustomizedFuelEnum.OLD5.getFuel().averageNetFuelLoading()- mandelFuelLoading[4]);
		System.out.println(StandardCustomizedFuelEnum.OLD6.getFuel().averageNetFuelLoading()- mandelFuelLoading[5]);
		System.out.println(StandardCustomizedFuelEnum.OLD7.getFuel().averageNetFuelLoading()- mandelFuelLoading[6]);
		System.out.println(StandardCustomizedFuelEnum.OLD8.getFuel().averageNetFuelLoading()- mandelFuelLoading[7]);
		System.out.println(StandardCustomizedFuelEnum.OLD9.getFuel().averageNetFuelLoading()- mandelFuelLoading[8]);
		System.out.println(StandardCustomizedFuelEnum.OLD10.getFuel().averageNetFuelLoading()- mandelFuelLoading[9]);
		System.out.println(StandardCustomizedFuelEnum.OLD11.getFuel().averageNetFuelLoading()- mandelFuelLoading[10]);
		System.out.println(StandardCustomizedFuelEnum.OLD12.getFuel().averageNetFuelLoading()- mandelFuelLoading[11]);
		System.out.println(StandardCustomizedFuelEnum.OLD13.getFuel().averageNetFuelLoading()- mandelFuelLoading[12]);
		
		System.out.println("total fuel loading");
		System.out.println(StandardCustomizedFuelEnum.OLD1.getFuel().totalNetFuelLoading()- mandelFuelLoading[0]);
		System.out.println(StandardCustomizedFuelEnum.OLD2.getFuel().totalNetFuelLoading()- mandelFuelLoading[1]);
		System.out.println(StandardCustomizedFuelEnum.OLD3.getFuel().totalNetFuelLoading()- mandelFuelLoading[2]);
		System.out.println(StandardCustomizedFuelEnum.OLD4.getFuel().totalNetFuelLoading()- mandelFuelLoading[3]);
		System.out.println(StandardCustomizedFuelEnum.OLD5.getFuel().totalNetFuelLoading()- mandelFuelLoading[4]);
		System.out.println(StandardCustomizedFuelEnum.OLD6.getFuel().totalNetFuelLoading()- mandelFuelLoading[5]);
		System.out.println(StandardCustomizedFuelEnum.OLD7.getFuel().totalNetFuelLoading()- mandelFuelLoading[6]);
		System.out.println(StandardCustomizedFuelEnum.OLD8.getFuel().totalNetFuelLoading()- mandelFuelLoading[7]);
		System.out.println(StandardCustomizedFuelEnum.OLD9.getFuel().totalNetFuelLoading()- mandelFuelLoading[8]);
		System.out.println(StandardCustomizedFuelEnum.OLD10.getFuel().totalNetFuelLoading()- mandelFuelLoading[9]);
		System.out.println(StandardCustomizedFuelEnum.OLD11.getFuel().totalNetFuelLoading()- mandelFuelLoading[10]);
		System.out.println(StandardCustomizedFuelEnum.OLD12.getFuel().totalNetFuelLoading()- mandelFuelLoading[11]);
		System.out.println(StandardCustomizedFuelEnum.OLD13.getFuel().totalNetFuelLoading()- mandelFuelLoading[12]);
		
		System.out.println("======heat content===============");
		System.out.println(StandardCustomizedFuelEnum.OLD1.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD2.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD3.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD4.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD5.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD6.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD7.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD8.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD9.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD11.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD12.getFuel().rothermelLowHeatContent());
		System.out.println(StandardCustomizedFuelEnum.OLD13.getFuel().rothermelLowHeatContent());
		
		System.out.println("======fuel moisture===============");
		System.out.println(StandardCustomizedFuelEnum.OLD1.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD2.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD3.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD4.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD5.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD6.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD7.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD8.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD9.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD11.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD12.getFuel().rothermelFuelMoisture());
		System.out.println(StandardCustomizedFuelEnum.OLD13.getFuel().rothermelFuelMoisture());
	}

}
