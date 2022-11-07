package generic;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Iterator;

import processor.Clock;
import processor.pipeline.InstructionFetch;

public class EventQueue {
	
	PriorityQueue<Event> queue;
	
	public EventQueue()
	{
		queue = new PriorityQueue<Event>(new EventComparator());
	}
	
	public void addEvent(Event event)
	{
		queue.add(event);
	}

	public void processEvents()
	{
		while(queue.isEmpty() == false && queue.peek().getEventTime() <= Clock.getCurrentTime())
		{
			Event event = queue.poll();
			event.getProcessingElement().handleEvent(event);
		}
	}

	public void deleteIF_Instruction()
	{
		Iterator<Event> queueIterator = queue.iterator() ;
		Event event ;

		while (queueIterator.hasNext())
		{
			// We have to remove the event which was added in Instruction fetch to read instruction
			// If queue is not empty then checking each element of queue is that the instruction added in IF 
			event = queueIterator.next() ;

			// reqyesting element is IF means instruction fetch added this mempory read event to read from main memory
			if (event.requestingElement instanceof InstructionFetch)
			{
				queueIterator.remove();		// removing the instruction in IF stage

				// We should set IF stage as not busy
				((InstructionFetch)event.requestingElement).IF_EnableLatch.setIF_busy(false);
				((InstructionFetch)event.requestingElement).IF_EnableLatch.setIF_enable(true);
			}
		}
	}
}

class EventComparator implements Comparator<Event>
{
	@Override
    public int compare(Event x, Event y)
    {
		if(x.getEventTime() < y.getEventTime())
		{
			return -1;
		}
		else if(x.getEventTime() > y.getEventTime())
		{
			return 1;
		}
		else
		{
			return 0;
		}
    }
}
