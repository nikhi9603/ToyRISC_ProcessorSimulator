package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfCycles;
	public static int numOfData_Hazards ;
	public static int numOfControl_Hazards ;
	public static int numberOfInstructions ;

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println("NUmber of Data Hazards = " + numOfData_Hazards);
			writer.println("Number of Control Hazards = " + numOfControl_Hazards);
			writer.println("Instruction count per cycle = " + (double)( (double) numberOfInstructions / (double)numberOfCycles));

			
			// TODO add code here to print statistics in the output file
			
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}
}
