package edu.gsu.hxue.desHigherResolutionDESFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.FireCell;
import edu.gsu.hxue.desFire.FireCell.IgnitionTypeEnum;
import edu.gsu.hxue.desFire.FireSystem;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class ScalableIgniter extends Model
{
	protected Vector<Point> ignitions = new Vector<Point>();
	private int scaleFactor;

	public ScalableIgniter(DESSystem system, String ignitionFile, int xDim, int yDim, int scaleFactor )
	{
		super(system);
		
		int realXDim = xDim*scaleFactor;
		int realYDim = yDim*scaleFactor;
		
		this.scaleFactor = scaleFactor;
		
		
		try
		{
			System.out.println("Reading initial ignitions from " + ignitionFile);
			Scanner s = new Scanner(new File(ignitionFile));
			while(s.hasNextLine() && s.hasNextInt())
			{
				int x = s.nextInt();
				int y = s.nextInt();
				
				
				for( int scaledX=x*scaleFactor; scaledX<(x+1)*scaleFactor; scaledX++)
					for( int scaledY=y*scaleFactor; scaledY<(y+1)*scaleFactor; scaledY++)
					{
						this.ignitions.add(new Point(scaledX, scaledY));
						//System.out.println("loaded: " + scaledX + "-" +scaledY);
					}
			}
			s.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
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
		System.out.println("Ignitor igniting ");
		for( Point p: this.ignitions)
		{
			//System.out.println("Ignitor igniting " + p.x+"-"+p.y);
			int index  = ((FireSystem)getSystem()).getFireCellInex(p.x, p.y);
			if(index!=-1)
				this.sendMessage(index, new FireCell.MessageAmongFireCells(FireCell.MessageAmongFireCells.MessageAmongCellsEnum.IGNITION, null, IgnitionTypeEnum.BY_IGNITER));		
		}
	}

	@Override
	public double getNextInternalTransitionTime()
	{
		return Double.POSITIVE_INFINITY;
	}
	
}
