	.data
n:
	10
	.text
main:
	load %x0, $n, %x3
	add %x0, %x0, %x4
	addi %x0, 1, %x5
	addi %x0, 3, %x6
	addi %x0, 65535, %x10
	store %x4, 0, %x10
	subi %x10, 1, %x10
	bgt %x3, %x5, moreThan1
	jmp end1
moreThan1:
	store %x5, 0, %x10
	subi %x10, 1, %x10
loop:
	bgt %x6, %x3, end1
	add %x5, %x0, %x7
	add %x4, %x5, %x5
	add %x7, %x0, %x4
	store %x5, 0, %x10
	subi %x10, 1, %x10
	addi %x6, 1, %x6
	jmp loop
end1:
	end
