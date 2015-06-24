package edu.gsu.hxue.desFire.testing;

import edu.gsu.hxue.desFire.FireSystem;
import edu.gsu.hxue.desFire.FireSystemConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class TestFireSystem {
    public static void main(String[] args) {
        try {
            double time = System.currentTimeMillis();
            FireSystemConfig config = new FireSystemConfig();
            config.showPresentationLayer = true;

            config.fuelFileName = "MooreBranch_fuel.txt";
            config.aspectFileName = "MooreBranch_aspect.txt";
            config.slopeFileName = "MooreBranch_slope.txt";

            config.initialContainedCells = "InitialContainedCells_MB_D.txt";
            config.initialIgnitionFile = "IgnitionPoints_MB_corrected_C.txt";
            config.laterIgnitionFile = "IgnitionPoints_later_empty.txt";

            config.initialWeatherFileName = "weather_artificial_MB.txt";

            config.heatUpdateInterval = 3600;

            FireSystem sys = new FireSystem(config);
            System.out.println("Construction time: " + (System.currentTimeMillis() - time));

            //result test
            time = System.currentTimeMillis();
            System.out.println("Simulation started at simulation time " + sys.getSystemTime() + "s");

            //FireSystem c = (FireSystem) sys.clone();
            sys.forceToUpdateWeather(5, 125);
            sys.run(3600 * 14);


            System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + "MB");
            System.out.println("Simulation finished at simulation time " + sys.getSystemTime() + "s");
            System.out.println("Execution time: " + (System.currentTimeMillis() - time));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static FireSystem deserializeFrom(String file) {
        System.out.print("Deserializing ... ");
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            FireSystem sys_ser = (FireSystem) in.readObject();
            in.close();
            fileIn.close();
            System.out.print("Deserialized");
            return sys_ser;
        } catch (IOException i) {
            i.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
