	.data
a:
	190
	.text
main:
	load %x0, $a, %x3
	divi %x3, 2, %x6
	addi %x0, 1, %x4
	beq %x3, %x0, notprime
	beq %x3, %x4, notprime
	addi %x4, 1, %x4
loop:
	bgt %x4, %x6, prime
	div %x3, %x4, %x5
	beq %x31, %x0, notprime
	addi %x4, 1, %x4
	jmp loop
notprime:
	subi %x0, 1, %x10
	end
prime:
	addi %x0, 1, %x10
	end	
