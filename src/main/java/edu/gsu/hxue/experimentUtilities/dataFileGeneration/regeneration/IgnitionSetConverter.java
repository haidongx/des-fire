package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Cell;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class IgnitionSetConverter
{

	public static void convertFile(File dataFile, String newDataFilePath, CoordinateConverter con) 
	{
		try
		{
			//old cell set
			List<Cell> oldCells = new ArrayList<Cell>();
			Scanner s = new Scanner(dataFile);
			while(s.hasNextLine())
			{
				int x, y;
				if(s.hasNextInt()) x=s.nextInt();
				else break;
				
				if(s.hasNextInt()) y=s.nextInt();
				else break;
				
				oldCells.add(new Cell(x, y));
			}
			s.close();
			
			// build a value map
			double[][] oldValueMap = new double[con.getOldXDim()][con.getOldYDim()];
			for(int i=0; i<con.getOldXDim(); i++)
				for(int j=0; j<con.getOldYDim(); j++)
					oldValueMap[i][j]=0;
			for( Cell c: oldCells)
				oldValueMap[c.getX()][c.getY()]=1;
			
			//new cell set
			List<CellWithValue> tempNewCells = new ArrayList<CellWithValue>();
			for( int x=0; x<con.getNewXDim(); x++)
				for(int y=0; y<con.getNewYDim(); y++)
				{
					List<CellWithPortion> re = con.calculateCoveredOldCells(x, y);
					double weightedValue = 0;
					double totalPortion = 0;
					
					//int c=0;
					for( CellWithPortion cp : re)
					{
						int oldX = cp.getCell().getX();
						int oldY = cp.getCell().getY();
					
						if(oldX>=0 && oldX<con.getOldXDim() && oldY>=0 && oldY<con.getOldYDim() )
						{
							// calculate the weighted value
							weightedValue += oldValueMap[oldX][oldY] * cp.getPortion();
							totalPortion += cp.getPortion();
							
						}
					}
					
					weightedValue /= totalPortion;
					if(weightedValue>0 ) tempNewCells.add(new CellWithValue(new Cell(x, y), weightedValue));
					
					//if(c>0)
					//System.out.println(x+"-"+y+" contains " + c + " " + isContainingWholeCell);
				}
			
			//remove some new cells
			boolean[][] newValueMap = new boolean[con.getNewXDim()][con.getNewYDim()];
			for(int i=0; i<con.getNewXDim(); i++)
				for(int j=0; j<con.getNewYDim(); j++)
					newValueMap[i][j]=false;
			
			for( CellWithValue c: tempNewCells)
				newValueMap[c.getCell().getX()][c.getCell().getY()]=true;
			
			double oldArea = oldCells.size() * con.getOldCellSize() * con.getOldCellSize();
			double newArea = tempNewCells.size() * con.getNewCellSize() * con.getNewCellSize();
			int cellsNeededToRemove = (int)((newArea - oldArea) / (con.getNewCellSize() * con.getNewCellSize()));
			
			Collections.sort(tempNewCells);
	
			int cRemoved=0;
			for( CellWithValue c: tempNewCells)
			{
				NeighborStatus ns = findNeighborStatus(c, newValueMap);
				
				//System.out.println(c.getCell().getX() +"-"+c.getCell().getY() + ": " + ns.getNumber() + " " + ns.isConnected() );
				if( ns.getNumber() >=2 && ns.isConnected() )
					{
						newValueMap[c.getCell().getX()][c.getCell().getY()]=false;
						cRemoved++;
						
						if(cRemoved>cellsNeededToRemove) break;
					}
			}
			
			
			// write the new cells
			PrintWriter pw = new PrintWriter( new File(newDataFilePath));
			for( int x=0; x<con.getNewXDim(); x++)
				for(int y=0; y<con.getNewYDim(); y++)
				{
					if(newValueMap[x][y]) pw.println(x + "\t" + y);
				}
			pw.close();
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static NeighborStatus findNeighborStatus(CellWithValue c, boolean[][] newValueMap)
	{
		int neightborNumber=0;
		boolean previousGap = false;
		
		int gapNumber=0;
		{
			int x = c.getCell().getX()-1;
			int y= c.getCell().getY()+1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()+0;
			int y= c.getCell().getY()+1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()+1;
			int y= c.getCell().getY()+1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()+1;
			int y= c.getCell().getY()+0;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()+1;
			int y= c.getCell().getY()-1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()+0;
			int y= c.getCell().getY()-1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()-1;
			int y= c.getCell().getY()-1;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}
		{
			int x = c.getCell().getX()-1;
			int y= c.getCell().getY()+0;
			
			if( x>=0 && x<newValueMap.length && y>=0 && y<newValueMap[x].length && newValueMap[x][y])
			{
				neightborNumber++;
				previousGap = false;
			}
			else
			{
				if(!previousGap) gapNumber++;
				previousGap=true;
			}
		}

		
		boolean connected = true;
		if( gapNumber>=2) connected=false;
		 return new NeighborStatus(neightborNumber, connected);
		
	}

	private static class NeighborStatus
	{
		private int number;
		public NeighborStatus(int number, boolean connected)
		{
			super();
			this.number = number;
			this.connected = connected;
		}
		private boolean connected;
		public int getNumber()
		{
			return number;
		}
		public boolean isConnected()
		{
			return connected;
		}
	}
	
	private static class CellWithValue implements Comparable<CellWithValue>
	{
		private Cell cell;
		public CellWithValue(Cell cell, double value)
		{
			super();
			this.cell = cell;
			this.value = value;
		}
		public Cell getCell()
		{
			return cell;
		}
		public double getValue()
		{
			return value;
		}
		private double value;
		@Override
		public int compareTo(CellWithValue o)
		{
			if( value - o.getValue() <0 ) return -1;
			if(value - o.getValue() >0) return 1;
			return 0;
		}
	}

}
