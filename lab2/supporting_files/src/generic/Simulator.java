package generic;

import generic.Operand.OperandType;
import java.io.* ;
import java.math.BigInteger;

public class Simulator {
		
	static FileInputStream inputcodeStream = null;

	public static void setupSimulation(String assemblyProgramFile, String objectProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.setFirstCodeAddress(firstCodeAddress);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	public static void assemble(String objectProgramFile)
	{
		FileOutputStream outputStream = null ;
		DataOutputStream outputFile = null ;

		// handling exceptions while creating an object for dataoutput stream
		try 
		{		
			// creating an object for dataoutput stream
			outputStream = new FileOutputStream(objectProgramFile) ;
			outputFile = new DataOutputStream(outputStream) ;	

		} 
		catch (Exception e) 
		{
			Misc.printErrorAndExit(" DataOutputStream object is not created : " + e );
		}

		// handling exceptions while writing the header into the output file 
		try 
		{
			// Header consists of address of first instruction
			/* We know that first instruction address is stored in firstCodeAddress since ParsedDataSection returns
			first instruction address and that is stored in firstCodeAddress of setupSimulation
			*/
			int firstCodeAddress = ParsedProgram.firstCodeAddress ;
			outputFile.writeInt(firstCodeAddress) ;		// writing this value into 4 bytes	
		}
		catch (Exception e) 
		{
			Misc.printErrorAndExit(" First Instruction Address is not wtitten properly :  " + e );
		}

		// handling exceptions while writing the data into the output file 
		try 
		{
			// Now data is present from address 0 to firstCodeAddress
			for ( int i = 0 ; i < ParsedProgram.firstCodeAddress ; i++ )
			{
				// In arraylist data of parsed program data section will be there from index 0 to firstCodeAddress
				// writing that into outputfile
				outputFile.writeInt(ParsedProgram.data.get(i));
			}	
		} 
		catch (Exception e) 
		{
			Misc.printErrorAndExit(" Data Section not added properly :  " + e );
		}

		for ( int addr = ParsedProgram.firstCodeAddress; addr < ParsedProgram.firstCodeAddress + ParsedProgram.code.size()  ; addr++ ) 
		{
			Instruction newInstruction = new Instruction() ;
			// taking each instruction at those addr in code ( list of Instructions ) in Parsedprogram
			newInstruction = ParsedProgram.getInstructionAt( addr ) ;

			// This string is used to store binarystring representation of instruction
			String binaryString = null ;

			// According to type of Instructions , we will use different functions to get binaryString of instruction
			switch (newInstruction.getOperationType())
			{
				// R3 type
				case add : 
				case sub : 
				case mul : 
				case div : 
				case and : 
				case or : 
				case xor : 
				case slt : 
				case sll : 
				case srl : 
				case sra :
				{
					// This string is used to store binarystring representation of instruction
					binaryString = R3_type( newInstruction ) ;  

					break ;
				}
					
				//R2I type
				case addi :
				case subi :
				case muli :
				case divi : 
				case andi : 
				case ori : 
				case xori : 
				case slti : 
				case slli : 
				case srli : 
				case srai :
				case load :
				case store :
				{
					// This string is used to store binarystring representation of instruction
					binaryString = R2I_type( newInstruction ) ;  

					break ;
				}
				case beq : 
				case bne : 
				case blt : 
				case bgt :
				{
					// This string is used to store binarystring representation of instruction
					binaryString = R2I_type1( newInstruction , addr ) ;  

					break ;
				}
				case jmp :
				{
					// This string is used to store binarystring representation of instruction
					binaryString = RI_type( newInstruction , addr ) ;  

					break ;
				}

				case end :
				{
					// This string is used to store binarystring representation of instruction
					binaryString = RI_type1( newInstruction ) ;  

					break ;
				}

				default :
					Misc.printErrorAndExit("Error while finding operation type \n");
			}

			
			// Conversion of binary string to bigInteger then storing into a integer using intValue
			int intOfBinary = new BigInteger(binaryString , 2 ).intValue() ;

			try 
			{
				// writing integer corresponding to instruction into output file in binary format
				outputFile.writeInt( intOfBinary );	
			} 
			catch (Exception e) 
			{
				//TODO: handle exception
				Misc.printErrorAndExit("Tntruction not wrtiiten properlu  \n" + addr );
			}
			
		}


	}
	
	private static String R3_type( Instruction newInstruction )
	{
		// storing opcode , rs1 , rs2 , rd , unused
		String opcode = null ;

		// rs1 , rs2 , rd are of 5 bits each
		// Formatting to 5 bits
		String rs1 , rs2 , rd ;

		rs1 = String.format("%5s", Integer.toBinaryString(newInstruction.getSourceOperand1().getValue()) ) ;
		rs1 = rs1.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		rs2 = String.format("%5s", Integer.toBinaryString(newInstruction.getSourceOperand2().getValue()) ) ;
		rs2 = rs2.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		rd = String.format("%5s", Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ) ;
		rd = rd.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		// there are unused 12 bits
		String unused = "000000000000" ;

		switch(newInstruction.getOperationType())
		{
			// R3 type
			case add:
			{
				opcode="00000";
				break;
			}
			case sub:
			{
				opcode="00010";
				break;
			}
			case mul:
			{
				opcode="00100";
				break;
			}
			case div:
			{
				opcode="00110";
				break;
			}
			case and:
			{
				opcode="01000";
				break;
			}
			case or:
			{
				opcode="01010";
				break;
			}
			case xor:
			{
				opcode="01100";
				break;
			}
			case slt:
			{
				opcode="01110";
				break;
			}
			case sll:
			{
				opcode="10000";
				break;
			}
			case srl:
			{
				opcode="10010";
				break;
			}
			case sra:
			{
				opcode="10100";
				break;
			}
			default:
				break ;
		}

		String binaryString = opcode + rs1 + rs2 + rd + unused ;
		return binaryString ;
	}

	private static String R2I_type( Instruction newInstruction )
	{
		// storing opcode , rs1 , rs2 , rd , unused
		String opcode = null ;

		// rs1 , rd are of 5 bits each
		// Formatting to 5 bits
		String rs1 , rd ;

		rs1 = String.format("%5s", Integer.toBinaryString(newInstruction.getSourceOperand1().getValue()) ) ;
		rs1 = rs1.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		

		rd = String.format( "%5s", Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ) ;
		rd = rd.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

				
		// String immediate
		String imm = null ;

		if(newInstruction.getSourceOperand2().getOperandType() == OperandType.valueOf("Immediate"))
		{
			if ( newInstruction.getSourceOperand2().getValue() >= 0 )
			{
				imm = String.format("%17s", Integer.toBinaryString(newInstruction.getSourceOperand2().getValue()) ) ;
				imm = imm.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	
			}
			else
			{
				imm = Integer.toBinaryString(newInstruction.getSourceOperand2().getValue()) ;
				imm = imm.substring(15, 32) ;
			}
		}
		else
		{
			String label = newInstruction.getSourceOperand2().getLabelValue() ;
			Integer labelAddress = ParsedProgram.symtab.get(label) ;

			imm = String.format("%17s", Integer.toBinaryString(labelAddress)) ;
			imm = imm.replace(" ", "0") ;

			
		}

		switch(newInstruction.getOperationType())
		{
			case addi:
			{
				opcode="00001";
				break;
			}
			case subi:
			{
				opcode="00011";
				break;
			}
			case muli:
			{
				opcode="00101";
				break;
			}
			case divi:
			{
				opcode="00111";
				break;
			}
			case andi:
			{
				opcode="01001";
				break;
			}
			case ori:
			{
				opcode="01011";
				break;
			}
			case xori:
			{
				opcode="01101";
				break;
			}
			case slti:
			{
				opcode="01111";
				break;
			}
			case slli:
			{
				opcode="10001";
				break;
			}
			case srli:
			{
				opcode="10011";
				break;
			}
			case srai:
			{
				opcode="10101";
				break;
			}
			case load :
			{
				opcode="10110";
				break;
			}
			case store :
			{
				opcode="10111";
				break;
			}
			default :
				break ;

		}

		String binaryString = opcode + rs1 + rd + imm ;
		return binaryString ;
	}

	private static String R2I_type1 ( Instruction newInstruction , int addr )
	{
		// storing opcode , rs1 , rs2 , rd , unused
		String opcode = null ;

		// rs1 , rd are of 5 bits each
		// Formatting to 5 bits
		String rs1 , rd ;

		rs1 = String.format( "%5s", Integer.toBinaryString(newInstruction.getSourceOperand1().getValue()) ) ;
		rs1 = rs1.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		rd = String.format( "%5s", Integer.toBinaryString(newInstruction.getSourceOperand2().getValue()) ) ;
		rd = rd.replace(" ", "0" ) ;	// Since integer will be written into atmost last 4 bits

		// imm
		String imm = null ;


		if(newInstruction.getDestinationOperand().getOperandType() == OperandType.valueOf("Immediate"))
		{
			if(newInstruction.getDestinationOperand().getValue() >= 0 )
			{
				imm = String.format( "%17s", Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ) ;
				imm = imm.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	
			}
			else
			{
				imm = Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ;
				imm = imm.substring(15, 32) ;
			}
		}
		else
		{
			String label = newInstruction.getDestinationOperand().getLabelValue() ;
			Integer labelAddress = ParsedProgram.symtab.get(label) ;

			Integer immediateValue = labelAddress - addr ;

			if(immediateValue >= 0 )
			{
				imm = String.format( "%17s", Integer.toBinaryString(immediateValue) ) ;
				imm = imm.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	
			}
			else
			{
				imm = Integer.toBinaryString(immediateValue) ;
				imm = imm.substring(15, 32) ;
			}
		}

		switch(newInstruction.getOperationType())
		{
			case beq : 
			{
				opcode="11001";
				break;
			}
			case bne :
			{
				opcode="11010";
				break;
			} 
			case blt : 
			{
				opcode="11011";
				break;
			}
			case bgt :
			{
				opcode="11100";
				break;
			}
			default:
				break ;
		}

		String binaryString = opcode + rs1 + rd + imm ;
		return binaryString ;
		
	}

	private static String RI_type( Instruction newInstruction , int addr )
	{
		// storing opcode , rs1 , rs2 , rd , unused
		String opcode = "11000" ;

		// rd , imm any one of them can be used
		String rd = null ;
		String imm = null ;

		// If rd is used then imm is set to 0
		// Else rd is to 0
		if(newInstruction.getDestinationOperand().getOperandType() == OperandType.valueOf("Immediate"))
		{
			if(newInstruction.getDestinationOperand().getValue() >= 0 )
			{
				imm = String.format( "%22s", Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ) ;
				imm = imm.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	
			}
			else
			{
				imm = Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ;
				imm = imm.substring(10 , 32) ;
			}
			rd = "00000" ;
		}
		else if (newInstruction.getDestinationOperand().getOperandType() == OperandType.valueOf("Label"))
		{
			String label = newInstruction.getDestinationOperand().getLabelValue() ;
			Integer labelAddress = ParsedProgram.symtab.get(label) ;

			Integer immediateValue = labelAddress - addr ;

			if(immediateValue  >= 0 )
			{
				imm = String.format( "%22s", Integer.toBinaryString(immediateValue) ) ;
				imm = imm.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	
			}
			else
			{
				imm = Integer.toBinaryString(immediateValue) ;
				imm = imm.substring(10, 32) ;
			}
			
			rd = "00000" ;
		}
		else
		{
			rd = String.format( "%5s", Integer.toBinaryString(newInstruction.getDestinationOperand().getValue()) ) ;
			rd= rd.replace(" ", "0" ) ;	// If negative toBinaryString gives 2's complement of that in 32 bits but we take only 17 bits	

			imm = "0000000000000000000000" ;
		}


		String binaryString = opcode + rd + imm ;
		return binaryString ;
	}

	private static String RI_type1( Instruction newInstruction )
	{
		String opcode = "11101" ;
		String imm = "000000000000000000000000000" ;

		String binaryString = opcode + imm ;
		return binaryString ;
	}
}





