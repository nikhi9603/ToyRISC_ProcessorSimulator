package processor.pipeline;

public class IF_OF_LatchType {
	
	boolean OF_enable , OF_busy;
	int instruction;
	int PC ;
	
	public IF_OF_LatchType()
	{
		OF_enable = false;
		OF_busy = false;
	}

	public boolean isOF_enable() 
	{
		return OF_enable;
	}

	public void setOF_enable(boolean oF_enable) 
	{
		OF_enable = oF_enable;
	}

	public void setPC(int PC)
	{
		this.PC = PC ;
	}

	public void setInstruction(int instruction) 
	{
		this.instruction = instruction;
	}

	public int getInstruction() 
	{
		return instruction;
	}

	public int getPC()
	{
		return PC ;
	}

	public boolean isOF_busy()
	{
		return OF_busy;
	}

	public void setOF_busy( boolean OF_busy )
	{
		this.OF_busy = OF_busy ;
	}
}
