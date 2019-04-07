@curfunname.return-address.index
D=A
@0
A=M
M=D
@0
M=M+1 //压入返回值
@1
D=A
@0
A=M
M=D
@0
M=M+1 //压入LCL
@2
D=A
@0
A=M
M=D
@0
M=M+1 //压入arg
@3
D=A
@0
A=M
M=D
@0
M=M+1 //压入this
@4
D=A
@0
A=M
M=D
@0
M=M+1 //压入that
@numArgs
D=A
@0
D=M-D
@5
D=D-A
@2
M=D //重置arg
@0
D=A
@1
M=D //重置sp
@functionName
0;JMP
(curfunname.return-address.index)
