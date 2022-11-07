package processor.pipeline;

import processor.Processor;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}
	
	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			//TODO
			// Getting instruction from IF_OF_latch
			int instruction = IF_OF_Latch.getInstruction() ;
			int opcode , rs1 , rs2 , rd , imm ;

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
				OF_EX_Latch.setSourceOperand1(rs1) ;

				// rs2 
				instruction = instruction << 5 ;	// removes rs1
				rs2 = instruction >>> 27 ;		// next 5 bits = rs2
				OF_EX_Latch.setSourceOperand2(rs2) ;

				// rd 
				instruction = instruction << 5 ;	// removes rs2
				rd = instruction >>> 27 ;
				OF_EX_Latch.setDestinationOperand(rd) ;
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
				OF_EX_Latch.setSourceOperand1(rs1) ;

				// rd 
				instruction = instruction << 5 ;	// removes rs1
				rd = instruction >>> 27 ;
				OF_EX_Latch.setDestinationOperand(rd) ;

				//imm
				instruction = instruction << 5 ;	// removes rd
				imm = instruction >> 15 ;
				OF_EX_Latch.setImmediate(imm) ;
			}

			// Checking if instruction is of jmp(24) type (RI type)
			if ( opcode == 24 )
			{
				// rd 
				instruction = instruction << 5 ;	// remove opcode bits
				rd = instruction >>> 27 ;
				OF_EX_Latch.setDestinationOperand(rd) ;

				//imm
				instruction = instruction << 5 ;	// remove rd
				imm = instruction >> 10 ;
				OF_EX_Latch.setImmediate(imm) ;
			}
							
			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}
