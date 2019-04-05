// push constant 17
@17
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 17
@17
D=A
@0
A=M
M=D
@0
M=M+1
// eq
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.0.TRUE
D;JEQ
@0
A=M
M=0
(COMP.0.TRUE)
@0
M=M+1
// push constant 17
@17
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 16
@16
D=A
@0
A=M
M=D
@0
M=M+1
// eq
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.1.TRUE
D;JEQ
@0
A=M
M=0
(COMP.1.TRUE)
@0
M=M+1
// push constant 16
@16
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 17
@17
D=A
@0
A=M
M=D
@0
M=M+1
// eq
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.2.TRUE
D;JEQ
@0
A=M
M=0
(COMP.2.TRUE)
@0
M=M+1
// push constant 892
@892
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 891
@891
D=A
@0
A=M
M=D
@0
M=M+1
// lt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.3.TRUE
D;JLT
@0
A=M
M=0
(COMP.3.TRUE)
@0
M=M+1
// push constant 891
@891
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 892
@892
D=A
@0
A=M
M=D
@0
M=M+1
// lt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.4.TRUE
D;JLT
@0
A=M
M=0
(COMP.4.TRUE)
@0
M=M+1
// push constant 891
@891
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 891
@891
D=A
@0
A=M
M=D
@0
M=M+1
// lt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.5.TRUE
D;JLT
@0
A=M
M=0
(COMP.5.TRUE)
@0
M=M+1
// push constant 32767
@32767
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 32766
@32766
D=A
@0
A=M
M=D
@0
M=M+1
// gt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.6.TRUE
D;JGT
@0
A=M
M=0
(COMP.6.TRUE)
@0
M=M+1
// push constant 32766
@32766
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 32767
@32767
D=A
@0
A=M
M=D
@0
M=M+1
// gt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.7.TRUE
D;JGT
@0
A=M
M=0
(COMP.7.TRUE)
@0
M=M+1
// push constant 32766
@32766
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 32766
@32766
D=A
@0
A=M
M=D
@0
M=M+1
// gt
@0
AM=M-1
D=M
@0
AM=M-1
D=M-D
@0
A=M
M=-1
@COMP.8.TRUE
D;JGT
@0
A=M
M=0
(COMP.8.TRUE)
@0
M=M+1
// push constant 57
@57
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 31
@31
D=A
@0
A=M
M=D
@0
M=M+1
// push constant 53
@53
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
// push constant 112
@112
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
// neg
@0
A=M-1
M=!M
M=M+1
// and
@0
AM=M-1
D=M
@0
A=M-1
M=D&M
// push constant 82
@82
D=A
@0
A=M
M=D
@0
M=M+1
// or
@0
AM=M-1
D=M
@0
A=M-1
M=D|M
// not
@0
A=M-1
M=!M
