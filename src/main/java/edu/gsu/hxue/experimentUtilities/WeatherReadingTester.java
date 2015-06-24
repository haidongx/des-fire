package edu.gsu.hxue.experimentUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WeatherReadingTester
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		read("weather_symdebug.txt");

	}
	
	private static void read( String weatherFilePath)
	{
		// Read in weather schedules
				File weatherFile = new File(weatherFilePath);
				System.out.print("Reading weather data from " + weatherFile);
				try
				{
					// Add new schedules
					Scanner s = new Scanner(weatherFile);
					// skip the first two lines
					s.nextLine();
					s.nextLine();
					int indicateCount = 0;
					int recordCount = 0;
					while (s.hasNextLine() && s.hasNextInt())
					{
						// update weather
						int x = s.nextInt() - 1;
						int y = s.nextInt() - 1;

						// skip LATITUDE LONGITUDE TEMPC T_STD RELH RH_STD
						for (int i = 0; i < 6; i++)
							s.next();

						// wind speed
						double speed = s.nextDouble();

						// skip WS_STD
						s.next();

						// wind direction
						double direction = s.nextDouble();

						// skip WD_STD
						s.next();

						// add schedule to wind model
						System.out.println(speed + " | " + direction + " | at " + x +"-" + y);

						indicateCount++;
						recordCount++;
						if (indicateCount >= 5000)
						{
							System.out.print(".");
							indicateCount = 0;
						}
					}
					s.close();
					System.out.println("Number of cell weather read in: " + recordCount);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
	}

}
