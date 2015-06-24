package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.FireCell.IgnitionTypeEnum;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Vector;

public class Igniter extends Model implements Cloneable
{
	private static final long serialVersionUID = -6899860047533778321L;
	private int spaceWidth;
	private int spaceHeight;	
	
	protected Vector<Point> ignitions = new Vector<Point>();
	PriorityQueue<IgnitionSchedule> ignitionSchedules = new PriorityQueue<IgnitionSchedule>(); // need a deep copy  

	public Object clone()
	{
		Igniter c = null;
		try
		{
			c = (Igniter) super.clone();
		
			c.ignitions = new Vector<Point>();
			for (Point p : ignitions)
				c.ignitions.add(new Point(p.x, p.y));

			c.ignitionSchedules = new PriorityQueue<IgnitionSchedule>();
			for (IgnitionSchedule s : ignitionSchedules)
				c.ignitionSchedules.add((IgnitionSchedule) s.clone());
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return c;
	}
	
	public Igniter(DESSystem system, String ignitionFile, int xDim, int yDim )
	{
		super(system);
		
		this.spaceHeight = yDim;
		this.spaceWidth = xDim;
		  
		try
		{
			Scanner s = new Scanner(new File(ignitionFile));
			while(s.hasNextLine() && s.hasNextInt())
			{
				int x = s.nextInt();
				int y = s.nextInt();
				this.ignitions.add(new Point(x, y));
			}
			s.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public Igniter(DESSystem system, String initialIgnitionFile, String scheduledIgnitionFile, int xDim, int yDim )
	{
		this(system, initialIgnitionFile, xDim, yDim);
		try
		{
			Scanner s = new Scanner(new File(scheduledIgnitionFile));
			while(s.hasNextLine() && s.hasNextDouble())
			{
				double time = s.nextDouble();
				int x = s.nextInt();
				int y = s.nextInt();
				this.addSchedule(new IgnitionSchedule(time, x, y) );
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

		for( Point p: this.ignitions)
		{
			this.sendMessage(((FireSystem)getSystem()).getFireCellInex(p.x, p.y), new FireCell.MessageAmongFireCells(FireCell.MessageAmongFireCells.MessageAmongCellsEnum.IGNITION, null, IgnitionTypeEnum.BY_IGNITER));	
		}
		
		this.ignitions.clear();

		if(!ignitionSchedules.isEmpty()  && Math.abs(ignitionSchedules.peek().time-getTime())<GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			IgnitionSchedule w = ignitionSchedules.poll();
			this.sendMessage(((FireSystem)getSystem()).getFireCellInex(w.cellX, w.cellY), new MessageToFireCells(MessageToFireCells.MessageToFireCellsEnum.IGNITION));
		}
	}

	public void addSchedule( IgnitionSchedule schedule )
	{
		if(schedule.cellX>=0 && schedule.cellX < this.spaceWidth && schedule.cellY>=0 && schedule.cellY<this.spaceHeight)
		{
			ignitionSchedules.add(schedule);
		}
	}
	
	@Override
	public double getNextInternalTransitionTime()
	{
		
		if (ignitionSchedules.isEmpty())
			return Double.POSITIVE_INFINITY;

		while (!ignitionSchedules.isEmpty() && ignitionSchedules.peek().time < getTime())
			ignitionSchedules.poll();

		if (ignitionSchedules.isEmpty())
			return Double.POSITIVE_INFINITY;

		return (ignitionSchedules.peek().time - getTime());
	}
	
	public static class IgnitionSchedule implements Comparable<IgnitionSchedule>, Serializable, Cloneable
	{
		private static final long serialVersionUID = -1206872414642086435L;
		double time;
		int cellX;
		int cellY;
		
		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}
		
		public double getTime() { return time; }
		
		public IgnitionSchedule( double time, int cellX, int cellY)
		{
			this.time = time;
			this.cellX = cellX;
			this.cellY = cellY;
		}
		
		@Override
		public int compareTo(IgnitionSchedule is)
		{
			return Double.compare(time, is.time);
		}

	}
	
	public static class MessageToFireCells extends Message
	{	
		private static final long serialVersionUID = 6865444899813016435L;
		public MessageToFireCellsEnum type;

		public MessageToFireCells (MessageToFireCellsEnum type)
		{
			this.type = type;	
		}
		
		public enum MessageToFireCellsEnum implements Serializable
		{
			IGNITION
		}
	}

}
