package edu.gsu.hxue.desFire;

import java.io.Serializable;

public class FireSystemConfig implements Serializable {
    private static final long serialVersionUID = 5717093465904455325L;
    public boolean showPresentationLayer = true;
    public double presentationLayerWakeUpInterval = 600;
    public double displayScale = 1;
    public boolean useSystemMonitor = false;
    public boolean useHeatWriter = false;
    public boolean useNewHeatModelForInitialIgnitionCells = true;

    //PF paper experiment
    public String gisFileFolder = "resources/data_files/GISData/";
    public String otherFileFolder = "resources/data_files/OtherData/";
    public String heatOutputFolder = "resources/data_files/HeatFiles/";
  
 /*   public String slopeFileName = "GIS_slope_15.txt";//"MooreBranch_slope.txt";
    public String aspectFileName =  "GIS_aspect_15.txt";//"MooreBranch_aspect.txt";
	public String fuelFileName = "GIS_fuel_15.txt";//"MooreBranch_fuel.txt";

	
	public String initialWeatherFileName = "weather_artificial_unchangedWind.txt";//"weather_Feng_real.txt"; //"weather_artificial_unchangedWind.txt"; //"weather_artificial_MB.txt"
	public String initialIgnitionFile = "IgnitionPoints_SMC.txt";//"IgnitionPoints_MB_corrected.txt";//"IgnitionPoints_MB.txt";
	public String laterIgnitionFile = "IgnitionPoints_SMC_later.txt";
	public String initialContainedCells = "InitialContainedCells_empty.txt";//"InitialContainedCells_MB.txt";
	public double heatUpdateInterval = 1800;*/


    // Symmetry Test
	/*String folder = "C:/Users/haydon/SkyDrive/ResearchCloud/ExperimentResults/Fire_Atmosphere_coupled simulation/SymmetryTest/DESFire2_sym_settings/";
	
	public String GISFileFolder = folder + "GISData/";
    public String slopeFileName = "slope200_60.0.txt";//"slope200.txt";
	public String aspectFileName = "aspect200_60.0.txt"; //"aspect200.txt";
	public String fuelFileName = "fuel200_60.0.txt";//"fuel200.txt";
    public String initialWeatherFileName = "weather_artificial_unchangedWind.txt"; //"weather_artificial_MB.txt"
	
	public String ignitionFile = folder + "IgnitionPoints_Sym.txt";
	
	public String initialContainedCells = folder + "InitialContainedCells_Empty.txt";
	
	public double heatUpdateInterval = 60;
*/
    // Moore Branch fire
/*	public String GISFileFolder = "GISData/";
    public String slopeFileName = "MooreBranch_slope.txt";
	public String aspectFileName =  "MooreBranch_aspect.txt";
	public String fuelFileName = "MooreBranch_fuel.txt";

	public String initialWeatherFileName = "weather_artificial_unchangedWind.txt"; //"weather_artificial_MB.txt"
	
	public String ignitionFile = "IgnitionPoints_MB_corrected_C.txt";
	
	public String initialContainedCells = "InitialContainedCells_MB_D.txt";//"InitialContainedCells_MB.txt";
	
	public double heatUpdateInterval = 60;*/

    //Rock House
    //public String GISFileFolder = "GISData/";
    public String slopeFileName = "RockHouse_slope.txt";
    public String aspectFileName = "RockHouse_aspect.txt";
    public String fuelFileName = "RockHouse_fuel.txt";
    public String initialWeatherFileName = "weather0409_rockHouse_12hours.txt"; //"weather_artificial_MB.txt"
    public String initialIgnitionFile = "IgnitionPoints_RH_2.txt";
    public String laterIgnitionFile;// = "IgnitionPoints_later_empty.txt";
    public String initialContainedCells = "InitialContainedCells_Empty.txt";//"InitialContainedCells_MB.txt";
    public double heatUpdateInterval = 600;


}
