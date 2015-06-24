package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * This wind model provide dynamic wind speed and direction; different cells can have different wind at the same time
 * @author Haydon
 *
 */
public class ComplexWindModel extends Model implements Cloneable
{
	private static final long serialVersionUID = -2719920120486136650L;
	//Physical info
	PriorityQueue<WindSchedule> windSchedules;  
	private int spaceWidth; // unit: number of cells
	private int spaceHeight; //// unit: number of cells
	protected CellWindInfo[][] windSpace; 
	
	//Update for ignited cells
	protected HashSet<Integer> observorCellIndex = new HashSet<Integer>(); 
	
	//Update for a cell (often it is just ignited)
	protected boolean isNeededToUpdateACell = false;
	protected int targetCellModelIndex = -1;
	
	//Is there a new wind
	protected boolean newWind = false;
	
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		ComplexWindModel c = null;
		try
		{
			c = (ComplexWindModel) super.clone();
			
			c.windSchedules = new PriorityQueue<WindSchedule>();
			for (WindSchedule w : windSchedules)
				c.windSchedules.add((WindSchedule) w.clone());
			
			c.windSpace = new CellWindInfo[spaceWidth][spaceHeight];
			for( int x = 0; x< spaceWidth; x++)
				for(int y=0; y<spaceHeight; y++)
					c.windSpace[x][y] = (CellWindInfo) windSpace[x][y].clone();
			
			c.observorCellIndex= (HashSet<Integer>) observorCellIndex.clone(); 
			
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return c;
	}
	
	public void clearSchedule()
	{
		windSchedules.clear();
	}
	
	public void invalidateWeather()
	{
		for( CellWindInfo[] infos :windSpace )
			for(CellWindInfo info : infos )
			{
				info.lastUpdateTimeStamp = -1;
				info.observorTimeStamp = -1;
			}
	}
	
	public void addSchedule( WindSchedule schedule )
	{
		if(schedule.cellX==341 && schedule.cellY==1078) System.err.println(String.format("A wind schedule is added as time=%.2f speed=%.2.f dir=%d", schedule.time, schedule.speed, schedule.direction));
		if(schedule.cellX>=0 && schedule.cellX < this.spaceWidth && schedule.cellY>=0 && schedule.cellY<this.spaceHeight)
		{
			windSchedules.add(schedule);
			//schedule.printInfo();
		}
	}
	
	public static class WindSchedule implements Comparable<WindSchedule>, Serializable, Cloneable
	{
		private static final long serialVersionUID = 802787120305951812L;
		double time;
		double direction;
		double speed;
		int cellX;
		int cellY;
		
		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}
		
		public double getTime() { return time; }
		public double getDirection() {return direction; }
		public double getSpeed() { return speed; }
		
		public WindSchedule( double time, double speed, double direction, int cellX, int cellY)
		{
			this.time = time;
			this.direction = direction;
			this.speed = speed;
			this.cellX = cellX;
			this.cellY = cellY;
		}
		
		public void printInfo()
		{
			System.out.println(time + "\t" + speed + "\t" + direction + "\t " + cellX + "-" + cellY );
		}


		@Override
		public int compareTo(WindSchedule w)
		{
			return Double.compare(time, w.time);
		}
	}

	public static class CellWindInfo implements Serializable, Cloneable
	{
		private static final long serialVersionUID = 4418113891653596762L;
		private double lastUpdateTimeStamp=-1;
		private double observorTimeStamp=-1;
		public double currentWindSpeed=0; // the default value
		public double currentWindDirection=0; // the default value
		//private int cellIndex = -1;
		
		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}
	}
	
	public void addObservorCell ( FireCell cell )
	{
		//int i = this.system.getModelIndex(cell);
		int i = cell.getModelIndex();
		this.observorCellIndex.add(i);
	}
	
	public ComplexWindModel(DESSystem system, String filePath, int spaceWidth, int spaceHeight)
	{
		super(system);
		
		//Initialize wind space
		this.spaceHeight = spaceHeight;
		this.spaceWidth = spaceWidth;
		
		windSpace = new CellWindInfo[spaceWidth][spaceHeight];
		for( int x = 0; x< spaceWidth; x++)
			for(int y=0; y<spaceHeight; y++)
				windSpace[x][y] = new CellWindInfo();
		
		
		//Initialize schedules
		try
		{
			Scanner s = new Scanner ( new File(filePath) );
			s.nextLine();
			s.next();
			int n = s.nextInt();
			windSchedules = new PriorityQueue<WindSchedule>();
			s.nextLine();
			s.nextLine();
			for(int i=0; i<n; i++)
			{
				double hour = s.nextDouble();
				double min = s.nextDouble();
				s.next();
				double speed = s.nextDouble();
				double dir = s.nextDouble();
				for (int x = 0; x < this.spaceWidth; x++)
					for (int y = 0; y < this.spaceHeight; y++)
					{
						//When speed is in mph,  0.44704 should be multiplied with it
						WindSchedule ws = new WindSchedule(3600 * hour + 60 * min, speed, dir, x, y); // speed is converted from mph to m/s
						windSchedules.add(ws);
						//ws.printInfo();
						/*if(x==100 && y==100) ws.printInfo();*/
					}
			}
			s.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public static void main(String[] args)
	{
	/*	String foler = "C:/Users/Haydon/Documents/ResearchCloud/OnUsingDataSet/GISData/";
		TestWindModelSystem s = new TestWindModelSystem();
		ComplexWindModel w = new ComplexWindModel(s, foler + "weather_artificial_DEVSFIRE.txt", 200, 200);
		System.out.println("construction done");
		
		System.out.println("OK1");
		s.run(3600*2);
		System.out.println("OK2");*/
		
	}

	public void printInfo()
	{
		for( WindSchedule w : this.windSchedules )
			w.printInfo();
	}
	
	@Override
	public void internalTransit(double delta_time)
	{
		//Update weather when needed according to weather schedule
		while(!windSchedules.isEmpty() && Math.abs(windSchedules.peek().time-getTime())<GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			WindSchedule w = windSchedules.poll();
			newWind = true;
			/*if(w.cellX<110 && w.cellX>100 && w.cellY<110 && w.cellY>100) w.printInfo();*/
			
			//if(w.cellX==100 && w.cellY==100) w.printInfo();
			
			CellWindInfo windInfo = this.windSpace[w.cellX][w.cellY];
			windInfo.lastUpdateTimeStamp = getTime();
			windInfo.currentWindSpeed = w.speed;
			windInfo.currentWindDirection = w.direction;
		}
	}


	@Override
	public void externalTransit(Message message) throws Exception
	{
		
		if( message instanceof FireCell.MessageToWindModel)
		{
			// A cell needs its weather to be updated
			FireCell.MessageToWindModel m = (FireCell.MessageToWindModel)message;
			if( m.type == FireCell.MessageToWindModel.MessageToWindModelEnum.UPDATE)
			{
				this.isNeededToUpdateACell = true;
				this.targetCellModelIndex = m.cellIndexRequiringWeather;
			}
		
			// A cell asks for future update
			else if( m.type == FireCell.MessageToWindModel.MessageToWindModelEnum.SUBSCRIBE)
			{
				//update this cell in later steps
				this.observorCellIndex.add(m.cellIndexRequiringWeather);
			}
			
			// A cell asks for unsubscribing future update
			else if (m.type == FireCell.MessageToWindModel.MessageToWindModelEnum.UNSUBSCRIBE)
			{
				this.observorCellIndex.remove(m.cellIndexRequiringWeather);
			}
		}
	}

	@Override
	public void generateOutput()
	{
		// For ignited cells
		if(newWind)
		{
			for (int cellIndex : this.observorCellIndex)
			{
				FireCell f = (FireCell) system.getModel(cellIndex);
				int cellX = f.getX();
				int cellY = f.getY();
				CellWindInfo windInfo = this.windSpace[cellX][cellY];
				
				
					
				if (windInfo.lastUpdateTimeStamp > windInfo.observorTimeStamp)
				{
				
					//if(cellX ==341 && cellY==1078) System.err.println("As new wind, weather sent to " + cellX + "-" + cellY + " at " + this.getTime() + " " + windInfo.currentWindSpeed + " " + windInfo.currentWindDirection);
					
					this.sendMessage(cellIndex, new MessageFromWindModelToCell(windInfo.currentWindSpeed, windInfo.currentWindDirection));
					windInfo.observorTimeStamp = windInfo.lastUpdateTimeStamp;
				}
			}
			newWind = false;
		}
		
		// For a single cell
		if(this.isNeededToUpdateACell)
		{
			FireCell f = (FireCell) system.getModel(targetCellModelIndex);
			int cellX = f.getX();
			int cellY = f.getY();
			CellWindInfo windInfo = this.windSpace[cellX][cellY];
			
			//if(cellX ==341 && cellY==1078) System.err.println("As single cell, weather sent to " + cellX + "-" + cellY + "at" + this.getTime() + " " + windInfo.currentWindSpeed + " " + windInfo.currentWindDirection);
			
			this.sendMessage(targetCellModelIndex, new MessageFromWindModelToCell(windInfo.currentWindSpeed, windInfo.currentWindDirection));
			//System.out.println("Reply " + cellX + "-" + cellY + ": " + windInfo.currentWindSpeed + " " + windInfo.currentWindDirection);
			windInfo.observorTimeStamp = windInfo.lastUpdateTimeStamp;
			this.isNeededToUpdateACell = false;
		}
	}

	@Override
	public double getNextInternalTransitionTime()
	{
		//System.out.println(windSchedules.size());
		if(windSchedules.isEmpty()) return Double.MAX_VALUE;
		
		while(!windSchedules.isEmpty() && windSchedules.peek().time<getTime())
			windSchedules.poll();
		
		if(windSchedules.isEmpty()) return Double.MAX_VALUE;
		
		return (windSchedules.peek().time-getTime());
	}

	public static class MessageFromWindModelToCell extends Message
	{
		private static final long serialVersionUID = -7252973379628866766L;
		public double windSpeed;
		public double windDirection;
		 
		public MessageFromWindModelToCell( double speed, double direction )
		{
			windSpeed = speed;
			windDirection = direction;
		}
	}
}
