package edu.gsu.hxue.experimentUtilities;

import edu.gsu.hxue.gis.GISData;
import edu.gsu.hxue.gis.SingleGISLayer;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuel;
import edu.gsu.hxue.gis.fuel.StandardCustomizedFuelEnum;
import edu.gsu.hxue.physicalModels.rothermel.RothermelModel;
import edu.gsu.hxue.physicalModels.rothermel.RothermelModelImp20120322;
import edu.gsu.hxue.CellularAutomataPresentation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class ExpUtilities {
    public static void drawFireMap(int[][] map, CellularAutomataPresentation p, int xDim, int yDim, Color c) {
        for (int x = 0; x < xDim; x++)
            for (int y = 0; y < yDim; y++) {
                if (map[x][y] == 0)
                    continue;
                p.setCellColor(x, y, c);
                p.drawDirtyCellsInBuffer();
                p.showBufferOnScreen();
            }

    }

    public static void drawFireMapThick(int[][] map, CellularAutomataPresentation p, int xDim, int yDim, Color c) {
        for (int x = 0; x < xDim; x++)
            for (int y = 0; y < yDim; y++) {
                if (map[x][y] == 0)
                    continue;
                int[] dxs = {-1, 0, 1};
                int[] dys = {-1, 0, 1};
                for (int dx : dxs)
                    for (int dy : dys) {
                        int moreX = x + dx;
                        int moreY = y + dy;
                        if (moreX > 0 && moreX < xDim && moreY > 0 && moreY < yDim)
                            p.setCellColor(moreX, moreY, c);
                        ;
                    }
                p.setCellColor(x, y, c);
                p.drawDirtyCellsInBuffer();
                p.showBufferOnScreen();
            }

    }

    public static int[][] getFireMapFromPointSetFile(String filePath, int xDim, int yDim) {
        File f = new File(filePath);
        int[][] map = null;
        try {
            Scanner s = new Scanner(f);
            map = new int[xDim][yDim];
            while (s.hasNextInt()) {
                int x = s.nextInt();
                if (!s.hasNextInt()) break;
                int y = s.nextInt();
                map[x][y] = 1;
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static int[][] getFireOutline(int[][] map, int xDim, int yDim) {
        int[][] outline = new int[xDim][yDim];

        for (int i = 0; i < xDim; i++)
            for (int j = 0; j < yDim; j++) {
                if (map[i][j] == 1 && isOnFront(map, i, j, xDim, yDim))
                    outline[i][j] = 1;
                else
                    outline[i][j] = 0;
            }

        return outline;
    }


    public static boolean isOnFront(int[][] map, int i, int j, int xDim, int yDim) {
        boolean allNeighborIgnited = true;
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++) {
                int x = i + dx;
                int y = j + dy;

                if (x < 0 || x >= xDim || y < 0 || y >= yDim) continue;

                if (x == i && y == j) continue;

                if (map[x][y] != 1) {
                    allNeighborIgnited = false;
                    break;
                }

            }
        if (allNeighborIgnited) return false;
        return true;
    }

    public static int[][] getFireMapFromHeatFiles(String folder, int stepLength, int stepNumber, int xDim, int yDim) throws FileNotFoundException {
        return getFireMapFromHeatFiles(folder, stepLength, 0, stepNumber, xDim, yDim);
    }

    public static int[][] getFireMapFromHeatFiles(String folder, int stepLength, int stepStartNumber, int stepEndNumber, int xDim, int yDim) throws FileNotFoundException {
        int[][] map = new int[xDim][yDim];
        for (int i = 0; i < xDim; i++)
            for (int j = 0; j < yDim; j++)
                map[i][j] = 0;
        System.out.println("Reading heat files ... ");
        for (int i = stepStartNumber; i <= stepEndNumber; i++) {

            File f = new File(folder + Integer.toString(i * stepLength) + ".txt");
            //System.out.println(f.getName());
            Scanner s = new Scanner(f);
            for (int j = 0; j < 7; j++) {
                s.nextLine();
            }

            while (s.hasNextLine()) {
                if (!s.hasNextInt())
                    break;
                int x = s.nextInt();
                if (!s.hasNextInt())
                    break;
                int y = s.nextInt();
                for (int j = 0; j < 4; j++)
                    s.next();
                map[x][y] = 1;
            }
            s.close();
        }
        return map;
    }

    public static void drawIgnitedCellsOutlineFromHeatFiles(String folder, int stepLength, int stepStartNumber, int stepEndNumber, CellularAutomataPresentation p, int xDim, int yDim, Color c) throws FileNotFoundException {
        int[][] map = ExpUtilities.getFireMapFromHeatFiles(folder, stepLength, stepStartNumber, stepEndNumber, xDim, yDim);
        int[][] outline = ExpUtilities.getFireOutline(map, xDim, yDim);
        ExpUtilities.drawFireMap(outline, p, xDim, yDim, c);
    }

    public static void drawIgnitedCellsOutlineFromHeatFiles(String folder, int stepLength, int stepNumber, CellularAutomataPresentation p, int xDim, int yDim, Color c) throws FileNotFoundException {
        drawIgnitedCellsOutlineFromHeatFiles(folder, stepLength, 0, stepNumber, p, xDim, yDim, c);
    }

    public static void drawIgnitedCellsFromHeatFiles(String folder, int stepLength, int stepNumber, CellularAutomataPresentation p, int xDim, int yDim, Color c) throws FileNotFoundException {
        drawIgnitedCellsFromHeatFiles(folder, stepLength, 0, stepNumber, p, xDim, yDim, c);
    }

    public static void drawIgnitedCellsFromHeatFiles(String folder, int stepLength, int stepStartNumber, int stepEndNumber, CellularAutomataPresentation p, int xDim, int yDim, Color c) throws FileNotFoundException {
        int[][] map = ExpUtilities.getFireMapFromHeatFiles(folder, stepLength, stepStartNumber, stepEndNumber, xDim, yDim);
        ExpUtilities.drawFireMap(map, p, xDim, yDim, c);
    }

    public static void drawGISMap(CellularAutomataPresentation cellularAutomataPresentation, GISData gisData) {
        for (int i = 0; i < gisData.xDim(); i++)
            for (int j = 0; j < gisData.yDim(); j++) {
                cellularAutomataPresentation.setCellColor(i, j, StandardCustomizedFuelEnum.modelIndexToFuelType(gisData.fuelModelIndexAt(i, j)).getColor());
                cellularAutomataPresentation.setCellText(i, j, StandardCustomizedFuelEnum.modelIndexToFuelType(gisData.fuelModelIndexAt(i, j)).toString());
            }
        cellularAutomataPresentation.drawDirtyCellsInBuffer();
        cellularAutomataPresentation.showBufferOnScreen();

    }

    public static void drawIgnition(CellularAutomataPresentation cellularAutomataPresentation, File ignitionFile, Color c) throws FileNotFoundException {
        drawPointList(cellularAutomataPresentation, ignitionFile, c);
    }

    public static void drawContainedCells(CellularAutomataPresentation cellularAutomataPresentation, File containedCellFile, Color c) throws FileNotFoundException {
        drawPointList(cellularAutomataPresentation, containedCellFile, c);
    }

    public static void drawPointList(CellularAutomataPresentation cellularAutomataPresentation, File ignitionFile, Color c) throws FileNotFoundException {
        Scanner s = new Scanner(ignitionFile);
        while (s.hasNextInt()) {
            int x = s.nextInt();
            if (!s.hasNextInt())
                break;
            int y = s.nextInt();

            cellularAutomataPresentation.setCellColor(x, y, c);
        }
        s.close();
        cellularAutomataPresentation.drawDirtyCellsInBuffer();
        cellularAutomataPresentation.showBufferOnScreen();
    }

    public static void drawDelayedIgnition(CellularAutomataPresentation cellularAutomataPresentation, File ignitionFile, Color c) throws FileNotFoundException {

        Scanner s = new Scanner(ignitionFile);
        while (s.hasNextLine() && s.hasNextDouble()) {
            s.nextDouble(); // skip time

            if (!s.hasNextInt())
                break;
            int x = s.nextInt();

            if (!s.hasNextInt())
                break;
            int y = s.nextInt();

            cellularAutomataPresentation.setCellColor(x, y, c);
        }
        s.close();
        cellularAutomataPresentation.drawDirtyCellsInBuffer();
        cellularAutomataPresentation.showBufferOnScreen();
    }

    /**
     * This method is to answer the question:
     * "  In order to check, I need the information I requested
     * ((1-B)/Pe,
     * mineral damping coefficient,
     * and %moisture content
     * for each fuel type).  Once I've checked it, I'll be able"
     * <p/>
     * B: packing ratio
     * Pe:  effective fuel bulk density (is found using equations 3 (page 4) and 14 (page 8) in the Rothermel paper)
     */
    public static List<FlameTemperatureRelatedInfo> getFuelInfoAboutFlameTemperature() {
        Vector<FlameTemperatureRelatedInfo> re = new Vector<FlameTemperatureRelatedInfo>();

        for (StandardCustomizedFuelEnum fuelType : StandardCustomizedFuelEnum.values()) {
            String fuelName = fuelType.toString();
            StandardCustomizedFuel fuel = fuelType.getFuel();

            RothermelModel rothermel = new RothermelModelImp20120322();
            double windSpeed6m = 0;
            double windDirection = 100;
            double slope = 0;
            double upSlopeDirection = 100;
            rothermel.setInputs(fuelType.getFuel(), windSpeed6m, windDirection, slope, upSlopeDirection);

            double B = rothermel.getPackingRatio();
            double Pe = rothermel.getEffectiveHeatingNumber() * rothermel.getBulkDensity();
            double oneMinusBOverPe = (1 - B) / Pe;

            double mineralDampingCoefficient = rothermel.getMineralDampingCoefficient();

            double md1 = fuel.getFuel(0, 0).fuelMoistureContent();
            double md2 = fuel.getFuel(0, 1).fuelMoistureContent();
            double md3 = fuel.getFuel(0, 2).fuelMoistureContent();
            double ml1 = fuel.getFuel(1, 0).fuelMoistureContent();
            double ml2 = fuel.getFuel(1, 1).fuelMoistureContent();

            double wd1 = fuel.getFuel(0, 0).ovendryFuelLoading();
            double wd2 = fuel.getFuel(0, 1).ovendryFuelLoading();
            double wd3 = fuel.getFuel(0, 2).ovendryFuelLoading();
            double wl1 = fuel.getFuel(1, 0).ovendryFuelLoading();
            double wl2 = fuel.getFuel(1, 1).ovendryFuelLoading();

			/*
			 * M_avg = (L1*M1 + L2*M2 + L3*M3 + L4*M4 + L5*M5)/(M1+M2+M3+M4+M5)
			 * where 
			 * L1 is the fuel loading for the 1st subtype, L2 for the second type and so on ...
			 * M1 is the moisture content (%) for the first subtype, M2 for the second type, and so on ...
			 */
            double l_sum = wd1 + wd2 + wd3 + wl1 + wl2;
            double m_avg = (md1 * wd1 + md2 * wd2 + md3 * wd3 + ml1 * wl1 + ml2 * wl2) / l_sum;

            double fln = rothermel.getFlameLength();

            double w = fuelType.getWeightingFactor();
            re.add(new FlameTemperatureRelatedInfo(fuelName, oneMinusBOverPe, mineralDampingCoefficient, md1, md2, md3, ml1, ml2, m_avg, fln, w));

        }


        return re;

    }

    public static class FlameTemperatureRelatedInfo {
        private String fuelName;
        private double oneMinusBOverPe;
        private double mineralDampingCoefficient;

        private double percentMoistureContent_dead1;
        private double percentMoistureContent_dead2;
        private double percentMoistureContent_dead3;
        private double percentMoistureContent_live1;
        private double percentMoistureContent_live2;

        private double avgPercentMoisture;

        private double flameLength;
        private double weightingFactor;


        public FlameTemperatureRelatedInfo(String fuelName, double oneMinusBOverPe, double mineralDampingCoefficient, double percentMoistureContent_dead1,
                                           double percentMoistureContent_dead2, double percentMoistureContent_dead3, double percentMoistureContent_live1, double percentMoistureContent_live2,
                                           double avgPercentMoisture, double flameLength, double weightingFactor) {
            super();
            this.fuelName = fuelName;
            this.oneMinusBOverPe = oneMinusBOverPe;
            this.mineralDampingCoefficient = mineralDampingCoefficient;
            this.percentMoistureContent_dead1 = percentMoistureContent_dead1;
            this.percentMoistureContent_dead2 = percentMoistureContent_dead2;
            this.percentMoistureContent_dead3 = percentMoistureContent_dead3;
            this.percentMoistureContent_live1 = percentMoistureContent_live1;
            this.percentMoistureContent_live2 = percentMoistureContent_live2;
            this.avgPercentMoisture = avgPercentMoisture;
            this.flameLength = flameLength;
            this.weightingFactor = weightingFactor;
        }

        public double getFlameLength() {
            return flameLength;
        }

        public void setFlameLength(double flameLength) {
            this.flameLength = flameLength;
        }

        public double getAvgPercentMoisture() {
            return avgPercentMoisture;
        }

        public void setAvgPercentMoisture(double avgPercentMoisture) {
            this.avgPercentMoisture = avgPercentMoisture;
        }

        public String getFuelName() {
            return fuelName;
        }

        public void setFuelName(String fuelName) {
            this.fuelName = fuelName;
        }

        public double getOneMinusBOverPe() {
            return oneMinusBOverPe;
        }

        public void setOneMinusBOverPe(double oneMinusBOverPe) {
            this.oneMinusBOverPe = oneMinusBOverPe;
        }

        public double getMineralDampingCoefficient() {
            return mineralDampingCoefficient;
        }

        public void setMineralDampingCoefficient(double mineralDampingCoefficient) {
            this.mineralDampingCoefficient = mineralDampingCoefficient;
        }

        public double getPercentMositureContent_dead1() {
            return percentMoistureContent_dead1;
        }

        public void setPercentMositureContent_dead1(double percentMositureContent_dead1) {
            this.percentMoistureContent_dead1 = percentMositureContent_dead1;
        }

        public double getPercentMositureContent_dead2() {
            return percentMoistureContent_dead2;
        }

        public void setPercentMositureContent_dead2(double percentMositureContent_dead2) {
            this.percentMoistureContent_dead2 = percentMositureContent_dead2;
        }

        public double getPercentMositureContent_dead3() {
            return percentMoistureContent_dead3;
        }

        public void setPercentMositureContent_dead3(double percentMositureContent_dead3) {
            this.percentMoistureContent_dead3 = percentMositureContent_dead3;
        }

        public double getPercentMositureContent_live1() {
            return percentMoistureContent_live1;
        }

        public void setPercentMositureContent_live1(double percentMositureContent_live1) {
            this.percentMoistureContent_live1 = percentMositureContent_live1;
        }

        public double getPercentMositureContent_live2() {
            return percentMoistureContent_live2;
        }

        public void setPercentMositureContent_live2(double percentMositureContent_live2) {
            this.percentMoistureContent_live2 = percentMositureContent_live2;
        }

        public double getWeightingFactor() {
            return this.weightingFactor;
        }


    }

    public static void showAspect(File aspectFile) {
        try {
            System.out.println("Loading data ...");
            SingleGISLayer s = new SingleGISLayer(aspectFile);
            System.out.println("Data loaded.");
            CellularAutomataPresentation p = new CellularAutomataPresentation(s.xDim(), s.yDim());

            for (int x = 0; x < s.xDim(); x++)
                for (int y = 0; y < s.yDim(); y++) {
                    double value = s.dataAt(x, y);
                    while (value < 0) value += 360;
                    Color c = Color.getHSBColor(0.8f, (float) ((value % 360) / 360), 1f);
                    p.setCellColor(x, y, c);
                    p.setCellText(x, y, String.format("Aspect=%.2f", value));
                }
            p.drawDirtyCellsInBuffer();
            p.showBufferOnScreen();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CellularAutomataPresentation showSlope(File slopeFile, boolean showBar, final double min, final double max) {
        try {
            System.out.println("Loading data ...");
            SingleGISLayer s = new SingleGISLayer(slopeFile);
            System.out.println("Data loaded.");
            CellularAutomataPresentation p = new CellularAutomataPresentation(s.xDim(), s.yDim());

            for (int x = 0; x < s.xDim(); x++)
                for (int y = 0; y < s.yDim(); y++) {
                    double value = s.dataAt(x, y);
                    if (value < min) value = min;
                    if (value > max) value = max;
                    Color c = slopeToColor(value, max, min);
                    p.setCellColor(x, y, c);
                    p.setCellText(x, y, String.format("Slope=%.2f", value));
                }

            if (showBar) {
                {
                    int xStart = (int) (s.xDim() * 0.2);
                    int xEnd = (int) (s.xDim() * 0.4);
                    int yStart = (int) (s.yDim() * 0.1);
                    int yEnd = (int) (s.yDim() * 0.15);
                    for (int x = xStart; x < xEnd; x++)
                        for (int y = yStart; y < yEnd; y++) {
                            double value = (x - xStart) / (double) (xEnd - xStart) * (max - min) + min;
                            if (value < min) value = min;
                            if (value > max) value = max;
                            Color c = slopeToColor(value, max, min);
                            p.setCellColor(x, y, c);
                            p.setCellText(x, y, String.format("Slope=%.2f", value));
                        }
                }

                {
                    int xStart = (int) (s.xDim() * 0.2);
                    int xEnd = (int) (s.xDim() * 0.4);
                    int xLength = (xEnd - xStart);
                    int yStart = (int) (s.yDim() * 0.2);
                    int yEnd = (int) (s.yDim() * 0.25);
                    for (int x = xStart; x < xEnd; x++)
                        for (int y = yStart; y < yEnd; y++) {
                            double value = 0;
                            if ((x - xStart) < (1 / 6.0) * xLength) value = 0;
                            else if ((x - xStart) < (2 / 6.0) * xLength) value = 3;
                            else if ((x - xStart) < (3 / 6.0) * xLength) value = 6;
                            else if ((x - xStart) < (4 / 6.0) * xLength) value = 9;
                            else if ((x - xStart) < (5 / 6.0) * xLength) value = 12;
                            else value = 15;

                            if (value < min) value = min;
                            if (value > max) value = max;

                            Color c = slopeToColor(value, max, min);
                            p.setCellColor(x, y, c);
                            p.setCellText(x, y, String.format("Slope=%.2f", value));
                        }
                }


            }


            p.drawDirtyCellsInBuffer();
            p.showBufferOnScreen();
            return p;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Color slopeToColor(double value, double max, double min) {
        //return Color.getHSBColor(0.2f, (float)((value)/(max-min)), 1f);
        return Color.getHSBColor(0.5f + (float) ((value) / (max - min) * 0.5), 1f, 1f);
    }
}
