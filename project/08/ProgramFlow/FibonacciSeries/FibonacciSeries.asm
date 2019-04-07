// push argument 1
@1
D=A
@2
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// pop pointer 1
@1
D=A
@3
D=D+A
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// push constant 0
@0
D=A
@0
A=M
M=D
@0
M=M+1
// pop that 0
@0
D=A
@4
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// push constant 1
@1
D=A
@0
A=M
M=D
@0
M=M+1
// pop that 1
@1
D=A
@4
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// push argument 0
@0
D=A
@2
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// push constant 2
@2
D=A
@0
A=M
M=D
@0
M=M+1
// sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
// pop argument 0
@0
D=A
@2
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// label MAIN_LOOP_START
(null$MAIN_LOOP_START)
// push argument 0
@0
D=A
@2
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// if-goto COMPUTE_ELEMENT
@0
AM=M-1
D=M
@null$COMPUTE_ELEMENT
D;JNE
// goto END_PROGRAM
@null$END_PROGRAM
0;JMP
// label COMPUTE_ELEMENT
(null$COMPUTE_ELEMENT)
// push that 0
@0
D=A
@4
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// push that 1
@1
D=A
@4
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// add
@0
AM=M-1
D=M
@0
AM=M-1
M=D+M
@0
M=M+1
// pop that 2
@2
D=A
@4
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// push pointer 1
@1
D=A
@3
A=D+A
D=M
@0
A=M
M=D
@0
M=M+1
// push constant 1
@1
D=A
@0
A=M
M=D
@0
M=M+1
// add
@0
AM=M-1
D=M
@0
AM=M-1
M=D+M
@0
M=M+1
// pop pointer 1
@1
D=A
@3
D=D+A
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// push argument 0
@0
D=A
@2
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
// push constant 1
@1
D=A
@0
A=M
M=D
@0
M=M+1
// sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
// pop argument 0
@0
D=A
@2
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// goto MAIN_LOOP_START
@null$MAIN_LOOP_START
0;JMP
// label END_PROGRAM
(null$END_PROGRAM)
