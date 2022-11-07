package processor.pipeline;

import generic.Simulator;
import processor.Processor;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
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
			}

			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		}
	}

}
