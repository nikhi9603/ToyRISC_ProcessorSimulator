package processor.pipeline;

import generic.Statistics;
import processor.Processor;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch , IF_OF_LatchType iF_OF_Latch , IF_EnableLatchType iF_EnableLatchType)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatchType;
	}

	public boolean checkOverflow( long value )
	{
		if ( (value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE) )
		{
			int overflow = (int) ((long)value >> 32) ;		// upper 32 bits
			EX_MA_Latch.setOverflow(overflow) ;
			return true ;	
		}
		return false ;
	}

	public boolean rightShiftOverflow( int rs1 , int rs2 )
	{
		if ( rs2 != 0 )
		{
			int removedBits = (rs1 << (32 - rs2)) >>> (32 - rs2) ;
			EX_MA_Latch.setOverflow(removedBits) ;
			return true ;
		}
		return false ;
		
	}

	
	public void performEX()
	{
		//TODO
		
		if (OF_EX_Latch.isEX_enable()){
			
			int PC = OF_EX_Latch.getProgramCounter() ;
			int opcode =  OF_EX_Latch.getOpcode() ;
			int rs1 = OF_EX_Latch.getSourceOperand1() ;
			int rs2 = OF_EX_Latch.getSourceOperand2() ;
			int rd =  OF_EX_Latch.getDestinationOperand() ;
			int imm =  OF_EX_Latch.getImmediate() ;
			int aluResult ;
			long result ;
			boolean overflowCheck ;
			boolean controlHazardCheck = false ;
			//System.out.println("Execute" + "opcode- " + opcode + " rs1- " + rs1 + " rs2 " + rs2 + " imm " + imm);

			// Computing ALU Result
			switch(opcode)
			{
				case 0 : 	// add 
				{
					result = ((long)rs1 + (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				}
				case 1 : 	// addi
				{
					result = ((long)rs1 + (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				} 
				case 2 : 	// sub
				{
					result = ((long)rs1 - (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				}
				case 3 : 	// subi
				{
					result = ((long)rs1 - (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				}
				case 4 : 	// mul
				{
					result = ((long)rs1 * (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				}
				case 5 : 	// muli
				{
					result = ((long)rs1 * (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break;
				}
				case 6 :	//div
				{
					aluResult = rs1 / rs2;
					EX_MA_Latch.setAluResult(aluResult);
					int remainder = rs1 % rs2;
					EX_MA_Latch.setOverflow(remainder);
					EX_MA_Latch.setIsOverflow(true) ;		// remainder is kept in x31 (not overflow in real but have to write something into x31)
					break;
				}
				case 7 :	//divi
				{
					aluResult = rs1 / imm;
					EX_MA_Latch.setAluResult(aluResult);
					int remainder = rs1 % imm;
					EX_MA_Latch.setOverflow(remainder);
					EX_MA_Latch.setIsOverflow(true) ;		// remainder is kept in x31 (not overflow in real but have to write something into x31)
					break;
				}
				case 8 :	//and
				{
					aluResult = rs1 & rs2 ;
					EX_MA_Latch.setAluResult(aluResult);
					EX_MA_Latch.setIsOverflow(false) ;
					break;
				}
				case 9 :	//andi
				{
					aluResult = rs1 & imm ;
					EX_MA_Latch.setAluResult(aluResult) ;
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 10 :	//or
				{
					aluResult = rs1 | rs2 ;
					EX_MA_Latch.setAluResult(aluResult);
					EX_MA_Latch.setIsOverflow(false) ;
					break;
				}
				case 11 : 	//ori
				{
					aluResult = rs1 | imm ;
					EX_MA_Latch.setAluResult(aluResult) ;
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 12 :	//xor
				{
					aluResult = rs1 ^ rs2 ;
					EX_MA_Latch.setAluResult(aluResult);
					EX_MA_Latch.setIsOverflow(false) ;
					break;
				}
				case 13 :	//xori
				{
					aluResult = rs1 ^ imm ;
					EX_MA_Latch.setAluResult(aluResult);
					EX_MA_Latch.setIsOverflow(false) ;
					break;
				}
				case 14 :	//slt
				{
					if ( rs1 < rs2 )
					{
						aluResult = 1 ;
					}
					else
					{
						aluResult = 0 ;
					}
					EX_MA_Latch.setIsOverflow(false) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 15 : 	//slti
				{
					if ( rs1 < imm )
					{
						aluResult = 1 ;
					}
					else
					{
						aluResult = 0 ;
					}
					EX_MA_Latch.setIsOverflow(false) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 16 : 	//sll
				{
					result = ((long)rs1 << rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 17 : 	//slli
				{
					result = ((long)rs1 << imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 18 : 	//srl
				{
					aluResult = rs1 >>> rs2 ;
					overflowCheck = rightShiftOverflow(rs1 , rs2) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 19 : 	//srli
				{
					aluResult = rs1 >>> imm ;
					overflowCheck = rightShiftOverflow(rs1 , imm) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 20 : 	//sra
				{
					aluResult = rs1 >> rs2 ;
					overflowCheck = rightShiftOverflow(rs1 , rs2) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 21 : 	//srai
				{
					aluResult = rs1 >> imm ;
					overflowCheck = rightShiftOverflow(rs1 , imm) ;
					EX_MA_Latch.setIsOverflow(overflowCheck) ;
					EX_MA_Latch.setAluResult(aluResult) ;
					break ;
				}
				case 22 :	//load
				{
					// value at aluResult location will be stored in rd in MA stage
					aluResult = rs1 + imm ;		// no overflow occurs sincd aluresult is a memory address which is < 2^16
					EX_MA_Latch.setAluResult(aluResult) ;
					EX_MA_Latch.setIsOverflow(false) ;
					break;
				}
				case 23 :	//store 
				{
					//value at rs1 will be stored in location of aluResult in MA stage
					aluResult = rd + imm ;		// no overflow occurs sincd aluresult is a memory address which is < 2^16
					EX_MA_Latch.setAluResult(aluResult) ;
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 24 :	//jmp
				{
					PC = PC + rd + imm  ;		// no overflow occurs
					EX_MA_Latch.setIsOverflow(false) ;
					containingProcessor.getRegisterFile().setProgramCounter(PC);
					controlHazardCheck =  true ;
					break ; 
				}
				case 25 : 	//beq
				{
					if ( rs1 == rd )
					{
						PC = PC + imm ;
						controlHazardCheck = true ;
						containingProcessor.getRegisterFile().setProgramCounter(PC);
					}
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 26 : 	//bne
				{
					if ( rs1 != rd )
					{
						PC = PC + imm ;
						controlHazardCheck = true ;
						containingProcessor.getRegisterFile().setProgramCounter(PC);
					}
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 27 : 	//blt
				{
					if ( rs1 < rd )
					{
						PC = PC + imm ;
						controlHazardCheck = true ;
						containingProcessor.getRegisterFile().setProgramCounter(PC);
					}
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 28 : 	//bgt
				{
					if ( rs1 > rd )
					{
						PC = PC + imm ;
						controlHazardCheck = true ;
						containingProcessor.getRegisterFile().setProgramCounter(PC);
					}
					EX_MA_Latch.setIsOverflow(false) ;
					break ;
				}
				case 29 :
				{
					IF_EnableLatch.setIF_enable(false);
				}
				default :
					EX_MA_Latch.setIsOverflow(false);
					break ;
			}

			// If there is a control hazard , then we will nullify the instruction which was read in instruction fetch and going to execute in OF stage
			// So setting the instruction in IF_OF latch as 0 (nop)
			if(controlHazardCheck)
			{
				IF_OF_Latch.setInstruction(0);	//nop
				Statistics.numOfControl_Hazards += 1 ;		// one intruction was entered the pipeline
			}

			EX_MA_Latch.setOpcode(opcode) ;
			EX_MA_Latch.setDestinationOperand(rd) ;
			EX_MA_Latch.setSourceOperand1(rs1);

			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);
		}
	}
}
