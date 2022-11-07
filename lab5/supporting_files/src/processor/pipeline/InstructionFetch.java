package processor.pipeline;

import configuration.Configuration;
import processor.Clock;
import processor.Processor;
import generic.MemoryReadEvent;
import generic.Event;
import generic.Element;
import generic.Simulator;
import generic.Statistics;
import generic.MemoryResponseEvent;

public class InstructionFetch implements Element {
	
	Processor containingProcessor;
	public IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performIF()
	{
		if(IF_EnableLatch.isIF_enable())
		{
			if (IF_EnableLatch.isIF_busy())		
			{
				return;
			}

			int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			IF_OF_Latch.setPC(currentPC);

			//System.out.println("IF - opcode - " + ( (containingProcessor.getRegisterFile().getValue(currentPC)) >>> 27 ) ) ;

			Simulator.getEventQueue().addEvent( 
				new MemoryReadEvent( 
					Clock.getCurrentTime() + Configuration.mainMemoryLatency , 
					this , 
					containingProcessor.getMainMemory() , 
					currentPC ) ) ;
			
			containingProcessor.getRegisterFile().setProgramCounter( currentPC + 1 );
			IF_EnableLatch.setIF_busy(true);	
		}
	}


	@Override
	public void handleEvent(Event event)
	{
		if( IF_OF_Latch.isOF_busy() )
		{
			event.setEventTime( Clock.getCurrentTime () + 1 ) ;
			Simulator.getEventQueue().addEvent(event) ;
		}
		else
		{
			MemoryResponseEvent responseEvent = ( MemoryResponseEvent ) event;

			Statistics.numberOfInstructions += 1 ;

			// System.out.println("IF - opcode - " + ( responseEvent.getValue() >>> 27 ) + " Clock  " + Clock.getCurrentTime() );
			IF_OF_Latch.setInstruction( responseEvent.getValue() ) ;
			IF_OF_Latch.setOF_enable(true );
			IF_EnableLatch.setIF_busy( false );
		}
	}

}