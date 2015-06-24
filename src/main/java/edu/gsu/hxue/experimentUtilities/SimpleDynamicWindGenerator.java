package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.desFire.SimpleWindModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

public class SimpleDynamicWindGenerator
{
	public void randomWind( String filepath, double length, double interval, double minSpeed, double maxSpeed, double minDirection, double maxDirection )
	{
		//Make wind schedule
		Vector<SimpleWindModel.WindSchedule> winds = new Vector<SimpleWindModel.WindSchedule>();
		for( double time=0; time<length; time+=interval)
			winds.add( new SimpleWindModel.WindSchedule(time, Math.random()*(maxSpeed-minSpeed)+minSpeed, Math.random()*(maxDirection-minDirection)+minDirection));
		
		try
		{
			PrintWriter p = new PrintWriter (new File(filepath) );
			p.println( "ncols\t" + 5);
			p.println("nrows\t" + winds.size());
			p.println("Hour\tMin\tTemp\twspd\tDir");
			
			for( SimpleWindModel.WindSchedule w : winds)
			{
				int hour = (int)(w.getTime() / 3600);
				double min = (w.getTime()%3600)/60.0;
				p.println( hour + "\t" + min + "\t" + "0\t" + w.getSpeed() + "\t" + w.getDirection());
			}
			
			
			p.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		SimpleDynamicWindGenerator g = new SimpleDynamicWindGenerator();
		g.randomWind("test.txt", 3600*5, 10, 3, 5, 50, 250);
	}
	

}
