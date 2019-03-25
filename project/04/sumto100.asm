

//Adds 1+...+100

//@i
//M=1
M[i]=1
M[sum]=0

(LOOP)
D=M[i]
// D=D-@100
@100
D=D-A
@END
D;JGT

D=M[i]
@sum
M=D+M
@i
M=M[i]+1
@LOOP
0;JMP

(END)
@END
0;JMP

