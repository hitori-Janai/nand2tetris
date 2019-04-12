// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
@i
M=0 //保存变黑的格子数,线性连续增长

(LOOP)
@24576
D=M
@BLACK
D;JGT

//white start
D=M[i]
@LOOP
D;JLT //i LE 0 ,nothing
D=M[i]
@SCREEN
A=A+D
M=0
@i
M=M-1
@LOOP
0;JMP
//white end

(BLACK)
D=M[i]
@8192 //32*256 _16
D=D-A //-1 - 8192 // 
@LOOP
D;JGE //i GE 8192 , nothing 
D=M[i]
@SCREEN
A=A+D
M=-1
@i
M=M+1

@LOOP
0;JMP