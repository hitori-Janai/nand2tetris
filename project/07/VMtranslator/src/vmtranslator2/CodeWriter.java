package vmtranslator2;
import java.io.*;

/**
 * RAM[0] SP, RAM[1] LCL, RAM[2] ARG, RAM[3] THIS, RAM[4] THAT, RAM[5-12] temp, RAM[13-15] 通用
 */
public class CodeWriter {


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
                /**
                 * //从RAM[16] 开始
                 * 还有一种处理方式添加关联ram[x]的陌生字串到符号表  @RX
                 */
            case Parser.TYPE_SEG_STAT:
                /**
                 * 		○ push 二
                            § @R[16+index]
                            § D=M
                            § @0
                            § A=M
                            § M=D
                            § @0
                            § M=M+1
                 */
                if (command.equals(Parser.TYPE_PUSH)) { //A=D+A
                    lines.append("@R").append(16+index).append("\nD=M\n@0\nA=M\nM=D\n@0\nM=M+1\n");
                }
                /**
                 * 		○ pop
                            § @0
                            § AM=M-1
                            § D=M  //取出栈中值
                            § @R[16+index]
                            § M=D
                 */
                if (command.equals(Parser.TYPE_POP)) {
                    lines.append("@0\nAM=M-1\nD=M\n@R").append(16+index).append("\nM=D\n");
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
}