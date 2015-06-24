package edu.gsu.hxue.experimentUtilities;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

public class Day5IgnitionGenerator
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// read in day4
		String day4 = "Day4_B.txt";
		Vector<Point> day4Cells = new Vector<Point>();
		Scanner s;
		try
		{
			s = new Scanner( new File(day4) );
			while(s.hasNextInt())
			{
				int x = s.nextInt();
				if(!s.hasNextInt()) break;
				int y = s.nextInt();
					
				day4Cells.add(new Point(x, y));	
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		// read in day5
		String day5 = "Day5_B.txt";
		Vector<Point> day5Cells = new Vector<Point>();
		try
		{
			s = new Scanner( new File(day5) );
			while(s.hasNextInt())
			{
				int x = s.nextInt();
				if(!s.hasNextInt()) break;
				int y = s.nextInt();
					
				day5Cells.add(new Point(x, y));	
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		// generate day 5 ignition
		try
		{
			PrintWriter pw = new PrintWriter( new File("CalculatedMBDay5IgnitionPoints.txt") );
			int counter = 0;
			for(Point p5 : day5Cells )
			{
				for(Point p4 : day4Cells)
				{
					if( Math.abs(p4.x - p5.x) <= 1 && Math.abs(p4.y - p5.y) <= 1 )
					{
						pw.println(p5.x + "\t" + p5.y);
						System.out.println(p5);
					}
							
				}
				counter++;
				System.out.println( (double)counter / day5Cells.size());
			}
			
			pw.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		

	}

}
