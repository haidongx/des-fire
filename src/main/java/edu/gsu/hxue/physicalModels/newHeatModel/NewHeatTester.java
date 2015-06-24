package edu.gsu.hxue.physicalModels.newHeatModel;

import edu.gsu.hxue.desFire.FireCell.IgnitionDirectionTypeEnum;
import edu.gsu.hxue.desFire.FireCell.IgnitionTypeEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class NewHeatTester
{

	public static void main(String[] args)
	{
		Scanner s;
		try
		{
			NewHeatModel heatM = new NewHeatModel();
			
			s = new Scanner(new File(args[0]));
			PrintWriter pw = new PrintWriter("output.txt");
			pw.println(String.format("IgnitionType\t DirectionType\t IgnitionTime\t Time\t SpreadRate1\t SpreadRate2\t weightingFactor\t cellSize\t Fraction"));
			
			while(s.hasNextLine())
			{	
				String type;
				if(s.hasNext()) type = s.next(); else break;
				
				IgnitionTypeEnum ignitionType=null;
				if(type.trim().equalsIgnoreCase("byCell"))
					ignitionType = IgnitionTypeEnum.BY_CELL;
				else if(type.trim().equalsIgnoreCase("byIgniter"))
					ignitionType = IgnitionTypeEnum.BY_IGNITER;
				
				String direction;
				if(s.hasNext()) direction = s.next(); else break;
				IgnitionDirectionTypeEnum directionType=null;
				if(direction.trim().equalsIgnoreCase("straight"))
					directionType = IgnitionDirectionTypeEnum.N;
				else if(direction.trim().equalsIgnoreCase("diagonal"))
					directionType = IgnitionDirectionTypeEnum.NW;
				
				double t_ig;
				if(s.hasNextDouble()) t_ig = s.nextDouble(); else break;
				
				double t;
				if(s.hasNextDouble()) t = s.nextDouble(); else break;
				
				double spreadRate1;
				if(s.hasNextDouble()) spreadRate1 = s.nextDouble(); else break;
				
				double spreadRate2;
				if(s.hasNextDouble()) spreadRate2 = s.nextDouble(); else break;
				
				double weightingFactor;
				if(s.hasNextDouble()) weightingFactor = s.nextDouble(); else break;
				
				double cellSize;
				if(s.hasNextDouble()) cellSize = s.nextDouble(); else break;
				
				double fraction = heatM.fuelFraction(ignitionType, directionType, weightingFactor, t_ig, t, spreadRate1, spreadRate2, cellSize);
				
				pw.println(String.format("%s\t %s\t %4.4f\t %4.4f\t %4.2f\t %4.2f\t %4.2f\t %4.1f\t %f", type, direction, t_ig, t, spreadRate1, spreadRate2, weightingFactor, cellSize, fraction));
				
			}
			
			pw.close();
			s.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NotSupportedHeatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
