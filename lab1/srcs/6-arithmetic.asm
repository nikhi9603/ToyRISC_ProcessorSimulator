	.data
a:
	10
d:
	5
n:
	10
	.text
main:
	load %x0, $a, %x3
	load %x0, $d, %x4
	load %x0, $n, %x5
	addi %x0, 1, %x6
loop:
	bgt %x6, %x5, end1
	subi %x6, 1, %x7
	mul %x7, %x4, %x8
	add %x3, %x8, %x9
	sub %x0, %x6, %x10
	store %x9, 65536, %x10
	addi %x6, 1, %x6
	jmp loop
end1:
	end
