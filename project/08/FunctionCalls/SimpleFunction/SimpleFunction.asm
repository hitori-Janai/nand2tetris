// function SimpleFunction.test 2
(SimpleFunction.test)
@0
A=M
M=0
@0
M=M+1
@0
A=M
M=0
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
// push local 1
@1
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
// not
@0
A=M-1
M=!M
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
// add
@0
AM=M-1
D=M
@0
AM=M-1
M=D+M
@0
M=M+1
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
// sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
// return 
@5
D=A
@1
D=M-D
A=D
D=M
@15
M=D
@0
AM=M-1
D=M
@2
A=M
M=D
@2
D=M+1
@0
M=D
@1
D=A
@1
D=M-D
A=D
D=M
@4
M=D
@2
D=A
@1
D=M-D
A=D
D=M
@3
M=D
@3
D=A
@1
D=M-D
A=D
D=M
@2
M=D
@4
D=A
@1
D=M-D
A=D
D=M
@1
M=D
@15
A=M
0;JMP
