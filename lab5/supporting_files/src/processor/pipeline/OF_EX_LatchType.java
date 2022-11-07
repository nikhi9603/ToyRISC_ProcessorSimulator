package processor.pipeline;

public class OF_EX_LatchType {
	
	boolean EX_enable , EX_busy ;
	int opcode , rs1 , rs2 , rd , imm ;
	int ProgramCounter;

	public OF_EX_LatchType()
	{
		EX_busy = false;
		EX_enable = false;
		opcode = -1 ;
		rs1 = -1 ;
		rs2 = -1 ;
		rd = -1 ;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	public void setOpcode( int opcode )
	{
		this.opcode = opcode ;
	}

	public int getOpcode()
	{
		return opcode  ;
	}

	public void setSourceOperand1( int rs1 )
	{
		this.rs1 = rs1 ;
	}

	public int getSourceOperand1()
	{
		return rs1 ;
	}

	public void setSourceOperand2( int rs2 )
	{
		this.rs2 = rs2 ;
	}

	public int getSourceOperand2()
	{
		return rs2 ;
	}

	public void setDestinationOperand( int rd )
	{
		this.rd = rd ;
	}

	public int getDestinationOperand()
	{
		return rd ;
	}

	public void setImmediate( int imm )
	{
		this.imm = imm ;
	}

	public int getImmediate()
	{
		return imm ;
	}

	public void setNopInstruction()
	{
		this.opcode = 0 ;
		this.rs1 = 0 ;
		this.rs2 = 0 ;
		this.rd = 0 ;
	}

	public int getProgramCounter(){
		return ProgramCounter;
	}

	public void setProgramCounter(int PC){
		this.ProgramCounter = PC;
	}

	public boolean isEX_busy()
	{
		return EX_busy;
	}
	
	public void setEX_busy( boolean EX_busy)
	{
		this.EX_busy = EX_busy;
	}
}
