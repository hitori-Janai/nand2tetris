// push constant 0
@0
D=A
@0
A=M
M=D
@0
M=M+1
// pop local 0
@0
D=A
@1
D=D+M
@15
M=D
@0
AM=M-1
D=M
@15
A=M
M=D
// label LOOP_START
(null$LOOP_START)
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
// push local 0
@0
D=A
@1
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
// pop local 0
@0
D=A
@1
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
// if-goto LOOP_START
@0
AM=M-1
D=M
@null$LOOP_START
D;JNE
// push local 0
@0
D=A
@1
A=D+M
D=M
@0
A=M
M=D
@0
M=M+1
