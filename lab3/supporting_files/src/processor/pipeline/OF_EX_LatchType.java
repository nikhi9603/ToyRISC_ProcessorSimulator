package processor.pipeline;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	int opcode , rs1 , rs2 , rd , imm ;

	public OF_EX_LatchType()
	{
		EX_enable = false;
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
}
