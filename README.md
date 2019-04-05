# nand2tetris

> [官网](https://www.nand2tetris.org/ "") [coursera视频](https://www.coursera.org/learn/build-a-computer "") [工具包](https://www.nand2tetris.org/software "") [参考@ReionChan](https://reionchan.github.io/2016/05/28/nand2tetris-zh_CN/ "")

## 目录


### 布尔逻辑

* 基本逻辑门<sup>8</sup>

* 多位<sub>16</sub>基本门<sup>5</sup>

* 多通道逻辑门<sup>6</sup>

### 布尔算术

* 加法器
    * [半加器](./project/02/HalfAdder.hdl "")
    * [全加器](./project/02/FullAdder.hdl "")
* ALU
    * [ALU 16-位算术逻辑单元](./project/02/ALU.hdl "")

### 时序逻辑

* [Bit 1-位寄存器](./project/03/a/Bit.hdl "DFF一个周期的延迟?")
* [Register 16-位寄存器](./project/03/a/Register.hdl "")
* ...
* [PC 计数器](./project/03/a/PC.hdl )

### 机器语言
* [Mult.asm 乘法程序](./project/04/mult/mult.asm)
* [Fill.asm I/O处理程序](./project/04/fill/Fill.asm)

### 计算机体系结构

* [Memory 内存](./project/05/Memory.hdl)
* [CPU 中央处理器](./project/05/CPU.hdl)
* [Computer 计算机](./project/05/Computer.hdl)

### 汇编编译器

* [无符号的汇编编译器](./project/06/assembler/src/assembler/)
* [汇编编译器](./project/06/assembler/src/assembler2/)
    * [Parser.java](./project/06/assembler/src/assembler2/Parser.java "分词,添加符号表") 语法分析器
    * [Code.java](./project/06/assembler/src/assembler2/Code.java "助记符字典") 汇编语言助记符译码器
    * [SymbolTable.java](./project/06/assembler/src/assembler2/SymbolTable.java "自定义字典") 符号表
    * [Assembler.java](./project/06/assembler/src/assembler2/Assembler.java "主控逻辑") 汇编编译器入口类
* 测试程序
    * ``` java assembler2.Assembler C:\Users\11054\Desktop\nand2tetris\project\06\pong\Pong.asm```

### 虚拟机-上篇

* [VM翻译器](./project/07/VMtranslator/src/vmtranslator2/) 算术逻辑和内存存取
    * [Parser.java](./project/07/VMtranslator/src/vmtranslator2/Parser.java) VM语法分析
    * [CodeWriter.java](./project/07/VMtranslator/src/vmtranslator2/CodeWriter.java ) VM语法--->汇编
    * [VMTranslator.java](./project/07/VMtranslator/src/vmtranslator2/VMTranslator.java) VM翻译入口
* 测试
    * 堆栈运算
    * 内存访问
