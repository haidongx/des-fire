package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration;

        import edu.gsu.hxue.gis.SingleGISLayer;
        import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
        import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;
        import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.CellWithPortion;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class FuelFileRegenerator extends DataFileConverter {

    public void convertFile(File oldDataFile, String newDataFilePath, double newCellSize, BorderType borderType, Alignment alignment) throws Exception {
        try {
            //old data
            SingleGISLayer s = new SingleGISLayer(oldDataFile);

            //construct the coordinate converter
            CoordinateConverter con = new CoordinateConverter(s.cellSize(), s.xDim(), s.yDim(), newCellSize, borderType, alignment);

            //construct new data
            double[][] newData = new double[con.getNewXDim()][con.getNewYDim()];
            for (int x = 0; x < con.getNewXDim(); x++)
                for (int y = 0; y < con.getNewYDim(); y++) {
                    // calculate new value
                    List<CellWithPortion> re = con.calculateCoveredOldCells(x, y);

                    if (re.isEmpty())
                        throw new Exception("a new cell covers no old cells");

                    newData[x][y] = 0;

                    // calculate the portion for each fuel type
                    Map<Integer, Double> valueMap = new HashMap<Integer, Double>();
                    for (CellWithPortion cp : re) {
                        if (cp.getCell().getX() >= 0 && cp.getCell().getX() < s.xDim() && cp.getCell().getY() >= 0 && cp.getCell().getY() < s.yDim()) {
                            int key = (int) s.dataAt(cp.getCell().getX(), cp.getCell().getY());
                            if (!valueMap.containsKey(key)) {
                                valueMap.put(key, cp.getPortion());
                            } else {
                                valueMap.put(key, cp.getPortion() + valueMap.get(key));
                            }
                        }
                    }

                    // set the fuel type with most portion as the new fuel type
                    double maxP = 0;
                    for (Integer i : valueMap.keySet()) {
                        if (valueMap.get(i) > maxP) {
                            newData[x][y] = i;
                            maxP = valueMap.get(i);
                        }
                    }

                }

            // save to file
            SingleGISLayer newS = new SingleGISLayer(con.getNewXDim(), con.getNewYDim(), newCellSize, newData);
            File dataFile = new File(newDataFilePath);
            newS.saveToFile(dataFile);
            //System.out.println("New data file generated.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
