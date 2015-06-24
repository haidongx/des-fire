package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SymmetryTestPresenter
{
	private final int X_DIM = 200;
	private final int Y_DIM = 200;

	private void drawIgnitedCells(String folder, int stepLength, int stepNumber, CellularAutomataPresentation p) throws FileNotFoundException
	{
		for (int i = 0; i <= stepNumber; i++)
		{
			File f = new File(folder + Integer.toString(i * stepLength) + ".txt");
			System.out.println(f.getName());
			Scanner s = new Scanner(f);
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
				p.setCellColor(x, y, Color.black);
			}
			p.drawDirtyCellsInBuffer();
			p.showBufferOnScreen();
			s.close();
		}
	}
	
	public void showResults()
	{
		try
		{
			int[] speeds = {3, 10, 20};
			double scaler = 4;//4;//1.5;
			
			for( int i : speeds)
			{
				
					CellularAutomataPresentation p = new CellularAutomataPresentation(this.X_DIM, this.Y_DIM, scaler);
					p.setTitle("1way(black) 2way(red) ---  " + i + "m/s" + " 3600s");
					
				{//p.setTitle("One-way ---  " + i + "m/s" + " 3600s");
					String folder = "C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_"+i+"ms_1way_20121215/";
					//drawIgnitedCells(folder, 60, 60, p);
					ExpUtilities.drawIgnitedCellsOutlineFromHeatFiles(folder, 60, 60, p, this.X_DIM, this.Y_DIM, Color.black);
				}

				/*{
					CellularAutomataPresentation p = new CellularAutomataPresentation(this.X_DIM, this.Y_DIM, scaler);
					p.setTitle("One-way ---  " + i + "m/s" + " 7200s");
					String folder = "C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_"+i+"ms_1way_20121215/";
					drawIgnitedCells(folder, 60, 120, p);
				}*/
				
				{
					//CellularAutomataPresentation p = new CellularAutomataPresentation(this.X_DIM, this.Y_DIM, scaler);
					//p.setTitle("Two-way ---  " + i + "m/s" + " 3600s");
					String folder = "C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_"+i+"ms_coupled_20121215/";
					//drawIgnitedCells(folder, 60, 60, p);
					ExpUtilities.drawIgnitedCellsOutlineFromHeatFiles(folder, 60, 60, p, this.X_DIM, this.Y_DIM, Color.red);
				}

			/*	{
					CellularAutomataPresentation p = new CellularAutomataPresentation(this.X_DIM, this.Y_DIM, scaler);
					p.setTitle("Two-way ---  " + i + "m/s" + " 7200s");
					String folder = "C:/Users/Haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/HeatmapFiles_"+i+"ms_coupled_20121215/";
					drawIgnitedCells(folder, 60, 120, p);
				}*/
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		SymmetryTestPresenter t = new SymmetryTestPresenter();
		t.showResults();

	}

}
