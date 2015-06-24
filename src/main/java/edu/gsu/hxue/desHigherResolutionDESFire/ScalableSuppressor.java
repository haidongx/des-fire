package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.Suppressor;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScalableSuppressor extends Suppressor
{	
	private int scaleFactor;	
	private int xDim; //the x dimension after being scaled
	private int yDim; //the y dimension after being scaled

	public ScalableSuppressor(DESSystem system, String containedCellFile, int origXDim, int origYDim, int scaleFactor )
	{
		super(system, containedCellFile, origXDim, origYDim );
		
		this.scaleFactor = scaleFactor;
		
		
		this.xDim = origXDim * scaleFactor;
		this.yDim = origYDim * scaleFactor;
		
		cellIndex = new int[xDim][yDim];
		
		for( int i=0; i<xDim; i++)
			for(int j=0; j<yDim; j++)
				cellIndex[i][j] = -1;
		
		try
		{
			Scanner s = new Scanner(new File(containedCellFile));
			while(s.hasNextLine() && s.hasNextInt())
			{
				int x = s.nextInt();
				int y = s.nextInt();
				this.containedCells.add(new Point(x, y));
			}
			s.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void generateOutput()
	{
		for( Point p: this.containedCells)
		{
			// suppress all the scaled cells
			for( int x=p.x*this.scaleFactor; x<(p.x+1)*this.scaleFactor; x++)
				for( int y=p.y*this.scaleFactor; y<(p.y+1)*this.scaleFactor; y++)
				{
					this.sendMessage(cellIndex[x][y], new MessageFromSuppressorToCells());
				}
		}
	}
}
