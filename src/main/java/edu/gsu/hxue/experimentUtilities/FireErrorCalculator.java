package edu.gsu.hxue.experimentUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FireErrorCalculator
{
    
	public FireError calculateFireError( boolean[][] sim, boolean[][] real, int xDim, int yDim)
	{
		FireError e = new FireError();
		for( int i=0; i<xDim; i++)
			for( int j=0; j<yDim; j++)
			{
				if(sim[i][j]==true && real[i][j]==true)
					e.correctPositives++;
				else if(sim[i][j]==false && real[i][j]==false)
					e.correctNegatives++;
				else if(sim[i][j]==true && real[i][j]==false)
					e.falsePositives++;
				else if(sim[i][j]==false && real[i][j]==true)
					e.falseNegatives++;
			}
		
		e.probFalseDetection = (double)e.falsePositives/(e.falsePositives+e.correctNegatives);
		e.criticalSuccessIndex = (double)e.correctPositives/(e.correctPositives + e.falseNegatives + e.falsePositives);
		
		int N = xDim*yDim;
		System.out.println("N: "+N);
		double E = (1.0/N)*
				(
						(e.correctPositives+e.falseNegatives)*(e.correctPositives+e.falsePositives)
						+(e.correctNegatives+e.falseNegatives)*(e.correctNegatives+e.falsePositives)
				);
		
		System.out.println("CN: "+e.correctNegatives);
		System.out.println("FN: "+e.falseNegatives);
		System.out.println("FP: "+e.falsePositives);
		System.out.println("A: "+(e.correctPositives+e.falseNegatives)*(e.correctPositives+e.falsePositives));
		System.out.println("B: "+(e.correctNegatives+e.falseNegatives)*(e.correctNegatives+e.falsePositives));
	
		System.out.println("A+B: " + 
		(
				(e.correctPositives+e.falseNegatives)*(e.correctPositives+e.falsePositives)+(e.correctNegatives+e.falseNegatives)*(e.correctNegatives+e.falsePositives)
		)
		);
		
		System.out.println("CN+FN: " + (e.correctNegatives+e.falseNegatives));
		System.out.println("CN+FP: " + (e.correctNegatives+e.falsePositives));
		System.out.println(270738 *251621.0 );
		System.out.println("E: " + E);
		e.heidkeSkillScore = (double)((e.correctPositives+e.correctNegatives)-E)/(N-E);
		System.out.println("HSS: " + e.heidkeSkillScore);
		return e;
		
	}
	
	public FireError calculatorShell( String realFireFile, String heatFolder, int heatStepLength, int heatStepNumber, int xDim, int yDim)
	{
		boolean[][] sim = this.simFireReader(heatFolder, heatStepLength, heatStepNumber, xDim, yDim);
		boolean[][] real = this.realFireReader(realFireFile, xDim, yDim);
		return this.calculateFireError(sim, real, xDim, yDim);
	}
	
	public static boolean[][] simFireReader(String heatFolder, int heatStepLength, int heatStepNumber, int xDim, int yDim)
	{
		boolean[][] sim = new boolean[xDim][yDim];

		for (int i = 0; i < xDim; i++)
			for (int j = 0; j < yDim; j++)
			{
				sim[i][j] = false;
			}
		
		try
		{
			Scanner s;
			// simulated fire
			for (int i = 0; i <= heatStepNumber; i++)
			{
				File f = new File(heatFolder + Integer.toString(i * heatStepLength) + ".txt");
				System.out.println(f.getName());
				s = new Scanner(f);
				for (int j = 0; j < 7; j++)
				{
					s.nextLine();
				}

				while (s.hasNextLine())
				{
					if (!s.hasNextInt())
						break;
					int x = s.nextInt();
					if (!s.hasNextInt())
						break;
					int y = s.nextInt();
					for (int j = 0; j < 4; j++)
						s.next();
					sim[x][y] = true;
				}
				s.close();
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return sim;

	}
	
	private boolean[][] realFireReader( String realFireFile, int xDim, int yDim)
	{
		boolean[][] real = new boolean[xDim][yDim];
		for (int i = 0; i < xDim; i++)
			for (int j = 0; j < yDim; j++)
			{
				real[i][j] = false;
			}

		// real fire
		try
		{
			Scanner s = new Scanner(new File(realFireFile));
			while (s.hasNextInt())
			{
				int x = s.nextInt();
				if (!s.hasNextInt())
					break;
				int y = s.nextInt();
				real[x][y] = true;
			}
			s.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		return real;
	}
	
	public static void main(String[] args)
	{
		String realFireFile = "Day5_B.txt";
		String heatFolder = "C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/HeatmapFiles_corrig_T0/";
		//String heatFolder = "HeatmapFiles/";
		int heatStepLength = 60;
		int heatStepNumber = 1440;
		int xDim = 547;
		//int yDim = 545;
		int yDim = 349;
		FireErrorCalculator c = new FireErrorCalculator();
		System.out.println(c.calculatorShell(realFireFile, heatFolder, heatStepLength, heatStepNumber, xDim, yDim));
		
	/*	boolean[][] real = c.realFireReader(realFireFile, xDim, yDim);
		System.out.println(c.calculateFireError(real, real, xDim, yDim));*/
		
	}
	
	class FireError
	{
		double correctPositives=0;
		double correctNegatives=0;
		double falsePositives=0;
		double falseNegatives=0;
		
		double probFalseDetection=0;
		double criticalSuccessIndex=0;
		double heidkeSkillScore=0;
		
		public String toString()
		{
			String title = "CP\tCN\tFP\tFN\tPOFD\tCSI\tHSS\n";
			String value = correctPositives + "\t" +correctNegatives+ "\t" +falsePositives+ "\t" +falseNegatives+ "\t" +probFalseDetection+ "\t" +criticalSuccessIndex + "\t" +heidkeSkillScore+"\n";
			return title + value;
		}
	}

}
