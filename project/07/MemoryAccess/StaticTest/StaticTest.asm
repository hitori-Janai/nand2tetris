// push constant 111
@111
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 333
@333
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 888
@888
D=A
@0
A=M
M=D
@0
M=M+1
// pop static 8
@0
AM=M-1
D=M
@R24
M=D
// pop static 3
@0
AM=M-1
D=M
@R19
M=D
// pop static 1
@0
AM=M-1
D=M
@R17
M=D
// push static 3
@R19
D=M
@0
A=M
M=D
@0
M=M+1
// push static 1
@R17
D=M
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
// push static 8
@R24
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
