package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.experimentUtilities.ExpUtilities.FlameTemperatureRelatedInfo;

import java.util.List;

public class PrintFlameTemperatureRelatedInfo
{
	public static void main(String[] args)
	{
		List<FlameTemperatureRelatedInfo> list = ExpUtilities.getFuelInfoAboutFlameTemperature();

		System.out.println(
				"fuel" 
				+ "\t" + "(1-B)/Pe Pe is from eq(14) times bulk density" /* the value in Eqn 14 is the ratio of the effective bulk density to the actual bulk density, so the value you used for Pe before needs to be multiplied by the bulk density of the fuel*/ 
				+ "\t" + "MineralDampingCoefficient"
				+ "\t" + "PercentMositureContent_dead1"
				+ "\t" + "PercentMositureContent_dead2"
				+ "\t" + "PercentMositureContent_dead3"
				+ "\t" + "PercentMositureContent_live1"
				+ "\t" + "PercentMositureContent_live2"
				+ "\t" + "M_Avg=sum(Li*Mi/sum(Li))    ---- Note: Li is OVENDRY fuel loading"
				+ "\t" + "flameLength (0 wind and 0 slope)"
				+ "\t" + "weightingFactor (w)");
			
		for(FlameTemperatureRelatedInfo f : list)
		{	
			System.out.println(f.getFuelName() + "\t" + f.getOneMinusBOverPe() + "\t" + f.getMineralDampingCoefficient()
					+ "\t" + f.getPercentMositureContent_dead1()
					+ "\t" + f.getPercentMositureContent_dead2()
					+ "\t" + f.getPercentMositureContent_dead3()
					+ "\t" + f.getPercentMositureContent_live1()
					+ "\t" + f.getPercentMositureContent_live2()
					+ "\t" + f.getAvgPercentMoisture()
					+ "\t" + f.getFlameLength()
					+ "\t" + f.getWeightingFactor());
		}
	}

}
