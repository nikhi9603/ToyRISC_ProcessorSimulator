package processor.pipeline;

import processor.Processor;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performMA()
	{
		//TODO

		if(EX_MA_Latch.isMA_enable())
		{
			
			int opcode = EX_MA_Latch.getOpcode() ;
			int alu_Result = EX_MA_Latch.getAluResult() ;
			//System.out.println("MA - opcode -" + opcode + " aluresult " + alu_Result) ;
			switch(opcode)
			{
				case 22 :
				{
						int result = containingProcessor.getMainMemory().getWord(alu_Result) ;
						MA_RW_Latch.setLoadResult(result) ;
						break ;
				}

				case 23:
				{
						int data = EX_MA_Latch.getSourceOperand1() ;
						containingProcessor.getMainMemory().setWord(alu_Result, data);
						break ;
				}
				case 29:
				{
					IF_EnableLatch.setIF_enable(false);
				}
			}
			
			MA_RW_Latch.setOpcode(opcode) ;
			MA_RW_Latch.setAluResult(alu_Result) ;
			MA_RW_Latch.setDestinationOperand(EX_MA_Latch.getDestinationOperand());


			if ( EX_MA_Latch.isOverflow() == true )
			{
				MA_RW_Latch.setOverflow(EX_MA_Latch.getOverflow());
			}
			MA_RW_Latch.setIsOverflow( EX_MA_Latch.isOverflow() );

			MA_RW_Latch.setRW_enable(true);
			EX_MA_Latch.setMA_enable(false);
		}
	}

}
