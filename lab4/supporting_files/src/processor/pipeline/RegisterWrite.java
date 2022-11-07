package processor.pipeline;

import generic.Simulator;
import processor.Processor;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;

	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch ;
		this.OF_EX_Latch = oF_EX_Latch ;
		this.EX_MA_Latch = eX_MA_Latch ;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable())
		{
			//TODO
			
			int opcode , rd , ldResult , aluResult ;
			opcode = MA_RW_Latch.getOpcode() ;
			rd = MA_RW_Latch.getDestinationOperand() ;
			ldResult = MA_RW_Latch.getLoadResult() ;
			aluResult = MA_RW_Latch.getAluResult() ;
			
			//System.out.println("RW " + "opcode " + opcode + " rd " + rd + " ldRe " + ldResult + " aluR " + aluResult );

			// if instruction being processed is an end instruction, remember to call Simulator.setSimulationComplete(true);
			
			if ( MA_RW_Latch.isOverflow() == true )
			{
				containingProcessor.getRegisterFile().setValue(31, MA_RW_Latch.getOverflow());
			}
			
			if ( opcode < 22 )
			{
				containingProcessor.getRegisterFile().setValue( rd , aluResult );
			}
			else if ( opcode == 22 )
			{
				containingProcessor.getRegisterFile().setValue( rd , ldResult );
			}
			else if ( opcode == 29 )		// end
			{
				Simulator.setSimulationComplete(true);	
				EX_MA_Latch.setMA_enable(false);
				OF_EX_Latch.setEX_enable(false);
				IF_OF_Latch.setOF_enable(false);
				IF_EnableLatch.setIF_enable(false);
			}

			MA_RW_Latch.setRW_enable(false);
		}
	}

}