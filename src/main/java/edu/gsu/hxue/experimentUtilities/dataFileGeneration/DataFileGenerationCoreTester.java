package edu.gsu.hxue.experimentUtilities.dataFileGeneration;

public class DataFileGenerationCoreTester
{
	public static void main(String[] args)
	{
		String filePath = "dataTest.txt";
		int numberOfColumns = 10;
		int numberOfRows = 5;
		double cornerXCoordinate = 0.123;
		double cornerYCoordinate = 0.456;
		double cellSize = 15;
		double value = 19;
		DataFileGenerationCore.generateUniformDataFile(filePath, numberOfColumns, numberOfRows, cornerXCoordinate, cornerYCoordinate, cellSize, value);

	}

}
