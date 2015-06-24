package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * This wind model provide dynamic wind speed and direction, but at the same time, the whole space has the same wind
 * @author Haydon
 *
 */
public class SimpleWindModel extends Model
{
	private static final long serialVersionUID = 2824769106570717209L;
	//Physical info
	PriorityQueue<WindSchedule> windSchedules;
	private double windModelUpdateTimeStamp=0;
	private double windModelWindSpeed=0;
	private double windModelWindDirection=0;
	private double observorTimeStamp=-1;
	
	//Update for ignited cells
	HashSet<Integer> observorCellIndex = new HashSet<Integer>();
	
	//Update for a cell (often it is just ignied)
	boolean isNeededToUpdateACell = false;
	int targetCellModelIndex = -1;
	
	public static class WindSchedule implements Comparable<WindSchedule>
	{
		double time;
		double direction;
		double speed;
		
		public double getTime() { return time; }
		public double getDirection() {return direction; }
		public double getSpeed() { return speed; }
		
		public WindSchedule( double time, double speed, double direction)
		{
			this.time = time;
			this.direction = direction;
			this.speed = speed;
		}
		
		public void printInfo()
		{
			System.out.println(time + "\t" + speed + "\t" + direction);
		}

		
		@Override
		public int compareTo(WindSchedule w)
		{
			return Double.compare(time, w.time);
		}
	}
	
	public void addObservorCell ( FireCell cell )
	{
		int i = cell.getModelIndex();
		this.observorCellIndex.add(i);
	}
	
	public SimpleWindModel(DESSystem system, String filePath)
	{
		super(system);
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
				WindSchedule ws = new WindSchedule(3600*hour + 60*min, speed * 0.44704, dir); // speed is converted from mph to m/s
				windSchedules.add(ws);
				//ws.printInfo();
			}
			s.close();
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
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
		if(!windSchedules.isEmpty() && Math.abs(windSchedules.peek().time-getTime())<GlobalConstants.DOUBLE_COMPARISON_TOLERANCE)
		{
			WindSchedule w = windSchedules.poll();
			this.windModelUpdateTimeStamp = getTime();
			this.windModelWindSpeed = w.speed;
			this.windModelWindDirection = w.direction;
		}
	}

	@Override
	public void externalTransit(Message message)
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
		if(this.windModelUpdateTimeStamp > this.observorTimeStamp )
		{
			for( int cellIndex : this.observorCellIndex )
			{
				this.sendMessage(cellIndex, new MessageFromWindModelToCell(this.windModelWindSpeed, this.windModelWindDirection));
				this.observorTimeStamp = this.windModelUpdateTimeStamp;
			}
		}
		
		// For a single cell
		if(this.isNeededToUpdateACell)
		{
			this.sendMessage(this.targetCellModelIndex, new MessageFromWindModelToCell(this.windModelWindSpeed, this.windModelWindDirection));
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
