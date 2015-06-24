package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

/**
 * 
 * @author Haydon
 *
 */
public class Suppressor extends Model implements Cloneable
{
	
	private static final long serialVersionUID = -4525351597401693560L;
	protected Vector<Point> containedCells = new Vector<Point>();
	protected int cellIndex[][];

	public Suppressor(DESSystem system, String containedCellFile, int xDim, int yDim )
	{
		super(system);
		
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

	/**
	 * Set a receiver cell's model index
	 * @param x
	 * @param y
	 * @param index
	 */
	public void setCellIndex ( int x, int y, int index )
	{
		this.cellIndex[x][y]=index;
	}

	@Override
	public void internalTransit(double delta_time)
	{
	}

	@Override
	public void externalTransit(Message message)
	{	
		
	}

	@Override
	public void generateOutput()
	{
		for( Point p: this.containedCells)
		{
			this.sendMessage(cellIndex[p.x][p.y], new MessageFromSuppressorToCells());
		}
	}

	@Override
	public double getNextInternalTransitionTime()
	{
		return Double.POSITIVE_INFINITY;
	}
	
	public static class MessageFromSuppressorToCells extends Message
	{
		private static final long serialVersionUID = 5282242850124775450L;
	}
}
