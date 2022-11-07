	.data
n:
	10
	.text
main:
	load %x0, $n, %x3
	add %x0, %x0, %x4
	addi %x0, 1, %x5
	addi %x0, 3, %x6
	store %x4, 65535, %x0
	bgt %x3, %x5, moreThan1
	jmp loop
moreThan1:
	store %x5, 65534, %x0
loop:
	bgt %x6, %x3, end1
	add %x5, %x0, %x7
	add %x4, %x5, %x5
	add %x7, %x0, %x4
	sub %x0, %x6, %x8
	store %x5, 65536, %x8
	addi %x6, 1, %x6
	jmp loop
end1:
	end
