	.data
n:
	5
l:
	2
	-1
	7
	5
	3
	.text
main:
	add %x0, %x0, %x3
	add %x0, %x0, %x10
	load %x0, $n, %x6
loop:
	beq %x3, %x6, end1
	load %x3, $l, %x4
	bgt %x4, %x0, positive
	beq %x4, %x0, even
	addi %x3, 1, %x3
	jmp loop
positive:
	divi %x4, 2, %x5
	beq %x31, %x0, even
	addi %x3, 1, %x3
	jmp loop
even:
	addi %x10, 1, %x10
	addi %x3, 1, %x3
	jmp loop
end1:
	end
