package processor.pipeline;

public class EX_MA_LatchType {

	boolean MA_enable , MA_busy;
	int aluResult ;

	int rd , opcode , rs1 , overflow;
	boolean overflowCheck ;


	public EX_MA_LatchType()
	{
		MA_busy = false;
		MA_enable = false;
		opcode = -1 ;
		rs1 = -1 ;
		rd = -1 ;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

	public int getAluResult()
	{
		return aluResult;
	}

	public void setAluResult(int aluResult)
	{
		this.aluResult = aluResult;
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

	public void setSourceOperand1(int rs1)		// register number will be set as rs1
	{
		this.rs1 = rs1 ;
	}

	public int getSourceOperand1()
	{
		return rs1 ;
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

	public boolean isMA_busy()
	{
		return MA_busy;
	}

	public void setMA_busy(boolean MA_busy)
	{
		this.MA_busy = MA_busy;
	}
}
