package edu.gsu.hxue.desFire;

import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.des.models.Message;
import edu.gsu.hxue.des.models.Model;
import edu.gsu.hxue.des.system.DESSystem;
import edu.gsu.hxue.desFire.exceptions.NotSupportedFunctionException;
import edu.gsu.hxue.sensors.TemperatureSensorProfile;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.util.Random;

/**
 * This class uses the twoDimensions package from the DES_Presentation project to show the visualization of a fire.
 *
 * @author Haidong Xue
 */
public class FirePresentation extends Model implements Cloneable {
    private static final long serialVersionUID = 5810336342428643025L;
    CellularAutomataPresentation statePresentation;
    double wakeUpInterval = 600;
    double timer = 0;
    int xDim;

    public double getWakeUpInterval() {
        return wakeUpInterval;
    }

    public void setWakeUpInterval(double wakeUpInterval) {
        this.wakeUpInterval = wakeUpInterval;
    }

    int yDim;
    double displayScale = 1;

    CellularAutomataPresentation heatPresentation;
    boolean showHeat = true;
    boolean showGISOnHeat = false;
    boolean showHeatBar = true;
    double heatInterval = 600; // show the heat released in heatInterval seconds
    double maxHeat = 20000; // unit: j m^-2 s^-1

    CellularAutomataPresentation sensorPresentation;
    boolean showSensorTemperature = false;
    boolean showSensorTemperatureBar = true;
    double maxTemperature = 400;
    TemperatureSensorProfile[] sensorProfiles;

    CellularAutomataPresentation fullSensorPresentation;
    boolean showFullSensorPresentation = false;
    boolean showFullTemperatureBar = true;
    TemperatureSensorProfile[] fullSensorProfiles;

    private int cellIndex[][];

    public Object clone() {
        FirePresentation c = (FirePresentation) super.clone();
        c.statePresentation = new CellularAutomataPresentation(c.statePresentation);
        if (showHeat) heatPresentation = new CellularAutomataPresentation(xDim, yDim, displayScale, 0, 0);
        if (showSensorTemperature)
            sensorPresentation = new CellularAutomataPresentation(xDim, yDim, displayScale, 0, 0);
        return c;
    }

    public FirePresentation(DESSystem system, GISData gisMap) {
        this(system, gisMap, 1);
    }

    public FirePresentation(DESSystem system, GISData gisMap, double displayScale) {
        super(system);

        this.displayScale = displayScale;
        this.xDim = gisMap.xDim();
        this.yDim = gisMap.yDim();

        final double RADIUS = 700;
        // predefine a sensor profile
        int N = 100;
        this.sensorProfiles = new TemperatureSensorProfile[N];

        final int RAND_SEED = 22345;
        Random rand = new Random(RAND_SEED);

        for (int i = 0; i < N; i++) {
            sensorProfiles[i] = new TemperatureSensorProfile();
            sensorProfiles[i].radius = RADIUS; //RADIUS; //100
            sensorProfiles[i].location = new Point((int) Math.round(rand.nextDouble() * (xDim - 10) + 5), (int) Math.round(rand.nextDouble() * (yDim - 10) + 5));
        }

        // define the full sensor profile
        this.fullSensorProfiles = new TemperatureSensorProfile[xDim * yDim];
        for (int x = 0; x < xDim; x++)
            for (int y = 0; y < yDim; y++) {
                int i = y * xDim + x;
                fullSensorProfiles[i] = new TemperatureSensorProfile();
                fullSensorProfiles[i].radius = RADIUS; //100
                fullSensorProfiles[i].location = new Point(x, y);
            }

        int interval = 300;

        // fire state
        statePresentation = new CellularAutomataPresentation(gisMap.xDim(), gisMap.yDim(), displayScale, 0, 0);
        for (int x = 0; x < gisMap.xDim(); x++)
            for (int y = 0; y < gisMap.yDim(); y++) {
                statePresentation.setCellText(x, y, "fuel=" + Integer.toString(gisMap.fuelModelIndexAt(x, y)));
            }

        // heat
        if (showHeat) {
            heatPresentation = new CellularAutomataPresentation(gisMap.xDim(), gisMap.yDim(), displayScale, interval, 0);
            heatPresentation.setDrawFrameOfReference(true);
        }

        // sensors
        if (showSensorTemperature) {
            sensorPresentation = new CellularAutomataPresentation(gisMap.xDim(), gisMap.yDim(), displayScale, 2 * interval, 0);
            sensorPresentation.setDrawFrameOfReference(true);
        }

        // full sensor temperature
        if (showFullSensorPresentation) {
            fullSensorPresentation = new CellularAutomataPresentation(gisMap.xDim(), gisMap.yDim(), displayScale, 3 * interval, 0);
            fullSensorPresentation.setDrawFrameOfReference(true);
        }

        cellIndex = new int[xDim][yDim];
        for (int i = 0; i < xDim; i++)
            for (int j = 0; j < yDim; j++)
                cellIndex[i][j] = -1;

        drawGISMap(gisMap);
    }

    private void drawBarOnHeatMap() {
        if (this.heatPresentation == null) return;

        int yStart = 0;
        int yHeight = 6;
        int xStart = 0;
        int xEnd = this.xDim - 1;
        for (int i = xStart; i <= xEnd; i++)
            for (int j = yStart; j < yStart + yHeight; j++) {
                double r = ((double) (i - xStart)) / (double) (xEnd - xStart + 1);
                double heat = r * this.maxHeat;
                Color c = this.heatToColor(heat);
                heatPresentation.setCellColor(i, j, c);
            }

        heatPresentation.drawDirtyCellsInBuffer();
        heatPresentation.showBufferOnScreen();

    }

    private void drawBarOnSenorMap() {
        if (this.sensorPresentation == null) return;

        int yStart = 0;
        int yHeight = 6;
        int xStart = 0;
        int xEnd = this.xDim - 1;
        for (int i = xStart; i <= xEnd; i++)
            for (int j = yStart; j < yStart + yHeight; j++) {
                double r = ((double) (i - xStart)) / (double) (xEnd - xStart + 1);
                double t = r * this.maxTemperature;
                Color c = this.temperatureToColor(t);
                sensorPresentation.setCellColor(i, j, c);
            }

        sensorPresentation.drawDirtyCellsInBuffer();
        sensorPresentation.showBufferOnScreen();

    }

    private void drawBarOnTemperatureMap() {
        if (this.fullSensorPresentation == null) return;

        int yStart = 0;
        int yHeight = 6;
        int xStart = 0;
        int xEnd = this.xDim - 1;
        for (int i = xStart; i <= xEnd; i++)
            for (int j = yStart; j < yStart + yHeight; j++) {
                double r = ((double) (i - xStart)) / (double) (xEnd - xStart + 1);
                double t = r * this.maxTemperature;
                Color c = this.temperatureToColorForFullTemperatureMap(t);
                fullSensorPresentation.setCellColor(i, j, c);
            }

        fullSensorPresentation.drawDirtyCellsInBuffer();
        fullSensorPresentation.showBufferOnScreen();

    }

    private Color heatToColor(double heat) {
        if (heat > this.maxHeat) heat = maxHeat;
        float r = (float) heat / (float) this.maxHeat;
        return Color.getHSBColor(0f, r, 1f);
    }

    private void drawGISMap(GISData gisMap) {
        for (int i = 0; i < xDim; i++)
            for (int j = 0; j < yDim; j++) {
                statePresentation.setCellColor(i, j, StandardCustomizedFuelEnum.modelIndexToFuelType(gisMap.fuelModelIndexAt(i, j)).getColor());
            }
        statePresentation.drawDirtyCellsInBuffer();
        statePresentation.showBufferOnScreen();

        if (showHeat && showGISOnHeat) {
            for (int i = 0; i < xDim; i++)
                for (int j = 0; j < yDim; j++) {
                    heatPresentation.setCellColor(i, j, StandardCustomizedFuelEnum.modelIndexToFuelType(gisMap.fuelModelIndexAt(i, j)).getColor());
                }

            heatPresentation.drawDirtyCellsInBuffer();
            heatPresentation.showBufferOnScreen();
        }


    }

    /**
     * Set a receiver cell's model index
     *
     * @param x
     * @param y
     * @param index
     */
    public void setCellIndex(int x, int y, int index) {
        this.cellIndex[x][y] = index;
    }

    @Override
    public void internalTransit(double delta_time) {
        timer += delta_time;

    }

    @Override
    public void externalTransit(Message message) {

    }

    @Override
    public void generateOutput() throws NotSupportedFunctionException {
        if (this.wakeUpInterval - timer <= GlobalConstants.DOUBLE_COMPARISON_TOLERANCE) {
            // Fire States
            for (int x = 0; x < xDim; x++)
                for (int y = 0; y < yDim; y++) {
                    FireCell cell = (FireCell) system.getModel(this.cellIndex[x][y]);
                    if (cell.isBurning()) {
                        /*double value = cell.getFractionAt(getTime());
						float h = (float) 0.6;
						float s = (float) value;
						float b = (float) 1;
						Color c = Color.getHSBColor(h, 1-s, b);
						presentation.setCellColor(x, y, c);*/

                        if (cell.isBurning())
                            statePresentation.setCellColor(x, y, Color.red);

                        //if(cell.isAllSent())
                        //if(cell.getFractionAt(cell.getTime()) <=0.05)

                        boolean allNeighborsBurning = true;
                        for (int i = -1; i <= 1; i++)
                            for (int j = -1; j <= 1; j++) {
                                if (i == 0 && j == 0) continue;

                                if (x + i < 0 || x + i >= xDim || y + j < 0 || y + j >= yDim) continue;

                                if (!((FireCell) system.getModel(this.cellIndex[x + i][y + j])).isBurning()) {
                                    allNeighborsBurning = false;
                                    break;
                                }
                            }
                        if (allNeighborsBurning)
                            statePresentation.setCellColor(x, y, Color.black);


                    }
                }
            statePresentation.drawDirtyCellsInBuffer();
            statePresentation.showBufferOnScreen();
            statePresentation.setTitle("Fire states at " + (int) this.getTime() + "s");

            // show heat
            if (showHeat) {
                for (int x = 0; x < xDim; x++)
                    for (int y = 0; y < yDim; y++) {
                        FireCell cell = (FireCell) system.getModel(this.cellIndex[x][y]);
                        if (cell.isBurning()) {
                            double heat = cell.getSensibleHeatFluxDensity(cell.getTime() - this.heatInterval, cell.getTime());
                            heatPresentation.setCellColor(x, y, this.heatToColor(heat));
                            heatPresentation.setCellText(x, y, String.format("%4.1f W/(m*m)", heat));
                        }
                    }

                if (showHeatBar) this.drawBarOnHeatMap();

                heatPresentation.drawDirtyCellsInBuffer();
                heatPresentation.showBufferOnScreen();
                heatPresentation.setTitle("Heat at " + (int) this.getTime() + "s");
            }

            // show sensor
            //	double max = 0;
            int enlargedSize = 1;
            if (showSensorTemperature) {
                FireSystem fire = (FireSystem) this.getSystem();
                double[] readings = fire.generateSensorReadings(sensorProfiles);

                for (int i = 0; i < sensorProfiles.length; i++) {
                    TemperatureSensorProfile p = sensorProfiles[i];
                    double temperature = readings[i];
                    //if(temperature > max)
                    //{
                    //max = temperature;
                    //System.out.println("Max temp updated to: " + max);
                    //}
                    for (int x = p.location.x - enlargedSize; x <= p.location.x + enlargedSize; x++)
                        for (int y = p.location.y - enlargedSize; y <= p.location.y + enlargedSize; y++) {
                            sensorPresentation.setCellColor(x, y, this.temperatureToColor(temperature));
                            sensorPresentation.setCellText(x, y, String.format("%4.1f Celsius", temperature));
                        }

                }

                if (showSensorTemperatureBar) this.drawBarOnSenorMap();

                sensorPresentation.drawDirtyCellsInBuffer();
                sensorPresentation.showBufferOnScreen();
                sensorPresentation.setTitle("Sensors at " + (int) this.getTime() + "s");
            }

            // show full sensor map
            if (this.showFullSensorPresentation) {
                FireSystem fire = (FireSystem) this.getSystem();
                double[] readings = fire.generateSensorReadings(this.fullSensorProfiles);

                for (int i = 0; i < fullSensorProfiles.length; i++) {
                    TemperatureSensorProfile p = this.fullSensorProfiles[i];
                    double temperature = readings[i];
                    this.fullSensorPresentation.setCellColor(p.location.x, p.location.y, this.temperatureToColorForFullTemperatureMap(temperature));
                    fullSensorPresentation.setCellText(p.location.x, p.location.y, String.format("%4.1f Celsius", temperature));
                }

                if (showFullTemperatureBar) this.drawBarOnTemperatureMap();

                fullSensorPresentation.drawDirtyCellsInBuffer();
                fullSensorPresentation.showBufferOnScreen();
                fullSensorPresentation.setTitle("Temperature map at " + (int) this.getTime() + "s");
            }


            // reset timer
            timer = 0;
        }
    }

    public Color temperatureToColor(double temperature) {
        float zeroS = 0.3f;
        float maxS = 1f;

        float zeroB = 0.5f;
        float maxB = 1f;

        if (temperature > this.maxTemperature) temperature = maxTemperature;
        float s = (float) temperature / (float) this.maxTemperature * (maxS - zeroS) + zeroS;
        float b = (float) temperature / (float) this.maxTemperature * (maxB - zeroB) + zeroB;
        return Color.getHSBColor(0f, s, b);
    }

    public Color temperatureToColorForFullTemperatureMap(double temperature) {
        if (temperature > this.maxTemperature) temperature = maxTemperature;

        int colorValue = (int) ((temperature * 255 * 3) / maxTemperature);
        // Decide R, G, B based on the color value
        int R, G, B;
        if (colorValue >= 255 * 2) {
            R = 255;
            G = 255;
            B = colorValue - 255 * 2;
        } else if (colorValue >= 255) {  // 255 < colorValue < 255*2
            R = 255;
            G = colorValue - 255;
            B = 0;
        } else { // colorValue < 255
            R = colorValue;
            G = 0;
            B = 0;
        }
        //System.out.println(String.format("R=%d, G=%d, B=%d", R, G, B));
        return new Color(R, G, B);

    }

    public static Color temperatureToColor(double temperature, double maxTemperature) {
        if (temperature > maxTemperature) temperature = maxTemperature;
        float r = (float) temperature / (float) maxTemperature;
        return Color.getHSBColor(0f, r, 1f);
    }

    @Override
    public double getNextInternalTransitionTime() {
        return wakeUpInterval - timer;
    }


}
