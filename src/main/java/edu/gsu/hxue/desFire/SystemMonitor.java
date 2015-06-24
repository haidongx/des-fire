package edu.gsu.hxue.desFire;

import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;

import java.io.*;

public class SystemMonitor extends Model implements Cloneable
{
	private static final long serialVersionUID = 1223892963289503648L;
	transient private PrintWriter pw;
	
	@Override
	public Object clone()
	{
		SystemMonitor c = (SystemMonitor)super.clone();
		
		try
		{
			String sysName = this.getSystem().getName();
			c.pw =  new PrintWriter( new File(sysName +"_DESFireResults.txt") );
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return c;
	}
	
	public SystemMonitor(DESSystem system)
	{
		super(system);
		
		try
		{
			String sysName = this.getSystem().getName();
			pw = new PrintWriter( new File( sysName + "DESFireResultsFrom" + this.system.getSystemTime() + ".txt") );
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return "I am System Monitor";
	}

	@Override
	public void internalTransit(double delta_time)
	{
	}

	@Override
	public void externalTransit(Message message)
	{
		//System.out.println("Monitor received a message: ");
		if(message instanceof FireCell.MessageCellToMonitor)
		{
			FireCell.MessageCellToMonitor m = (FireCell.MessageCellToMonitor)message;
			//System.out.println("Monitor recerived a message from " + m.x +"-"+m.y);
			//System.out.println(m.x + "\t" + m.y + "\t" + m.time);
			pw.println(m.x + "\t" + m.y + "\t" + m.time);
			pw.flush();
		}
	}

	@Override
	public void generateOutput()
	{

	}

	@Override
	public double getNextInternalTransitionTime()
	{
		return Double.POSITIVE_INFINITY;
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
	    out.defaultWriteObject(); 
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// default de-serialization
		in.defaultReadObject();
		// re-create PrintWriter
		pw = new PrintWriter(new File("DESFireResultsFrom" + Math.round(this.getTime())+ "s.txt"));

	}
}
