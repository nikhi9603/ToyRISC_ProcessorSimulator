package processor.pipeline;

public class EX_MA_LatchType {

	boolean MA_enable;
	int aluResult ;

	int rd , opcode , rs1 , overflow;
	boolean overflowCheck ;
	int ProgramCounter;

	public int getProgramCounter(){
		return ProgramCounter;
	}

	public void setProgramCounter(int PC){
		this.ProgramCounter = PC;
	}

	public EX_MA_LatchType()
	{
		MA_enable = false;
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
}
