package edu.gsu.hxue.gis.fuel;

import java.awt.*;
import java.io.Serializable;

/**
 * This enumeration enumerates all the customized fuel types in the customized fuel model, it is from the paper (Joe H Scott, 2005).
 * @author Haydon
 *
 */
public enum StandardCustomizedFuelEnum implements Serializable
{
	
	//Old fuel types, those fuels are not it (Joe H Scott, 2005), and they are used before it
	OLD1("nffl1.prp", 7, Color.getHSBColor(100, 245, 255) , 928), // short grass
	OLD2("nffl2.prp", 7, Color.getHSBColor(200, 230, 255), 928), // timber (grass and understory)
	OLD3("nffl3.prp", 7, Color.pink, 928), //tall grass
	OLD4("nffl4.prp", 180, Color.getHSBColor(200, 220, 255), 790), // chaparral
	OLD5("nffl5.prp", 100, Color.getHSBColor(215, 245, 250), 812), // brush
	OLD6("nffl6.prp", 100, Color.pink, 812), // dormant brush, hardwood slash
	OLD7("nffl7.prp", 100, Color.getHSBColor(200, 240, 250), 812), // southern rough
	OLD8("nffl8.prp", 900, Color.getHSBColor(0.35f, 1f, .7f), 735), // closed timber litter
	OLD9("nffl9.prp", 900, Color.getHSBColor(200, 220, 245), 735), // hardwood litter
	OLD10("nffl10.prp", 900, Color.pink, 735), // timber (litter + understory)
	OLD11( "nffl11.prp", 900, Color.getHSBColor(0.11f, 1f, 0.8f), 735), // light logging slash
	OLD12("nffl12.prp", 900, Color.getHSBColor(0.3f, 1f, 0.9f), 735), // medium logging slash
	OLD13("nffl13.prp", 900, Color.getHSBColor(0.3f, 1f, 1f), 735), // heavy logging slash
	
	//Customized fuel
	GR1("cfm101.prp", 7, Color.getHSBColor(0.3f, 1f, .7f), 928), 
	//GR2("cfm102_new.prp", 7, Color.darkGray),// 
	GR2("nffl2.prp", 7, Color.getHSBColor(0.3f, 1f, .7f), 928), // since it does not spread, use OLD2 as it currently. Haidong, 20130213
	GR3("cfm103.prp", 7, Color.getHSBColor(0.33f, 1f, .7f), 928), 
	GR4( "cfm104.prp", 7, Color.getHSBColor(0.36f, 1f, .7f), 928), 
	GR5("cfm105.prp", 7, Color.getHSBColor(0.38f, 1f, .7f), 928), 
	GR6("cfm106.prp", 7, Color.getHSBColor(0.4f, 1f, .7f), 928), 
	GR7("cfm107.prp", 7, Color.getHSBColor(0.4f, 1f, .7f), 928), 
	GR8("cfm108.prp", 7, Color.getHSBColor(0.43f, 1f, .7f), 928), 
	GR9("cfm109.prp", 7, Color.getHSBColor(0.45f, 1f, .7f), 928),   
	
	GS1("cfm121.prp", 7, Color.getHSBColor(0.12f, 0.7f, .7f), 928), 
	GS2("cfm122.prp", 7, Color.getHSBColor(0.12f, 0.7f, .7f), 928), 
	GS3("cfm123.prp", 7, Color.getHSBColor(0.12f, 0.8f, .7f), 928), 
	GS4("cfm124.prp", 7, Color.getHSBColor(0.12f, 0.9f, .7f), 928), 
	
	SH1("cfm141.prp", 900, Color.getHSBColor(0.4f, 1f, 0.7f), 735),
	
	//NB1("cfm_NB.prp", 9999, Color.white),
	NB1("nffl2.prp", 7, Color.white, 928),  // treat roads as grass
	//NB2("cfm_NB.prp", 9999, Color.white),
	NB2("nffl2.prp", 7, Color.white, 928),  // treat roads as grass
	NB3("cfm_NB.prp", 9999, Color.getHSBColor(0.35f, 0.5f, 0.7f), 928),
	NB8("cfm_NB.prp", 9999, Color.getHSBColor(0.4f, 0.5f, 0.7f), 928),
	NB9("cfm_NB.prp", 9999, Color.getHSBColor(0.1f, 0.5f, 0.7f), 928),
	
	// The fuel for fireflux experiment
	FIREFLUX ("fireflux.prp", 7, Color.green, 928);
	
	//The following fuels are in (Joe H Scott, 2005), but currently not implemented
	/*
	SH2(""), 
	SH3(""), 
	SH4(""), 
	SH5(""), 
	SH6(""), 
	SH7(""), 
	SH8(""), 
	SH9(""), 
	
	TU1(""), 
	TU2(""), 
	TU3(""), 
	TU4(""), 
	TU5(""), 
	
	TL1(""), 
	TL2(""), 
	TL3(""), 
	TL4(""), 
	TL5(""), 
	TL6(""), 
	TL7(""), 
	TL8(""), 
	TL9(""), 
	
	SB1(""), 
	SB2(""), 
	SB3(""), 
	SB4("");*/
	
	private StandardCustomizedFuel fuel;
	private String fuelFileName;
	private double weightingFactor;
	private Color color;
	private double ignitionTemperature; // in K
	
	public double getIgnitionTemperature()
	{
		return ignitionTemperature;
	}

	private final static String folder = "resources/data_files/FuelData/";
	
	StandardCustomizedFuelEnum(String prpFilePath, double weightingFactor, Color c, double ignitionTemperature)
	{
		this.fuelFileName = prpFilePath;
		this.weightingFactor = weightingFactor;
		this.color = c;
		this.ignitionTemperature = ignitionTemperature;
		fuel = new StandardCustomizedFuel(folder + fuelFileName);
	}
		
	public static StandardCustomizedFuelEnum modelIndexToFuelType( int modelIndex )
	{
		switch(modelIndex)
		{
		case 1:
			return OLD1;
		case 2:
			return OLD2;
		case 3:
			return OLD3;
		case 4:
			return OLD4;
		case 5:
			return OLD5;
		case 6:
			return OLD6;
		case 7:
			return OLD7;
		case 8:
			return OLD8;
		case 9:
			return OLD9;
		case 10:
			return OLD10;
		case 11:
			return OLD11;
		case 12:
			return OLD12;
		case 13:
			return OLD13;
		case 101:
			return GR1;
		case 102:
			return GR2;
		case 103:
			return GR3;
		case 104:
			return GR4;
		case 105:
			return GR5;
		case 106:
			return GR6;
		case 107:
			return GR7;
		case 108:
			return GR8;
		case 109:
			return GR9;
		case 121:
			return GS1;
		case 122:
			return GS2;
		case 123:
			return GS3;
		case 124:
			return GS4;
		case 141:
			return SH1;
		case 91:   
			return NB1;
		case 92:  
			return NB2;
		case 93:   
			return NB3;
		case 98:   
			return NB8;
		case 99:   
			return NB9;
		case -9999:  // the boards are considered as NB1
			return NB1;
		case 142:  // currently use SH1 as SH2
			return SH1;
		case 503:
			return FIREFLUX;
		default:
			{
				System.err.println("Wrong fuel type: " + modelIndex);
				return OLD1;
			}
		}
		
/*		System.err.println("Wrong fuel model id detected: " + modelIndex);
		System.exit(1);
		
		return null;*/
	}

	public StandardCustomizedFuel getFuel()
	{
		return this.fuel;
	}
	
	public Color getColor()
	{
		if(this.color==null) return Color.getHSBColor(200, 240, 250);
		return this.color;
	}
	
	public double getWeightingFactor()
	{
		return this.weightingFactor;
	}
}
