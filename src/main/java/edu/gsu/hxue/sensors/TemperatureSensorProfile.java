package edu.gsu.hxue.sensors;

import java.awt.*;

public class TemperatureSensorProfile implements Cloneable
{
	public Point location;
	public double radius; // sensor detecting range, [m]
	
	public Object clone() throws CloneNotSupportedException
	{
		TemperatureSensorProfile c = (TemperatureSensorProfile) super.clone();
		c.location = (Point) location.clone();
		return c;
	}

}
