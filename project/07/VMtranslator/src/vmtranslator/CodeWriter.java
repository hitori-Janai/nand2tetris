package vmtranslator;
import java.io.*;

/**
 * CodeWriter
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
        /**
         * hack中栈的增长方向与内存一致
         * 	• push constant index
		        ○ @index
		        ○ D=A  //index
		        ○ @0  //sp
		        ○ M=D
		        ○ @0 //sp
		        ○ M=M+1
         */
        if(command.equals(Parser.TYPE_PUSH)){
            if (segment.equals(Parser.TYPE_SEG_CONS)) {
                lines.append("@").append(index).append("\nD=A\n@0\nA=M\nM=D\n@0\nM=M+1\n");
            }
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