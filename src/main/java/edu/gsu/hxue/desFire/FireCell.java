package edu.gsu.hxue.desFire;


import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.exceptions.InvalidLogicException;
import edu.gsu.hxue.desFire.exceptions.NotSupportedFunctionException;
import edu.gsu.hxue.physicalModels.mandel2011Heat.Mandel2011HeatModel;
import edu.gsu.hxue.physicalModels.mandel2011Heat.Mandel2011HeatModelImp20120521;
import edu.gsu.hxue.physicalModels.newHeatModel.NewHeatModel;
import edu.gsu.hxue.physicalModels.newHeatModel.NotSupportedHeatException;
import edu.gsu.hxue.physicalModels.rothermel.RothermelModel;
import edu.gsu.hxue.physicalModels.rothermel.RothermelModelImp20120322;

import java.io.Serializable;

/**
 * This class defines the behavior a fire cell.
 *
 * @author Haidong Xue
 */
public class FireCell extends Model implements Cloneable, Serializable {

    private static final long serialVersionUID = 6627195992268123245L;
    //State
    // cell size
    private double xCellSize;
    private double yCellSize;

    public double getxCellSize() {
        return xCellSize;
    }

    public double getyCellSize() {
        return yCellSize;
    }

    // Spread behave related data
    private static final int DIRECTION_NUMBER = 8;// number of directions
    private static final int N = 0; // north
    private static final int NE = 1; // north-west
    private static final int E = 2; // west
    private static final int SE = 3; // south-west
    private static final int S = 4; // south
    private static final int SW = 5; // south-east
    private static final int W = 6; // east
    private static final int NW = 7; // north-east

    // Physical data
    private StandardCustomizedFuelEnum fuelType; // A standard customized fuel type
    private double slope; // The angel between the surface to the horizontal on the up-slope direction
    private double downSlopeDirection; // The down-slope direction

    private double[] distanceRemain = new double[DIRECTION_NUMBER]; // the distance left on all the directions, [m]
    private boolean[] messageSentOnDirection = new boolean[DIRECTION_NUMBER]; // indicates if it is finished, [true/false]
    private double reactionIntensity; // the fire intensity of this cell
    private double[] spreadRates = new double[DIRECTION_NUMBER]; // the horizontal spread rate on all directions, [m/s]
    private double firelineIntensity = 0;
    private double[] spreadRatesAtIgnition; // used by the new heat model only

    private static final double THRESHOLD_FI = GlobalConstants.SPREAD_THRESHOLD;//10; // a threshold, when the reaction intensity of a cell higher than this, the cell is ignited
    private static final double DELTA = 1e-6;

    // Temperatures
    private double backGroundFuelTemperature = GlobalConstants.DEFAULT_FUEL_BACKGROUD_TEMPERATURE;

    public double getBackGroundFuelTemperature() {
        return backGroundFuelTemperature;
    }

    public void setBackGroundFuelTemperature(double backGroundFuelTemperature) {
        this.backGroundFuelTemperature = backGroundFuelTemperature;
    }

    // Physical models
    RothermelModel rothermel = new RothermelModelImp20120322();
    private Mandel2011HeatModel mandelHeat = new Mandel2011HeatModelImp20120521();
    private NewHeatModel newHeat = new NewHeatModel();

    // Weather
    protected double weatherUpdatingTime = -1;
    protected double windSpeed; // Current 20-meter high wind speed, [m/s]

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        //if(x==341 && y==1078) System.err.println(String.format("Wind of (%d, %d) changed from %.2f to %.2f at %.2f", x, y, this.windSpeed, windSpeed, this.getTime()));
        this.windSpeed = windSpeed;
    }

    protected double windDirection; // Current horizontal wind direction [degree] (North = 0 degree)

    // Coordinate
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Observer index
    private int[] neighboringIndex = new int[DIRECTION_NUMBER];
    private int monitorIndice = -1;
    protected int windModelIndice = -1;
    private int heatWriterIndice = -1;

    // Coupling states
    private boolean ignitionTimeReportedToSystemMonitor = false;
    private boolean subscribedToDynamicWeather = false;
    private boolean subscribedToHeatWriter = false;

    // Additional States
    protected boolean igniting = false;
    protected IgnitionDirectionTypeEnum ignitionDirectionType;
    protected IgnitionTypeEnum ignitionType;
    protected boolean contained = false;
    private double ignitionTime = Double.POSITIVE_INFINITY;

    private boolean isUsingNewHeatModel = true;

    public boolean isUseNewHeatModelForInitialIgnitonCells() {
        return isUsingNewHeatModel;
    }

    public void setUseNewHeatModelForInitialIgnitonCells(boolean useNewHeatModelForInitialIgnitonCells) {
        this.isUsingNewHeatModel = useNewHeatModelForInitialIgnitonCells;
    }

    /**
     * Fire cell constructor
     *
     * @param system
     * @param xcoord
     * @param ycoord
     * @param windSpeed
     * @param windDirection
     * @param slope
     * @param upSlopeDirection
     * @param fuelType
     * @param xCellSize
     * @param yCellSize
     */
    public FireCell(
            DESSystem system,
            int xcoord, int ycoord,
            double windSpeed, double windDirection,
            double slope, double upSlopeDirection,
            StandardCustomizedFuelEnum fuelType,
            double xCellSize, double yCellSize) {
        super(system);
        this.x = xcoord;
        this.y = ycoord;
        setWindSpeed(windSpeed);
        this.windDirection = windDirection;
        this.slope = slope;
        this.downSlopeDirection = upSlopeDirection;

        this.fuelType = fuelType;
        this.initializeDistanceRemain(xCellSize, yCellSize);

        this.xCellSize = xCellSize;
        this.yCellSize = yCellSize;

        for (int i = 0; i < DIRECTION_NUMBER; i++) {
            spreadRates[i] = 0;
            messageSentOnDirection[i] = false;
        }

        reactionIntensity = 0;
    }

    @Override
    public Object clone() {
        // shallow copy
        FireCell c = (FireCell) super.clone();

        // deep copy
        c.distanceRemain = new double[DIRECTION_NUMBER];
        c.messageSentOnDirection = new boolean[DIRECTION_NUMBER];
        c.spreadRates = new double[DIRECTION_NUMBER];
        c.neighboringIndex = new int[DIRECTION_NUMBER];

        for (int dir = 0; dir < DIRECTION_NUMBER; dir++) {
            c.distanceRemain[dir] = distanceRemain[dir];
            c.messageSentOnDirection[dir] = messageSentOnDirection[dir];
            c.spreadRates[dir] = spreadRates[dir];
            c.neighboringIndex[dir] = neighboringIndex[dir];
        }

        return c;
    }

    // Model Coupling ===========================================================
    public void setMonitor(SystemMonitor monitor) {
        this.monitorIndice = monitor.getModelIndex();
    }

    public void setWindModel(Model windModel) {
        this.windModelIndice = windModel.getModelIndex();
    }

    public void setHeatWriter(HeatWriter heatWriter) {
        this.heatWriterIndice = heatWriter.getModelIndex();
    }

    /**
     * Set the observer cells of this cell
     *
     * @param N
     * @param NE
     * @param E
     * @param SE
     * @param S
     * @param SW
     * @param W
     * @param NW
     */
    public void setObserverFireCells(FireCell N, FireCell NE, FireCell E, FireCell SE, FireCell S, FireCell SW, FireCell W, FireCell NW) {
        if (N != null)
            this.neighboringIndex[FireCell.N] = N.getModelIndex();
        else
            this.neighboringIndex[FireCell.N] = -1;

        if (NE != null)
            this.neighboringIndex[FireCell.NE] = NE.getModelIndex();
        else
            this.neighboringIndex[FireCell.NE] = -1;

        if (E != null)
            this.neighboringIndex[FireCell.E] = E.getModelIndex();
        else
            this.neighboringIndex[FireCell.E] = -1;

        if (SE != null)
            this.neighboringIndex[FireCell.SE] = SE.getModelIndex();
        else
            this.neighboringIndex[FireCell.SE] = -1;

        if (S != null)
            this.neighboringIndex[FireCell.S] = S.getModelIndex();
        else
            this.neighboringIndex[FireCell.S] = -1;

        if (SW != null)
            this.neighboringIndex[FireCell.SW] = SW.getModelIndex();
        else
            this.neighboringIndex[FireCell.SW] = -1;

        if (W != null)
            this.neighboringIndex[FireCell.W] = W.getModelIndex();
        else
            this.neighboringIndex[FireCell.W] = -1;

        if (NW != null)
            this.neighboringIndex[FireCell.NW] = NW.getModelIndex();
        else
            this.neighboringIndex[FireCell.NW] = -1;
    }

    // Fire Cell State Getters ===========================================================
    public StandardCustomizedFuelEnum getFuelType() {
        return this.fuelType;
    }

    public String toString() {
        return "FireCell-" + this.x + "-" + this.y;
    }

    public double getIgnitionTime() {
        return this.ignitionTime;
    }

    public double getWeighingFactor() {
        return this.fuelType.getWeightingFactor();
    }

    public double getFuelMoisture() {
        return this.fuelType.getFuel().rothermelFuelMoisture();
    }

    public String getFuleName() {
        return this.fuelType.toString();
    }

    public double getTotalFuelLoadingDensity() {
        //Note that
        //Mandel's paper asks for total fuel loading, but there is no such parameter in the current fuel data
        //we currently use total net oven-dry fuel loading
        return this.fuelType.getFuel().totalNetFuelLoading();
    }

    /**
     * @return (kj/kg)
     */
    public double getHeatContent() {
        // In Rothermel model, we have only low heat content
        // I am not sure if it is the heat content of Mandel's paper  -- Haidong
        return this.fuelType.getFuel().rothermelLowHeatContent();
    }

    /**
     * Calculate the estimated temperature of a fire cell
     *
     * @return the temperature in Kelvin
     */
    public double getKelvinTemperature() {
        return getCelsiusTemperature() + 273;
    }

    /**
     * Calculate the estimated temperature of a fire cell
     *
     * @return the temperature in Celsius
     */
    public double getCelsiusTemperature() {
        // remain a low temperature when fuel is almost gone
        //if(this.getFractionAt(this.getTime())<0.05) return GlobalConstants.AMBIENT_TEMPERATURE;

        //Calculated from fire intensity
        double height = 1;//0.6; // the height of this temperature

        double fi = this.firelineIntensity;

        return 3.9 * Math.pow(fi, 0.6666667) / height;

    }

    public double getLatenHeatFluxDensity(double startTime, double endTime) throws NotSupportedFunctionException {
        if ((firelineIntensity > THRESHOLD_FI) && this.firelineIntensity >= THRESHOLD_FI)
            if (this.isUsingNewHeatModel) {
                if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_CELL) {

                    double spreadRate1 = this.spreadRatesAtIgnition[this.ignitionDirectionType.convertToDirectionIndex()];
                    double spreadRate2 = 0;
                    try {
                        return this.newHeat.latentHeatFluxDesnsity(ignitionType, this.ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity(), spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                    } catch (NotSupportedHeatException e) {
                        throw new NotSupportedFunctionException();
                    }
                } else if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_IGNITER) {
                    // find the max direction and max/min rate
                    MaxMinRatesDirs rateDir = this.getMaxMinSpreadRates(this.spreadRatesAtIgnition);

                    // determine the direction type
                    // note: the direction type of this cell is useless here, a cell ignited by an igniter use the max spread rate direction as the ignition direction
                    IgnitionDirectionTypeEnum ignitionDirectionType = IgnitionDirectionTypeEnum.convertedFromDirectionIndex(rateDir.maxDir);

                    double spreadRate1 = rateDir.maxRate;
                    double spreadRate2 = rateDir.minRate;

                    try {
                        return this.newHeat.latentHeatFluxDesnsity(ignitionType, ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity(), spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                    } catch (NotSupportedHeatException e) {
                        throw new NotSupportedFunctionException();
                    }

                } else
                    throw new NotSupportedFunctionException();
            } else
                return mandelHeat.latentHeatFluxDesnsity(getWeighingFactor(), getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity());
        else
            return 0;
    }

    public double getSensibleHeatFluxDensity(double startTime, double endTime) throws NotSupportedFunctionException {
        if (firelineIntensity >= THRESHOLD_FI)
            if (this.isUsingNewHeatModel) {
                if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_CELL) {

                    double spreadRate1 = this.spreadRatesAtIgnition[this.ignitionDirectionType.convertToDirectionIndex()];
                    double spreadRate2 = 0;
                    try {
                        return this.newHeat.sensibleHeatFluxDesnsity(ignitionType, this.ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity(), getHeatContent() * 1000, spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                    } catch (NotSupportedHeatException e) {
                        throw new NotSupportedFunctionException();
                    }
                } else if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_IGNITER) {
                    // find the max direction and max/min rate
                    MaxMinRatesDirs rateDir = this.getMaxMinSpreadRates(this.spreadRatesAtIgnition);

                    // determine the direction type
                    // note: the direction type of this cell is useless here, a cell ignited by an igniter use the max spread rate direction as the ignition direction
                    IgnitionDirectionTypeEnum ignitionDirectionType = IgnitionDirectionTypeEnum.convertedFromDirectionIndex(rateDir.maxDir);

                    double spreadRate1 = rateDir.maxRate;
                    double spreadRate2 = rateDir.minRate;

                    try {
                        return this.newHeat.sensibleHeatFluxDesnsity(ignitionType, ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity(), getHeatContent() * 1000, spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                    } catch (NotSupportedHeatException e) {
                        throw new NotSupportedFunctionException();
                    }

                } else
                    throw new NotSupportedFunctionException();
            } else
                return mandelHeat.sensibleHeatFluxDesnsity(getWeighingFactor(), getIgnitionTime(), startTime, endTime, getFuelMoisture(), getTotalFuelLoadingDensity(), getHeatContent() * 1000);
        else
            return 0;


    }

    public double getFractionAt(double time) throws NotSupportedFunctionException {
        if (time <= this.ignitionTime) return 1;

        if (this.isUsingNewHeatModel) {
            if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_CELL) {

                double spreadRate1 = this.spreadRatesAtIgnition[this.ignitionDirectionType.convertToDirectionIndex()];
                double spreadRate2 = 0;
                try {
                    return this.newHeat.fuelFraction(ignitionType, this.ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), time, spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                } catch (NotSupportedHeatException e) {
                    throw new NotSupportedFunctionException();
                }
            } else if (this.ignitionType == FireCell.IgnitionTypeEnum.BY_IGNITER) {
                // find the max direction and max/min rate
                MaxMinRatesDirs rateDir = this.getMaxMinSpreadRates(this.spreadRatesAtIgnition);

                // determine the direction type
                // note: the direction type of this cell is useless here, a cell ignited by an igniter use the max spread rate direction as the ignition direction
                IgnitionDirectionTypeEnum ignitionDirectionType = IgnitionDirectionTypeEnum.convertedFromDirectionIndex(rateDir.maxDir);

                double spreadRate1 = rateDir.maxRate;
                double spreadRate2 = rateDir.minRate;

                try {
                    return this.newHeat.fuelFraction(ignitionType, ignitionDirectionType, this.getWeighingFactor(), this.getIgnitionTime(), time, spreadRate1, spreadRate2, (xCellSize + yCellSize) / 2);
                } catch (NotSupportedHeatException e) {
                    throw new NotSupportedFunctionException();
                }

            } else
                throw new NotSupportedFunctionException();
        } else
            return mandelHeat.fuelFraction(this.getWeighingFactor(), this.getIgnitionTime(), time);
    }

    private MaxMinRatesDirs getMaxMinSpreadRates(double[] spreadRatesAtIgnition2) {
        // find the max direction and max/min rate
        double maxSpreadRate = 0;
        int maxDir = -1;
        int dir = 0;
        for (; dir <= 6; dir += 2)
            if (spreadRatesAtIgnition2[dir] > maxSpreadRate) {
                maxSpreadRate = spreadRatesAtIgnition2[dir];
                maxDir = dir;
            }
        int minDir = (dir + 4) % 7;
        double minSpreadRate = spreadRatesAtIgnition2[minDir];

        return new MaxMinRatesDirs(maxSpreadRate, minSpreadRate, maxDir, minDir);
    }

    protected boolean isUnburned() throws NotSupportedFunctionException {
        return 1 - this.getFractionAt(this.getTime()) <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE;
    }

    public boolean isBurning() {
        return firelineIntensity > THRESHOLD_FI;
    }

    public boolean isBurnedOut() throws NotSupportedFunctionException {
        return this.getFractionAt(this.getTime()) <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE;
    }

    public boolean isAllSent() {
        for (int dir = 0; dir < FireCell.DIRECTION_NUMBER; dir++)
            if (this.messageSentOnDirection[dir] == false)
                return false;
        return true;
    }

    /**
     * @return ignition temperature in Kelvin
     */
    public double getIgnitionTemperature() {
        return this.getFuelType().getIgnitionTemperature();
    }

    public double getPotentialFirelineIntensity() {
        double windSpeedMidFlame = this.windspd_adjust(this.windSpeed);
        double upSlopeDirection = this.downSlopeDirection + 180;

        // Apply Rothermel
        rothermel.setInputs(fuelType.getFuel(), windSpeedMidFlame, windDirection, this.slope, upSlopeDirection);

        return rothermel.getFirelineIntensity();
    }

    public static double getTemperatureAtFI(double fi) //in Kelvin
    {
        double height = 0.6; // the height of this temperature
        return 3.9 * Math.pow(fi, 0.6666667) / height;
    }

    protected boolean isWeatherUpdated() {
        return (Math.abs(this.weatherUpdatingTime - getTime()) < GlobalConstants.DOUBLE_COMPARISON_TOLERANCE);
    }

    // Discrete Event Behavior ===========================================================
    @Override
    public void internalTransit(double delta_time) {
        //Update distance remained
        for (int i = 0; i < FireCell.DIRECTION_NUMBER; i++)
            this.distanceRemain[i] -= this.spreadRates[i] * delta_time;

        //Update fire intensity
    /*	if( isBurnedOut() && isAllSent())
        {
			this.firelineIntensity = 0;
			this.reactionIntensity = 0;
		}*/
    }

    @Override
    public void externalTransit(Message message) throws NotSupportedFunctionException, InvalidLogicException {
        // Ignition message from other cells
        if (message instanceof MessageAmongFireCells && !this.contained) {
            if (((MessageAmongFireCells) message).type == MessageAmongFireCells.MessageAmongCellsEnum.IGNITION && isUnburned() && !this.isBurning()) {

                // Update state
                igniting = true;
                this.ignitionDirectionType = ((MessageAmongFireCells) message).ignitionDirectionType;
                this.ignitionType = ((MessageAmongFireCells) message).ignitionType;

                // Update date spread rate if weather is updated, or update weather
                if (this.isWeatherUpdated())
                    this.updateBurningStateByRothermel();

            }
        }
        // Wind update from the SimpleWindModel
        else if (message instanceof SimpleWindModel.MessageFromWindModelToCell) {
            SimpleWindModel.MessageFromWindModelToCell m = (SimpleWindModel.MessageFromWindModelToCell) message;

            //Update wind
            setWindSpeed(m.windSpeed);
            this.windDirection = m.windDirection;
            this.weatherUpdatingTime = getTime();

            //Update spread state
            this.updateBurningStateByRothermel();

        }

        // Wind update from the ComplexWindModel
        else if (message instanceof ComplexWindModel.MessageFromWindModelToCell) {
            ComplexWindModel.MessageFromWindModelToCell m = (ComplexWindModel.MessageFromWindModelToCell) message;

            //Update wind
            setWindSpeed(m.windSpeed);
            this.windDirection = m.windDirection;
            this.weatherUpdatingTime = getTime();

            //Update spread state
            this.updateBurningStateByRothermel();

            //System.out.println(x+"-"+y+": " + " intensity: " + this.firelineIntensity);
        } else if (message instanceof Suppressor.MessageFromSuppressorToCells) {
            this.contain();
        }
    }

    @Override
    public void generateOutput() {
        //Send ignition message to neighborhood cells when needed
        for (int i = 0; i < FireCell.DIRECTION_NUMBER; i++) {
            if (this.distanceRemain[i] <= DELTA && !this.messageSentOnDirection[i]) {
                this.messageSentOnDirection[i] = true;
                if (this.neighboringIndex[i] != -1) {
                    IgnitionDirectionTypeEnum ignitionDirectionType = IgnitionDirectionTypeEnum.convertedFromDirectionIndex(i);
                    this.sendMessage(this.neighboringIndex[i], new MessageAmongFireCells(MessageAmongFireCells.MessageAmongCellsEnum.IGNITION, ignitionDirectionType, IgnitionTypeEnum.BY_CELL));
                }
            }
        }

        //Send message to weather model to update weather when needed
        if (igniting && !this.isWeatherUpdated()) {
            this.sendMessage(this.windModelIndice, new MessageToWindModel(MessageToWindModel.MessageToWindModelEnum.UPDATE, this.getModelIndex()));
        }

        //Send  message to weather model to subscribe dynamic weather when it is ignited and not subscribed
        if (firelineIntensity > THRESHOLD_FI && !subscribedToDynamicWeather) {
            this.sendMessage(this.windModelIndice, new MessageToWindModel(MessageToWindModel.MessageToWindModelEnum.SUBSCRIBE, this.getModelIndex()));
            subscribedToDynamicWeather = true;
        }

        //Send  message to weather model to unsubscribe dynamic weather
        if (this.isAllSent() && subscribedToDynamicWeather) {
            this.sendMessage(this.windModelIndice, new MessageToWindModel(MessageToWindModel.MessageToWindModelEnum.UNSUBSCRIBE, this.getModelIndex()));
            subscribedToDynamicWeather = false;
        }

        //Send ignition message to system monitor when needed
        if (igniting && !ignitionTimeReportedToSystemMonitor && monitorIndice != -1) {
            this.sendMessage(this.monitorIndice, new MessageCellToMonitor(this.x, this.y, this.getTime()));
            ignitionTimeReportedToSystemMonitor = true;
        }

        // Heat writer
        if (!subscribedToHeatWriter && this.firelineIntensity > THRESHOLD_FI && heatWriterIndice != -1) {
            this.sendMessage(this.heatWriterIndice, new MessageToHeatWriter(MessageToHeatWriter.MessageToHeatWriterEnum.SUBSCRIBE, this.getModelIndex()));
            subscribedToHeatWriter = true;
        }

    }

    @Override
    public double getNextInternalTransitionTime() {
        if (contained) return Double.POSITIVE_INFINITY;

        if (this.firelineIntensity <= THRESHOLD_FI) return Double.POSITIVE_INFINITY;

        if (this.isAllSent()) return Double.POSITIVE_INFINITY;

        double smallestTime = Double.POSITIVE_INFINITY;

        for (int dir = 0; dir < FireCell.DIRECTION_NUMBER; dir++) {
            if (this.messageSentOnDirection[dir])
                continue;
            double dirTime = this.distanceRemain[dir] / this.spreadRates[dir];

            if (dirTime < smallestTime)
                smallestTime = dirTime;
        }

        return smallestTime;
    }

    // Fire Cell State Changers
    private void contain() {
        for (int i = 0; i < FireCell.DIRECTION_NUMBER; i++)
            this.spreadRates[i] = 0;
        this.reactionIntensity = 0;
        this.firelineIntensity = 0;

        this.contained = true;
    }

    public void forceToIgnite() throws InvalidLogicException {
        igniting = true;
        this.updateBurningStateByRothermel();
    }

    public void forceToBurnOut() {
        this.contain();
    }

    public void forceToUnburn() {
        for (int i = 0; i < FireCell.DIRECTION_NUMBER; i++)
            this.spreadRates[i] = 0;
        this.reactionIntensity = 0;
        this.firelineIntensity = 0;
        ignitionTime = Double.POSITIVE_INFINITY;
    }

    //Fire Spread Calculation ===========================================================

    /**
     * This method adjust the wind speed based on a Wind adjustment factor (WAF) A number between 0 and 1 is used to adjust the wind speed at 20-ft above the
     * vegetation to midflame wind speed. Wind adjustment factor (WAF) depends on sheltering of fuels from the wind. If fuels are sheltered from the wind, WAF
     * does not depend on the surface fuel model. If fuels are not sheltered from the wind, WAF is a function of fuel bed depth. 20-ft wind speed * WAF =
     * midflame wind speed.
     */
    private double windspd_adjust(double windSpeed6m) {
        // Feng Gu 11/03/2007
        // Enhanced by Haidong Xue, 9/24/2011
        /*
		 * The table from BehavePlus help documents Wind Adjustment Factor and Fuel Bed Depth
		 * 
		 * Fuel Bed Depth | Unsheltered WAF | Fuel Model
		 *  
		 *  <0.9 ft | | (<0.3 m) | 0.3 | 8, 9 | | GR1 | | TU1,TU4 | | TL1, TL2, TL3, TL4, TL5, TL6, TL7, TL8, TL9 
		 * 
		 * 0.9 - 2.7 ft | | (0.3 - 0.8 m) | 0.4 | 1, 2, 3, 5, 6, 7, 10, 11, 12 | | GR2, GR3, GR4, GR5, GR6 | | GS1, GS2, GS3, GS4 | | SH1, SH2, SH3, SH4, SH6 || TU2, TU3, TU5 | | SB1, SB2, SB3, SB4

         * >2.7 ft ||(>0.8 m) | 0.5 | 4, 13 | | GR7, GR8, GR9 | | SH5, SH7, SH8, SH9 
		 */
		
		/*
		double WAF;
		if ((fuelModel == 4) || (fuelModel == 13))
			WAF = 0.5;
		else if ((fuelModel == 8) || (fuelModel == 9))
			WAF = 0.3;
		else
			WAF = 0.4;
		 * 
		 */
        double WAF;
        if (fuelType == StandardCustomizedFuelEnum.OLD4
                || fuelType == StandardCustomizedFuelEnum.OLD13
				/*this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.GR7
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.GR8
				||this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.GR9
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.SH5
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.SH7
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.SH8
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.SH9*/)
            WAF = 0.5;
        else if (fuelType == StandardCustomizedFuelEnum.OLD8
                || fuelType == StandardCustomizedFuelEnum.OLD9
				/*|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TU1
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TU4
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL1
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL2
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL3
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL4
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL5
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL6
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL7
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL8
				|| this.fuelType==data.gis.fuel.StandardCustomizedFuelEnum.TL9*/)
            WAF = 0.3;
        else
            WAF = 0.4;

        return windSpeed6m * WAF;
    }

    /**
     * Calculate spread rate and intensity based on current state
     *
     * @throws InvalidLogicException
     */
    protected void updateBurningStateByRothermel() throws InvalidLogicException {
        if ((igniting || firelineIntensity > THRESHOLD_FI) && !this.contained) {
            // Adjust wind speed and slope direction before applying Rothermel
            double windSpeedMidFlame = this.windspd_adjust(this.windSpeed);
            double upSlopeDirection = this.downSlopeDirection + 180;

            // Apply Rothermel
            rothermel.setInputs(fuelType.getFuel(), windSpeedMidFlame, windDirection, this.slope, upSlopeDirection);

            this.reactionIntensity = rothermel.getReactionIntensity();
            this.firelineIntensity = rothermel.getFirelineIntensity();


            // Calculate spread rate for each direction
            for (int dir = 0; dir < FireCell.DIRECTION_NUMBER; dir++) {
                double angel = dir * 45;
                if (firelineIntensity > THRESHOLD_FI)
                    this.spreadRates[dir] = this.spreadRateOn(angel, rothermel);
                else {
                    this.spreadRates[dir] = 0;
                }
            }

            // set ignition time
            if (igniting) {
                igniting = false;
                if (firelineIntensity > THRESHOLD_FI) ignitionTime = this.getTime();

                // record ignition rates
                //if (this.spreadRatesAtIgnition != null) throw new InvalidLogicException();
                this.spreadRatesAtIgnition = new double[DIRECTION_NUMBER];
                for (int i = 0; i < DIRECTION_NUMBER; i++)
                    this.spreadRatesAtIgnition[i] = this.spreadRates[i];
            }

            // print spread info for debug
			/*System.out.println("x=" + this.x + " y=" + this.y + " at " + this.getTime());
			System.out.println("Rothermel inputs: fuelType=" + this.fuelType + " midFlameWSpeed=" + windSpeedMidFlame + " slope=" + this.slope + " upSlopeDir=" +  upSlopeDirection);
			for (int dir = 0; dir < FireCell.DIRECTION_NUMBER; dir++)
			{
				double angel = dir * 45;
				System.out.println(String.format("  spread rate towards %4.0f: %4.2f m/s", angel, spreadRates[dir]));
			}*/

        }
    }

    public void printlnInfo() {
        System.out.println("Fire Intensity: " + this.reactionIntensity);
        System.out.println("Fuel: " + this.fuelType.getFuel());
    }

    private double spreadRateOn(double direction, RothermelModel rothermel) {
        double rateOfSpread = 0;
        double azimuth;

        double angle_to_upslope;  // Direction of spread of interest from the upslope direction
        double upslope_speed;
        double upslope_speed_honrizontal;
        double paraslope_speed; // this is also the paraslope_speed_honrizontal

        double maxrosdir = rothermel.getMaxSpreadDirection();
        double maxros = rothermel.getSpreadRateOnMaxDirection();
        double upslopeDir = this.downSlopeDirection + 180;

        angle_to_upslope = upslopeDir - direction;
        azimuth = direction - maxrosdir;

        // Calculate the fire spread rate in this direction
        double eccentricity = this.getEccentricity(rothermel.getEfficientWindSpeedOnMaxDirection(), maxros);
        rateOfSpread = maxros * (1 - eccentricity) / (1 - eccentricity * Math.cos(Math.toRadians(azimuth)));

        // convert the rate of spread on the slope surface to the horizontal plane
        upslope_speed = rateOfSpread * Math.cos(Math.toRadians(angle_to_upslope));
        upslope_speed_honrizontal = upslope_speed * Math.cos(Math.toRadians(slope));
        paraslope_speed = rateOfSpread * Math.sin(Math.toRadians(angle_to_upslope)); // this is also the paraslope_speed_honrizontal
        rateOfSpread = Math.sqrt(upslope_speed_honrizontal * upslope_speed_honrizontal + paraslope_speed * paraslope_speed);

        return rateOfSpread;
    }

    /**
     * Calculate the eccentricity based on the given wind speed and max spread direction.
     * <p/>
     * Fe Geng finished the algorithm implementation around 2009.
     * <p/>
     * Haidong Xue enhanced the code on 9/24/2011
     * <p/>
     * This method determines fire ellipse parameters from the effective wind speed lwRatio = 1.0 + 0.25*effWindSpd/(88*60*3.280840)
     * wind speed is in meters/sec
     * Conversion from wind speed in ft/min: 60sec/min, 3.280840ft/m
     */
    private double getEccentricity(double effwindspeed, double maxros) {
        double LB, HB,/* a,*/ b, c; // Feng Gu,calcuate the eccencity,
        double eccentricity = 0;
        if (effwindspeed > DELTA) {
            LB = 0.936 * Math.pow(2.71828183, 0.2566 * effwindspeed) + 0.461 * Math.pow(2.71828183, -0.1548 * effwindspeed) - 0.397;

            HB = (LB + Math.sqrt(LB * LB - 1)) / (LB - Math.sqrt(LB * LB - 1));

            //a = 0.5 * (maxros + maxros / HB) / LB;
            b = (maxros + maxros / HB) / 2.0;
            c = b - maxros / HB;
            eccentricity = c / b;
        }
        return eccentricity;
    }

    /**
     * Initialize the spread distance for all the directions.
     *
     * @param xCellSize
     * @param yCellSize
     */
    private void initializeDistanceRemain(double xCellSize, double yCellSize) {
        this.distanceRemain[W] = xCellSize;
        this.distanceRemain[E] = xCellSize;
        this.distanceRemain[S] = yCellSize;
        this.distanceRemain[N] = yCellSize;

        double diagonal = Math.sqrt(xCellSize * xCellSize + yCellSize * yCellSize);
        this.distanceRemain[NW] = diagonal;
        this.distanceRemain[NE] = diagonal;
        this.distanceRemain[SW] = diagonal;
        this.distanceRemain[SE] = diagonal;
    }

    // Nested Types
    public static class MessageAmongFireCells extends Message {
        private static final long serialVersionUID = -6546361553554103341L;
        public MessageAmongCellsEnum type;
        public IgnitionDirectionTypeEnum ignitionDirectionType;
        public IgnitionTypeEnum ignitionType;

        public MessageAmongFireCells(MessageAmongCellsEnum type, IgnitionDirectionTypeEnum ignitingType, IgnitionTypeEnum ignitionType) {
            this.type = type;
            this.ignitionDirectionType = ignitingType;
            this.ignitionType = ignitionType;
        }

        public enum MessageAmongCellsEnum implements Serializable {
            IGNITION
        }
    }

    public enum IgnitionDirectionTypeEnum implements Serializable {
        //SIDE_TO_SIDE, DIAGONAL, CENTER
        // indicating where the ignition fire goes to
        N, NE, E, SE, S, SW, W, NW;

        public static IgnitionDirectionTypeEnum convertedFromDirectionIndex(int i) {
            if (i == FireCell.N)
                return IgnitionDirectionTypeEnum.N;
            else if (i == FireCell.NW)
                return IgnitionDirectionTypeEnum.NW;
            else if (i == FireCell.W)
                return IgnitionDirectionTypeEnum.W;
            else if (i == FireCell.SW)
                return IgnitionDirectionTypeEnum.SW;
            else if (i == FireCell.S)
                return IgnitionDirectionTypeEnum.S;
            else if (i == FireCell.SE)
                return IgnitionDirectionTypeEnum.SE;
            else if (i == FireCell.E)
                return IgnitionDirectionTypeEnum.E;
            else if (i == FireCell.NE)
                return IgnitionDirectionTypeEnum.NE;

            return null;
        }

        public int convertToDirectionIndex() {
            switch (this) {
                case N:
                    return FireCell.N;
                case NW:
                    return FireCell.NW;
                case W:
                    return FireCell.W;
                case SW:
                    return FireCell.SW;
                case S:
                    return FireCell.S;
                case SE:
                    return FireCell.SE;
                case E:
                    return FireCell.E;
                case NE:
                    return FireCell.NE;
                default:
                    return -1;
            }
        }


    }

    public enum IgnitionTypeEnum implements Serializable {
        BY_CELL, BY_IGNITER
    }

    public static class MessageCellToMonitor extends Message {
        private static final long serialVersionUID = -5403111416677007127L;
        public int x;
        public int y;
        public double time;

        public MessageCellToMonitor(int x, int y, double time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }

    public static class MessageToWindModel extends Message {
        private static final long serialVersionUID = -1032271365202545220L;
        public MessageToWindModelEnum type;
        public int cellIndexRequiringWeather; //the cell requiring weather update

        public MessageToWindModel(MessageToWindModelEnum type, int cellIndexRequiringWeather) {
            this.type = type;
            this.cellIndexRequiringWeather = cellIndexRequiringWeather;
        }

        public enum MessageToWindModelEnum implements Serializable {
            UPDATE, SUBSCRIBE, UNSUBSCRIBE
        }
    }

    public static class MessageToHeatWriter extends Message {
        private static final long serialVersionUID = 2864001597304643344L;
        public MessageToHeatWriterEnum type;
        public int fireCellIndex;

        public MessageToHeatWriter(MessageToHeatWriterEnum type, int fireCellIndex) {
            this.type = type;
            this.fireCellIndex = fireCellIndex;
        }

        enum MessageToHeatWriterEnum implements Serializable {
            SUBSCRIBE, UNSUBSCRIBE
        }
    }

    private class MaxMinRatesDirs {
        public double maxRate;
        public int maxDir;
        public double minRate;
        @SuppressWarnings("unused")
        public int minDir;

        public MaxMinRatesDirs(double maxR, double minR, int maxDir, int minDir) {
            this.maxRate = maxR;
            this.minRate = minR;
            this.maxDir = maxDir;
            this.minDir = minDir;

        }
    }

    // Other Methods
    public void printSpreadRatesAndDisRemain() {
        for (int i = 0; i < DIRECTION_NUMBER; i++)
            System.out.println(i + " - " + this.spreadRates[i] + " || " + this.distanceRemain[i]);
    }
}
