package processor.pipeline;


import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;

public class MemoryAccess implements Element {
	Processor containingProcessor;
	public EX_MA_LatchType EX_MA_Latch;
	public MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public OF_EX_LatchType OF_EX_Latch;
	public IF_OF_LatchType IF_OF_Latch;


	static  int opcode , rs1 , alu_Result , rd;
	int loadResult ;
	boolean overflowCheck ;
	int overflow ;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch , OF_EX_LatchType oF_EX_Latch , IF_OF_LatchType iF_OF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch ;
		this.IF_OF_Latch = iF_OF_Latch;
	}
	
	public void performMA()
	{
		//TODO
		if(EX_MA_Latch.isMA_enable())
		{
			
			if(EX_MA_Latch.isMA_busy())
			{
				return ;
			}

			opcode = EX_MA_Latch.getOpcode() ;
			rs1 = EX_MA_Latch.getSourceOperand1() ;
			alu_Result = EX_MA_Latch.getAluResult() ;
			rd = EX_MA_Latch.getDestinationOperand() ;
			overflowCheck = EX_MA_Latch.isOverflow() ;
			overflow = EX_MA_Latch.getOverflow() ;

			//System.out.println("MA - opcode -" + opcode + " aluresult " + alu_Result + " Clock "+ Clock.getCurrentTime()) ;

			switch(opcode)
			{
				case 22 :
				{
						Simulator.getEventQueue().addEvent(
							new MemoryReadEvent(
								Clock.getCurrentTime() + Configuration.mainMemoryLatency , 
								this , 
								containingProcessor.getMainMemory() , 
								alu_Result ) );
						EX_MA_Latch.setMA_busy(true);
						OF_EX_Latch.setEX_busy(true);
						IF_OF_Latch.setOF_busy(true);
						break ;
				}

				case 23:
				{
						Simulator.getEventQueue().addEvent(
							new MemoryWriteEvent(
								Clock.getCurrentTime() + Configuration.mainMemoryLatency , 
								this , 
								containingProcessor.getMainMemory() ,  
								alu_Result ,
								rs1 ) );
						EX_MA_Latch.setMA_busy(true);
						OF_EX_Latch.setEX_busy(true);
						IF_OF_Latch.setOF_busy(true);
						break ;
				}
				case 29:
				{
					IF_EnableLatch.setIF_enable(false);
				}
			}
			
			if ( opcode != 22 && opcode != 23)
			{
				EX_MA_Latch.setMA_busy(false);
				MA_RW_Latch.setOpcode(opcode) ;
				MA_RW_Latch.setAluResult(alu_Result) ;
				MA_RW_Latch.setDestinationOperand(EX_MA_Latch.getDestinationOperand());
				EX_MA_Latch.setDestinationOperand(0);

				if ( EX_MA_Latch.isOverflow() == true )
				{
					MA_RW_Latch.setOverflow(EX_MA_Latch.getOverflow());
				}
				MA_RW_Latch.setIsOverflow( EX_MA_Latch.isOverflow() );

				MA_RW_Latch.setRW_enable(true);
				EX_MA_Latch.setMA_enable(false);

				// System.out.println("MA - opcode -" + opcode + " aluresult " + alu_Result + " Clock "+ Clock.getCurrentTime()) ;
			}			
			else
			{
				// System.out.println("MA - opcode -" + opcode + " got busy " +  "Clock "+ Clock.getCurrentTime()) ;
			}
			
		}
	}

	@Override
	public void handleEvent(Event event)
	{
		MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent) event ;

		EX_MA_Latch.setMA_busy(false);
		OF_EX_Latch.setEX_busy(false);
		IF_OF_Latch.setOF_busy(false);
		MA_RW_Latch.setRW_enable(true);
		EX_MA_Latch.setMA_enable(false);

		MA_RW_Latch.setLoadResult(memoryResponseEvent.getValue());
		MA_RW_Latch.setOpcode(opcode) ;
		MA_RW_Latch.setAluResult(alu_Result) ;
		MA_RW_Latch.setDestinationOperand(rd);
		EX_MA_Latch.setDestinationOperand(0);

		// System.out.println("MA - opcode -" + opcode + " aluresult " + alu_Result + " ldResult " + memoryResponseEvent.getValue() + " Clock "+ Clock.getCurrentTime()) ;


		if ( overflowCheck == true )
		{
			MA_RW_Latch.setOverflow(overflow);
		}
		MA_RW_Latch.setIsOverflow( overflowCheck );
	}

}
