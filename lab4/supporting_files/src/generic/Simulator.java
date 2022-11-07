package generic;

import processor.Clock;
import processor.Processor;
import processor.memorysystem.MainMemory;
import processor.pipeline.RegisterFile;

import java.io.*;

public class Simulator {

	static Processor processor;
	static boolean simulationComplete;


	public static void setupSimulation(String assemblyProgramFile, Processor p) {
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);

		simulationComplete = false;

		Statistics.numOfData_Hazards = 0;
		Statistics.numOfControl_Hazards = 0;

	}


	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */

		// For reading the assembly program file it should be input stream
		FileInputStream inputstream = null ;
		DataInputStream binaryFile = null ;

		try 
		{
			inputstream = new FileInputStream(assemblyProgramFile) ;
			binaryFile = new DataInputStream(inputstream) ;
		} catch (Exception e) 
		{
			Misc.printErrorAndExit(" Data input stream not created \n");
		}

		// Getting mainMemory from processor
		MainMemory mainMemory = processor.getMainMemory() ;

		/* 2. SET PC TO THE FIRST INSTRUCTION IN THE MAIN */
		// Now instruction in first line of binaryFile contains the address of the first instruction in the main 
		try 
		{
			int firstInstruction = binaryFile.readInt() ;
			
			// We need to get register file and then update its PC
			RegisterFile registerFile = processor.getRegisterFile() ;
			registerFile.setProgramCounter(firstInstruction);

			/* 3.SETTING x0,x1,x2 TO THEIR INITIAL VALUES */
			registerFile.setValue( 0 , 0 );		// x0 = 0
			registerFile.setValue( 1 , 65535 ); 	// Stack pointer x1 = 65535
			registerFile.setValue( 2 , 65535 ); 	// Frame pointer x2 = 65535

			// setting this register file to processor
			processor.setRegisterFile(registerFile);	
		} 
		catch (Exception e) 
		{
			Misc.printErrorAndExit("First Instruction cannot be read from binary file \n");
		}
				
		/* 1.LOADING PROGRAM INTO MEMORY */

		// According to memory layout we have to set data segment and then followed by code/text segment in memory from 0 
		/* The available() method is a built-in method of the Java.io.ByteArrayInputStream 
		   returns the number of remaining bytes that can be read (or skipped over) from this input stream */
		try 
		{
			int address = 0 ;

			while  ( binaryFile.available() > 0 )
			{
				int instruction = binaryFile.readInt() ;
				mainMemory.setWord( address , instruction );
				address++ ;
			}

			// setting updated mainMemory to the processor 
			processor.setMainMemory(mainMemory) ;
		} 
		catch (IOException e) 
		{
			Misc.printErrorAndExit("Exception when reading instruction from binary file \n" );
			e.printStackTrace();
		}	
	}

	public static void simulate() 
	{
		// The number of instructions executed
		int numberOfCycles = 0;
		while (simulationComplete == false) {
			processor.getRWUnit().performRW();
			processor.getMAUnit().performMA();
			processor.getEXUnit().performEX();
			processor.getOFUnit().performOF();
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			
			++numberOfCycles ;
		}

		// TODO
		// set statistics
		
		//Setting the current clock time which will be the number of cycles occured
		//Also set the number of instructions
		Statistics.setNumberOfCycles(numberOfCycles);
	}

	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}
}
