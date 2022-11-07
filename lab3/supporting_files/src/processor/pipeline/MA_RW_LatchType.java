package processor.pipeline;

public class MA_RW_LatchType {

	boolean RW_enable;
	int aluResult ;
	int rd , opcode , overflow ;
	int ldResult ;
	boolean overflowCheck ;

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	public void setAluResult(int aluResult)
	{
		this.aluResult = aluResult ;
	}

	public int getAluResult()
	{
		return aluResult ;
	}

	public void setLoadResult(int ldResult)
	{
		this.ldResult = ldResult ;
	}

	public int getLoadResult()
	{
		return ldResult ;
	}

	public void setDestinationOperand(int rd)
	{
		this.rd = rd ;
	}

	public int getDestinationOperand()
	{
		return rd ;
	}

	public void setOpcode(int opcode)
	{
		this.opcode = opcode ;
	}

	public int getOpcode()
	{
		return opcode ;
	}

	public void setOverflow(int overflow)
	{
		this.overflow = overflow ;
	}

	public int getOverflow()
	{
		return overflow ;
	}

	public void setIsOverflow( boolean value )
	{
		this.overflowCheck = value ;
	}

	public boolean isOverflow()
	{
		return overflowCheck ;
	}
}
