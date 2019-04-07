package vmtranslator;
import java.io.*;

/**
 * RAM[0] SP, RAM[1] LCL, RAM[2] ARG, RAM[3] THIS, RAM[4] THAT, RAM[5-12] temp, RAM[13-15] 通用
 */
public class CodeWriter {

    //当前处在的函数名
    private String curFunctionName = "null";
    private int funReturnAddressIndex = 0;
    //算术逻辑跳转
    private int index_bool = 0;
    BufferedWriter out;

    public CodeWriter(String outpath) throws UnsupportedEncodingException, FileNotFoundException {
        File file = new File(outpath);
        if (file.exists() && file.isFile())
            file.delete();
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath), "UTF-8"));
    }


    public void setFileName(String filename){

    }

    /**
     * @param Command command.asm
     * @throws IOException
     */
    public void writerArithmetic(String command) throws IOException {
        StringBuffer lines = new StringBuffer();
        switch (command) {
            /**
             * 	• and
		            ○ @0
		            ○ AM=M-1
		            ○ D=M //argu1
		            ○ @0
		            ○ AM=M-1//argu0
		            ○ M=D+M
		            ○ @0
		            ○ M=M+1
             */
            case Parser.TYPE_ARI_LOG_ADD:
                lines.append("@0\nAM=M-1\nD=M\n@0\nAM=M-1\nM=D+M\n@0\nM=M+1\n");
                break;
                /**
                 * 	• sub
		                ○ @0
		                ○ AM=M-1
		                ○ D=M //argu1
		                ○ @0
		                ○ A=M-1
                        ○ M=M-D
                 */
            case Parser.TYPE_ARI_LOG_SUB:
                lines.append("@0\nAM=M-1\nD=M\n@0\nA=M-1\nM=M-D\n");
                break;  
                /**
                 * 	• neg (!m +1) not !m
		                ○ @0
		                ○ A=M-1
		                ○ M=!M
                        ○ M=M+1
                 */             
            case Parser.TYPE_ARI_LOG_NEG:
                lines.append("@0\nA=M-1\nM=!M\nM=M+1\n");
                break;
            case Parser.TYPE_ARI_LOG_NOT:
                lines.append("@0\nA=M-1\nM=!M\n");
                break;
                /**
                 * 	• eq.JEQ gt.JGT lt.JLT
		                ○ @0
		                ○ AM=M-1            argu1
		                ○ D=M
		                ○ @0
		                ○ AM=M-1            argu0
		                ○ D=M-D  //对比结果  argu0-argu1
		                ○ @0  //先假设argu0: true(0xFFFF:-1)
		                ○ A=M
		                ○ M=-1
		                ○ @comp.[index].true
		                ○ D;[JEQ] //符合条件,猜对了.跳
		                ○ @0 // false(0x0000:0)
		                ○ A=M
		                ○ M=0
		                ○ (comp.[index].true)
		                ○ @0
		                ○ M=M+1
                 */
            case Parser.TYPE_ARI_LOG_EQ:
                lines.append("@0\nAM=M-1\nD=M\n@0\nAM=M-1\nD=M-D\n@0\nA=M\nM=-1\n@COMP."+index_bool+".TRUE\nD;JEQ\n@0\nA=M\nM=0\n(COMP."+index_bool+".TRUE)\n@0\nM=M+1\n");
                index_bool++;
                break;
            case Parser.TYPE_ARI_LOG_GT:
                lines.append("@0\nAM=M-1\nD=M\n@0\nAM=M-1\nD=M-D\n@0\nA=M\nM=-1\n@COMP."+index_bool+".TRUE\nD;JGT\n@0\nA=M\nM=0\n(COMP."+index_bool+".TRUE)\n@0\nM=M+1\n");
                index_bool++;
                break;
            case Parser.TYPE_ARI_LOG_LT:
                lines.append("@0\nAM=M-1\nD=M\n@0\nAM=M-1\nD=M-D\n@0\nA=M\nM=-1\n@COMP."+index_bool+".TRUE\nD;JLT\n@0\nA=M\nM=0\n(COMP."+index_bool+".TRUE)\n@0\nM=M+1\n");
                index_bool++;
                break;
                /**
                 * 	• and& or|
		                ○ @0
		                ○ AM=M-1
		                ○ D=M
		                ○ @0
		                ○ A=M-1
		                ○ M=D&M
                 */
            case Parser.TYPE_ARI_LOG_AND:
                lines.append("@0\nAM=M-1\nD=M\n@0\nA=M-1\nM=D&M\n");
                break;
            case Parser.TYPE_ARI_LOG_OR:
                lines.append("@0\nAM=M-1\nD=M\n@0\nA=M-1\nM=D|M\n");
            break;
        }
        out.write("// " + command + "\n");
        out.write(lines.toString());
    }

    /**
     * 
     * @param command C_PUSH/POP
     * @param segment
     * @param index
     * @throws IOException
     */
    public void WriterPushPop(String command, String segment, int index) throws IOException {
        StringBuffer lines = new StringBuffer();
        switch (segment) {
            /**
             * 	• push constant index
		            ○ @index
		            ○ D=A  //index
		            ○ @0  //sp
		            ○ A=M
		            ○ M=D
		            ○ @0 //sp
		            ○ M=M+1
             */
            case Parser.TYPE_SEG_CONS:
                if(command.equals(Parser.TYPE_PUSH)){
                    lines.append("@").append(index).append("\nD=A\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    throw new RuntimeException("[ pop constant index ] isnt permitted!!!"); 
                }              
                break;
            case Parser.TYPE_SEG_LOCA:
                /**
                 * 		○ push
                            § @index
                            § D=A
                            § @1  // LCL
                            § A=D+A
                            § D=M  //取出local值
                            § @0
                            § A=M
                            § M=D
                            § @0
                            § M=M+1
                */
                if (command.equals(Parser.TYPE_PUSH)) {
                    lines.append("@").append(index).append("\nD=A\n@1\nA=D+M\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                /**
                 * 		○ pop
                            § @index
                            § D=A
                            § @1
                            § D=D+A
                            § @15
                            § M=D //RAM[15]保存LCL地址
                            § @0
                            § AM=M-1
                            § D=M  //取出栈中值
                            § @15
                            § A=M
                            § M=D
                 */
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@1\nD=D+M\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;
            case Parser.TYPE_SEG_ARGU:
                if (command.equals(Parser.TYPE_PUSH)) {
                    lines.append("@").append(index).append("\nD=A\n@2\nA=D+M\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@2\nD=D+M\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;
            case Parser.TYPE_SEG_THIS:
                if (command.equals(Parser.TYPE_PUSH)) {
                    lines.append("@").append(index).append("\nD=A\n@3\nA=D+M\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@3\nD=D+M\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;
            case Parser.TYPE_SEG_THAT:
                if (command.equals(Parser.TYPE_PUSH)) {
                    lines.append("@").append(index).append("\nD=A\n@4\nA=D+M\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@4\nD=D+M\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;
            case Parser.TYPE_SEG_POIN:
                if (index != 0 && index != 1) {
                    throw new RuntimeException(command+" the value of the pointer index can only be 1 or 0");
                }
                if (command.equals(Parser.TYPE_PUSH)) { //A=D+A
                    lines.append("@").append(index).append("\nD=A\n@3\nA=D+A\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@3\nD=D+A\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;
            case Parser.TYPE_SEG_TEMP:
                if (command.equals(Parser.TYPE_PUSH)) { //A=D+A
                    lines.append("@").append(index).append("\nD=A\n@5\nA=D+A\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@").append(index).append("\nD=A\n@5\nD=D+A\n@15\nM=D\n@0\nAM=M-1\nD=M\n@15\nA=M\nM=D\n");
                }
                break;                
            case Parser.TYPE_SEG_STAT:
                /**
                 * 		○ push 二
                            § @stat.index
                            § D=M
                            § @0
                            § A=M
                            § M=D
                            § @0
                            § M=M+1
                 */
                String stat = curFunctionName.substring(0,curFunctionName.lastIndexOf("."));
                if (command.equals(Parser.TYPE_PUSH)) { //A=D+A
                    lines.append("@" + stat + ".").append(index).append("\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                /**
                 * 		○ pop
                            § @0
                            § AM=M-1
                            § D=M  //取出栈中值
                            § @stat.index
                            § M=D
                 */
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@0\nAM=M-1\nD=M\n@").append(stat + ".").append(index).append("\nM=D\n");
                }
                break;
            default:
                throw new RuntimeException("unknow segment : " + segment + "'");

        }
        
        out.write("// " + command + " "+ segment + " " + index + "\n");
        out.write(lines.toString());
    }
    
    /**
     * close output file
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        out.close();
    }

    public void writerInit() throws IOException {
        StringBuffer lines = new StringBuffer();
        lines.append("@256\nD=A\n@0\nM=D\n");
        out.write("// bootstrap \n");
        out.write(lines.toString());
        curFunctionName = "bootstrap";
        writerCall("Sys.init", 0);
    }

    public void writerLabel(String label) throws IOException {
        StringBuffer lines = new StringBuffer();
        //(funName$label)
        lines.append("("+ curFunctionName + "$" + label +")\n");
        out.write("// label " + label + "\n");
        out.write(lines.toString());

    }
    public void writerGoto(String label) throws IOException {
        StringBuffer lines = new StringBuffer();
        /**
         * 		○ @funName$XXX
                ○ 0;JMP
         */
        lines.append("@"+ curFunctionName + "$" + label +"\n0;JMP\n");
        out.write("// goto " + label + "\n");
        out.write(lines.toString());

    }
    public void writerIf(String label) throws IOException {
        StringBuffer lines = new StringBuffer();
        /**
         * 		○ @0
                ○ AM=M-1
                ○ D=M
                ○ @funName$XXX
                ○ D;JNE
         */
        lines.append("@0\nAM=M-1\nD=M\n" + "@"+ curFunctionName + "$" + label +"\nD;JNE\n");
        out.write("// if-goto " + label + "\n");
        out.write(lines.toString());

    }
    
    public void writerFunction(String functionName,int numLocals) throws IOException {
        StringBuffer lines = new StringBuffer();
        curFunctionName = functionName;
        out.write("// function " + functionName + " " + numLocals +"\n");
        /**
         * 		○ (f)
                ○ @0 //start
                ○ A=M
                ○ M=0
                ○ @0
                ○ M=M+1 //start ->enb, k times 
         */
        lines.append("(" + functionName + ")\n");
        while (numLocals-- > 0) {
            lines.append("@0\nA=M\nM=0\n@0\nM=M+1\n");
        }

        out.write(lines.toString());

    }
    public void writerReturn() throws IOException {
        StringBuffer lines = new StringBuffer();

        /**
         * 						@5
                                D=A
                                @1
                                D=M-D
                                A=D
                                D=M
                                @15
                                M=D ////保存返回地址在ram[15]
                                @0
                                AM=M-1
                                D=M //取得返回值 pop()
                                @2 //arg
                                A=M
                                M=D //保存返回值在arg0
                                @2
                                D=M+1 //*
                                @0
                                M=D //恢复SP
                                @1 
                                D=A
                                @1
                                D=M-D
                                A=D
                                D=M
                                @4
                                M=D  //恢复that
                                @2 
                                D=A
                                @1
                                D=M-D
                                A=D
                                D=M
                                @3
                                M=D  //恢复this
                                @3 
                                D=A
                                @1
                                D=M-D
                                A=D
                                D=M
                                @2
                                M=D  //恢复arg
                                @4 
                                D=A
                                @1
                                D=M-D
                                A=D
                                D=M
                                @1
                                M=D  //恢复LCL
                                @15
                                A=M
                                0;JMP // 跳转

         */
        lines.append("@5\nD=A\n@1\nD=M-D\nA=D\nD=M\n@15\nM=D\n@0\nAM=M-1\nD=M\n@2\nA=M\nM=D\n@2\nD=M+1\n@0\nM=D\n@1\nD=A\n@1\nD=M-D\nA=D\nD=M\n@4\nM=D\n@2\nD=A\n@1\nD=M-D\nA=D\nD=M\n@3\nM=D\n@3\nD=A\n@1\nD=M-D\nA=D\nD=M\n@2\nM=D\n@4\nD=A\n@1\nD=M-D\nA=D\nD=M\n@1\nM=D\n@15\nA=M\n0;JMP\n");
        out.write("// return \n");
        out.write(lines.toString());
    }
    public void writerCall(String functionName,int numArgs) throws IOException {
        StringBuffer lines = new StringBuffer();
        String retAdd = curFunctionName + "." + "return-address." + funReturnAddressIndex++;
        /**
         * 
         */
        lines.append("@"+ retAdd +"\nD=A\n@0\nA=M\nM=D\n@0\nM=M+1\n@1\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n@2\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n@3\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n@4\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n@" + numArgs + "\nD=A\n@0\nD=M-D\n@5\nD=D-A\n@2\nM=D\n@0\nD=M\n@1\nM=D\n@" + functionName + "\n0;JMP\n(" + retAdd + ")\n");
        out.write("// call " + functionName + " " + numArgs +"\n");
        out.write(lines.toString());
    }
}