	.data
count:
	0
	0
	0
	0
	0
	0
	0
	0
  0
  0
  0
marks:
	10
  3
  0
  0
  10
  7
  1
  10
  7
  8
  9
  9
  9
  8
  2
  4
  2
  0
  9
  1
  8
n:
  21
	.text
main:
	load %x0, $n, %x3
	add %x0, %x0, %x4
loop:
	load %x4, $marks, %x5
	load %x5, $count, %x6
	addi %x6, 1, %x6
	store %x6, $count, %x5
	addi %x4, 1, %x4
	beq %x4, %x3, end1
	jmp loop
end1:
	end
