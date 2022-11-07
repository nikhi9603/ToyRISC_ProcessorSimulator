package processor.pipeline;


import processor.Processor;
import generic.Statistics ;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch ;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch , EX_MA_LatchType eX_MA_Latch , MA_RW_LatchType mA_RW_Latch  , IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public boolean isConflictObserved( int op1 , int op2 )
	{
		// Checking the data hazards i.e. RAW(Read after write)
		// We want to make sure that every operand is read correct in operand fetch
		// We are checking if op1 , op2 have conflicts the destination operand values of instructions in EX , MA , RW stages
		int MA_stage_opcode = EX_MA_Latch.getOpcode() ;
		int RW_stage_opcode = MA_RW_Latch.getOpcode() ;

		// If other stage instructions are other than branch instructions and store , end we can check for data hazard
		// Since in these branch , store , end instructions we dont have any register writing
		// Initialising those rd as some negative value and if it is an instruction to check for data hazard it has rd value
		int MA_stage_rd = -2 ;
		int RW_stage_rd = -2 ;


		if ( MA_stage_opcode <= 22 )
		{
			MA_stage_rd = EX_MA_Latch.getDestinationOperand() ;
		}

		if ( RW_stage_opcode <= 22 )
		{
			RW_stage_rd = MA_RW_Latch.getDestinationOperand() ;
		}

		// check for data hazards
		if (   ( op1 == MA_stage_rd && MA_stage_rd != 0 ) || ( op2 == MA_stage_rd && MA_stage_rd != 0)
		   || ( op1 == RW_stage_rd && RW_stage_rd != 0 ) || ( op2 == RW_stage_rd && RW_stage_rd != 0) )
		{
			return true ;		// there exists RAW data hazard
		}

		// Checking for overflow 
		// If it has overflow in other stage instructions and x31 here is op1 or op2 then we have data hazard
		if ( op1 == 31 || op2 == 31 )
		{
			if ( EX_MA_Latch.isOverflow() == true || MA_RW_Latch.isOverflow() == true )
			{
				return true ;
			}
		}
		return false ;
	}

	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			
			//TODO
			// Getting instruction from IF_OF_latch
			int instruction = IF_OF_Latch.getInstruction() ;
			int opcode , rs1 = -1 , rs2 = -1 , rd = -1 , imm = -1 ;
			Boolean conflictCheck = false ;
			int value1 , value2 ;

			// Now instruction should be divided into its respective operands rs1,rs2,rd,imm based on opcode
			// Fetching opcode which is first 5 bits of instruction
			opcode = instruction >>> 27 ;
			OF_EX_Latch.setOpcode( opcode ) ;

			// According to opcode : we will get to know which type of instruction it is R3 or R2I or RI
			// Checking if it is R3 
			// R3 instructions :  add(0) , sub(2) , mul(4) , div(6) , and(8) , or(10) , xor(12) , 
			// 					  slt(14) , sll(16) , srl(18) , sra(20) i.e. opcode <= 20 and divisible by 2
			if ( (opcode <= 20) && (opcode % 2 == 0) )
			{
				// rs1
				instruction = instruction << 5 ;	// opcode - 5 bits is taken off
				rs1 = instruction >>> 27 ;			// next 5 bits = rs1

				// rs2 
				instruction = instruction << 5 ;	// removes rs1
				rs2 = instruction >>> 27 ;		// next 5 bits = rs2

				// rd 
				instruction = instruction << 5 ;	// removes rs2
				rd = instruction >>> 27 ;

				conflictCheck = isConflictObserved( rs1 , rs2 ) ;		// R3 type we will read values of rs1 , rs2 so we have to check conflicts only for rs1 , rs2

				value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
				value2 = containingProcessor.getRegisterFile().getValue(rs2) ;
				OF_EX_Latch.setSourceOperand1(value1);
				OF_EX_Latch.setSourceOperand2(value2);
				OF_EX_Latch.setDestinationOperand(rd);
			}

			// Checking if instruction is of R2I type 
			// R2I instructions :  addi(1) , subi(3) , muli(5) , divi(7) , andi(9) , ori(11) , xori(13) , 
			// 					  slti(15) , slli(17) , srli(19) , srai(21) i.e. opcode <= 21 and not divisible by 2
			// 					  load(22) , store(23) , beq(25) , bne(26) , blt(27) , bgt(28) i.e. 21 < opcode <= 28 and opcode != 24
			if ( ((opcode <= 21) && (opcode % 2 != 0)) || ((opcode <= 28 && opcode > 21) && (opcode != 24)) )
			{
				// rs1
				instruction = instruction << 5 ;	// opcode - 5 bits is taken off
				rs1 = instruction >>> 27 ;			// next 5 bits = rs1

				// rd 
				instruction = instruction << 5 ;	// removes rs1
				rd = instruction >>> 27 ;

				//imm
				instruction = instruction << 5 ;	// removes rd
				imm = instruction >> 15 ;
			}

			// Checking if instruction is of jmp(24) type (RI type)
			if ( opcode == 24 )
			{
				// rd 
				instruction = instruction << 5 ;	// remove opcode bits
				rd = instruction >>> 27 ;

				//imm
				instruction = instruction << 5 ;	// remove rd
				imm = instruction >> 10 ;
			}
			
			// setting values in registers as rs1 , rs2 , rd if needed
			switch(opcode)
			{
				case 1 : 	// addi
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				} 
				case 3 : 	// subi
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				}
				case 5 : 	// muli
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				}
				case 7 :	//divi
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				}
				case 9 :	//andi
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 11 : 	//ori
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 13 :	//xori
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				}
				case 15 : 	//slti
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 17 : 	//slli
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 19 : 	//srli
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 21 : 	//srai
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break ;
				}
				case 22 :	//load
				{
					conflictCheck = isConflictObserved(rs1, rs1) ;		// Here we will only read rs1 so conflict check is only for rs1
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(rd);
					break;
				}
				case 23 :	//store 
				{
					conflictCheck = isConflictObserved(rs1, rd) ;		// Here we will read rs1 , rd so conflict check is only for rs1,rd
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					value2 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value2);
					break ;
				}
				case 24 :	//jmp
				{
					conflictCheck = isConflictObserved(rd, rd) ;		// Here we will only read rd so conflict check is only for rd
					value1 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value1);
					break ; 
				}
				case 25 : 	//beq
				{
					conflictCheck = isConflictObserved(rs1, rd) ;		// Here we will read rs1 , rd so conflict check is only for rs1,rd
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					value2 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value2);
					break ;
				}
				case 26 : 	//bne
				{
					conflictCheck = isConflictObserved(rs1, rd) ;		// Here we will read rs1 , rd so conflict check is only for rs1,rd
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					value2 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value2);
					break ;
				}
				case 27 : 	//blt
				{
					conflictCheck = isConflictObserved(rs1, rd) ;		// Here we will read rs1 , rd so conflict check is only for rs1,rd
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					value2 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value2);
					break ;
				}
				case 28 : 	//bgt
				{
					conflictCheck = isConflictObserved(rs1, rd) ;		// Here we will read rs1 , rd so conflict check is only for rs1,rd
					value1 = containingProcessor.getRegisterFile().getValue(rs1) ;
					value2 = containingProcessor.getRegisterFile().getValue(rd) ;
					OF_EX_Latch.setSourceOperand1(value1);
					OF_EX_Latch.setImmediate(imm);
					OF_EX_Latch.setDestinationOperand(value2);
					break ;
				}
				case 29 :	//end
				{
					IF_OF_Latch.setOF_enable(false);
					System.out.println("End");
					break ;
				}
				default :
					break ;
			}

			// if there exists data hazard
			if(conflictCheck)
			{
				OF_EX_Latch.setNopInstruction();
				IF_EnableLatch.setIF_enable(false);
				IF_OF_Latch.setOF_enable(true);
				Statistics.numOfData_Hazards ++ ;
				//IF_OF_Latch.setInstruction(instruction);
			}
			else
			{
				IF_EnableLatch.setIF_enable(true);
				IF_OF_Latch.setOF_enable(false);
			}

			if(opcode == 29)
			{
				IF_EnableLatch.setIF_enable(false);
			}
			
			//System.out.println("OF " + "opcode " + opcode + " rs1 " + rs1 + " rs2 " + rs2 + " imm " + imm) ;
			OF_EX_Latch.setProgramCounter(IF_OF_Latch.getPC());
			OF_EX_Latch.setEX_enable(true);
		}
	}
}
