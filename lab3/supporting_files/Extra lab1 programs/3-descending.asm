	.data
a:
	0
	70
	80
	40
	20
	10
	30
	50
	60
n:
	9
	.text
main:
	load %x0, $n, %x3
	load %x0, $n, %x4
	addi %x0, 1, %x5
	addi %x0, 1, %x6
	add %x0, %x0, %x7
step1:
	beq %x3, %x5, step2
	load %x7, $a, %x10
	subi %x3, 1, %x3
	addi %x7, 1, %x7
	load %x7, $a, %x11
	blt %x10, %x11, swap
	jmp step1
step2:
	load %x0, $n, %x3
	subi %x4, 1, %x4
	addi %x5, 1, %x5
	add %x0, %x0, %x7
	beq %x4, %x6, end1
	jmp step1
swap:
	store %x10, $a, %x7
	subi %x7, 1, %x7
	store %x11, $a, %x7
	addi %x7, 1, %x7
	jmp step1
end1:
	end
