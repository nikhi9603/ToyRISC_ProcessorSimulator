package processor.pipeline;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;
import generic.Simulator;
import generic.Statistics;
import processor.Processor;
import processor.Clock;

public class Execute implements Element{
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;

	int PC , rs1 , rs2 , rd , opcode , imm;
	int aluResult ;
	long result ;
	boolean overflowCheck ;
	boolean controlHazardCheck ;
	int overflow;
	
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
			overflow = (int) ((long)value >> 32) ;		// upper 32 bits
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

			if ( OF_EX_Latch.isEX_busy() )
			{
				return ;
			}

			controlHazardCheck = false;

			PC = OF_EX_Latch.getProgramCounter() ;
			opcode =  OF_EX_Latch.getOpcode() ;
			rs1 = OF_EX_Latch.getSourceOperand1() ;
			rs2 = OF_EX_Latch.getSourceOperand2() ;
			rd =  OF_EX_Latch.getDestinationOperand() ;
			imm =  OF_EX_Latch.getImmediate() ;

			// System.out.println("Execute" + "opcode- " + opcode + " rs1- " + rs1 + " rs2 " + rs2 + " imm " + imm + " Clock  " + Clock.getCurrentTime());

			// Computing ALU Result

			/* if(opcode == 0 && rd == 0)
			{
				System.out.println("Hello nops");
				OF_EX_Latch.setEX_busy(false);
				OF_EX_Latch.setEX_enable(true);

				EX_MA_Latch.setOpcode(opcode);
				EX_MA_Latch.setDestinationOperand(rd);
				EX_MA_Latch.setSourceOperand1(rs1);
				EX_MA_Latch.setAluResult(0);
				EX_MA_Latch.setIsOverflow(false);
				return ;
			} */
			

			switch(opcode)
			{
				case 0 : 	// add 
				{
					result = ((long)rs1 + (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency , this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 1 : 	// addi
				{
					result = ((long)rs1 + (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency , this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				} 
				case 2 : 	// sub
				{
					result = ((long)rs1 - (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency , this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 3 : 	// subi
				{
					result = ((long)rs1 - (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency , this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 4 : 	// mul
				{
					result = ((long)rs1 * (long)rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.multiplier_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 5 : 	// muli
				{
					result = ((long)rs1 * (long)imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.multiplier_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 6 :	//div
				{
					aluResult = rs1 / rs2;
					EX_MA_Latch.setAluResult(aluResult);
					int remainder = rs1 % rs2;
					overflow = remainder;
					overflowCheck = true;	// remainder is kept in x31 (not overflow in real but have to write something into x31)
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.divider_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 7 :	//divi
				{
					aluResult = rs1 / imm;
					EX_MA_Latch.setAluResult(aluResult);
					int remainder = rs1 % imm;
					overflow = remainder;
					overflowCheck = true;		// remainder is kept in x31 (not overflow in real but have to write something into x31)
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.divider_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 8 :	//and
				{
					aluResult = rs1 & rs2 ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 9 :	//andi
				{
					aluResult = rs1 & imm ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 10 :	//or
				{
					aluResult = rs1 | rs2 ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 11 : 	//ori
				{
					aluResult = rs1 | imm ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 12 :	//xor
				{
					aluResult = rs1 ^ rs2 ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 13 :	//xori
				{
					aluResult = rs1 ^ imm ;
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
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
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
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
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 16 : 	//sll
				{
					result = ((long)rs1 << rs2) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 17 : 	//slli
				{
					result = ((long)rs1 << imm) ;
					aluResult = (int) result ;
					overflowCheck = checkOverflow(result) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 18 : 	//srl
				{
					aluResult = rs1 >>> rs2 ;
					overflowCheck = rightShiftOverflow(rs1 , rs2) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 19 : 	//srli
				{
					aluResult = rs1 >>> imm ;
					overflowCheck = rightShiftOverflow(rs1 , imm) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 20 : 	//sra
				{
					aluResult = rs1 >> rs2 ;
					overflowCheck = rightShiftOverflow(rs1 , rs2) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 21 : 	//srai
				{
					aluResult = rs1 >> imm ;
					overflowCheck = rightShiftOverflow(rs1 , imm) ;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 22 :	//load
				{
					// value at aluResult location will be stored in rd in MA stage
					aluResult = rs1 + imm ;		// no overflow occurs sincd aluresult is a memory address which is < 2^16
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break;
				}
				case 23 :	//store 
				{
					//value at rs1 will be stored in location of aluResult in MA stage
					aluResult = rd + imm ;		// no overflow occurs sincd aluresult is a memory address which is < 2^16
					overflowCheck = false;
					Simulator.getEventQueue().addEvent( new ExecutionCompleteEvent( Clock.getCurrentTime() + Configuration.ALU_latency, this , this ));
					OF_EX_Latch.setEX_busy(true);
					break ;
				}
				case 24 :	//jmp
				{
					PC = PC + rd + imm  ;		// no overflow occurs
					overflowCheck = false;
					containingProcessor.getRegisterFile().setProgramCounter(PC);
					controlHazardCheck =  true ;
					OF_EX_Latch.setEX_busy(false);
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
					overflowCheck = false;
					OF_EX_Latch.setEX_busy(false);
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
					overflowCheck = false;
					OF_EX_Latch.setEX_busy(false);
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
					overflowCheck = false;
					OF_EX_Latch.setEX_busy(false);
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
					overflowCheck = false;
					OF_EX_Latch.setEX_busy(false);
					break ;
				}
				case 29 :
				{
					overflowCheck = false;
					IF_EnableLatch.setIF_enable(false);
					OF_EX_Latch.setEX_busy(false);
				}
				default :
					overflowCheck = false;
					break ;
			}

			// If there is a control hazard , then we will nullify the instruction which was read in instruction fetch and going to execute in OF stage
			// So setting the instruction in IF_OF latch as 0 (nop)
			if(controlHazardCheck)
			{
				// IF_OF_Latch.setInstruction(0);	//nop
				Simulator.getEventQueue().deleteIF_Instruction();
				Statistics.numOfControl_Hazards += 1 ;		// one intruction was entered the pipeline
			}

			if (opcode >= 24)
			{
				OF_EX_Latch.setEX_enable(false);
				EX_MA_Latch.setMA_enable(true);

				EX_MA_Latch.setOpcode(opcode);
				EX_MA_Latch.setDestinationOperand(rd) ;
				EX_MA_Latch.setSourceOperand1(rs1);
				EX_MA_Latch.setAluResult(aluResult);
				EX_MA_Latch.setIsOverflow(overflowCheck);
				EX_MA_Latch.setOverflow(overflow);
			}
		}
	}

	@Override
	public void handleEvent( Event event )
	{
		if (EX_MA_Latch.isMA_busy())
		{
			event.setEventTime( Clock.getCurrentTime() + 1 );
			Simulator.getEventQueue().addEvent( event );
		}
		else
		{
			OF_EX_Latch.setEX_busy(false);
			EX_MA_Latch.setMA_enable(true);
			OF_EX_Latch.setEX_enable(false);

			EX_MA_Latch.setOpcode(opcode);
			EX_MA_Latch.setDestinationOperand(rd) ;
			EX_MA_Latch.setSourceOperand1(rs1);
			EX_MA_Latch.setAluResult(aluResult);
			EX_MA_Latch.setIsOverflow(overflowCheck);
			EX_MA_Latch.setOverflow(overflow);
		}
	}
}
